package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.*;

import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.sgb.SGB_Color;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;

import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_Austenite_Core extends BaseHullMod {
    /*
     * if (any=null){
     * go fuck you self
     * }
     */

    // For good luck^^^
    private static float debuff = 0;
    private static float debuff1 = 0;
    private static float debuff2 = 0;
    private static float THIS_IS_ONE = 1;
    // weapon
    private final Map<Integer, String> LEFT_WEAPON = new HashMap<>();
    private final Map<Integer, String> RIGHT_WEAPON = new HashMap<>();

    {
        LEFT_WEAPON.put(0, "SGB_Austenite_Weapon_Rocket_L");
        LEFT_WEAPON.put(1, "SGB_Austenite_Weapon_Gun_L");
        LEFT_WEAPON.put(2, "SGB_Austenite_Weapon_Blast_L");
    }
    {
        RIGHT_WEAPON.put(0, "SGB_Austenite_Weapon_Rocket_R");
        RIGHT_WEAPON.put(1, "SGB_Austenite_Weapon_Gun_R");
        RIGHT_WEAPON.put(2, "SGB_Austenite_Weapon_Blast_R");
    }
    // wing
    private final Map<Integer, String> MAIN_WING_L = new HashMap<>();
    private final Map<Integer, String> MAIN_WING_R = new HashMap<>();

    {
        MAIN_WING_L.put(0, "SGB_Austenite_WingDive_L");
        MAIN_WING_L.put(1, "SGB_Austenite_Wing_L");
        MAIN_WING_L.put(2, "SGB_Austenite_WingNormal_L");
    }
    {
        MAIN_WING_R.put(0, "SGB_Austenite_WingDive_R");
        MAIN_WING_R.put(1, "SGB_Austenite_Wing_R");
        MAIN_WING_R.put(2, "SGB_Austenite_WingNormal_R");
    }
    // Engine_wing
    private final Map<Integer, String> ENGINE_WING_L = new HashMap<>();
    private final Map<Integer, String> ENGINE_WING_R = new HashMap<>();

    {
        ENGINE_WING_L.put(0, "SGB_Austenite_Engine_Wing_L_F");
        ENGINE_WING_L.put(1, "SGB_Austenite_Engine_Wing_L");
        ENGINE_WING_L.put(2, "SGB_Austenite_Engine_Wing_L_P");
    }
    {
        ENGINE_WING_R.put(0, "SGB_Austenite_Engine_Wing_R_F");
        ENGINE_WING_R.put(1, "SGB_Austenite_Engine_Wing_R");
        ENGINE_WING_R.put(2, "SGB_Austenite_Engine_Wing_R_P");
    }
    // head
    private final Map<Integer, String> HEAD = new HashMap<>();

    {
        HEAD.put(0, "SGB_Austenite_Head");
        HEAD.put(1, "SGB_Austenite_Head_Armor");
        HEAD.put(2, "SGB_Austenite_Head_Empty");
    }
    // SWITCH EVERY ONE————
    private final Map<String, Integer> SWITCH_TO_L = new HashMap<>();
    private final Map<String, Integer> SWITCH_TO_R = new HashMap<>();
    private final Map<String, Integer> SWITCH_TO_WING = new HashMap<>();
    private final Map<String, Integer> SWITCH_TO_ENGINE_WING = new HashMap<>();
    private final Map<String, Integer> SWITCH_TO_HEAD = new HashMap<>();
    // change 0 1 2 3 4 5 to 1 2 3 4 5 0
    {
        SWITCH_TO_L.put("SGB_Austenite_Weapon_Rocket_L", 1);
        SWITCH_TO_L.put("SGB_Austenite_Weapon_Gun_L", 2);
        SWITCH_TO_L.put("SGB_Austenite_Weapon_Blast_L", 0);
    }
    {
        SWITCH_TO_R.put("SGB_Austenite_Weapon_Rocket_R", 1);
        SWITCH_TO_R.put("SGB_Austenite_Weapon_Gun_R", 2);
        SWITCH_TO_R.put("SGB_Austenite_Weapon_Blast_R", 0);
    }
    {
        SWITCH_TO_WING.put("SGB_Austenite_WingDive_L", 1);
        SWITCH_TO_WING.put("SGB_Austenite_Wing_L", 2);
        SWITCH_TO_WING.put("SGB_Austenite_WingNormal_L", 0);
    }
    {
        SWITCH_TO_ENGINE_WING.put("SGB_Austenite_Engine_Wing_L_F", 1);
        SWITCH_TO_ENGINE_WING.put("SGB_Austenite_Engine_Wing_L", 2);
        SWITCH_TO_ENGINE_WING.put("SGB_Austenite_Engine_Wing_L_P", 0);
    }
    {
        SWITCH_TO_HEAD.put("SGB_Austenite_Head", 1);
        SWITCH_TO_HEAD.put("SGB_Austenite_Head_Armor", 2);
        SWITCH_TO_HEAD.put("SGB_Austenite_Head_Empty", 0);
    }
    // Hullmods————
    private final Map<Integer, String> WEAPON_MOD_L = new HashMap<>();
    private final Map<Integer, String> WEAPON_MOD_R = new HashMap<>();
    private final Map<Integer, String> MAIN_WING_MOD = new HashMap<>();
    private final Map<Integer, String> ENGINE_WING_MOD = new HashMap<>();
    private final Map<Integer, String> HEAD_MOD = new HashMap<>();
    {
        WEAPON_MOD_L.put(0, "SGB_Austenite_Rocket_L");
        WEAPON_MOD_L.put(1, "SGB_Austenite_Gun_L");
        WEAPON_MOD_L.put(2, "SGB_Austenite_Weapon_Blast_L");
    }
    {
        WEAPON_MOD_R.put(0, "SGB_Austenite_Rocket_R");
        WEAPON_MOD_R.put(1, "SGB_Austenite_Gun_R");
        WEAPON_MOD_R.put(2, "SGB_Austenite_Weapon_Blast_R");
    }
    {
        MAIN_WING_MOD.put(0, "SGB_Austenite_DrivenWing");
        MAIN_WING_MOD.put(1, "SGB_Austenite_HeavyWing");
        MAIN_WING_MOD.put(2, "SGB_Austenite_NormalWing");
    }
    {
        ENGINE_WING_MOD.put(0, "SGB_Austenite_Engine_Wing_Unstable");
        ENGINE_WING_MOD.put(1, "SGB_Austenite_Engine_Wing_BackLoad");
        ENGINE_WING_MOD.put(2, "SGB_Austenite_Engine_Wing_Polarization");
    }

    {
        HEAD_MOD.put(0, "SGB_Austenite_Head_Assault");
        HEAD_MOD.put(1, "SGB_Austenite_Head_Armor");
        HEAD_MOD.put(2, "SGB_Austenite_Head_Empty");
    }
    // IDs————
    private final String leftslotID = "WEAPON_LEFT";
    private final String rightslotID = "WEAPON_RIGHT";
    private final String lwingslotID = "WING_LEFT";
    private final String rwingslotID = "WING_RIGHT";
    private final String lEnginewingslotID = "WING_ENGINE_LEFT";
    private final String rEnginewingslotID = "WING_ENGINE_RIGHT";
    // private final String systemslotID = "TOP";
    private final String headslotID = "HEAD";

    private final Map<String, Integer> SWITCH_SHIP_TO = new HashMap<>();
    private final Map<Integer, String> SYSTEM_MOD = new HashMap<>();
    {
        SWITCH_SHIP_TO.put("SGB_Austenite", 1);
        SWITCH_SHIP_TO.put("SGB_Austenite_Drive", 2);
        SWITCH_SHIP_TO.put("SGB_Austenite_Ammo", 3);
        SWITCH_SHIP_TO.put("SGB_Austenite_Carrier", 0);
    }
    private final Map<Integer, String> SWITCH_SHIP = new HashMap<>();
    {
        SWITCH_SHIP.put(0, "SGB_Austenite");
        SWITCH_SHIP.put(1, "SGB_Austenite_Drive");
        SWITCH_SHIP.put(2, "SGB_Austenite_Ammo");
        SWITCH_SHIP.put(3, "SGB_Austenite_Carrier");
    }
    {
        SYSTEM_MOD.put(0, "SGB_Austenite_System_Repair");
        SYSTEM_MOD.put(1, "SGB_Austenite_System_Drive");
        SYSTEM_MOD.put(2, "SGB_Austenite_System_Ammo");
        SYSTEM_MOD.put(3, "SGB_Austenite_System_Wing");
    }

    // Wingman System
    private static final List<String> WINGMAN_MOD = new ArrayList<>();
    private static final List<String> WINGMAN_WINGS = new ArrayList<>();
    static{
        WINGMAN_MOD.add("SGB_Austenite_System_Wing_Attack");
        WINGMAN_MOD.add("SGB_Austenite_System_Wing_Assault");
        WINGMAN_MOD.add("SGB_Austenite_System_Wing_Support");
    }
    static{
        WINGMAN_WINGS.add( "SGB_Austenite_Wingman_Attack_wing");
        WINGMAN_WINGS.add("SGB_Austenite_Wingman_Assault_wing");
        WINGMAN_WINGS.add("SGB_Austenite_Wingman_Intercepter_wing");
    }

    public SGB_Austenite_Core() {
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // 锁死甲板可行性
        /*List<String> wingslist = stats.getVariant().getWings();

        ArrayList<String> Wing_ids = new ArrayList<String>();
        Wing_ids.add("SGB_Austenite_Wingman_Attack_wing");
        Wing_ids.add("SGB_Austenite_Wingman_Assault_wing");
        Wing_ids.add("SGB_Austenite_Wingman_Intercepter_wing");
        Global.getLogger(this.getClass()).info("WingListBeforeEveryThings" + wingslist);
        if (stats.getVariant().getWings().retainAll(Wing_ids)) {
            Global.getLogger(this.getClass()).info("ContainOtherThings so it will be clear" + wingslist);
            stats.getVariant().getWings().clear();
            Global.getLogger(this.getClass()).info("ContainOtherThings so is now been cleared" + wingslist);
        }
        Global.getLogger(this.getClass()).info("No other so it will be good" + wingslist);
        wingslist.retainAll(Wing_ids);
        Global.getLogger(this.getClass()).info("good" + wingslist);*/

        stats.getDynamic().getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyFlat(id, 1000f);

        for (String h : stats.getVariant().getHullMods()) {
            if (h.contains("unstable_injector")) {
                debuff1 = 0.1F;
                stats.getEngineDamageTakenMult().modifyMult(id, 2f);
                stats.getMaxSpeed().modifyMult(id, 1.2f);
            }
            if (h.contains("safetyoverrides")) {
                debuff2 = 0.2F;
                stats.getHullBonus().modifyMult(id, 0.7f);
                stats.getArmorBonus().modifyMult(id, 0.7f);
                stats.getBallisticWeaponDamageMult().modifyMult(id, 1.2f);
                stats.getMissileWeaponDamageMult().modifyMult(id, 1.2f);
                stats.getEnergyWeaponDamageMult().modifyMult(id, 1.2f);
            }
            if (!h.contains("unstable_injector")) {
                debuff1 = 0F;
            }
            if (!h.contains("safetyoverrides")) {
                debuff2 = 0F;
            }
        }
        debuff = debuff1 + debuff2;
        stats.getPeakCRDuration().modifyMult(id, 1 - debuff);

        // switch a weapon if none of it has been put on
        boolean toSwitchL = true;
        boolean toSwitchR = true;
        boolean toSwitchW = true;
        boolean toSwitchEW = true;
        boolean toSwitchS = true;
        boolean toSwitchH = true;
        for (int l = 0; l < WEAPON_MOD_L.size(); l++) {
            if (stats.getVariant().getHullMods().contains(WEAPON_MOD_L.get(l))) {
                toSwitchL = false;
            }
        }
        for (int r = 0; r < WEAPON_MOD_R.size(); r++) {
            if (stats.getVariant().getHullMods().contains(WEAPON_MOD_R.get(r))) {
                toSwitchR = false;
            }
        }
        for (int w = 0; w < MAIN_WING_MOD.size(); w++) {
            if (stats.getVariant().getHullMods().contains(MAIN_WING_MOD.get(w))) {
                toSwitchW = false;
            }
        }
        for (int ew = 0; ew < ENGINE_WING_MOD.size(); ew++) {
            if (stats.getVariant().getHullMods().contains(ENGINE_WING_MOD.get(ew))) {
                toSwitchEW = false;
            }
        }
        /*
         * for (int s = 0; s < SYSTEM_MOD.size(); s++) {
         * if (stats.getVariant().getHullMods().contains(SYSTEM_MOD.get(s))) {
         * toSwitchS = false;
         * }
         * }
         */
        boolean switchShip = true;
        for (int s = 0; s < SYSTEM_MOD.size(); s++) {
            if (stats.getVariant().getHullMods().contains(SYSTEM_MOD.get(s))) {
                switchShip = false;
            }
        }
        for (int h = 0; h < HEAD_MOD.size(); h++) {
            if (stats.getVariant().getHullMods().contains(HEAD_MOD.get(h))) {
                toSwitchH = false;
            }
        }
        /*
        boolean switchWingman = false;
        boolean FirstRunWingman = false;
        // if(stats.getVariant().getWings().contains())
        for (int wm = 0; wm < WINGMAN_MOD.size(); wm++) {
            if (switchWingman||(stats.getVariant().getHullMods().contains("SGB_Austenite_System_Wing") &&
                    !stats.getVariant().getHullMods().contains(WINGMAN_MOD.get(wm)))) {
                switchWingman = true;
                runOnceWing = false;
            } else {
                switchWingman = false;
            }
        }
        Global.getLogger(this.getClass()).info("switchWingman=" + switchWingman);
        Global.getLogger(this.getClass()).info("runOnceWing=" + runOnceWing);
        */
        // Change hull
        if (switchShip) {

            int switchToShip = SWITCH_SHIP_TO.get(stats.getVariant().getHullSpec().getHullId());

            ShipHullSpecAPI ship = Global.getSettings().getHullSpec(SWITCH_SHIP.get(switchToShip));
            stats.getVariant().setHullSpecAPI(ship);

            // add the proper hullmod
            stats.getVariant().addMod(SYSTEM_MOD.get(switchToShip));
            //stats.getVariant().addPermaMod(SYSTEM_MOD.get(switchToShip));
        }
        /*
        // SWITCH those WINGs, and might can custom in future.
        if (!runOnceWing && switchWingman) {
            int NowWingman;
            boolean Cleard = false;
            List<String> wingList = stats.getVariant().getWings();

            if (!runOnceWing) {
                Global.getLogger(this.getClass()).info("==the WINGMAN PROJECT job==");
                Global.getLogger(this.getClass()).info("WingList(before=" + wingList);
                // Not auto

                if (wingList.contains("SGB_Austenite_Wingman_Attack_wing")) {
                    NowWingman = 1;
                    Global.getLogger(this.getClass()).info("== NowWingman=1 ==");
                } else if (wingList.contains("SGB_Austenite_Wingman_Assault_wing")) {
                    NowWingman = 2;
                    Global.getLogger(this.getClass()).info("== NowWingman=2 ==");
                } else if (wingList.contains("SGB_Austenite_Wingman_Intercepter_wing")) {
                    NowWingman = 0;
                    Global.getLogger(this.getClass()).info("== NowWingman=0 ==");
                } else {
                    NowWingman = (int) MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS_GET.size() - 1);
                }
                stats.getVariant().getWings().clear();

                // IF AND IF, I CANT AUTO IT!
                // clear the wings to replace
                // Do it clear?
                if (wingList.isEmpty()) {
                    Global.getLogger(this.getClass()).info("== wingList is empty! ==");
                    NowWingman = (int) MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS_GET.size() - 1);

                    // add next
                    stats.getVariant().addMod(WINGMAN_MOD.get(NowWingman));
                    if (NowWingman == 1) {
                        stats.getVariant().removeMod(WINGMAN_MOD.get(0));
                        stats.getVariant().removeMod(WINGMAN_MOD.get(2));
                    }
                    if (NowWingman == 2) {
                        stats.getVariant().removeMod(WINGMAN_MOD.get(0));
                        stats.getVariant().removeMod(WINGMAN_MOD.get(1));
                    }
                    if (NowWingman == 0) {
                        stats.getVariant().removeMod(WINGMAN_MOD.get(1));
                        stats.getVariant().removeMod(WINGMAN_MOD.get(2));
                    }
                    String toInstallAllWingman = WINGMAN_WINGS.get(NowWingman);

                    wingList.clear();
                    Global.getLogger(this.getClass()).info("== before add ==" + wingList);
                    wingList.add(0, toInstallAllWingman);
                    Global.getLogger(this.getClass()).info("== add*1 ==" + wingList);
                    wingList.add(1, toInstallAllWingman);
                    Global.getLogger(this.getClass()).info("== add*2 ==" + wingList);
                } else {
                    // add next hullmod
                    stats.getVariant().addMod(WINGMAN_MOD.get(NowWingman));
                    Global.getLogger(this.getClass()).info("add next HullMods=" + WINGMAN_MOD.get(NowWingman));

                    // select and place those wings
                    String toInstallAllWingman = WINGMAN_WINGS.get(NowWingman);
                    wingList.set(0, toInstallAllWingman);
                    wingList.set(1, toInstallAllWingman);

                    // runOnceWing = true;
                    Global.getLogger(this.getClass()).info("==WING JOB HAS BEEN DONE==");
                    switchWingman = false;
                    I cant let it get in to an auto code, whatever. 
                     * if (FirWing != null && SecWing!= null) {
                     * //Normally this "if" and "else if" will be run when you change system to
                     * Wingman or change those Hullmods
                     * if (stats.getVariant().getHullMods().contains("SGB_Austenite_System_Wing")
                     * && WINGMAN_WINGS_GET.get(FirWing) !=null) {
                     * Global.getLogger(this.getClass()).info("==the 1 job==");
                     * NowWingman = WINGMAN_WINGS_GET.get(FirWing);
                     * Global.getLogger(this.getClass()).
                     * info("NowWingman(The number in WINGMAN_WINGS_GET for this wing)="+NowWingman)
                     * ;
                     * //stats.getVariant().addMod("SGB_Austenite_System_Wingman_CORED1");
                     * 
                     * }if (stats.getVariant().getHullMods().contains("SGB_Austenite_System_Wing")
                     * && WINGMAN_WINGS_GET.get(FirWing) ==null) {
                     * Global.getLogger(this.getClass()).info("==the 2 job==");
                     * NowWingman = MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS_GET.size() -
                     * 1);
                     * Global.getLogger(this.getClass()).
                     * info("NowWingman(The number in WINGMAN_WINGS_GET for this wing)="+NowWingman)
                     * ;
                     * 
                     * //stats.getVariant().addMod("SGB_Austenite_System_Wingman_CORED2");
                     * }
                     * //Mostly wont run "else"
                     * } else {
                     * Global.getLogger(this.getClass()).info("==the 3 job==");
                     * NowWingman = MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS_GET.size() -
                     * 1);
                     * Global.getLogger(this.getClass()).
                     * info("NowWingman(The number in WINGMAN_WINGS_GET for this wing)="+NowWingman)
                     * ;
                     * 
                     * //stats.getVariant().addMod("SGB_Austenite_System_Wingman_CORED3");
                     * }
                     * 
                     * //add next hullmod
                     * stats.getVariant().addMod(WINGMAN_MOD.get(NowWingman));
                     * Global.getLogger(this.getClass()).info("add next HullMods="+WINGMAN_MOD.get(
                     * NowWingman));
                     * 
                     * //clear the wings to replace
                     * stats.getVariant().getFittedWings().clear();
                     * Global.getLogger(this.getClass()).info("WingList(after clear FittedWings="
                     * +wingList);
                     * stats.getVariant().getWings().clear();
                     * Global.getLogger(this.getClass()).info("WingList(after clear Wings="+wingList
                     * );
                     * //select and place those wings
                     * String toInstallAllWingman = WINGMAN_WINGS.get(NowWingman);
                     * Global.getLogger(this.getClass()).
                     * info("toInstallAllWingman(Which Wing is now for the re-put)="
                     * +toInstallAllWingman);
                     * 
                     * wingList.set(0, toInstallAllWingman);
                     * Global.getLogger(this.getClass()).info("WingList(after add to 0 bar="
                     * +wingList);
                     * wingList.set(1, toInstallAllWingman);
                     * Global.getLogger(this.getClass()).info("WingList(after add to 1 bar="
                     * +wingList);
                     * for (int AllWings = 0; AllWings <
                     * Math.max(stats.getNumFighterBays().getModifiedInt()-1,0); AllWings++) {
                     * if (wingList.size() != 0) {
                     * wingList.set(AllWings, toInstallAllWingman);
                     * 
                     * Global.getLogger(this.getClass()).info("==the 4 job==");
                     * Global.getLogger(this.getClass()).
                     * info("AllWings(Which Bay is now for the re-put)="+AllWings);
                     * Global.getLogger(this.getClass()).
                     * info("toInstallAllWingman(Which Wing is now for the re-put)="
                     * +toInstallAllWingman);
                     * Global.getLogger(this.getClass()).info(
                     * "stats.getNumFighterBays().getModifiedInt-1=" +
                     * (stats.getNumFighterBays().getModifiedInt()-1));
                     * Global.getLogger(this.getClass()).info("WingList(after add to bar="+wingList)
                     * ;
                     * //NeedWingmanMod = false;
                     * stats.getVariant().addMod("SGB_Austenite_System_Wingman_CORED4");
                     * }
                     * //Mostly will run "else", but better still use "if" for careful
                     * else{
                     * wingList.add(AllWings, toInstallAllWingman);
                     * Global.getLogger(this.getClass()).info("==the 5 job==");
                     * stats.getVariant().addMod("SGB_Austenite_System_Wingman_CORED5");
                     * Global.getLogger(this.getClass()).info("WingList(after add to bar="+wingList)
                     * ;
                     * }
                     * }

                }
            }
            if (!runOnceWing && wingList.size() < 1
                    && stats.getVariant().getHullMods().contains("SGB_Austenite_System_Wing")) {

                NowWingman = MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS_GET.size() - 1);

                String toInstallAllWingman = WINGMAN_WINGS.get(NowWingman);
                stats.getVariant().addMod(WINGMAN_MOD.get(NowWingman));
                Global.getLogger(this.getClass()).info("add next HullMods=" + WINGMAN_MOD.get(NowWingman));
                wingList.clear();
                wingList.add(toInstallAllWingman);
                wingList.add(toInstallAllWingman);
            }
            Global.getLogger(this.getClass()).info("runOnceWing_Over=" + runOnceWing);
            Global.getLogger(this.getClass()).info("currWing="+stats.getVariant().getWings().toString());
        }
        Global.getLogger(this.getClass()).info("========================" + runOnceWing);*/

        //reworked wingman replacer
        if(stats.getVariant().hasHullMod(SYSTEM_MOD.get(3))){//should get wings
            int indexOfWing = 0;
            if(stats.getVariant().getWings().isEmpty()){//如果甲板是空的 那需要初始化 初始化可以随机也可以指定 不影响什么
                indexOfWing = MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS.size() - 1);
            }else{//这里我们使用第一个甲板来作为标记 玩家可能会手动卸掉 那就会进入下面的else部分——初始化一次
                String wingSymbol = stats.getVariant().getWings().get(0);
                if(WINGMAN_WINGS.contains(wingSymbol)){
                    indexOfWing = WINGMAN_WINGS.indexOf(wingSymbol);//直接获取index 这需要保证船插和联队的list顺序是对应的——并不是很难的事情 反正是我们自己填
                }else{
                    indexOfWing = MathUtils.getRandomNumberInRange(0, WINGMAN_WINGS.size() - 1);
                }
            }
            //现在我们获得了联队的index——接下来处理是否要替换成下一个——依据是否有对应的船插
            if(stats.getVariant().hasHullMod(WINGMAN_MOD.get(indexOfWing))){//有对应的船插 直接清理wings替换就好了
                stats.getVariant().getWings().clear();
                stats.getVariant().getWings().add(WINGMAN_WINGS.get(indexOfWing));
                stats.getVariant().getWings().add(WINGMAN_WINGS.get(indexOfWing));
                
            }else{//没有对应的船插 就给index+1 这时候可能会越界 于是要加入判断 把越界的重置回0 完成一次循环 这时候还需要加入对应的船插
                stats.getVariant().getWings().clear();
                indexOfWing++;
                if(indexOfWing>WINGMAN_WINGS.size()-1){
                    indexOfWing = 0;
                }
                stats.getVariant().getWings().add(WINGMAN_WINGS.get(indexOfWing));
                stats.getVariant().getWings().add(WINGMAN_WINGS.get(indexOfWing));
                stats.getVariant().addMod(WINGMAN_MOD.get(indexOfWing));
            }
            //清理掉可能多余的船插——因为我们是检测甲板1，如果玩家手欠把甲板1卸掉 就会随机 这时候不会移除原本的船插 所以我们在这里清理一遍
            for(int i = 0;i<WINGMAN_MOD.size();i++){
                if(i!=indexOfWing) stats.getVariant().removeMod(WINGMAN_MOD.get(i));
            }
        }
        // remove the weapons to change & swap the hullmod——L
        if (toSwitchL) {
            int selectedL;
            boolean randomL = false;
            if (stats.getVariant().getWeaponSpec(leftslotID) != null) {
                selectedL = SWITCH_TO_L.get(stats.getVariant().getWeaponSpec(leftslotID).getWeaponId());

            } else {
                selectedL = MathUtils.getRandomNumberInRange(0, SWITCH_TO_L.size() - 1);
                randomL = true;
            }

            // add next hullmod
            stats.getVariant().addMod(WEAPON_MOD_L.get(selectedL));

            // clear the weapons to replace
            stats.getVariant().clearSlot(leftslotID);
            // select and place that weapon
            String toInstallLeft = LEFT_WEAPON.get(selectedL);

            stats.getVariant().addWeapon(leftslotID, toInstallLeft);

            if (randomL) {
                stats.getVariant().autoGenerateWeaponGroups();
            }
        }

        // ——R
        if (toSwitchR) {
            int selectedR;
            boolean randomR = false;
            if (stats.getVariant().getWeaponSpec(rightslotID) != null) {
                selectedR = SWITCH_TO_R.get(stats.getVariant().getWeaponSpec(rightslotID).getWeaponId());

            } else {
                selectedR = MathUtils.getRandomNumberInRange(0, SWITCH_TO_R.size() - 1);
                randomR = true;
            }
            stats.getVariant().addMod(WEAPON_MOD_R.get(selectedR));

            stats.getVariant().clearSlot(rightslotID);

            String toInstallRight = RIGHT_WEAPON.get(selectedR);

            stats.getVariant().addWeapon(rightslotID, toInstallRight);

            if (randomR) {
                stats.getVariant().autoGenerateWeaponGroups();
            }
        }

        // ——WING
        if (toSwitchW) {
            int selected;
            boolean random = false;
            if (stats.getVariant().getWeaponSpec(lwingslotID) != null) {
                selected = SWITCH_TO_WING.get(stats.getVariant().getWeaponSpec(lwingslotID).getWeaponId());

            } else {
                selected = MathUtils.getRandomNumberInRange(0, SWITCH_TO_WING.size() - 1);
                // random=true;
            }

            // add the proper hullmod
            stats.getVariant().addMod(MAIN_WING_MOD.get(selected));

            // clear the weapons to replace
            stats.getVariant().clearSlot(lwingslotID);
            stats.getVariant().clearSlot(rwingslotID);
            // select and place the proper weapon
            String toInstallLeftWing = MAIN_WING_L.get(selected);
            String toInstallRightWing = MAIN_WING_R.get(selected);

            stats.getVariant().addWeapon(lwingslotID, toInstallLeftWing);
            stats.getVariant().addWeapon(rwingslotID, toInstallRightWing);

            // if(random){
            // stats.getVariant().autoGenerateWeaponGroups();
            // }
        }

        // ——ENGINE_WING
        if (toSwitchEW) {
            int selected;
            boolean random = false;
            if (stats.getVariant().getWeaponSpec(lEnginewingslotID) != null) {
                selected = SWITCH_TO_ENGINE_WING.get(stats.getVariant().getWeaponSpec(lEnginewingslotID).getWeaponId());

            } else {
                selected = MathUtils.getRandomNumberInRange(0, SWITCH_TO_ENGINE_WING.size() - 1);
                // random=true;
            }

            // add the proper hullmod
            stats.getVariant().addMod(ENGINE_WING_MOD.get(selected));

            // clear the weapons to replace
            stats.getVariant().clearSlot(lEnginewingslotID);
            stats.getVariant().clearSlot(rEnginewingslotID);
            // select and place the proper weapon
            String toInstallLeftWing = ENGINE_WING_L.get(selected);
            String toInstallRightWing = ENGINE_WING_R.get(selected);

            stats.getVariant().addWeapon(lEnginewingslotID, toInstallLeftWing);
            stats.getVariant().addWeapon(rEnginewingslotID, toInstallRightWing);
        }

        // ——SYSTEM
        /*
         * if (toSwitchS) {
         * int selectedS;
         * if (stats.getVariant().getWeaponSpec(systemslotID) != null) {
         * selectedS =
         * SWITCH_TO_SYSTEM.get(stats.getVariant().getWeaponSpec(systemslotID).
         * getWeaponId());
         * } else {
         * selectedS = MathUtils.getRandomNumberInRange(0, SWITCH_TO_SYSTEM.size() - 1);
         * }
         * stats.getVariant().addMod(SYSTEM_MOD.get(selectedS));
         * stats.getVariant().clearSlot(systemslotID);
         * 
         * String toInstallTop = SYSTEM.get(selectedS);
         * 
         * stats.getVariant().addWeapon(systemslotID, toInstallTop);
         * }
         */
        // ——HEAD
        if (toSwitchH) {
            int selectedH;
            if (stats.getVariant().getWeaponSpec(headslotID) != null) {
                selectedH = SWITCH_TO_HEAD.get(stats.getVariant().getWeaponSpec(headslotID).getWeaponId());
            } else {
                selectedH = MathUtils.getRandomNumberInRange(0, SWITCH_TO_HEAD.size() - 1);
            }
            stats.getVariant().addMod(HEAD_MOD.get(selectedH));

            stats.getVariant().clearSlot(headslotID);

            String toInstallHead = HEAD.get(selectedH);

            stats.getVariant().addWeapon(headslotID, toInstallHead);
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        // only check for undo in refit to avoid issues
        if (ship.getOriginalOwner() < 0) {
            // undo fix for harvests put in cargo
            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getCargo() != null &&
                    Global.getSector().getPlayerFleet().getCargo().getStacksCopy() != null &&
                    !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()) {
                for (CargoStackAPI s : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
                    if (s.isWeaponStack() ){
                        if (LEFT_WEAPON.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                RIGHT_WEAPON.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                MAIN_WING_L.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                MAIN_WING_R.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                ENGINE_WING_L.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                ENGINE_WING_R.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                // ENGINE_WING_MOD.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                // SYSTEM_MOD.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) ||
                                HEAD.containsValue(s.getWeaponSpecIfWeapon().getWeaponId())){
                            Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                        }
                    }
                    if (s.isFighterWingStack() ){
                        if (WINGMAN_WINGS.contains(s.getFighterWingSpecIfWing().getId())){
                            Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                        }
                    }
                    /*if (s.isWeaponStack()){
                        WeaponSpecAPI spec = s.getWeaponSpecIfWeapon();
                        if(spec!=null){
                            String ids = spec.getWeaponId();
                            if(LEFT_WEAPON.containsValue(ids) ||
                                    RIGHT_WEAPON.containsValue(ids) ||
                                    MAIN_WING_L.containsValue(ids) ||
                                    MAIN_WING_R.containsValue(ids) ||
                                    WINGMAN_WINGS.contains(s.getFighterWingSpecIfWing().getId()) ||
                                    ENGINE_WING_L.containsValue(ids) ||
                                    ENGINE_WING_R.containsValue(ids) ||
                                    // ENGINE_WING_MOD.containsValue(ids) ||
                                    // SYSTEM_MOD.containsValue(ids) ||
                                    HEAD.containsValue(ids)) {
                                Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                            }
                        }
                    }*/
                }
            }
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0)
            return Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName();
        if (index == 1)
            return Global.getSettings().getHullModSpec("unstable_injector").getDisplayName();
        if (index == 2)
            return Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName();
        if (index == 3)
            return "" + (int) (THIS_IS_ONE * 30f) + "%";
        if (index == 4)
            return "" + (int) (THIS_IS_ONE * 20f) + "%";
        if (index == 5)
            return Global.getSettings().getHullModSpec("unstable_injector").getDisplayName();
        if (index == 6)
            return "" + (int) (THIS_IS_ONE * 100f) + "%";
        if (index == 7)
            return "" + (int) (THIS_IS_ONE * 20f) + "%";
        return null;
    }

    // 更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width,
            boolean isForModSpec) {
        tooltip.addSectionHeading(txt("HullMods_Remarks"), Alignment.TMID, 5f);
        tooltip.addPara("此插件为 奥氏体 的核心插件。", SGB_Color.SGBhardWord, 4f);
        tooltip.addPara("奥氏体 - 是 第二十七作战群 的'特种战机'之一，但实际上奥氏体-级已经被划分为了护卫舰，毕竟大多数的航母并不能很好的搭载这艘战机；", SGB_Color.SGBhardWord,
                4f);
        tooltip.addPara("奥氏体具有非常独特的环境适应模块，使得它能在大多数极端环境中执行任务；", SGB_Color.SGBhardWord, 4f);
        tooltip.addPara("它那独特的专属内置武器模块是许多链戍舰长的最爱。", SGB_Color.SGBhardWord, 4f);
        tooltip.addSectionHeading(txt("HullMods_Tips"), Alignment.MID, 5f);
        tooltip.addPara(
                "奥氏体的设计时间相当早，并且曾经在第二十七作战群中大量部署，但其大多数蓝图已经在漫长的历史中遗失；由于链戍的自甘堕落，仅剩的一点蓝图也都不知沦落到何处，现如今能出现在大众眼中的奥氏体已经少之又少。",
                SGB_Color.SGBcoreIntersting_Word, 4f);
        tooltip.addPara("而你现在手上就有一个！", SGB_Color.SGBcoreIntersting_Word, 4f);
        tooltip.addPara("提示：由于技术力原因，在奥氏体存在D插时，请不要高频率使用内置的插件更换，这有可能造成游戏弹出。这个bug会尽可能尽快修复。", SGB_Color.SGBcoreDanger_Word, 5f);
    }
}
