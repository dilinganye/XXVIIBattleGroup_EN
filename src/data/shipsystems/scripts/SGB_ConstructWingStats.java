package data.shipsystems.scripts;
// Thanks to cjy4312 for debug
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

import static data.shipsystems.scripts.SGB_AsAboveSoBelow.getRandomBlendColor;

public class SGB_ConstructWingStats extends BaseShipSystemScript {

    public static String RD_NO_EXTRA_CRAFT = "rd_no_extra_craft";
    public static String RD_FORCE_EXTRA_CRAFT = "rd_force_extra_craft";

    //	public static float RATE_COST = 0.25f;
//	public static float RATE_COST_1_BAY = 0.15f;
    public static float EXTRA_FIGHTER_DURATION = 30;
    public static float RATE_COST = 0f;
    public static float RATE_COST_1_BAY = 0f;

    public static float getRateCost(int bays) {
        if (bays <= 1) return RATE_COST_1_BAY;
        return RATE_COST;
    }

    private Set<FighterWingAPI> wings = new HashSet<>();
    private Set<FighterWingAPI> bombers = new HashSet<>();

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) { //这种不符合条件就不允许执行的代码要放在最前面
            return;
        }
        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();

        //  String Special_id = "SGB_ConstructWingStats" + ship.getId();//不推荐直接形式的使用shipid，天知道会不会有哪个mod把这个id拿去干别的用，所以要加独特的前缀/后缀  //ship + "_" + ListOfShip.indexOf(ship);
        //  String Special_id_bomber = "SGB_ConstructWingStatsBomber" + ship.getId();


        if (effectLevel == 1) {
            //====================轰炸机自爆==========================
            if (!bombers.isEmpty()) {
                for (FighterWingAPI wing : bombers) {
                    for (ShipAPI bomber : wing.getWingMembers()) {
                        if (bomber != null && bomber.isAlive()) {
                            bomber.setHitpoints(0); //击杀轰炸机
                            engine.applyDamage(bomber, bomber.getLocation(), bomber.getHullSpec().getArmorRating() * 2, DamageType.HIGH_EXPLOSIVE, 0, true, true, null, false);

                            Vector2f killBomberLoc = new Vector2f(bomber.getLocation());
                            float killBomberHull = 100f;
                            DamagingExplosionSpec killBomberExplosion = new DamagingExplosionSpec(    // 设置自毁爆炸
                                    0.1f, // duration
                                    50f, // radius
                                    20f, // coreRadius
                                    killBomberHull, // maxDamage
                                    killBomberHull / 2f, // minDamage
                                    CollisionClass.PROJECTILE_FF, // collisionClass
                                    CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                                    2f, // particleSizeMin
                                    1.75f, // particleSizeRange
                                    0.5f, // particleDuration
                                    150, // particleCount
                                    new Color(255, 255, 255, 255), // particleColor
                                    new Color(193, 255, 100, 175)  // explosionColor
                            );
                            killBomberExplosion.setDamageType(DamageType.FRAGMENTATION);
                            killBomberExplosion.setUseDetailedExplosion(true);
                            killBomberExplosion.setSoundSetId("explosion_guardian");

                            engine.spawnDamagingExplosion(killBomberExplosion, bomber, killBomberLoc);    // 爆炸模拟轰炸机归西
                            engine.spawnExplosion(killBomberLoc, bomber.getVelocity(),
                                    new Color(206, 197, 167, 255),
                                    45f,
                                    5f);
                        }
                    }
                }
                bombers.clear();
            }

            //==================废弃轰炸机自爆======================
            if (1 > 2) { //用不可能达成的条件包裹
                List<ShipAPI> ListOfShip = Global.getCombatEngine().getShips(); //非必要尽可能不要去每帧遍历全场的飞机
                for (ShipAPI ConstructWings : ListOfShip) {    // 清理不必要的轰炸机留存
                    String Special_id_bomber = "SomeWillingToFindIt";
                    if (ConstructWings.hasTag(Special_id_bomber) && ConstructWings.isFighter() && ConstructWings.isAlive()) {
                        ShipAPI thisKillTarget = ConstructWings;
                        Vector2f killBomberLoc = new Vector2f(thisKillTarget.getLocation());
                        Float killBomberHull = thisKillTarget.getHitpoints() + 75f;

                        DamagingExplosionSpec killBomberExplosion = new DamagingExplosionSpec(    // 设置自毁爆炸
                                0.1f, // duration
                                50f, // radius
                                20f, // coreRadius
                                killBomberHull, // maxDamage
                                killBomberHull / 2f, // minDamage
                                CollisionClass.PROJECTILE_FF, // collisionClass
                                CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                                3f, // particleSizeMin
                                3f, // particleSizeRange
                                0.5f, // particleDuration
                                150, // particleCount
                                new Color(255, 255, 255, 255), // particleColor
                                new Color(193, 255, 100, 175)  // explosionColor
                        );

                        killBomberExplosion.setDamageType(DamageType.FRAGMENTATION);
                        killBomberExplosion.setUseDetailedExplosion(false);
                        killBomberExplosion.setSoundSetId("explosion_guardian");

                        engine.spawnDamagingExplosion(killBomberExplosion, thisKillTarget, killBomberLoc);    // 自毁爆炸,模拟轰炸机归西
                        engine.spawnExplosion(killBomberLoc, thisKillTarget.getVelocity(),
                                new Color(206, 197, 167, 255),
                                45f,
                                5f);

                        thisKillTarget.setHitpoints(0);    // 清除此轰炸机
                    }
                }
            }
            //========================================
            //need to make this more balanced
            //possibly don't count the "added" fighters to helping restore the replacement rate?
            //also: need to adjust the AI to be more conservative using this


            float minRate = Global.getSettings().getFloat("minFighterReplacementRate");

            int bays = ship.getLaunchBaysCopy().size();
            float cost = getRateCost(bays);
            for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {    //逐个甲板进行操作
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
                    if (wingLeader != null) {
                        loc = bay.getLandingLocation(wingLeader);
                    } else {
                        loc = ship.getLocation();
                    }
                    float facing = ship.getFacing();
				/*
					Global.getLogger(this.getClass()).info("SGB_WHAT WRONG WITH THIS SYSTEM");
					Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---wingID[bay.getWing().getSpec().getId()]:" + wingID);
					Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---wingID2[bay.getWing().getWingId()]:" + wingID2);
					Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---wingLeader[bay.getWing().getLeader()]:" + wingLeader);
					Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---LeaderID[bay.getWing().getLeader().getVariant()]:" + LeaderID);
					*/

                    PersonAPI captain = Global.getSettings().createPerson();    // LPC默认Captain效果加成 理论上不会起作用
                    captain.setPersonality(Personalities.RECKLESS);
                    captain.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
                    captain.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);

                    if (wingID != null) {    // 生成领队
                        leader = engine.getFleetManager(bay.getShip().getOriginalOwner()).spawnShipOrWing(wingID2, loc, facing, 1f, captain);
                        if (leader.getWing().getSpec().isBomber()) {//将生成的战机联队保存至Set
                            bombers.add(leader.getWing());
                        } else {
                            wings.add(leader.getWing());
                        }
                    } else {
                        //leader = null;
                        continue;
                    }

                    //Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---wingID[bay.getWing().getSpec().getId()]:" + wingID);
                    //===========================替换遍历代码========================================
                    for (ShipAPI wingMember : leader.getWing().getWingMembers()) {
                        Color colorWing = leader.getSpriteAPI().getAverageBrightColor();
                        Color colorShake = getRandomBlendColor(new Color(255, 245, 204, 255),
                                colorWing);
                        Color colorShakeOut = getRandomBlendColor(new Color(44, 100, 17, 255),
                                new Color(colorWing.getRed(), colorWing.getGreen(), colorWing.getBlue(), 100));
                        wingMember.getLocation().set(loc);
                        //根本不需要什么bomber的tag
                        //      if (!bay.getWing().getSpec().isBomber()) {
                        //            wingMember.addTag(Special_id);
                        //       } else {
                        //	if(isBomber){
                        //         wingMember.addTag(Special_id_bomber);
                        //     }
                        engine.spawnEmpArcVisual(loc, wingMember, MathUtils.getRandomPointInCircle(loc,
                                        wingMember.getCollisionRadius() * MathUtils.getRandomNumberInRange(1.2f, 2f)),
                                ship,
                                20f * MathUtils.getRandomNumberInRange(0.3f, 1.05f), // thickness
                                colorShakeOut,
                                colorShake
                        );
                    }
                    //===================================================================
                    if (1 > 2) { // 老旧代码，不会执行
                        //	if (leader != null) {//leader不可能为null	//生成领队后 对甲板进行索引并逐个生成其舰载机编队成员
                        ShipAPI[] ships = new ShipAPI[spec.getNumFighters()];
                        if (ships != null) {
                            Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---ships != null]:" + ships);
                            for (int i = 0; i < addForWing; i++) {
                                if (leader.getWing() != null) {
                                    Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---leader.getWing() != null]:" + leader.getWing());
                                    Color colorWing = leader.getSpriteAPI().getAverageBrightColor();
                                    Color colorShake = getRandomBlendColor(new Color(255, 245, 204, 255),
                                            colorWing);
                                    Color colorShakeOut = getRandomBlendColor(new Color(44, 100, 17, 255),
                                            new Color(colorWing.getRed(), colorWing.getGreen(), colorWing.getBlue(), 100));
                                    if (leader.getWing().getWingMembers() != null) {
                                        ships[i] = leader.getWing().getWingMembers().get(i);
                                        ships[i].getLocation().set(loc);

                                        boolean isBomber = bay.getWing().getSpec().isBomber();
//										boolean isBomber = false;	//侦测是否为轰炸机 用以隔离
//										if(bay.getWing().getSpec().isBomber()){
//											isBomber = true;
//										}
                                        // 标记重补法 并对轰炸机进行Tag隔离
                                        if (!isBomber) {
                                            ships[i].addTag("xxx");
                                        } else {
                                            //	if(isBomber){
                                            ships[i].addTag("xxxbomber");
                                        }
                                        Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---ships[i]]:" + ships[i] + " isBomber " + isBomber);
                                        Global.getLogger(this.getClass()).info("SGB_CONWING_Get to DO the wingBug ---ships[i]tag]:" + ships[i].getTags());
                                        engine.spawnEmpArcVisual(loc, ships[i], MathUtils.getRandomPointInCircle(loc,
                                                        ships[i].getCollisionRadius() * MathUtils.getRandomNumberInRange(1.2f, 2f)),
                                                ship,
                                                20f * MathUtils.getRandomNumberInRange(0.3f, 1.05f), // thickness
                                                colorShakeOut,
                                                colorShake
                                        );

                                    } else {    // 未成功的随性特效 以防特殊情况 大多数情况下不会运行
                                        engine.spawnEmpArcVisual(loc, ship, MathUtils.getRandomPointInCircle(loc, 70f), ship,
                                                40f * MathUtils.getRandomNumberInRange(0.5f, 1.5f), // thickness
                                                new Color(57, 133, 19, 255),
                                                new Color(204, 255, 211, 255)
                                        );
                                    }
                                } else {
                                    engine.spawnEmpArcVisual(loc, ship, MathUtils.getRandomPointInCircle(loc, 70f), ship,
                                            40f * MathUtils.getRandomNumberInRange(0.5f, 1.5f), // thickness
                                            new Color(133, 108, 19, 255),
                                            new Color(255, 245, 204, 255)
                                    );
                                }
                            }
                        }
                    }


                }
                //collisionClass = ships[0].getCollisionClass();
            }
				/*
				if (actualAdd > 0) {
					bay.setFastReplacements(bay.getFastReplacements() + addForWing);
					bay.setExtraDeployments(actualAdd);
					bay.setExtraDeploymentLimit(maxTotal);
					bay.setExtraDuration(EXTRA_FIGHTER_DURATION);
				}*/
        } else if (effectLevel > 0 && effectLevel < 1) {    //	系统开启时的预热可视化
            for (FighterLaunchBayAPI bay_Dec : ship.getLaunchBaysCopy()) {
                if (bay_Dec.getWing() == null || bay_Dec.getWing().getLeader() == null) continue;
                ShipAPI wingLeader_dec = bay_Dec.getWing().getLeader();
                Vector2f bay_Dec_Loc = bay_Dec.getLandingLocation(wingLeader_dec);
                bay_Dec_Loc = MathUtils.getRandomPointInCircle(bay_Dec_Loc, 20f);

                Color colorWing = wingLeader_dec.getSpriteAPI().getAverageBrightColor();
                Color colorShake = getRandomBlendColor(new Color(255, 245, 204, 255),
                        colorWing);

                engine.addSmoothParticle(
                        bay_Dec_Loc,
                        ship.getVelocity(),
                        40 * effectLevel,
                        2f * Math.max(effectLevel, 0.5f),
                        0.1f,
                        colorShake);


            }

        }
    }


    public static int getAdditionalFor(FighterWingSpecAPI spec, int bays) {
        //if (spec.isBomber() && !spec.hasTag(RD_FORCE_EXTRA_CRAFT)) return 0;
        if (spec.hasTag(RD_NO_EXTRA_CRAFT)) return 0;

        int size = spec.getNumFighters();
        if (true) return size;

        if (bays == 1) {
            return Math.max(size, 2);
        }

        if (size <= 3) return 2;
        if (size <= 5) return 3;
        return 4;
    }


    public void unapply(MutableShipStatsAPI stats, String id) {
    }


    public StatusData getStatusData(int index, State state, float effectLevel) {
//		if (index == 0) {
//			return new StatusData("deploying additional fighters", false);
//		}
        return null;
    }


    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        if (!wings.isEmpty()) {
            Iterator<FighterWingAPI> iter = wings.iterator();
            while (iter.hasNext()){
                FighterWingAPI wing = iter.next();
                boolean hasMember = false;
                for(ShipAPI w :wing.getWingMembers()){
                    if(w!=null && w.isAlive()){
                        hasMember=true;
                        break;
                    }
                }
                if(!hasMember){
                    iter.remove();
                }
            }
        }
        return wings.isEmpty();
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) return null;
        if (system.getState() != ShipSystemAPI.SystemState.IDLE) return null;
        if(!wings.isEmpty()){
            return "-==<o>==-";
        }

//        boolean canUsable = true;
//        if (Global.getCombatEngine() != null && !Global.getCombatEngine().isPaused()) {
//            List<ShipAPI> ListOfShip = Global.getCombatEngine().getShips();
//            for (ShipAPI ConstructWings : ListOfShip) {
//                String id = ship + "_" + ListOfShip.indexOf(ship);
//                if (ConstructWings.hasTag(id) && ConstructWings.isFighter() && ConstructWings.isAlive()) {
//                    return "-==<o>==-";
//                }
//            }
//        }
        return null;
    }

}








