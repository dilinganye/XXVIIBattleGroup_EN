package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.CoreLifecyclePluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import data.scripts.world.systems.SGB_Detachment_outpost;

import static data.utils.sgb.SGB_stringsManager.txt;


public class SGBModGen {

    public void generate(SectorAPI sector) {

        FactionAPI SGB = sector.getFaction("SGB");
        // 设置势力好感度
        SGB.setRelationship(Factions.LUDDIC_CHURCH, 0f);
        SGB.setRelationship(Factions.LUDDIC_PATH, -0.6f);
        SGB.setRelationship(Factions.TRITACHYON, -0.85f);
        SGB.setRelationship(Factions.PERSEAN, 0.3f);
        SGB.setRelationship(Factions.PIRATES, -1.0f);
        SGB.setRelationship(Factions.INDEPENDENT, 0.1f);
        SGB.setRelationship(Factions.LIONS_GUARD, 0.0f);
        SGB.setRelationship(Factions.HEGEMONY, -0.85f);
        SGB.setRelationship(Factions.DIKTAT, 0.1f);
        SGB.setRelationship(Factions.REMNANTS, -1.0f);
        SGB.setRelationship(Factions.OMEGA, -1.0f);
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("SGB");

        //For Bound and some Missions
        FactionAPI SGB_SF = sector.getFaction("SGB_SF");
        SGB.setRelationship("SGB_SF", -0.6f);

        SGB_SF.setRelationship(Factions.TRITACHYON, -0.75f);
        SGB_SF.setRelationship(Factions.HEGEMONY, -0.75f);

        SGB_SF.setRelationship(Factions.LIONS_GUARD, 0.0f);
        SGB_SF.setRelationship(Factions.PIRATES, 0.8f);
        SGB_SF.setRelationship(Factions.REMNANTS, -0.40f);
        SGB_SF.setRelationship(Factions.LUDDIC_CHURCH, 0f);
        SGB_SF.setRelationship(Factions.LUDDIC_PATH, 0f);

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("SGB_SF");

        FactionAPI SGB_Omega_Watcher = sector.getFaction("SGB_Omega_Watcher");
        SGB_Omega_Watcher.setRelationship(Factions.REMNANTS, -0.10f);
        SGB_Omega_Watcher.setRelationship(Factions.PIRATES, -0.10f);
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("SGB_Omega_Watcher");

        this.generateSGBInTia(sector);
        this.generateSGBInMagec(sector);
        new SGB_Detachment_outpost().generate(sector);
    }

    private void generateSGBInTia(SectorAPI sector) {
        StarSystemAPI BLG_Tia_system = sector.getStarSystem("Tia");
        //StarSystemAPI BLG_Aosnit = sector.getStarSystem("Aosnit Fort");
        if (BLG_Tia_system != null) {
            this.spawnAouintes(BLG_Tia_system);
        }
    }

