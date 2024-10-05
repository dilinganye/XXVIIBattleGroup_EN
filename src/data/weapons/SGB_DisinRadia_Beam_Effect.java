package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.shipsystems.scripts.SGB_AsAboveSoBelow.getRandomBlendColor;

public class SGB_DisinRadia_Beam_Effect implements BeamEffectPlugin {
				//com.fs.starfarer.api.impl.combat.IonBeamEffect
	private final IntervalUtil fireInterval = new IntervalUtil(0.2f, 0.3f),
			hitPInterval = new IntervalUtil(0.07f, 0.2f);

	public static boolean shieldHit(BeamAPI beam, ShipAPI target) {
		return target.getShield() != null && target.getShield().isOn() && target.getShield().isWithinArc(beam.getTo());
	}
	
	@Override
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		CombatEntityAPI target = beam.getDamageTarget();
		if (engine != null &&
				target != null &&
				target instanceof ShipAPI &&
				beam.getBrightness() >= 1f
		) {
			ShipAPI theTarget = (ShipAPI)target;
				//fireInterval.advance(amount * beam.getBrightness());
				// 		if (fireInterval.intervalElapsed()) {
				// 			Vector2f end = MathUtils.getRandomPointOnCircumference(beam.getRayEndPrevFrame(), 50f + (float) Math.random() * 10f);
				// 			engine.spawnEmpArcVisual(beam.getRayEndPrevFrame(), beam.getSource(), end, null, 15f, IIRT_Color.IIRTcorePurple_Light, IIRT_Color.IIRTcorePurple);
				// 	}

			if (beam.didDamageThisFrame()) {
				float damage = beam.getDamage().getDamage() * beam.getDamage().getDpsDuration();

				if (!shieldHit(beam, theTarget)){
					if(theTarget.getHitpoints() >= Math.min(1000,theTarget.getMaxHitpoints()/4f)) {
						theTarget.setHitpoints(theTarget.getHitpoints() - damage);

						engine.addFloatingDamageText(theTarget.getLocation(), damage,
								new Color(200, 231, 232, 255), target, target);

						//_______Particle EFFECT______
						hitPInterval.advance(amount * beam.getBrightness());
						if (hitPInterval.intervalElapsed()) {
							engine.addHitParticle(beam.getTo(), theTarget.getVelocity(),
									0 + MathUtils.getRandomNumberInRange(30f, 40f), 1f,
									0 + MathUtils.getRandomNumberInRange(0.35f, 0.5f),
									0 + MathUtils.getRandomNumberInRange(0.9f, 1.2f),
									getRandomBlendColor(beam.getFringeColor(), beam.getFringeColor())
							);

							Vector2f muzzle = beam.getTo();
							for (int i = 0; i < MathUtils.getRandomNumberInRange(3, 8); i++) {
								MagicRender.battlespace(
										Global.getSettings().getSprite("fx", "SGB_Va_Smoke"),
										muzzle,
										MathUtils.getRandomPointInCircle(theTarget.getVelocity(), 10f),
										new Vector2f(33, 33),
										new Vector2f(55, 55),
										//angle,
										360 * (float) Math.random(),
										0,
										new Color(beam.getFringeColor().getRed()
												, beam.getFringeColor().getGreen()
												, beam.getFringeColor().getBlue()
												, 47
										),
										false,
										0.1f,
										1f,
										1f
								);
							}
							MagicRender.battlespace(
									Global.getSettings().getSprite("fx", "SGB_Va_Smoke"),
									muzzle,
									MathUtils.getRandomPointInCircle(theTarget.getVelocity(), 10f),
									new Vector2f(40, 40),
									new Vector2f(80, 80),
									//angle,
									360 * (float) Math.random(),
									0,
									new Color(beam.getFringeColor().getRed()
											, beam.getFringeColor().getGreen()
											, beam.getFringeColor().getBlue()
											, 47
									),
									true,
									0.1f,
									1f,
									1f
							);
						}
					}
					else {
						//	直接进行一个炸死
						DamagingExplosionSpec KILL = new DamagingExplosionSpec(
								1f,
								50,
								40,
								Math.min(500,theTarget.getMaxHitpoints()/4f)+500,
								4000,
								CollisionClass.PROJECTILE_NO_FF,
								CollisionClass.PROJECTILE_FIGHTER,
								2,
								5,
								5,
								25,
								beam.getFringeColor(),
								beam.getCoreColor()
						);
						KILL.setDamageType(DamageType.HIGH_EXPLOSIVE);
						KILL.setShowGraphic(false);
						KILL.setSoundSetId("cryoflamer_hit_heavy");
						//KILL.setSoundSetId("rifttorpedo_explosion");
						engine.spawnDamagingExplosion(KILL, beam.getSource(), beam.getTo());

						//____________________Bombed EFFECT_________________
						Vector2f muzzle = beam.getTo();
						for (int i = 0; i < MathUtils.getRandomNumberInRange(4, 8); i++) {
							MagicRender.battlespace(
									Global.getSettings().getSprite("fx", "SGB_Va_Smoke"),
									muzzle,
									MathUtils.getRandomPointInCircle(theTarget.getVelocity(), 10f),
									new Vector2f(theTarget.getShieldRadiusEvenIfNoShield()/2, theTarget.getShieldRadiusEvenIfNoShield()/2),
									new Vector2f(20, 20),
									//angle,
									360 * (float) Math.random(),
									0,
									new Color(beam.getFringeColor().getRed()
											, beam.getFringeColor().getGreen()
											, beam.getFringeColor().getBlue()
											, 47
									),
									false,
									0.1f,
									1f,
									1f
							);
						}
						MagicRender.battlespace(
								Global.getSettings().getSprite("fx", "SGB_Va_Explosion_ring"),
								muzzle,
								theTarget.getVelocity(),
								new Vector2f(theTarget.getShieldRadiusEvenIfNoShield()*3f, theTarget.getShieldRadiusEvenIfNoShield()*3f),
								new Vector2f(120, 120),
								//angle,
								360 * (float) Math.random(),
								0,
								new Color(19, 59, 133, 189),
								false,
								0f,
								0f,
								0.17f
						);
						MagicRender.battlespace(
								Global.getSettings().getSprite("fx", "SGB_Va_Explosion"),
								muzzle,
								theTarget.getVelocity(),
								new Vector2f(theTarget.getShieldRadiusEvenIfNoShield()*3.5f, theTarget.getShieldRadiusEvenIfNoShield()*3.5f),
								new Vector2f(38, 38),
								//angle,
								360 * (float) Math.random(),
								0,
								new Color(201, 255, 253, 255),
								true,
								0.03f,
								0.05f,
								0.02f
						);
					}
				}
			}
		}
	}
}