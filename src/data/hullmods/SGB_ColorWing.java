package data.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.sgb.SGB_Color;
import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_ColorWing extends BaseHullMod {

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if(ship != null) {
            if (ship.isFighter()) {
                if (ship.isWingLeader()) {
                    ship.getMutableStats().getHullBonus().modifyFlat(id, 50f);
                    ship.getMutableStats().getArmorBonus().modifyFlat(id, 10f);
                }
            }
        }
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if(ship != null){
            if(ship.isFighter()){
                if(ship.isWingLeader()){
                    //ship.getSpriteAPI().
                    String normalFighter_ID = ship.getHullSpec().getBaseHullId();
                    String leadFighter_ID = normalFighter_ID + "_Leader";
                    ship.setSprite("SGB_WingColor",leadFighter_ID);
                }
            }
        }
    }
    @Override
    public int getDisplaySortOrder() {
        return 2000;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 3;
    }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
    }
}
