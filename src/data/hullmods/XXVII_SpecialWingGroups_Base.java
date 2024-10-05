package data.hullmods;

import java.util.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;

public class XXVII_SpecialWingGroups_Base{
    public ShipAPI ship;
    public static final List<FleetMemberAPI> XXVII_SpecialWingGroup_Coop_List = new ArrayList<FleetMemberAPI>(); //存储Coop系列LPC的ship文件
    {
        if(ship.getNumFighterBays() >= 1) {
            String wing;
            for (int i = 0; i < ship.getNumFighterBays(); i++) {
                FleetMemberAPI ThisMember = ship.getFleetMember();
                if(ThisMember.getHullId().contains("SGB_Agatha_Coop_wing")
                        || ThisMember.getHullId().contains("SGB_Agatha_DogFight_wing")
                        || ThisMember.getHullId().contains("SGB_Agatha_Assault_wing")
                        || ThisMember.getHullId().contains("SGB_Ares_Coop_wing")
                        || ThisMember.getHullId().contains("SGB_Ares_DogFight_wing")
                        || ThisMember.getHullId().contains("SGB_Ares_Assault_wing")
                        || ThisMember.getHullId().contains("SGB_Axio_Coop_wing")
                        || ThisMember.getHullId().contains("SGB_Axio_DogFight_wing")
                        || ThisMember.getHullId().contains("SGB_Axio_Assault_wing")
                ) {
                    XXVII_SpecialWingGroup_Coop_List.add(ThisMember);
                }
            }
        }
    }
    public static final Set<String> SpecialWingType = new HashSet<>();  //设置全部已有战术类型
    public static final float Max_SpecialWingType_Nub = 1;  //设置同时启用的最大数量
    // 前装备
    static {
        SpecialWingType.add( "XXVII_SpecialWingGroup_Coop");
        SpecialWingType.add( "XXVII_SpecialWingGroup_DogFight");
        SpecialWingType.add( "XXVII_SpecialWingGroup_Assault");
    }
    //单独存储一份船插id
    private static final List<String> MODS = new ArrayList<>();
    static {
        MODS.add("XXVII_SpecialWingGroup_Coop");
        MODS.add("XXVII_SpecialWingGroup_DogFight");
        MODS.add("XXVII_SpecialWingGroup_Assault");
    }
    private static final String SGB_All_Wings = "normal";
    //存储所有的需要此功能的联队
    private static final Map<String, Map<String, String>> WINGS = new HashMap<>();
    static {
        //一类战机的开始
        //首先设置一个需要放在里面的Map
        Map<String, String> SGB_Agatha = new HashMap<>();
        //需要的船插id在前，联队id在后
        SGB_Agatha.put(SGB_All_Wings, "SGB_Agatha_wing");
        SGB_Agatha.put("XXVII_SpecialWingGroup_Coop", "SGB_Agatha_Coop_wing");
        SGB_Agatha.put("XXVII_SpecialWingGroup_DogFight", "SGB_Agatha_DogFight_wing");
        SGB_Agatha.put("XXVII_SpecialWingGroup_Assault", "SGB_Agatha_Assault_wing");
        //战机的船体id在前(ship_data里的船体id)，然后把Map放进去
        WINGS.put("SGB_Agatha", SGB_Agatha);
        WINGS.put("SGB_Agatha_Coop", SGB_Agatha);
        WINGS.put("SGB_Agatha_DogFight", SGB_Agatha);
        WINGS.put("SGB_Agatha_Assault", SGB_Agatha);
        //一类战机的结束
        //如果要新开一类战机-新的一种ship，那就重复从新建Map开始的部分
        //__________________________________________________________________

        Map<String, String> SGB_Axio = new HashMap<>();
        SGB_Axio.put(SGB_All_Wings, "SGB_Axio_wing");
        SGB_Axio.put("XXVII_SpecialWingGroup_Coop", "SGB_Axio_Coop_wing");
        SGB_Axio.put("XXVII_SpecialWingGroup_DogFight", "SGB_Axio_DogFight_wing");
        SGB_Axio.put("XXVII_SpecialWingGroup_Assault", "SGB_Axio_Assault_wing");
        WINGS.put("SGB_Axio", SGB_Axio);
        WINGS.put("SGB_Axio_Coop", SGB_Axio);
        WINGS.put("SGB_Axio_DogFight", SGB_Axio);
        WINGS.put("SGB_Axio_Assault", SGB_Axio);
        //__________________________________________________________________

        Map<String, String> SGB_Ares = new HashMap<>();
        SGB_Ares.put(SGB_All_Wings, "SGB_Ares_wing");
        SGB_Ares.put("XXVII_SpecialWingGroup_Coop", "SGB_Ares_Coop_wing");
        SGB_Ares.put("XXVII_SpecialWingGroup_DogFight", "SGB_Ares_DogFight_wing");
        SGB_Ares.put("XXVII_SpecialWingGroup_Assault", "SGB_Ares_Assault_wing");
        WINGS.put("SGB_Ares", SGB_Ares);
        WINGS.put("SGB_Ares_Coop", SGB_Ares);
        WINGS.put("SGB_Ares_DogFight", SGB_Ares);
        WINGS.put("SGB_Ares_Assault", SGB_Ares);



    }
    //存放一些不受效果影响的联队，也许会有用
    private static final List<String> EXCEPTION = new ArrayList<>();
    {
        //EXCEPTION.add("HSI_Maid_Wing");
    }

