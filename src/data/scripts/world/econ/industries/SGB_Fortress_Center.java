package data.scripts.world.econ.industries;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;

import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_Fortress_Center extends BaseIndustry implements RouteManager.RouteFleetSpawner, FleetEventListener {

    protected IntervalUtil tracker = new IntervalUtil(Global.getSettings().getFloat("averagePatrolSpawnInterval") * 0.7f,
            Global.getSettings().getFloat("averagePatrolSpawnInterval") * 1.3f);

    protected float returningPatrolValue = 0f;

    @Override
    public boolean isHidden() {
        return !market.getFactionId().equals("SGB");
    }
    @Override
    public boolean isFunctional() {
        return super.isFunctional() && market.getFactionId().equals("SGB");
    }

    //com.fs.starfarer.api.impl.campaign.econ.impl.LionsGuardHQ
    public static float DEFENSE_BONUS_BASE = 1f;
    public static float DEFENSE_BONUS_NUMBER_EVERY_LEVEL = 1f;

    public static float IMPROVE_DEFENSE_BONUS = 0.25f;


    public void apply() {
        super.apply(true);

        int size = market.getSize();
        boolean hb = Industries.HEAVYBATTERIES.equals(getId());

        demand(Commodities.SUPPLIES, 2);
        demand(Commodities.HAND_WEAPONS, Math.min(size - 1,2));

        supply(Commodities.MARINES, Math.max(size - 3,1));

//		Pair<String, Integer> deficit = getMaxDeficit(Commodities.HAND_WEAPONS);
//		applyDeficitToProduction(1, deficit, Commodities.MARINES);

        modifyStabilityWithBaseMod();

        float mult = getDeficitMult(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
        String extra = "";
        if (mult != 1) {
            String com = getMaxDeficit(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS).one;
            extra = " (" + getDeficitText(com).toLowerCase() + ")";
        }
        float DEFENSE_BONUS_REAL = DEFENSE_BONUS_NUMBER_EVERY_LEVEL * Math.max(size - 3,1);
        float bonus = hb ? DEFENSE_BONUS_REAL : DEFENSE_BONUS_BASE;
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                .modifyMult(getModId(), DEFENSE_BONUS_REAL, getNameForModifier() + extra);
//        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
//                .modifyMult(getModId(), 1f + bonus * mult, getNameForModifier() + extra);


        if (!isFunctional()) {
            supply.clear();
            unapply();
        }

    }

    @Override
    public void unapply() {
        super.unapply();

        MemoryAPI memory = market.getMemoryWithoutUpdate();

        unmodifyStabilityWithBaseMod();

        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId());
        //market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyPercent(getModId());
        //market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyFlat(getModId());
    }

    @Override
    public String getCurrentImage() {
        boolean batteries = Industries.HEAVYBATTERIES.equals(getId());
        if (batteries) {
            PlanetAPI planet = market.getPlanetEntity();
            if (planet == null || planet.isGasGiant()) {
                return Global.getSettings().getSpriteName("industry", "SGB_CenterHub_Mix");
            }
        }
        return super.getCurrentImage();
    }

    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }

    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }


    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        //return mode == IndustryTooltipMode.NORMAL && isFunctional();
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);
            int size = market.getSize();

            boolean hb = Industries.HEAVYBATTERIES.equals(getId());
            float DEFENSE_BONUS_REAL = DEFENSE_BONUS_NUMBER_EVERY_LEVEL * Math.max(size - 3,1);
            float bonus = hb ? DEFENSE_BONUS_REAL : DEFENSE_BONUS_BASE;
            addGroundDefensesImpactSection(tooltip, DEFENSE_BONUS_REAL, Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
        }
    }

    @Override
    protected int getBaseStabilityMod() {
        return 1;
    }

    @Override
    protected Pair<String, Integer> getStabilityAffectingDeficit() {
        return getMaxDeficit(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
    }

    @Override
    public boolean canImprove() {
        return true;
    }

    protected void applyImproveModifiers() {
        if (isImproved()) {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("ground_defenses_improve",
                    1f + IMPROVE_DEFENSE_BONUS,
                    getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
        } else {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult("ground_defenses_improve");
        }
    }

    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        float a = IMPROVE_DEFENSE_BONUS;
        String str = Strings.X + (1f + a) + "";

        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Ground defenses increased by %s.", 0f, highlight, str);
        } else {
            info.addPara("Increases ground defenses by %s.", 0f, highlight, str);
        }

        info.addSpacer(opad);
        super.addImproveDesc(info, mode);
    }

    @Override
    public RaidDangerLevel adjustCommodityDangerLevel(String commodityId, RaidDangerLevel level) {
        return level.next();
    }

    @Override
    public RaidDangerLevel adjustItemDangerLevel(String itemId, String data, RaidDangerLevel level) {
        return level.next();
    }

    //————————————————————————————

    @Override
    public void advance(float amount) {
        super.advance(amount);

        if (Global.getSector().getEconomy().isSimMode()) return;

        if (!isFunctional()) return;

        float days = Global.getSector().getClock().convertToDays(amount);

        float spawnRate = 1f;
        float rateMult = market.getStats().getDynamic().getStat(Stats.COMBAT_FLEET_SPAWN_RATE_MULT).getModifiedValue();
        spawnRate *= rateMult;


        float extraTime = 0f;
        if (returningPatrolValue > 0) {
            // apply "returned patrols" to spawn rate, at a maximum rate of 1 interval per day
            float interval = tracker.getIntervalDuration();
            extraTime = interval * days;
            returningPatrolValue -= days;
            if (returningPatrolValue < 0) returningPatrolValue = 0;
        }
        tracker.advance(days * spawnRate + extraTime);

        //tracker.advance(days * spawnRate * 100f);

        if (tracker.intervalElapsed()) {
            String sid = getRouteSourceId();

            int light = getCount(FleetFactory.PatrolType.FAST);
            int medium = getCount(FleetFactory.PatrolType.COMBAT);
            int heavy = getCount(FleetFactory.PatrolType.HEAVY);

            int maxLight = 3;
            int maxMedium = 2;
            int maxHeavy = 1;

            WeightedRandomPicker<FleetFactory.PatrolType> picker = new WeightedRandomPicker<FleetFactory.PatrolType>();
            picker.add(FleetFactory.PatrolType.HEAVY, maxHeavy - heavy);
            picker.add(FleetFactory.PatrolType.COMBAT, maxMedium - medium);
            picker.add(FleetFactory.PatrolType.FAST, maxLight - light);

            if (picker.isEmpty()) return;

            FleetFactory.PatrolType type = picker.pick();
            MilitaryBase.PatrolFleetData custom = new MilitaryBase.PatrolFleetData(type);

            RouteManager.OptionalFleetData extra = new RouteManager.OptionalFleetData(market);
            extra.fleetType = type.getFleetType();

            RouteManager.RouteData route = RouteManager.getInstance().addRoute(sid, market, Misc.genRandomSeed(), extra, this, custom);
            float patrolDays = 35f + (float) Math.random() * 10f;

            route.addSegment(new RouteManager.RouteSegment(patrolDays, market.getPrimaryEntity()));
        }
    }

    public void reportAboutToBeDespawnedByRouteManager(RouteManager.RouteData route) {
    }

    public boolean shouldRepeat(RouteManager.RouteData route) {
        return false;
    }

    public int getCount(FleetFactory.PatrolType... types) {
        int count = 0;
        for (RouteManager.RouteData data : RouteManager.getInstance().getRoutesForSource(getRouteSourceId())) {
            if (data.getCustom() instanceof MilitaryBase.PatrolFleetData) {
                MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) data.getCustom();
                for (FleetFactory.PatrolType type : types) {
                    if (type == custom.type) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    public int getMaxPatrols(FleetFactory.PatrolType type) {
        if (type == FleetFactory.PatrolType.FAST) {
            return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).computeEffective(0);
        }
        if (type == FleetFactory.PatrolType.COMBAT) {
            return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).computeEffective(0);
        }
        if (type == FleetFactory.PatrolType.HEAVY) {
            return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).computeEffective(0);
        }
        return 0;
    }

    public boolean shouldCancelRouteAfterDelayCheck(RouteManager.RouteData route) {
        return false;
    }

    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if (!isFunctional()) return;

        if (reason == CampaignEventListener.FleetDespawnReason.REACHED_DESTINATION) {
            RouteManager.RouteData route = RouteManager.getInstance().getRoute(getRouteSourceId(), fleet);
            if (route.getCustom() instanceof MilitaryBase.PatrolFleetData) {
                MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) route.getCustom();
                if (custom.spawnFP > 0) {
                    float fraction  = fleet.getFleetPoints() / custom.spawnFP;
                    returningPatrolValue += fraction;
                }
            }
        }
    }

    public CampaignFleetAPI spawnFleet(RouteManager.RouteData route) {

        MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) route.getCustom();
        FleetFactory.PatrolType type = custom.type;

        Random random = route.getRandom();

        float combat = 0f;
        float tanker = 0f;
        float freighter = 0f;
        String fleetType = type.getFleetType();
        switch (type) {
            case FAST:
                combat = Math.round(5f + (float) random.nextFloat() * 2f) * 5f;
                break;
            case COMBAT:
                combat = Math.round(10f + (float) random.nextFloat() * 8f) * 5f;
                tanker = Math.round((float) random.nextFloat()) * 2f;
                break;
            case HEAVY:
                combat = Math.round(30f + (float) random.nextFloat() * 20f) * 5f;
                tanker = Math.round((float) random.nextFloat()) * 10f;
                freighter = Math.round((float) random.nextFloat()) * 10f;
                break;
        }

        FleetParamsV3 params = new FleetParamsV3(
                market,
                null, // loc in hyper; don't need if have market
                "SGB_SF",
                route.getQualityOverride(), // quality override
                fleetType,
                combat, // combatPts
                freighter, // freighterPts
                tanker, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                2f // qualityMod
        );
        params.timestamp = route.getTimestamp();
        params.random = random;
        params.modeOverride = FactionAPI.ShipPickMode.PRIORITY_THEN_ALL;
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if (fleet == null || fleet.isEmpty()) return null;

        fleet.setFaction(market.getFactionId(), true);
        fleet.setNoFactionInName(true);

        fleet.addEventListener(this);

