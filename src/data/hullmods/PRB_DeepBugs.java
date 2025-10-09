package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.sgb.SGB_BoxBasedUtil;
import data.utils.sgb.SGB_Color;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static data.utils.sgb.SGB_stringsManager.txt;

public class PRB_DeepBugs extends BaseHullMod {
	public PRB_DeepBugs() {
	}
	public static final String PRB = "PRB";
	private static Map Recover = new HashMap(); //不同船型
	private static Map ZERO_FLUX_SGB = new HashMap();
	/**
	 * This is the center hullmod for PRB.
	 */

	//private static final float SPEED_BONUS = 20f;
	static {
		Recover.put(HullSize.FIGHTER, 5f);
		Recover.put(HullSize.FRIGATE, 10f);
		Recover.put(HullSize.DESTROYER, 25f);
		Recover.put(HullSize.CRUISER, 30f);
		Recover.put(HullSize.CAPITAL_SHIP, 50f);

		ZERO_FLUX_SGB.put(HullSize.FIGHTER, 1f);
		ZERO_FLUX_SGB.put(HullSize.FRIGATE, 1f);
		ZERO_FLUX_SGB.put(HullSize.DESTROYER, 2f);
		ZERO_FLUX_SGB.put(HullSize.CRUISER, 3f);
		ZERO_FLUX_SGB.put(HullSize.CAPITAL_SHIP, 4f);
	}
	float FIFTEN = 50;
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//ShipAPI ship = (ShipAPI) stats.getEntity();
		stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(id, (Float) Recover.get(hullSize));

	}
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		if(ship == null) return;
		MutableShipStatsAPI stats = ship.getMutableStats();
		HullSize hullSize = ship.getHullSize();
		String id =ship.getId();
		String idC =ship.getId()+"in_Gombat";
		if (ship.getHullLevel()<0.5f) {
			stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(idC, -(Float) Recover.get(hullSize)/2f);
		}
		else{
			stats.getHullCombatRepairRatePercentPerSecond().unmodify(idC);
		}
		if(ship.getFluxTracker().isOverloaded()){
			if(SGB_BoxBasedUtil.hadBoxUtil()) {
				SGB_BoxBasedUtil.addDistortion(ship.getLocation(), ship.getCollisionRadius(),
						ship.getFluxLevel(),
						ship.getFluxLevel()/4,
						ship.getFluxTracker().getTimeToVent() / 4 * 1,
						ship.getFluxTracker().getTimeToVent() / 4 * 3 * 0.25f,
						ship.getFluxTracker().getTimeToVent() / 4 * 3 * 0.75f);
			}
			ship.setJitter(ship,ship.getVentFringeColor(),0.71f,5,8f);
		}
		else if(ship.getFluxTracker().isVenting()){
			if(SGB_BoxBasedUtil.hadBoxUtil()) {
				SGB_BoxBasedUtil.addDistortion(ship.getLocation(), ship.getCollisionRadius(),
						ship.getFluxLevel(),
						ship.getFluxLevel() / 2f,
						ship.getFluxTracker().getTimeToVent() / 4 * 3,
						ship.getFluxTracker().getTimeToVent() / 4 * 1 * 0.25f,
						ship.getFluxTracker().getTimeToVent() / 4 * 1 * 0.75f);
			}
			ship.setJitter(ship,ship.getVentFringeColor(),
					(float) Math.min(1.0,ship.getFluxTracker().getTimeToVent()),2,3f);
		}

	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		//if (index == 1) return "" + (int) Math.round((CAPACITY_MULT - 1f) * 100f) + "%";
		if (index == 0) return "" + ((Float) Recover.get(HullSize.FRIGATE)).intValue();
		if (index == 1) return "" + ((Float) Recover.get(HullSize.DESTROYER)).intValue();
		if (index == 2) return "" + ((Float) Recover.get(HullSize.CRUISER)).intValue();
		if (index == 3) return "" + ((Float) Recover.get(HullSize.CAPITAL_SHIP)).intValue();
		//if (index == 4) return "" + (int) FIFTEN + "%";
		return null;
	}

	//更多的描述拓展
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

		float pad = 10f;
		TooltipMakerAPI text;


		tooltip.addSectionHeading(txt("HullMods_Remarks"), Alignment.TMID, 5f);
		text = tooltip.beginImageWithText("graphics/hullmods/SGB_AllForHuman.png", 32);
		tooltip.addImageWithText(pad);
		tooltip.addPara(txt("HullMods_AllForHuman1"), SGB_Color.SGBhardWord, 4f);
		tooltip.addPara(txt("HullMods_AllForHuman2"), SGB_Color.SGBhardWord, 4f);
		tooltip.addPara(txt("HullMods_AllForHuman3"), SGB_Color.SGBhardWord, 4f);
		//tooltip.addPara("其加速效果必须在零赋能状态下！", SGB_Color.SGBcoreDanger_Word, 4f);
		tooltip.addSectionHeading(txt("HullMods_Tips"), Alignment.MID, 5f);
		tooltip.addPara(txt("HullMods_AllForHuman4"), SGB_Color.SGBcoreIntersting_Word, 4f);
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