    public static void XXVII_SpecialWingGroups_apEB_ShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipVariantAPI variant = stats.getVariant();
        String currentMod = SGB_All_Wings;
        //检索当前的核心船插，没有就设置为标准版
        for (String mod : variant.getHullMods()) {
            if (MODS.contains(mod)) {
                currentMod = mod;
            }
        }
        //遍历航母上的联队，判断是否存在WINGS里存储的战机类型
        if(variant.getWings().isEmpty()) return;
        for (int i = 0; i < variant.getWings().size(); i++) {
            if(variant.getWing(i)==null) continue;
            if(EXCEPTION.contains(variant.getWing(i).getId())) continue;//存储在EXCEPTION里的联队不会被替换为WINGS里的联队，用于一些特殊情况
            String hullspec = variant.getWing(i).getVariant().getHullSpec().getBaseHullId();
            if (WINGS.keySet().contains(hullspec)) {
                String currWing = WINGS.get(hullspec).get(currentMod);
                if (currWing != null)
                    variant.setWingId(i, currWing);//替换为指定核心的战机联队
            }
        }
    }


    public static void XXVII_SpecialWingGroups_apEA_ShipCreation(ShipAPI ship, String id) {
        if (ship.getOriginalOwner() < 0) {
            //检测仓库里是否存在WINGS里存储的战机类型
            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getCargo() != null &&
                    Global.getSector().getPlayerFleet().getCargo().getStacksCopy() != null &&
                    !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()) {
                //索引背包
                for (CargoStackAPI s : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
                    if (s.isFighterWingStack()) {
                        FighterWingSpecAPI wingSpec = s.getFighterWingSpecIfWing();
                        if(EXCEPTION.contains(wingSpec.getId())) continue;
                        String wingHull = wingSpec.getVariant().getHullSpec().getBaseHullId();
                        if(WINGS.keySet().contains(wingHull)){
                            //无论是不是标准版，都移除换成标准版
                            Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                            Global.getSector().getPlayerFleet().getCargo().addFighters(WINGS.get(wingHull).get(SGB_All_Wings), (int)s.getSize());
                        }
                    }
                }
            }
            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getMarket() != null &&
                    Global.getSector().getPlayerFleet().getMarket().getSubmarketsCopy() != null &&
                    !Global.getSector().getPlayerFleet().getMarket().getSubmarketsCopy().isEmpty()){
                //索引所在处市场
                for (SubmarketAPI s : Global.getSector().getPlayerFleet().getMarket().getSubmarketsCopy()) {
                    if (s.getCargo() != null) {
                        //索引市场仓库
                        for (CargoStackAPI c : s.getCargo().getStacksCopy()) {
                            if (c.isFighterWingStack()) {
                                FighterWingSpecAPI wingSpec = c.getFighterWingSpecIfWing();
                                if (EXCEPTION.contains(wingSpec.getId())) continue;
                                String wingHull = wingSpec.getVariant().getHullSpec().getBaseHullId();
                                if (WINGS.keySet().contains(wingHull)) {
                                    //无论是不是标准版，都移除换成标准版
                                    Global.getSector().getPlayerFleet().getCargo().removeStack(c);
                                    Global.getSector().getPlayerFleet().getCargo().addFighters(WINGS.get(wingHull).get(SGB_All_Wings), (int) c.getSize());

                                }
                            }
                        }
                    }
                }
            }

            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getInteractionTarget() != null &&
                    Global.getSector().getPlayerFleet().getInteractionTarget().getMarket() != null &&
                    Global.getSector().getPlayerFleet().getInteractionTarget().getMarket().getSubmarketsCopy() != null &&
                    !Global.getSector().getPlayerFleet().getInteractionTarget().getMarket().getSubmarketsCopy().isEmpty()){
                //索引所在处市场
                for (SubmarketAPI s : Global.getSector().getPlayerFleet().getInteractionTarget().getMarket().getSubmarketsCopy()) {
                    if (s.getCargo() != null) {
                        //索引市场仓库
                        for (CargoStackAPI c : s.getCargo().getStacksCopy()) {
                            if (c.isFighterWingStack()) {
                                FighterWingSpecAPI wingSpec = c.getFighterWingSpecIfWing();
                                if (EXCEPTION.contains(wingSpec.getId())) continue;
                                String wingHull = wingSpec.getVariant().getHullSpec().getBaseHullId();
                                if (WINGS.keySet().contains(wingHull)) {
                                    //无论是不是标准版，都移除换成标准版
                                    c.getCargo().removeStack(c);
                                    c.getCargo().addFighters(WINGS.get(wingHull).get(SGB_All_Wings), (int) c.getSize());

                                }
                            }
                        }
                    }
                }
            }
            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getContainingLocation() != null &&
                    Global.getSector().getPlayerFleet().getContainingLocation().getPlanets() != null &&
                    !Global.getSector().getPlayerFleet().getContainingLocation().getPlanets().isEmpty()){
                //索引所在处市场
                for (PlanetAPI s : Global.getSector().getPlayerFleet().getContainingLocation().getPlanets()) {
                    if(s!=null &&
                            s.getMarket()!=null &&
                            s.getMarket().getSubmarketsCopy()!=null &&
                            !s.getMarket().getSubmarketsCopy().isEmpty()) {
                        for (SubmarketAPI m : s.getMarket().getSubmarketsCopy()) {
                            if (m.getCargo() != null) {
                                //索引市场仓库
                                for (CargoStackAPI c : m.getCargo().getStacksCopy()) {
                                    if (c.isFighterWingStack()) {
                                        FighterWingSpecAPI wingSpec = c.getFighterWingSpecIfWing();
                                        if (EXCEPTION.contains(wingSpec.getId())) continue;
                                        String wingHull = wingSpec.getVariant().getHullSpec().getBaseHullId();
                                        if (WINGS.keySet().contains(wingHull)) {
                                            //无论是不是标准版，都移除换成标准版
                                            m.getCargo().removeStack(c);
                                            m.getCargo().addFighters(WINGS.get(wingHull).get(SGB_All_Wings), (int) c.getSize());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getContainingLocation() != null &&
                    Global.getSector().getPlayerFleet().getContainingLocation().getCustomEntities() != null &&
                    !Global.getSector().getPlayerFleet().getContainingLocation().getCustomEntities().isEmpty()){
                //索引所在处市场
                for (CustomCampaignEntityAPI s : Global.getSector().getPlayerFleet().getContainingLocation().getCustomEntities()) {
                    if(s!=null &&
                            s.getMarket()!=null &&
                            s.getMarket().getSubmarketsCopy()!=null &&
                            !s.getMarket().getSubmarketsCopy().isEmpty()) {
                        for (SubmarketAPI m : s.getMarket().getSubmarketsCopy()) {
                            if (m.getCargo() != null) {
                                //索引市场仓库
                                for (CargoStackAPI c : m.getCargo().getStacksCopy()) {
                                    if (c.isFighterWingStack()) {
                                        FighterWingSpecAPI wingSpec = c.getFighterWingSpecIfWing();
                                        if (EXCEPTION.contains(wingSpec.getId())) continue;
                                        String wingHull = wingSpec.getVariant().getHullSpec().getBaseHullId();
                                        if (WINGS.keySet().contains(wingHull)) {
                                            //无论是不是标准版，都移除换成标准版
                                            m.getCargo().removeStack(c);
                                            m.getCargo().addFighters(WINGS.get(wingHull).get(SGB_All_Wings), (int) c.getSize());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    //下面是一系列简化的插件能否使用的判定逻辑
    public static String XXVII_SpecialWingGroups_InapplicableReason(ShipAPI ship,String whatSpIsThis,float numberOfCount,Set<String> weaponKind) { //显示无法安装的原因
        /*if (!ship.getVariant().getHullMods().contains("坏掉的插件")) {
            return "舰载机纳米锻炉已受损";
        }
         */
        if(whatSpIsThis == null){
            whatSpIsThis = "某种";
            if(ship.getVariant().hasHullMod(XXVII_SpecialWingGroup_Coop.XXVII_SpecialWingGroup_Coop)){
                whatSpIsThis = "协同";
            }
            else if(ship.getVariant().hasHullMod(XXVII_SpecialWingGroup_DogFight.XXVII_SpecialWingGroup_DogFight)){
                whatSpIsThis = "制空";
            }
        }
        if ((!ship.getVariant().hasHullMod(XXVII_SpecialWingGroup_Carrier.XXVII_SpecialWingGroup_Carrier))) {
            return "只能安装在链戍航母上";
        }
        float i = 0;
        for (String weaponId : ship.getVariant().getHullMods() ) {
            if(weaponId != null && (weaponKind.contains(weaponId)) ){
                i=i+1;
            }
        }
        if(i>=numberOfCount){
            return "已经使用了"+whatSpIsThis+"航空教本，不能重叠使用";
        }


        return whatSpIsThis+"准备好了";
    }
    public static boolean XXVII_SpecialWingGroups_isApToShip(ShipAPI ship, float numberOfCount, HullModSpecAPI spec, Set<String> weaponKind) {
        float i = 0;
        for (String weaponId : ship.getVariant().getHullMods() ) {
            if(weaponId != null && (weaponKind.contains(weaponId)) && !weaponId.equals(spec.getId()) ){
                i=i+1;
            }
        }
        if(ship.getVariant().hasHullMod(XXVII_SpecialWingGroup_Carrier.XXVII_SpecialWingGroup_Carrier) && !(i >=numberOfCount)){//Global.getLogger(IIRT_AI_Core_Base.class).info("if=" + (ship.getVariant().hasHullMod(Gamma_Core.Gamma_Core)&& !(i >=numberOfCount)));
            return true;
        }
        else{
            return false;
        }
    }
}