    private void generateSGBInMagec(SectorAPI sector) {
        StarSystemAPI BLG_Tia_system = sector.getStarSystem("Magec");
        StarSystemAPI BLG_Aztlan_system = sector.getStarSystem("Aztlan");
        if (BLG_Tia_system != null) {
            this.spawnMagec(BLG_Tia_system);
            this.spawnAztlan(BLG_Aztlan_system);
        }
    }
    private void spawnAztlan(StarSystemAPI system) {
        PlanetAPI star = system.getStar();

        //设置战役后果
        if (Global.getSettings().getMissionScore("SGB_CleavedSky") >= 25) {
            for (PlanetAPI AllPlanetsForAztlan : system.getPlanets()) {
                if(AllPlanetsForAztlan.getId().equals("chicomoztoc")){
                    PlanetAPI chicomoztoc = AllPlanetsForAztlan;

                    Math.random();
                    TTBlackSite.addDerelict(system, chicomoztoc, "SGB_Ductility_2_Attack", txt("WorldSpawn_DamagedShips"), "SGB_Ductility_2", ShipRecoverySpecial.ShipCondition.BATTERED, chicomoztoc.getRadius() * 3.25F, true);
                    TTBlackSite.addDerelict(system, chicomoztoc, "SGB_Ductility_2_Attack", txt("WorldSpawn_DamagedShips"), "SGB_Ductility_2", ShipRecoverySpecial.ShipCondition.WRECKED, chicomoztoc.getRadius() * 3.25F, false);
                    TTBlackSite.addDerelict(system, chicomoztoc, "SGB_Ductility_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Ductility", ShipRecoverySpecial.ShipCondition.BATTERED, chicomoztoc.getRadius() * 2.5F, Math.random() < 0.8D);
                    TTBlackSite.addDerelict(system, chicomoztoc, "SGB_Anvil_Attack", txt("WorldSpawn_DamagedShips"), "SGB_Anvil", ShipRecoverySpecial.ShipCondition.WRECKED, chicomoztoc.getRadius() * 4F, Math.random() < 0.8D);
                }
            }TTBlackSite.addDerelict(system, star, "SGB_Aligate_DogFight", txt("WorldSpawn_DamagedShips"), "SGB_Aligate", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 2.5F, Math.random() < 0.75D);
            TTBlackSite.addDerelict(system, star, "falcon_xiv_Escort", txt("WorldSpawn_DamagedShips"), "falcon_xiv", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 4.5F, Math.random() < 0.5D);
            TTBlackSite.addDerelict(system, star, "falcon_xiv_Escort", txt("WorldSpawn_DamagedShips"), "falcon_xiv", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 2.5F, Math.random() < 0.5D);
            TTBlackSite.addDerelict(system, star, "legion_xiv_Elite", txt("WorldSpawn_DamagedShips"), "legion_xiv", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 4.5F, Math.random() < 0.8D);
        }
        else{
            TTBlackSite.addDerelict(system, star, "SGB_Ductility_2_Attack", txt("WorldSpawn_DamagedShips"), "SGB_Ductility_2", ShipRecoverySpecial.ShipCondition.WRECKED, star.getRadius() * 3.25F, false);
            TTBlackSite.addDerelict(system, star, "SGB_Ductility_2_Attack", txt("WorldSpawn_DamagedShips"), "SGB_Ductility_2", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 3.25F, false);
            TTBlackSite.addDerelict(system, star, "vanguard_Attack", txt("WorldSpawn_DamagedShips"), "vanguard", ShipRecoverySpecial.ShipCondition.WRECKED, star.getRadius() * 4.5F, Math.random() < 0.2D);
            TTBlackSite.addDerelict(system, star, "vanguard_Attack", txt("WorldSpawn_DamagedShips"), "vanguard", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 2.5F, Math.random() < 0.2D);
        }
    }
    private void spawnMagec(StarSystemAPI system) {
        PlanetAPI star = system.getStar();

        //设置战役后果
        if (Global.getSettings().getMissionScore("SGB_Retaliate") >= 50) {
            SectorEntityToken Ampoist_station = system.addCustomEntity("Ampoist_station",
                "Ampoist 前哨",
                "station_side04",
                "SGB");

            Ampoist_station.setCircularOrbitPointingDown(system.getEntityById("magec"), 47, 3150, 140);
            Ampoist_station.setInteractionImage("illustrations", "hound_hangar");

            MarketAPI Ampoist_station_Market = Global.getFactory().createMarket("Ampoist_station_Market", "Ampoist", 4);
            Ampoist_station_Market.setFactionId("SGB");
            Ampoist_station_Market.setPrimaryEntity(Ampoist_station);
            Ampoist_station_Market.getTariff().modifyFlat("generator", 0.45f);

            Ampoist_station.setCustomDescriptionId("SGB_Ampoist_station_Capt");
            Ampoist_station.setMarket(Ampoist_station_Market);
            EconomyAPI globalEconomy = Global.getSector().getEconomy();
            globalEconomy.addMarket(Ampoist_station_Market, true);
            Ampoist_station.getMarket().addCondition(Conditions.POPULATION_4);
            Ampoist_station.getMarket().addCondition(Conditions.INDUSTRIAL_POLITY);

            Ampoist_station.getMarket().addSubmarket(Submarkets.SUBMARKET_OPEN);
            Ampoist_station.getMarket().addSubmarket(Submarkets.GENERIC_MILITARY);
            Ampoist_station.getMarket().addSubmarket(Submarkets.SUBMARKET_STORAGE);

            Ampoist_station.getMarket().addIndustry(Industries.POPULATION);
            Ampoist_station.getMarket().addIndustry(Industries.SPACEPORT);
            Ampoist_station.getMarket().addIndustry(Industries.ORBITALWORKS);
            Ampoist_station.getMarket().addIndustry(Industries.HEAVYBATTERIES);
            Ampoist_station.getMarket().addIndustry(Industries.MILITARYBASE);

            Math.random();
            TTBlackSite.addDerelict(system, Ampoist_station, "SGB_Ductility_2_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Ductility_2", ShipRecoverySpecial.ShipCondition.BATTERED, Ampoist_station.getRadius() * 3.25F, true);
            TTBlackSite.addDerelict(system, star, "SGB_Ogent_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Ogent", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 4.5F, Math.random() < 0.8D);
            TTBlackSite.addDerelict(system, Ampoist_station, "SGB_Ascaedy_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Ascaedy", ShipRecoverySpecial.ShipCondition.BATTERED, Ampoist_station.getRadius() * 2.5F, Math.random() < 0.8D);
            TTBlackSite.addDerelict(system, Ampoist_station, "SGB_Wiesios_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Wiesios", ShipRecoverySpecial.ShipCondition.BATTERED, Ampoist_station.getRadius() * 4F, Math.random() < 0.8D);
            TTBlackSite.addDerelict(system, star, "aurora_Balanced", txt("WorldSpawn_DamagedShips"), "aurora", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 2.5F, Math.random() < 0.75D);
            TTBlackSite.addDerelict(system, star, "hyperion_Attack", txt("WorldSpawn_DamagedShips"), "hyperion", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 4.5F, Math.random() < 0.5D);
            TTBlackSite.addDerelict(system, star, "tempest_Attack", txt("WorldSpawn_DamagedShips"), "tempest", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 2.5F, Math.random() < 0.5D);



        }
        else{
            SectorEntityToken Ampoist_station = system.addCustomEntity("Ampoist_station",
                    "Ampoist 研究基地",
                    "station_side04",
                    "tritachyon");

            Ampoist_station.setCircularOrbitPointingDown(system.getEntityById("magec"), 47, 3150, 140);
            Ampoist_station.setInteractionImage("illustrations", "hound_hangar");

            MarketAPI Ampoist_station_Market = Global.getFactory().createMarket("Ampoist_station_Market", "Ampoist", 3);
            Ampoist_station_Market.setFactionId("tritachyon");
            Ampoist_station_Market.setPrimaryEntity(Ampoist_station);
            Ampoist_station_Market.getTariff().modifyFlat("generator", 0.75f);

            Ampoist_station.setCustomDescriptionId("SGB_Ampoist_station");
            Ampoist_station.setMarket(Ampoist_station_Market);
            EconomyAPI globalEconomy = Global.getSector().getEconomy();
            globalEconomy.addMarket(Ampoist_station_Market, true);
            Ampoist_station.getMarket().addCondition(Conditions.POPULATION_3);
            Ampoist_station.getMarket().addCondition(Conditions.CLOSED_IMMIGRATION);
            Ampoist_station.getMarket().addCondition(Conditions.FRONTIER);

            Ampoist_station.getMarket().addSubmarket(Submarkets.SUBMARKET_OPEN);
            Ampoist_station.getMarket().addSubmarket(Submarkets.SUBMARKET_BLACK);
            Ampoist_station.getMarket().addSubmarket(Submarkets.SUBMARKET_STORAGE);

            Ampoist_station.getMarket().addIndustry(Industries.POPULATION);
            Ampoist_station.getMarket().addIndustry(Industries.TECHMINING);
            Ampoist_station.getMarket().addIndustry(Industries.SPACEPORT);
            Ampoist_station.getMarket().addIndustry(Industries.WAYSTATION);

            TTBlackSite.addDerelict(system, Ampoist_station, "SGB_Forgnace_Battle_Artillery", txt("WorldSpawn_DamagedShips"), "SGB_Forgnace_Battle", ShipRecoverySpecial.ShipCondition.WRECKED, Ampoist_station.getRadius() * 3.25F, Math.random() < 0.2D);
            TTBlackSite.addDerelict(system, star, "SGB_Ogent_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Ogent", ShipRecoverySpecial.ShipCondition.WRECKED, star.getRadius() * 4.5F, Math.random() < 0.2D);
            TTBlackSite.addDerelict(system, Ampoist_station, "SGB_Ogent_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Ogent", ShipRecoverySpecial.ShipCondition.BATTERED, Ampoist_station.getRadius() * 2.5F, Math.random() < 0.2D);
            TTBlackSite.addDerelict(system, star, "SGB_Wiesios_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Wiesios", ShipRecoverySpecial.ShipCondition.WRECKED, star.getRadius() * 2.5F, Math.random() < 0.3D);
            TTBlackSite.addDerelict(system, star, "tempest_Attack", txt("WorldSpawn_DamagedShips"), "tempest", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 2.5F, Math.random() < 0.2D);
        }
    }
    private void spawnAouintes(StarSystemAPI system) {
        PlanetAPI star = system.getStar();

        PlanetAPI Aouintes = system.addPlanet("aouintes", star, "Aouintes", "lava_minor", 140, 100, 3850, 250);
        Aouintes.getSpec().setPlanetColor(new Color(66, 63, 61,255));
        Aouintes.getSpec().setAtmosphereColor(new Color(130, 111, 100,150));
        Aouintes.getSpec().setCloudColor(new Color(255, 211, 195,200));
        Aouintes.getSpec().setIconColor(new Color(131, 91, 114,255));
        Aouintes.applySpecChanges();

        Aouintes.setCustomDescriptionId("SGB_planet_aouintes");
        Aouintes.getMarket().addCondition(Conditions.HOT);
        Aouintes.getMarket().addCondition(Conditions.IRRADIATED);
        Aouintes.getMarket().addCondition(Conditions.POLLUTION);
        //设置战役后果
        if (Global.getSettings().getMissionScore("SGB_GroupWar") >= 75) {
            Aouintes.getSpec().setPlanetColor(new Color(255, 50, 129,255));
            Aouintes.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            Aouintes.getSpec().setGlowColor(new Color(248, 113, 31,145));
            Aouintes.getSpec().setUseReverseLightForGlow(true);
            Aouintes.getSpec().setAtmosphereThickness(0.5f);
            Aouintes.getMarket().addCondition(Conditions.THIN_ATMOSPHERE);
            Aouintes.getMarket().addCondition(Conditions.TECTONIC_ACTIVITY);
            Aouintes.getMarket().addCondition(Conditions.RUINS_EXTENSIVE);
            Aouintes.getMarket().addCondition(Conditions.ORE_ULTRARICH);
            Aouintes.getMarket().addCondition(Conditions.RARE_ORE_ULTRARICH);

            CoreLifecyclePluginImpl.addRuinsJunk(Aouintes);
            Aouintes.setCustomDescriptionId("SGB_planet_aouintes_Mission_Finish");


            SectorEntityToken Aouintes_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
                    new MagneticFieldTerrainPlugin.MagneticFieldParams(400f, // terrain effect band width
                            620, // terrain effect middle radius
                            Aouintes, // entity that it's around
                            100f, // visual band start
                            500f, // visual band end
                            new Color(100, 84, 30, 30), // base color
                            1f, // probability to spawn aurora sequence, checked once/day when no aurora in progress
                            new Color(110, 92, 20, 130),
                            new Color(150, 90, 90, 150),
                            new Color(200, 110, 100, 190),
                            new Color(250, 130, 110, 240),
                            new Color(200, 144, 80, 255),
                            new Color(149, 160, 0),
                            new Color(255, 221, 0)
                    ));
            Aouintes_field.setCircularOrbit(Aouintes, 0, 0, 100);

            Aouintes.applySpecChanges();
        TTBlackSite.addDerelict(system, star,  "kite_hegemony_Interceptor", txt("WorldSpawn_DamagedShips"), "kite_hegemony", ShipRecoverySpecial.ShipCondition.GOOD, star.getRadius() * 2.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "condor_Support", txt("WorldSpawn_DamagedShips"), "condor", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 4.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "eagle_xiv_Elite", txt("WorldSpawn_DamagedShips"), "eagle_xiv", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 2.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "legion_Strike", txt("WorldSpawn_DamagedShips"), "legion", ShipRecoverySpecial.ShipCondition.WRECKED, Aouintes.getRadius() * 3.2F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "legion_Strike", txt("WorldSpawn_DamagedShips"), "legion", ShipRecoverySpecial.ShipCondition.WRECKED, Aouintes.getRadius() * 3.8F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "legion_Strike", txt("WorldSpawn_DamagedShips"), "legion", ShipRecoverySpecial.ShipCondition.AVERAGE, Aouintes.getRadius() * 6.8F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "condor_Support", txt("WorldSpawn_DamagedShips"), "condor", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 3F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "eagle_xiv_Elite", txt("WorldSpawn_DamagedShips"), "eagle_xiv", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 4F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "eagle_xiv_Elite", txt("WorldSpawn_DamagedShips"), "eagle_xiv", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 7.5F, true);

        TTBlackSite.addDerelict(system, Aouintes,  "SGB_Anvil_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Anvil", ShipRecoverySpecial.ShipCondition.AVERAGE, Aouintes.getRadius() * 2.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "SGB_Posmous_Support", txt("WorldSpawn_DamagedShips"), "SGB_Posmous", ShipRecoverySpecial.ShipCondition.WRECKED, star.getRadius() * 2.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "SGB_Hammies_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Hammies", ShipRecoverySpecial.ShipCondition.BATTERED, star.getRadius() * 4.5F, true);
        TTBlackSite.addDerelict(system, star,  "SGB_Wiesios_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Wiesios", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 2.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "SGB_Wiesios_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Wiesios", ShipRecoverySpecial.ShipCondition.GOOD, Aouintes.getRadius() * 2.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "SGB_Felix_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Felix", ShipRecoverySpecial.ShipCondition.BATTERED, Aouintes.getRadius() * 3.5F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, star,  "SGB_Felix_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Felix", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 3.25F, Math.random() < 0.5D);
        TTBlackSite.addDerelict(system, Aouintes,  "SGB_Felix_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Felix", ShipRecoverySpecial.ShipCondition.GOOD, star.getRadius() * 4.6F, true);
        }else{
            Aouintes.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            Aouintes.getMarket().addCondition(Conditions.EXTREME_TECTONIC_ACTIVITY);
            Aouintes.getMarket().addCondition(Conditions.RUINS_SCATTERED);
            Aouintes.getMarket().addCondition(Conditions.ORE_ABUNDANT);
            Aouintes.getMarket().addCondition(Conditions.RARE_ORE_SPARSE);

            TTBlackSite.addDerelict(system, Aouintes,  "kite_hegemony_Interceptor", txt("WorldSpawn_DamagedShips"), "kite_hegemony", ShipRecoverySpecial.ShipCondition.AVERAGE, star.getRadius() * 1.5F, true);
            TTBlackSite.addDerelict(system, Aouintes,  "SGB_Duplin_Assault", txt("WorldSpawn_DamagedShips"), "SGB_Duplin", ShipRecoverySpecial.ShipCondition.GOOD, star.getRadius() * 1.75F, true);
        }
        system.addRingBand(star, "misc", "rings_ice0", 256f, 0, new Color(96, 78, 65,255), 256f, 4250, 100f);
        system.addRingBand(star, "misc", "rings_ice0", 256f, 2, new Color(96, 78, 65,255), 256f, 4350, 140f);
        system.addRingBand(star, "misc", "rings_ice0", 256f, 1, new Color(96, 78, 65,255), 256f, 4450, 160f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 4200, 180f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4400, 220f);

    }
    //Shorthand function for adding a market, just copy it
    public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name,
                                           int size, ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries, float tarrif,
                                           boolean freePort, boolean withJunkAndChatter) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", tarrif);

        //Adds submarkets
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Add market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(freePort);

        //Adds our connected entities, if any
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, withJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        //Finally, return the newly-generated market
        return newMarket;
    }
}