//		PatrolAssignmentAIV2 ai = new PatrolAssignmentAIV2(fleet, custom);
//		fleet.addScript(ai);

        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, true, 0.3f);

        if (type == FleetFactory.PatrolType.FAST || type == FleetFactory.PatrolType.COMBAT) {
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_CUSTOMS_INSPECTOR, true);
        }

        String postId = Ranks.POST_FLEET_COMMANDER;
        String rankId = Ranks.SPACE_CAPTAIN;
        String fleetName_F = txt("Econ_Industries_Fortess_No.");
        String fleetName_B = txt("Econ_Industries_Fortess_Garrison_Fleet");
        switch (type) {
            case FAST:
                rankId = Ranks.SPACE_LIEUTENANT;
                fleetName_F = txt("Econ_Industries_Fortess_FreeScout_Formation_No.");
                fleetName_B = txt("Econ_Industries_Fortess_FreeScout_Empty");
                break;
            case COMBAT:
                rankId = Ranks.SPACE_COMMANDER;
                fleetName_F = txt("Econ_Industries_Fortess_ReamerHunting_Group_No.");
                fleetName_B = txt("Econ_Industries_Fortess_ReamerHunting_Squadron");
                break;
            case HEAVY:
                rankId = Ranks.SPACE_CAPTAIN;
                fleetName_F = txt("Econ_Industries_Fortess_CorundumReaction_No.");
                fleetName_B = txt("Econ_Industries_Fortess_Garrison_Fleet");
                break;
        }
        fleet.setName(fleetName_F
                + MathUtils.getRandomNumberInRange(0,9)
                + MathUtils.getRandomNumberInRange(0,9)
                + fleetName_B
        );

        fleet.getCommander().setPostId(postId);
        fleet.getCommander().setRankId(rankId);

        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            if (member.isCapital()) {
                member.setVariant(member.getVariant().clone(), false, false);
                member.getVariant().setSource(VariantSource.REFIT);
                member.getVariant().addTag(Tags.TAG_NO_AUTOFIT);
                member.getVariant().addTag(Tags.VARIANT_CONSISTENT_WEAPON_DROPS);
            }
        }

        market.getContainingLocation().addEntity(fleet);
        fleet.setFacing((float) Math.random() * 360f);
        // this will get overridden by the patrol assignment AI, depending on route-time elapsed etc
        fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);

        fleet.addScript(new PatrolAssignmentAIV4(fleet, route));

        //market.getContainingLocation().addEntity(fleet);
        //fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);

        if (custom.spawnFP <= 0) {
            custom.spawnFP = fleet.getFleetPoints();
        }

        return fleet;
    }

    public String getRouteSourceId() {
        return getMarket().getId() + "_" + "market_guard";
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    public boolean showWhenUnavailable() {
        return false;
    }

}




