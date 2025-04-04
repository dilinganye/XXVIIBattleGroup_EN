package data.scripts.UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.EntropyAmplifierStats;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import static data.shipsystems.scripts.SGB_AsAboveSoBelow.getRandomBlendColor;
import static data.shipsystems.scripts.SGB_ConstructWingStats.getAdditionalFor;
import static data.shipsystems.scripts.SGB_ConstructWingStats.getRateCost;
import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_UNGP_EndlessWing extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float spawnRateMult, poolMaxSize, cooldownRateMult;
    public static Color TEXT_COLOR = new Color(255,55,55,255);
    private static final Map<ShipAPI.HullSize, Float> BASE_WING_TIME = new HashMap();
    private final WeightedRandomPicker<ShipAPI> cache = new WeightedRandomPicker();
    public SGB_UNGP_EndlessWing() {
    }

    static {
        BASE_WING_TIME.put(ShipAPI.HullSize.CAPITAL_SHIP, 60.0F);
        BASE_WING_TIME.put(ShipAPI.HullSize.CRUISER, 96.0F);
        BASE_WING_TIME.put(ShipAPI.HullSize.DESTROYER, 120.0F);
        BASE_WING_TIME.put(ShipAPI.HullSize.FRIGATE, 180.0F);
    }
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.spawnRateMult = this.getValueByDifficulty(0, difficulty);
        this.poolMaxSize = this.getValueByDifficulty(1, difficulty);
        this.cooldownRateMult = this.getValueByDifficulty(2, difficulty);
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        float rate = this.getValueByDifficulty(0, difficulty);
        if (index == 0) {
            return this.getFactorString((Float)BASE_WING_TIME.get(ShipAPI.HullSize.FRIGATE) / rate);
        } else if (index == 1) {
            return this.getFactorString((Float)BASE_WING_TIME.get(ShipAPI.HullSize.DESTROYER) / rate);
        } else if (index == 2) {
            return this.getFactorString((Float)BASE_WING_TIME.get(ShipAPI.HullSize.CRUISER) / rate);
        } else if (index == 3) {
            return this.getFactorString((Float)BASE_WING_TIME.get(ShipAPI.HullSize.CAPITAL_SHIP) / rate);
        } else {
            return index == 4 ? "50%" : null;
        }
    }
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
            return difficulty.getLinearValue(1.0F, 1.0F);
        } else if (index == 1) {
            return difficulty.getLinearValue(10.0F, 6.0F);
        } else {
            return index == 2 ? difficulty.getLinearValue(1.0F, 1.0F) : 0.0F;
        }
        /*
        else{
            return index == 1 ? (float)((int)difficulty.getLinearValue(10.0F, 100.0F)) : 1.0F;}
         */
    }

    public void advanceInCombat(CombatEngineAPI engine, float amount) {
    }

    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
        if (enemy.isAlive() && enemy != null) {
            if (!enemy.isDrone() && !enemy.isFighter()) {
                if (!enemy.isStation() && !enemy.isStationModule()) {
                    if (!(enemy.getNumFighterBays() == 0)) {
                        CombatEngineAPI engine = Global.getCombatEngine();
                        Float cooldownPool = (Float) getDataInEngine(engine, this.buffID);
                        if (cooldownPool == null) {
                            cooldownPool = 0.0F;
                            putDataInEngine(engine, this.buffID, cooldownPool);
                        }

                        cooldownPool = Math.max(cooldownPool - amount * this.cooldownRateMult, 0.0F);
                        if (enemy.getCustomData().get(this.buffID) == null) {
                            float rate = (Float) BASE_WING_TIME.get(enemy.getHullSize()) / this.spawnRateMult;
                            enemy.setCustomData(this.buffID, new IntervalUtil(rate, rate));
                        }

                        if (cooldownPool < this.poolMaxSize) {
                            IntervalUtil timer = (IntervalUtil) enemy.getCustomData().get(this.buffID);
                            timer.advance(amount);
                            if (timer.intervalElapsed()) {
                                //——核心效果
                                MutableShipStatsAPI stats = enemy.getMutableStats();
                                float minRate = Global.getSettings().getFloat("minFighterReplacementRate");
                                int bays = enemy.getLaunchBaysCopy().size();
                                float cost = getRateCost(bays);

                                Float randomA = (float) Math.random();
                                if (randomA >= 0.5f) {
                                    if (randomA <= 0.7f) {
                                        for (FighterLaunchBayAPI bay : enemy.getLaunchBaysCopy()) {
                                            if (bay.getWing() == null) continue;

                                            float rate = Math.max(minRate, bay.getCurrRate() - cost);
                                            bay.setCurrRate(rate);
                                            bay.makeCurrentIntervalFast();
                                            FighterWingSpecAPI spec = bay.getWing().getSpec();

                                            int maxTotal = spec.getNumFighters();
                                            int actualAdd = maxTotal - bay.getWing().getWingMembers().size();
                                            if (actualAdd > 0) {
                                                bay.setFastReplacements(bay.getFastReplacements());
                                                bay.setExtraDeployments(actualAdd);
                                                bay.setExtraDeploymentLimit(maxTotal);
                                            }
                                        }
                                        enemy.getFluxTracker().showOverloadFloatyIfNeeded(txt("SGB_UNGP_Wing"), TEXT_COLOR, 4f, true);
                                    } else if (randomA > 0.7f && randomA <= 1) {
                                        for (FighterLaunchBayAPI bay : enemy.getLaunchBaysCopy()) {
                                            if (bay.getWing() == null) continue;

                                            float rate = Math.max(minRate, bay.getCurrRate() - cost);
                                            bay.setCurrRate(rate);

                                            bay.makeCurrentIntervalFast();
                                            FighterWingSpecAPI spec = bay.getWing().getSpec();

                                            int addForWing = getAdditionalFor(spec, bays);
                                            int maxTotal = spec.getNumFighters() + addForWing;

                                            String wingID = bay.getWing().getSpec().getId();    // 一些基础参量 不用管
                                            String wingID2 = bay.getWing().getWingId();
                                            ShipAPI wingLeader = bay.getWing().getLeader();


                                            if (wingID2.endsWith("_wing")) {    // 对飞机进行处理
                                                // 代码不正规会导致不能生成 不正规就不要混进来了
                                                Vector2f loc;    // 获取目标位置 获取舰船基础朝向
                                                ShipAPI leader;
                                                //先决定方向
                                                int left = -1;
                                                if (Math.random() <= 0.5) {
                                                    left = 1;
                                                }
                                                Global.getCombatEngine().getCombatUI().addMessage(0, Color.RED, this.rule.getExtra1()); // 提示消息

                                                Float locx = (Global.getCombatEngine().getMapWidth() - MathUtils.getRandomNumberInRange(0, 400)) / 2 * left;
                                                Float locy = (Global.getCombatEngine().getMapHeight() -
                                                                MathUtils.getRandomNumberInRange(0, Global.getCombatEngine().getMapHeight() / 3)/2);
                                                if (wingLeader != null) {
                                                    //loc = bay.getLandingLocation(wingLeader);   //位置
                                                    loc = new Vector2f(locx, locy);
                                                } else {
                                                    loc = new Vector2f(locx, locy);   //位置
                                                }
                                                float facing = 90f;   //朝向


                                                PersonAPI captain = Global.getSettings().createPerson();    // LPC默认Captain效果加成 理论上不会起作用
                                                captain.setPersonality(Personalities.RECKLESS);
                                                captain.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
                                                captain.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);

                                                if (wingID != null) {    // 生成领队
                                                    leader = engine.getFleetManager(bay.getShip().getOriginalOwner()).spawnShipOrWing(wingID2, loc, facing, 1f, captain);
                                                } else {
                                                    //leader = null;
                                                    continue;
                                                }

                                                //Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---wingID[bay.getWing().getSpec().getId()]:" + wingID);
                                                //===========================替换遍历代码========================================
                                                for (ShipAPI wingMember : leader.getWing().getWingMembers()) {
                                                    wingMember.getLocation().set(loc);
                                                }

                                            }
                                        }


                                    }
                                }
                            }

                            putDataInEngine(engine, this.buffID, cooldownPool);
                        }
                    }
                }
            }
        }
    }

    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
    }
}
