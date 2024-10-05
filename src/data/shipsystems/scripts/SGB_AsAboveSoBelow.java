package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.util.MagicLensFlare;
import data.utils.sgb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static com.fs.starfarer.api.impl.combat.TargetingFeedStats.KEY_JITTER;
import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_AsAboveSoBelow extends BaseShipSystemScript {

	public static final float ROF_BONUS = 4f;
	public static final float FLUX_REDUCTION = 100f;
	public static final float SPEED_ADD_PERCENT = 100f;
	private boolean runOnce=false;
	private boolean runOnceWith_Shake=false;
	private ShipEngineControllerAPI engines;
	private ShipAPI ship;
	private static final Color COLOR_BASE1 = new Color(255, 253, 147, 150);
	private static final Color COLOR_BASE2 = new Color(255, 228, 169, 170);
	private Color CoreColor = new Color(255, 235, 160, 155);
	WeaponAPI F1;WeaponAPI F2;WeaponAPI F3;WeaponAPI F4;WeaponAPI F5;WeaponAPI F6;
	WeaponAPI FL;WeaponAPI FR;WeaponAPI BL;WeaponAPI BR;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		//探测武器
		if (!runOnce || ship==null) {
			ship = (ShipAPI) stats.getEntity();
			engines = ship.getEngineController();

			List<WeaponAPI> weapons = ship.getAllWeapons();
			for (WeaponAPI w : weapons) {
				switch (w.getSlot().getId()) {
					case "WS0002":
						FL = w;
						break;
					case "WS0003":
						FR = w;
						break;
					case "WS0004":
						BL = w;
						break;
					case "WS0005":
						BR = w;
						break;
					case "VENT1":
						F1 = w;
						break;
					case "VENT2":
						F2 = w;
						break;
					case "VENT3":
						F3 = w;
						break;
					case "VENT4":
						F4 = w;
						break;
					case "VENT5":
						F5 = w;
						break;
					case "VENT6":
						F6 = w;
						break;
				}
			}
			runOnce=true;
			return;
		}
		if(Global.getCombatEngine() != null) {
			CombatEngineAPI engine = Global.getCombatEngine();
			DecWeaponFlux(FL, F1, engine,effectLevel,120);
			DecWeaponFlux(FR, F2, engine,effectLevel,120);
			DecWeaponFlux(BL, F5, engine,effectLevel,225);
			DecWeaponFlux(BR, F6, engine,effectLevel,225);

			DecWeaponFluxWithTwo(FL,BL, F3, engine,effectLevel,350);
			DecWeaponFluxWithTwo(FR,BR, F4, engine,effectLevel,350);
		}

		ship.setJitterUnder(KEY_JITTER, getRandomBlendColor(COLOR_BASE1,COLOR_BASE2), 1, 25, 4);
		float mult = (1f + ROF_BONUS) * effectLevel;
		stats.getBallisticRoFMult().modifyMult(id, mult);
		stats.getEnergyRoFMult().modifyMult(id, mult);

		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));
		stats.getMissileWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));

		stats.getMaxSpeed().modifyFlat(id, 50f * effectLevel);
		stats.getAcceleration().modifyFlat(id, 85f * effectLevel);

		if (stats.getEntity() instanceof ShipAPI && false) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			String key = ship.getId() + "_" + id;
			Object test = Global.getCombatEngine().getCustomData().get(key);
			if (state == State.IN) {
				if (test == null && effectLevel > 0.2f) {
					Global.getCombatEngine().getCustomData().put(key, new Object());
					ship.getEngineController().getExtendLengthFraction().advance(1f);
					for (ShipEngineControllerAPI.ShipEngineAPI engine : ship.getEngineController().getShipEngines()) {
						if (engine.isSystemActivated()) {
							ship.getEngineController().setFlameLevel(engine.getEngineSlot(), 1f*effectLevel);
						}
					}
				}
			} else {
				Global.getCombatEngine().getCustomData().remove(key);
			}
		}

		if (!runOnceWith_Shake || ship==null) {

		}
		
