package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.fs.starfarer.api.campaign.CargoAPI.CargoItemType.RESOURCES;
import static data.scripts.world.SGBModGen.addMarketplace;


public class SGB_Detachment_outpost {
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Aosnit Fort");

        //星系位置
        system.getLocation().set(11000.0F, -33000.0F);
        //背景图片
        system.setBackgroundTextureFilename("graphics/backgrounds/background6.jpg");


        //恒星（大小，半径，日冕大小
        PlanetAPI star = system.initStar(
                "Qosurom",
                "star_yellow",
                800f,
                500f,
                3.0F,
                1.5F,
                1.0F
        );
        //背景光颜色
        system.setLightColor(new Color(255, 210, 159));

        //让小行星带环绕它
        system.addAsteroidBelt(
                star,
                130,
                5000f,
                600f,
                180,
                360,
                Terrain.ASTEROID_BELT,
                ""
        );


        SectorEntityToken SGB_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/eos_nebula.png", // png
                0, 0, // center of nebula
                system, // location to add to
                "terrain", "nebula_amber", // "nebula_blue", // texture to use, uses xxx_map for map
                4, 4, StarAge.AVERAGE); // number of cells in texture
        /*
          一号行星————————————————————————————————————————————————————————————————————————
          @return Trunking Post
         */
        system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 3900, 80f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4000, 120f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 4100, 160f);
        //行星（势力、圆心、引用、类型；设置星球简介以及归属
        SectorEntityToken ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(600 + 256, 400, null, "Limort Accretion Ring"));
        ring.setCircularOrbit(star, 0, 0, 100);
        PlanetAPI planet1 = system.addPlanet(
                "SGB_planet1", //行星ID
                star, //恒星ID
                "Limort Fort", //星球名字
                "barren-desert", //类型
                215,
                180f,
                4000f,
                365f
        );
        //行星环
        system.addAsteroidBelt(star,
                150, 4000f, 180f, 180, 360, Terrain.RING, ""
        );
        planet1.setFaction("SGB"); //行星所属势力
        planet1.getSpec().setGlowColor(new Color(255, 200, 200));   //行星散射光照(可以理解为大气层发光)
        planet1.getSpec().setUseReverseLightForGlow(true);
        planet1.getSpec().setCloudColor(new Color(255, 222, 198, 150));    //行星de云
        planet1.applySpecChanges(); //行星应用特殊设置
        planet1.getSpec().setAtmosphereThicknessMin(25);
        planet1.getSpec().setAtmosphereThickness(0.2f);
        planet1.getSpec().setAtmosphereColor( new Color(80, 92, 100,120) );
        Misc.initConditionMarket(planet1);

        SectorEntityToken planet1_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
                new MagneticFieldTerrainPlugin.MagneticFieldParams(300f, // terrain effect band width
                        480, // terrain effect middle radius
                        planet1, // entity that it's around
                        280f, // visual band start
                        500f, // visual band end// base color
                        new Color(50, 30, 100, 30),
                        1f, // probability to spawn aurora sequence, checked once/day when no aurora in progress
                        new Color(110, 52, 20, 130),
                        new Color(150, 122, 30, 150),
                        new Color(200, 198, 50, 190),
                        new Color(238, 250, 70, 240),
                        new Color(170, 200, 80, 255),
                        new Color(160, 0, 0),
                        new Color(255, 0, 68)
                ));
        planet1_field.setCircularOrbit(planet1, 0, 0, 100);

        // 设置环境，市场
        MarketAPI planet1Market = addMarketplace(
                "SGB",
                planet1,
                null,
                planet1.getName(),
                7,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7, // 设置殖民地规模
                                //这几块都在设置环境
                                Conditions.VERY_HOT, //芜热
                                Conditions.HABITABLE, //恶劣天气
                                Conditions.RUINS_VAST,
                                Conditions.RARE_ORE_MODERATE,
                                Conditions.LOW_GRAVITY,
                                Conditions.THIN_ATMOSPHERE,
                                Conditions.ORE_RICH)), //
                new ArrayList<>(
                        Arrays.asList(
                                //这几块都在设置市场类型
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_STORAGE)),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION, //这几块都在设置工业区划建设
                                Industries.MEGAPORT,
                                Industries.STARFORTRESS_MID,
                                Industries.HEAVYBATTERIES,
                                Industries.REFINING,
                                Industries.ORBITALWORKS,
                                Industries.WAYSTATION,
                                Industries.HIGHCOMMAND,
                                Industries.FUELPROD,
                                Industries.PLANETARYSHIELD
                        )
                ),
                0.3f,
                false,
                true
        );

        //descriptions.csv引用星球介绍位置
        planet1.setCustomDescriptionId("SGB_planet1_description");

        //give every industry an AiCoreId    给每个地方丢个AI核心
        Industry SGBMegaport = planet1Market.getIndustry("megaport");
        SGBMegaport.setAICoreId("gamma_core");

        Industry SGBPopulation = planet1Market.getIndustry("population");
        SGBPopulation.setAICoreId("gamma_core");

        Industry SGBHighCommand = planet1Market.getIndustry("highcommand");
        SGBHighCommand.setAICoreId("alpha_core");
        //一个纳米锻造炉
        planet1Market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));

        /*
          二号行星————————————————————————————————————————————————————————————————————————
          @return Description Post
         */
        system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 4700, 80f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4800, 100f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 4900, 130f);
        ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(200 + 256, 4800, null, "Postun Accretion Ring"));
        ring.setCircularOrbit(star, 0, 0, 100);
        //行星（势力、圆心、引用、类型；设置星球简介以及归属
        PlanetAPI planet2 = system.addPlanet(
                "SGB_planet2", //行星ID
                star, //恒星ID
                "Postun", //星球名字
                "rocky_unstable", //类型
                35,
                190f,
                4800f,
                365f
        );
        //行星环
        system.addAsteroidBelt(star,
                150, 4800f, 190f, 180, 360, Terrain.RING, ""
        );
        planet2.getSpec().setGlowColor(new Color(255, 255, 255));
        planet2.getSpec().setUseReverseLightForGlow(true);
        planet2.getSpec().setCloudColor(new Color(230, 244, 248, 150));

        ////TTBlackSite.addDerelict(system, planet2,  "SGB_Patrick_Normal", "帕特里克", "SGB_Patrick", ShipRecoverySpecial.ShipCondition.AVERAGE, planet2.getRadius() * 4.5F, Math.random() < 0.7D);


        /*
          三号行星————————————————————————————————————————————————————————————————————————
          @return Description Post
         */
        //行星（势力、圆心、引用、类型；设置星球简介以及归属
        PlanetAPI planet3 = system.addPlanet(
                "SGB_planet3", //行星ID
                star, //恒星ID
                "Apocalypse Union Ostuon", //星球名字
                "desert1", //类型
                70,
                120f,
                2400f,
                370f
        );
        //行星环
        system.addAsteroidBelt(star,
                200, 2400f, 120f, 180, 360, Terrain.RING, ""
        );
        planet3.getSpec().setGlowColor(new Color(255, 254, 236));
        planet3.getSpec().setUseReverseLightForGlow(true);
        planet3.getSpec().setCloudColor(new Color(196, 191, 158, 150));
        planet3.setFaction("SGB");
        Misc.initConditionMarket(planet3);
        //让小行星带环绕它
        system.addAsteroidBelt(planet3, 70, 500f, 300f, 180, 360, Terrain.ASTEROID_BELT, ""
        );

        // 设置环境，市场
        MarketAPI planet3Market = addMarketplace(
                "SGB",
                planet3,
                null,
                planet3.getName(),
                5,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_5, // 设置殖民地规模
                                //这几块都在设置环境
                                Conditions.VERY_HOT, //芜热
                                Conditions.EXTREME_WEATHER, //恶劣天气
                                Conditions.RUINS_SCATTERED,
                                Conditions.RARE_ORE_ABUNDANT,
                                Conditions.LOW_GRAVITY,
                                Conditions.THIN_ATMOSPHERE,
                                Conditions.ORE_ULTRARICH)), //
                new ArrayList<>(
                        Arrays.asList( //这几块都在设置市场类型
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE)),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION, //这几块都在设置工业区划建设
                                Industries.SPACEPORT,
                                Industries.GROUNDDEFENSES,
                                Industries.PATROLHQ,
                                Industries.MINING,
                                Industries.ORBITALWORKS,
                                Industries.WAYSTATION,
                                Industries.BATTLESTATION_MID
                        )
                ),
                0.3f,
                false,
                true
        );

        //descriptions.csv引用星球介绍位置
        planet3.setCustomDescriptionId("SGB_planet3_description");

        /*
          四号行星————————————————————————————————————————————————————————————————————————
          @return Description Post
         */
        //行星（势力、圆心、引用、类型；设置星球简介以及归属
        PlanetAPI planet4 = system.addPlanet(
                "SGB_planet4", //行星ID
                star, //恒星ID
                "Cousnhes", //星球名字
                "desert1",
                5,
                140f,
                3230f,
                370f
        );
        //行星环
        system.addAsteroidBelt(star,
                150, 3230f, 140f, 180, 360, Terrain.RING, ""
        );
        planet4.getSpec().setGlowColor(new Color(245, 241, 172));
        planet4.getSpec().setUseReverseLightForGlow(true);
        planet4.getSpec().setCloudColor(new Color(255, 243, 213, 150));

        //Num 6 Plants
        PlanetAPI planet6 = system.addPlanet(
                "SGB_planet6", //行星ID
                planet4, //恒星ID
                "Souehun", //星球名字
                "desert",
                120,
                90f,
                1100f,
                320f
        );
        //行星环
        system.addAsteroidBelt(planet4,
                150, 1100f, 90f, 180, 360, Terrain.RING, ""
        );
        planet6.setFaction("SGB");//descriptions.csv引用星球介绍位置
        planet6.setCustomDescriptionId("SGB_resourcesPlanet_description");
        planet6.getSpec().setGlowColor(new Color(180, 255, 246));
        planet6.getSpec().setUseReverseLightForGlow(true);
        planet6.getSpec().setCloudColor(new Color(245, 255, 254, 195));

        SectorEntityToken STR = system.addCustomEntity("corvus_abandoned_station",
                "Aosnit Abandoned Station", "station_side04", "neutral");

        STR.setCircularOrbitPointingDown(system.getEntityById("SGB_planet6"), 45, 300, 30);
        STR.setCustomDescriptionId("SGB_planet6_platform");
        STR.setInteractionImage("illustrations", "cargo_loading");
        Misc.setAbandonedStationMarket("corvus_abandoned_station_market", STR);

        STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addFuel(451f);
        STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSupplies(127f);
        STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addItems(RESOURCES,"organs",71f);
        //设置战役奖励
        if (Global.getSettings().getMissionScore("SGB_TheLongestDay") >= 55) {
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addWeapons("chaingun",1);
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addWeapons("lightdualac",1);
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addFuel(72f);
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addItems(RESOURCES,"organs",71f);
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addItems(RESOURCES,"drugs",28f);
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addItems(RESOURCES,"heavy_machinery",55f);
            STR.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addMothballedShip(FleetMemberType.SHIP, "SGB_Austenite_Assault_In_Campain", "Partially-Damaged Ship");
        }
         /*
          七号行星————————————————————————————————————————————————————————————————————————
          @return Description Post
         */
        PlanetAPI planet7 = system.addPlanet(
                "SGB_planet7",
                star,
                "Ougest",
                "gas_giant",
                230,
                420,
                6000,
                300);
        //行星环
        system.addAsteroidBelt(star,
                150, 7000f, 320f, 180, 360, Terrain.RING, ""
        );
        system.addAsteroidBelt(planet7,
                150, 800f, 100f, 180, 360, Terrain.RING, ""
        );
        planet7.setCustomDescriptionId("SGB_resourcesPlanet_description");
        planet7.getSpec().setGlowColor(new Color(155, 155, 155));
        planet7.getSpec().setUseReverseLightForGlow(true);
        planet7.getSpec().setCloudColor(new Color(200, 200, 200, 150));

        //为星系生成指定跳跃点
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("inside_point", "Aosnit Inner-System Jump Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(planet1, 0, 1000, 30);
        jumpPoint.setOrbit(orbit);
        jumpPoint.setRelatedPlanet(planet1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        //为星系生成指定跳跃点
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint("inside_point2", "Ougest Gravity Well");
        OrbitAPI orbit2 = Global.getFactory().createCircularOrbit(star, 40, 3500, 360);
        jumpPoint2.setOrbit(orbit2);
        jumpPoint2.setRelatedPlanet(planet1);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        //扫描本星系所有跳跃点并为之配置数据
        system.autogenerateHyperspaceJumpPoints(true, false);

        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);

        //生成星门
        SectorEntityToken gate = system.addCustomEntity("SGB_gate", // unique id 设置星门id
                "Aosnit Gate", // name - if null, defaultName from custom_entities.json will be used 设置你星门的名字
                "inactive_gate", // type of object, defined in custom_entities.json 设置标签（让系统识别这是个星门）根据custom_entities.json设置
                null); // faction


        gate.setCircularOrbit(star, 5, 3080f, 350f);

        //设置你星系的永久稳定点建筑
        SectorEntityToken A = system.addCustomEntity(null,null, "comm_relay", "SGB");
        A.setCircularOrbit(star, 180f, 2900f, 365f);
        SectorEntityToken B = system.addCustomEntity(null,null, "nav_buoy", "SGB");
        B.setCircularOrbit(star, 220f, 2500f, 365f);
        SectorEntityToken C = system.addCustomEntity(null,null, "sensor_array", "SGB");
        C.setCircularOrbit(star, 240f, 2900f, 365f);

    }
}
