package data.weapons;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.TimeoutTracker;
import data.scripts.util.MagicRender;
import data.utils.sgb.ExampleEveryFrame;
import data.utils.sgb.I18nUtil;
import data.utils.sgb.SGB_Color;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.magiclib.util.MagicAnim;

import static com.fs.starfarer.api.impl.combat.RealityDisruptorChargeGlow.isProjectileExpired;
import static data.shipsystems.scripts.SGB_AsAboveSoBelow.getRandomBlendColor;
import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_MatealDisper_OnHitEffect implements OnHitEffectPlugin,OnFireEffectPlugin {
				//com.fs.starfarer.api.impl.combat.RealityDisruptorEffect
				//com.fs.starfarer.api.impl.combat.CryofluxTransducerEffect
				//com.fs.starfarer.api.impl.combat.EntropyAmplifierStats
	static boolean ShieldBroke = false, ArmorBroke = false, runOnce = false, hidden = false;
	static final String modificationSource_A = "SGB_MatealDisper_Armor";
	static final String modificationSource_S = "SGB_MatealDisper_Shield";
	static final float Shield_debuff = 15f, Shield_debuff_2 = 25f, Shield_debuffTime = 5f;
	static final float Armor_debuff = 10f, Armor_debuff_2 = 20f, Armor_debuffTime = 10f;
	Vector2f ZERO = new Vector2f(0,0);
	protected float delay = 0.5f; //	一个自减计时

	@Override //	使用OnFire为projectile挂载一个每帧以实现效果
	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
		SGB_MatealDisper_EveryFrameEffect e = new SGB_MatealDisper_EveryFrameEffect(projectile);
		//if (engine.isPaused() || weapon.getShip().getOriginalOwner() == -1) {return;}
	}

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		float RandomNum = (float)Math.random();
		if (!shieldHit && target instanceof ShipAPI) {
			float emp = projectile.getEmpAmount();
			float dam = 1000f;	//	爆炸伤害

			engine.addHitParticle(point, new Vector2f(), 10 + (float)Math.random()*5, 1, 0.05f, SGB_Color.SGBpurple);

			ShipAPI ship = (ShipAPI) target;
			//	先炸再加成
			DamagingExplosionSpec boom = new DamagingExplosionSpec(
					1.25f,
					200,
					160,
					dam,
					dam/2,
					CollisionClass.PROJECTILE_NO_FF,
					CollisionClass.PROJECTILE_FIGHTER,
					2,
					5,
					5,
					25,
					projectile.getProjectileSpec().getFringeColor(),
					projectile.getProjectileSpec().getGlowColor()
			);
			boom.setDamageType(DamageType.ENERGY);
			boom.setShowGraphic(true);
			boom.setSoundSetId("realitydisruptor_fire");

			engine.spawnDamagingExplosion(boom, projectile.getSource(), point);
			for (int i = 0; i < MathUtils.getRandomNumberInRange(2,5); i++) {
				engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(),
						"riftbeam_minelayer",
						MathUtils.getRandomPointInCircle(point,300f),
						MathUtils.getRandomNumberInRange(0,360),ZERO);
			}

			//____护甲影响__
			if (!ship.hasListenerOfClass(modificationSource_Armor_DamageTakenMod.class)) {
				ship.addListener(new modificationSource_Armor_DamageTakenMod(ship));
			}
			List<modificationSource_Armor_DamageTakenMod> listeners = ship.getListeners(modificationSource_Armor_DamageTakenMod.class);
			if (listeners.isEmpty()) return; // ???

			modificationSource_Armor_DamageTakenMod listener = listeners.get(0);
			listener.notifyHit(projectile.getWeapon());
			//----护甲影响--
		}
		if (shieldHit && target instanceof ShipAPI) {

			ShipAPI ship = (ShipAPI) target;
			//____护盾影响__
			if (!ship.hasListenerOfClass(modificationSource_Shield_DamageTakenMod.class)) {
				ship.addListener(new modificationSource_Shield_DamageTakenMod(ship));
			}
			List<modificationSource_Shield_DamageTakenMod> listeners = ship.getListeners(modificationSource_Shield_DamageTakenMod.class);
			if (listeners.isEmpty()) return; // ???

			modificationSource_Shield_DamageTakenMod listener = listeners.get(0);
			listener.notifyHit(projectile.getWeapon());
			//----护盾影响--

			Vector2f to1 = MathUtils.getRandomPointOnCircumference(point,150f*RandomNum);
			Vector2f to2 = MathUtils.getRandomPointOnCircumference(point,200f*(float)Math.random());
			Vector2f to3 = MathUtils.getRandomPointOnCircumference(point,75f*RandomNum);

			for (int i = 0; i < 4; i++) {
				engine.spawnEmpArcVisual(projectile.getLocation(), target, to1, target,
						10f * RandomNum, // thickness
						new Color(91, 19, 133, 255),
						new Color(235, 204, 255, 255)
				);
				engine.spawnEmpArcVisual(projectile.getLocation(), target, to2, target,
						10f * RandomNum, // thickness
						new Color(91, 19, 133, 255),
						new Color(235, 204, 255, 255)
				);
				engine.spawnEmpArcVisual(projectile.getLocation(), target, to3, target,
						10f * RandomNum, // thickness
						new Color(91, 19, 133, 255),
						new Color(235, 204, 255, 255)
				);
			}
		}
	}

	public static class modificationSource_Armor_DamageTakenMod implements AdvanceableListener {
		protected ShipAPI ship;
		protected TimeoutTracker<WeaponAPI> recentHits = new TimeoutTracker<WeaponAPI>();
		public modificationSource_Armor_DamageTakenMod(ShipAPI ship) {
			this.ship = ship;
			//ship.addListener(new GravitonBeamDamageTakenModRemover(ship));
		}

		public void notifyHit(WeaponAPI w) { //	持续时间
			recentHits.add(w, Armor_debuffTime, Armor_debuffTime);

		}
		public void advance(float amount) {
			recentHits.advance(amount);

			int beams = recentHits.getItems().size();

			float bonus = 0;
			if (beams == 1) {
				bonus = Armor_debuff;
			} else if (beams == 2) {
				bonus = Armor_debuff_2;
			}

			if (bonus > 0) {
				ship.getMutableStats().getEffectiveArmorBonus().modifyMult(modificationSource_A, 1f - bonus * 0.01f);
				ship.fadeToColor(this,new Color(227, 171, 161,255),2f,Armor_debuffTime,2f);
				if (ship != null) {
					if (!ship.getFluxTracker().showFloaty() ||
							!ship.isAlly() ||
							ship == Global.getCombatEngine().getPlayerShip()) {
						if(!ArmorBroke) {
							ship.getFluxTracker().showOverloadFloatyIfNeeded(txt("SGB_Omega_ArmorBroken"), new Color(231, 133, 118, 255), 4f, true);
							ArmorBroke = true;
						}
					}
				}
			} else {
				ship.removeListener(this);
				ship.getMutableStats().getEffectiveArmorBonus().unmodify(modificationSource_A);
				ArmorBroke = false;
			}
		}
		public String modifyDamageTaken(Object param,
										CombatEntityAPI target,
										DamageAPI damage,
										Vector2f point,
										boolean shieldHit) {
			return null;
		}

	}

	public static class modificationSource_Shield_DamageTakenMod implements AdvanceableListener {
		protected ShipAPI ship;
		protected TimeoutTracker<WeaponAPI> recentHits = new TimeoutTracker<WeaponAPI>();
		public modificationSource_Shield_DamageTakenMod(ShipAPI ship) {
			this.ship = ship;
		}

		public void notifyHit(WeaponAPI w) { //	持续时间
			recentHits.add(w, Shield_debuffTime, Shield_debuffTime);

		}

		public void advance(float amount) {
			recentHits.advance(amount);

			int beams = recentHits.getItems().size();

			float bonus = 0;
			if (beams == 1) {
				bonus = Shield_debuff;
			} else if (beams == 2) {
				bonus = Shield_debuff_2;
			}

			if (bonus > 0) {
				ship.getMutableStats().getShieldDamageTakenMult().modifyMult(modificationSource_S, 1f + bonus * 0.01f);
				ship.fadeToColor(this,new Color(224, 191, 243,255),1.5f,Shield_debuffTime,2f);
				if (ship != null) {
					if (!ship.getFluxTracker().showFloaty() ||
							!ship.isAlly() ||
							ship == Global.getCombatEngine().getPlayerShip()) {
						if(!ShieldBroke) {
						ship.getFluxTracker().showOverloadFloatyIfNeeded(txt("SGB_Omega_ShieldBroken"), new Color(174, 117, 213,255), 4f, true);
							ShieldBroke = true;
						}
					}
				}
			} else {
				ship.removeListener(this);
				ship.getMutableStats().getShieldDamageTakenMult().unmodify(modificationSource_S);
				ShieldBroke = false;
			}
		}
		public String modifyDamageTaken(Object param,
										CombatEntityAPI target,
										DamageAPI damage,
										Vector2f point,
										boolean shieldHit) {
			return null;
		}

	}
	/*
	public static Color bonusRGB(Color RGBA, Color target, float bonus, float max){
		//bonus = 0 -> return target
		//bonus = max -> return RGBA
		int R = RGBA.getRed();
		int G = RGBA.getGreen();
		int B = RGBA.getBlue();
		int A = RGBA.getAlpha();
		int Rt = target.getRed();
		int Gt = target.getGreen();
		int Bt = target.getBlue();
		int At = target.getAlpha();

		Color newColor = new Color(
				(int)MagicAnim.smoothToRange(bonus,0,max,R,Rt),
				(int)MagicAnim.smoothToRange(bonus,0,max,G,Gt),
				(int)MagicAnim.smoothToRange(bonus,0,max,B,Bt),
				(int)MagicAnim.smoothToRange(bonus,0,max,A,At)
		);
		return newColor;
	}
	 */
}
