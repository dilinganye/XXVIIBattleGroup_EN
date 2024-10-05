package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.LazyLib;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;

import java.awt.*;

import static data.utils.sgb.I18nUtil.easyRippleOut;
import static data.utils.sgb.I18nUtil.easyWaving;

public class SGB_Temperom_Beam_Effect implements BeamEffectPlugin {

	private IntervalUtil fireInterval = new IntervalUtil(0.2f, 0.3f);
	public static boolean shieldHit(BeamAPI beam, ShipAPI target) {
		return target.getShield() != null && target.getShield().isOn() && target.getShield().isWithinArc(beam.getTo());
	}
	private boolean wasZero = true, light=false, runOnce=false;
	Vector2f ZERO = new Vector2f(0,0);

	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if(engine.isPaused()){return;}
		if(!runOnce){
			runOnce=true;
			if(Global.getSettings().getModManager().isModEnabled("shaderLib")){
				light=true;
			}
		}
		Boolean RandomTrue = true; if(Math.random()>=0.5){RandomTrue=false;}

		CombatEntityAPI target = beam.getDamageTarget();
		ShipAPI self = beam.getSource();
		Float getNum = (float) Math.random();
		float durEffect = beam.getDamage().getDpsDuration();
		if (target instanceof ShipAPI && beam.getBrightness() <= 0.5f) {
			if (!wasZero) durEffect = 0;
			wasZero = beam.getDamage().getDpsDuration() <= 0;
			fireInterval.advance(durEffect);
			if (fireInterval.intervalElapsed()) {
				if (getNum > 0.65) {
					MagicRender.battlespace(
							Global.getSettings().getSprite("fx", "SGB_smoke_1"),
							beam.getFrom(),
							ZERO,
							new Vector2f(60, 60),
							new Vector2f(30, 30),
							2 * (float) Math.random() + 90f,
							75 * (float) Math.random()+15,
							new Color(125, 204, 103, 175),
							RandomTrue,
							1f,
							0.05f,
							0.1f
					);
				}
				if (getNum < 0.35) {
					MagicRender.battlespace(
							Global.getSettings().getSprite("fx", "SGB_smoke_2"),
							beam.getFrom(),
							ZERO,
							new Vector2f(70, 70),
							new Vector2f(20, 20),
							2 * (float) Math.random() + 90f,
							75 * (float) Math.random()+15,
							new Color(125, 204, 103, 175),
							RandomTrue,
							1f,
							0.05f,
							0.1f
					);
				}
			}
		}
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
			float dur = beam.getDamage().getDpsDuration();
			self.getMutableStats().getMaxTurnRate().modifyMult("SGB_Temperom_Beam_Effect", 0f);
			// needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
			if (!wasZero) dur = 0;
			wasZero = beam.getDamage().getDpsDuration() <= 0;
			fireInterval.advance(dur);
			if (fireInterval.intervalElapsed()) {
				ShipAPI ship = (ShipAPI) target;

				if(light){
					easyWaving(
							beam.getTo(),
							ZERO,
							90,
							2,
							0
					);
				}

				MagicRender.battlespace(
						Global.getSettings().getSprite("fx", "SGB_Spark01A"),
						beam.getTo(),
						ZERO,
						new Vector2f(90, 120),
						new Vector2f(100, 150),
						2 * (float) Math.random() + 90f,
						75 * (float) Math.random()+15,
						new Color(125, 204, 103, 175),
						true,
						0f,
						0.1f,
						0.05f
				);

			}
		}
		else{
			self.getMutableStats().getMaxTurnRate().unmodify();
		}


		if (target instanceof ShipAPI) {
			ShipAPI theTarget = (ShipAPI)target;
			fireInterval.advance(amount * beam.getBrightness());
			if (beam.didDamageThisFrame()) {
				float damage = beam.getDamage().getDpsDuration() * beam.getDamage().getDamage();
				/*
				if (beam.getWeapon().getSize() == WeaponAPI.WeaponSize.SMALL) {
					damage *= 0.75f;
				}

				if (beam.getWeapon().getSize() == WeaponAPI.WeaponSize.MEDIUM) {
					damage *= 1f;
				}

				else if (beam.getWeapon().getSize() == WeaponAPI.WeaponSize.LARGE) {
					damage *= 1.25f;
				}
				*/
				if (shieldHit(beam, theTarget)) {	// 算武器的实际伤害
					if(beam.getDamage().getType().equals("KINETIC")){
						damage*= 2f;
					}
					if(beam.getDamage().getType().equals("HIGH_EXPLOSIVE")){
						damage*= 0.5f;
					}
					if(beam.getDamage().getType().equals("FRAGMENTATION")){
						damage*= 0.25f;
					}

					damage = damage * theTarget.getShield().getFluxPerPointOfDamage();	// 盾效
					//if(theTarget.getCurrFlux()>=damage) {	}
					//	theTarget.getFluxTracker().increaseFlux(-damage, false);
					//  damage = damage * MathUtils.getRandomNumberInRange(0.5f, 1.5f);
					//  theTarget.getFluxTracker().increaseFlux(damage, true);

					beam.getDamage().setForceHardFlux(true);
					damage*=  MathUtils.getRandomNumberInRange(-0.25f, 0.25f);
					theTarget.getFluxTracker().increaseFlux(damage, true);
				}
			}
		}
//			Global.getSoundPlayer().playLoop("system_emp_emitter_loop", 
//											 beam.getDamageTarget(), 1.5f, beam.getBrightness() * 0.5f,
//											 beam.getTo(), new Vector2f());
	}
}