//		ShipAPI ship = (ShipAPI)stats.getEntity();
//		ship.blockCommandForOneFrame(ShipCommand.FIRE);
//		ship.setHoldFireOneFrame(true);
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getEnergyWeaponFluxCostMod().unmodify(id);
		stats.getMissileWeaponFluxCostMod().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = 1f + ROF_BONUS * effectLevel;
		float bonusPercent = (int) ((mult - 1f) * 100f);
		if (index == 0) {
			return new StatusData(txt("SGB_AsAboveSoBelow_WeaponSpeed") + (int) bonusPercent + "%", false);
		}
		if (index == 1) {
			return new StatusData(txt("SGB_AsAboveSoBelow_FluxSpeed") + (int) FLUX_REDUCTION + "%", false);
		}
		if (index == 2) {
			return new StatusData(txt("SGB_AsAboveSoBelow_EngineSpeed"), false);
		}
		return null;
	}

	public static final Random RANDOM = new Random();

	public void DecWeaponFlux(WeaponAPI mainWeapon, WeaponAPI FluxWeapon, CombatEngineAPI engine, float level, float range) {
		if(mainWeapon != null){
			if(mainWeapon.isFiring()){
				Vector2f FluxTarget = new Vector2f(FluxWeapon.getLocation());
				Vector2f TargetTo = new Vector2f(level * 16f, 0);
				Vector2f Speed = new Vector2f(level * 60f, 0);
				TargetTo = VectorUtils.rotate(TargetTo, FluxWeapon.getCurrAngle());
				FluxTarget = new Vector2f(TargetTo.x+FluxTarget.x,TargetTo.y+FluxTarget.y);
				Speed = VectorUtils.rotate(Speed, FluxWeapon.getCurrAngle());

				range = MathUtils.getRandomNumberInRange(75,range);
				Vector2f empEndPozn = new Vector2f(new Vector2f(level * range, 0));
				empEndPozn = VectorUtils.rotate(empEndPozn, FluxWeapon.getCurrAngle());
				empEndPozn = new Vector2f(empEndPozn.x+FluxTarget.x,empEndPozn.y+FluxTarget.y);
				empEndPozn = new Vector2f(MathUtils.getRandomPointOnCircumference(empEndPozn,275f));

				//Vector2f empEndPozn = new Vector2f(MathUtils.getRandomPointInCircle(FluxWeapon.getLocation(),MathUtils.getRandomNumberInRange(50f,200f)));

				if(mainWeapon.getSpec().getGlowColor() != null) {
					CoreColor = getRandomBlendColor(mainWeapon.getSpec().getGlowColor(),COLOR_BASE1);
				}
				if(Math.random()<=0.05){
					MagicLensFlare.createSmoothFlare(engine, FluxWeapon.getShip(), FluxTarget, 10, 100, 0,
							new Color(CoreColor.getRed(),CoreColor.getGreen(),CoreColor.getBlue(),100),COLOR_BASE1);
					engine.spawnEmpArcVisual(FluxTarget,FluxWeapon.getShip(),empEndPozn,FluxWeapon.getShip(), MathUtils.getRandomNumberInRange(5f, (float) (15+(Math.random()*10f))), CoreColor,COLOR_BASE1);
				}
				if(Math.random()>=0.95){
					engine.addNebulaParticle(
							FluxTarget,
							Speed,
							MathUtils.getRandomNumberInRange(20,50f)*level,
							0.5F,
							0.25F,
							MathUtils.getRandomNumberInRange(0.6F, 1.0F) * level,
							0.15f,
							new Color(CoreColor.getRed(),CoreColor.getGreen(),CoreColor.getBlue(),255));

					engine.addSmoothParticle(
							FluxTarget,
							Speed,
							60f,
							2f,
							0.15f,
							CoreColor);
					//engine.spawnEmpArcVisual(FluxTarget,FluxWeapon.getShip(),empEndPozn,FluxWeapon.getShip(), MathUtils.getRandomNumberInRange(20f,50f), CoreColor,COLOR_BASE1);
				}

			}
		}
	}

	public void DecWeaponFluxWithTwo(WeaponAPI mainWeapon, WeaponAPI secWeapon, WeaponAPI FluxWeapon, CombatEngineAPI engine, float level, float range) {
		if(mainWeapon != null && secWeapon != null){
			if(mainWeapon.isFiring() && secWeapon.isFiring()){
				Vector2f FluxTarget = new Vector2f(FluxWeapon.getLocation());
				Vector2f TargetTo = new Vector2f(level * 16f, 0);
				Vector2f Speed = new Vector2f(level * 120f, 0);
				TargetTo = VectorUtils.rotate(TargetTo, FluxWeapon.getCurrAngle());
				FluxTarget = new Vector2f(TargetTo.x+FluxTarget.x,TargetTo.y+FluxTarget.y);
				Speed = VectorUtils.rotate(Speed, FluxWeapon.getCurrAngle());

				range = MathUtils.getRandomNumberInRange(75,range);
				Vector2f empEndPozn = new Vector2f(new Vector2f(level * range, 0));
				empEndPozn = VectorUtils.rotate(empEndPozn, FluxWeapon.getCurrAngle());
				empEndPozn = new Vector2f(empEndPozn.x+FluxTarget.x,empEndPozn.y+FluxTarget.y);
				empEndPozn = new Vector2f(MathUtils.getRandomPointOnCircumference(empEndPozn,275f));


				//Vector2f empEndPozn = new Vector2f(MathUtils.getRandomPointInCircle(FluxWeapon.getLocation(),MathUtils.getRandomNumberInRange(100f,300f)));
				if(mainWeapon.getSpec().getGlowColor() != null && secWeapon.getSpec().getGlowColor() != null) {
					CoreColor = getRandomBlendColor(mainWeapon.getSpec().getGlowColor(),COLOR_BASE1);
					CoreColor = getRandomBlendColor(CoreColor,secWeapon.getSpec().getGlowColor());
				}
				if(Math.random()<=0.05){
					MagicLensFlare.createSmoothFlare(engine, FluxWeapon.getShip(), FluxTarget, 10, 100, 0,
							new Color(CoreColor.getRed(),CoreColor.getGreen(),CoreColor.getBlue(),100),COLOR_BASE1);
					engine.spawnEmpArcVisual(FluxTarget,FluxWeapon.getShip(),empEndPozn,FluxWeapon.getShip(), MathUtils.getRandomNumberInRange(5f, (float) (15+(Math.random()*10f))), CoreColor,COLOR_BASE1);
				}
				if(Math.random()>=0.95){
					engine.addNebulaParticle(
							FluxTarget,
							Speed,
							MathUtils.getRandomNumberInRange(40,70f)*level,
							0.5F,
							0.25F,
							MathUtils.getRandomNumberInRange(0.6F, 1.0F) * level,
							0.15f,
							new Color(CoreColor.getRed(),CoreColor.getGreen(),CoreColor.getBlue(),255));
					engine.addSmoothParticle(
							FluxTarget,
							I18nUtil.nv,
							60f,
							2f,
							0.15f,
							CoreColor);
					//engine.spawnEmpArcVisual(FluxTarget,FluxWeapon.getShip(),empEndPozn,FluxWeapon.getShip(), MathUtils.getRandomNumberInRange(20f,50f), CoreColor,COLOR_BASE1);
				}

			}
		}
	}
	public static Color colorBlend(Color a, Color b, float amount) {
		float conjAmount = 1f - amount;
		return new Color((int) Math.max(0, Math.min(255, a.getRed() * conjAmount + b.getRed() * amount)),
				(int) Math.max(0, Math.min(255, a.getGreen() * conjAmount + b.getGreen() * amount)),
				(int) Math.max(0, Math.min(255, a.getBlue() * conjAmount + b.getBlue() * amount)),
				(int) Math.max(0, Math.min(255, a.getAlpha() * conjAmount + b.getAlpha() * amount)));
	}
	public static Color getRandomBlendColor(Color a, Color b) {
		return colorBlend(a, b, RANDOM.nextFloat());
	}

}
