package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.hullmods.HighScatterAmp;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.sgb.SGB_Color;

import java.awt.*;
import java.util.List;

import static data.utils.sgb.SGB_stringsManager.txt;
public class SGB_Achilles_FrontTurret extends BaseHullMod {
    private boolean runOnce=false;
    private static float rangeMore = 200f;
    private float rangeMult = 20f;
    private float recoMult = 15f;private float range;
    WeaponAPI FrontTurret;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new SGB_Achilles_FrontTurret_Range());
    }

    public static class SGB_Achilles_FrontTurret_Range implements WeaponBaseRangeModifier {
        public SGB_Achilles_FrontTurret_Range() {
        }
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getShip() == ship && weapon.getSlot().getId().contains("WS0010")) {
                return rangeMore;
            }
            return 0f;
        }
    }
/*
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        if (!runOnce && ship!=null) {
            List<WeaponAPI> weapons = ship.getAllWeapons();
            for (WeaponAPI w : weapons) {
                switch (w.getSlot().getId()) {
                    case "WS0010":
                        FrontTurret = w;
                        break;
                }
            }
        }
        range = FrontTurret.getOriginalSpec().getMaxRange();
        if(FrontTurret != null &&
                FrontTurret.getId() != null &&
                range != 0){
            FrontTurret.getSpec().setMaxRange(range*(1+(rangeMult/100)));
            //FrontTurret.getSpec().setSpreadBuildup(range*(1-(recoMult/100)));
        }
    }

 */

    @Override
    public int getDisplaySortOrder() {
        return 2000;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 3;
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return (int)rangeMore + "";
        //if (index == 1) return recoMult + "%";
        return null;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading(txt("HullMods_Remarks"), Alignment.TMID, 5f);
        tooltip.addPara(txt("HullMods_SGB_Achilles_FrontTurret1"), SGB_Color.SGBhardWord, 4f);
        tooltip.addPara(txt("HullMods_SGB_Achilles_FrontTurret2"), SGB_Color.SGBhardWord, 4f);
    }
    @Override
    public Color getBorderColor() {
        return new Color(255, 202, 12, 255);
    }

    @Override
    public Color getNameColor() {
        return new Color(213, 194, 65, 255);
    }
}
