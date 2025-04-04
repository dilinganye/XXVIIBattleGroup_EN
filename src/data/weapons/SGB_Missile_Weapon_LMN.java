package data.weapons;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicAnim;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import data.scripts.util.MagicTargeting;
import data.utils.sgb.I18nUtil;
import data.utils.sgb.SGB_Color;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class SGB_Missile_Weapon_LMN implements OnHitEffectPlugin {

	private final Color PARTICLE_COLOR = new Color(241, 205, 182);
	private final float PARTICLE_SIZE = 3f;
	private final float PARTICLE_BRIGHTNESS = 0.98f;
	private final float PARTICLE_DURATION = 0.75f;
	private final float EXPLOSION_SIZE = 30f;

	private final float DAMAGE_WITH_SHIELD = 1000f;
	private final float DAMAGE_OUT_OF_SHIELD = 5000f;
	private data.utils.sgb.I18nUtil I18nUtil;

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		if(target instanceof MissileAPI || target instanceof AsteroidAPI)return;
		Vector2f Speedo =projectile.getVelocity();
		Vector2f Speedo2 = Speedo;
		Speedo2.setX(Speedo.getX()/8);;
		Speedo2.setY(Speedo.getY()/8);
		float Angle =projectile.getFacing();

		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_plasma_red"),
				point,
				Speedo2,
				new Vector2f(96,96),
				new Vector2f(500,500),
				//angle,
				360*(float)Math.random(),
				0,
				new Color(75, 166, 255,175),
				true,
				0,
				0.1f,
				0.15f
		);
		/*MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_AE_Wave"),
				point,
				Speedo2,
				new Vector2f(96,96),
				new Vector2f(700,700),
				//angle,
				360*(float)Math.random(),
				40,
				new Color(208, 215, 204,225),
				false,
				0.1f,
				1f,
				2.15f
		);*/
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_AE_Wave"),
				point,
				Speedo2,
				new Vector2f(96,96),
				new Vector2f(600,600),
				//angle,
				360*(float)Math.random(),
				40,
				new Color(208, 215, 204,85),
				true,
				0.1f,
				1f,
				1.25f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_Wave"),
				point,
				Speedo2,
				new Vector2f(96,96),
				new Vector2f(600,600),
				//angle,
				360*(float)Math.random(),
				30,
				new Color(7, 199, 193,125),
				true,
				0,
				0.5f,
				0.75f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_smoke_1"),
				MathUtils.getRandomPointOnCircumference(point,90f),
				Speedo2,
				new Vector2f(32,32),
				new Vector2f(592,592),
				//angle,
				Angle+2*(float)Math.random(),
				-4,
				Color.white,
				false,
				0.25f,
				0.2f,
				1.55f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_smoke_2"),
				MathUtils.getRandomPointOnCircumference(point,90f),
				Speedo2,
				new Vector2f(75,75),
				new Vector2f(600,600),
				//angle,
				Angle+2*(float)Math.random(),
				2,
				Color.white,
				false,
				0.1f,
				0.2f,
				1.25f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_smoke_01"),
				MathUtils.getRandomPointOnCircumference(point,90f),
				Speedo2,
				new Vector2f(100,100),
				new Vector2f(540,540),
				//angle,
				Angle+2*(float)Math.random(),
				6,
				Color.white,
				true,
				0.2f,
				0.0f,
				0.75f
		);
		//_____
		for (int i = 0; i < 10; i++) {
			Vector2f drift = MathUtils.getRandomPointInCone(
					projectile.getVelocity(),
					MathUtils.getRandomNumberInRange(25, 100),
					projectile.getAngularVelocity()-5,
					projectile.getAngularVelocity()+5);
			engine.addNegativeParticle(
					point,drift,
					MathUtils.getRandomNumberInRange(30, 100),
					1.5f,
					1f,
					new Color(3, 100, 11,100));
		}
		I18nUtil.easyRippleOut(point, Speedo2, MathUtils.getRandomNumberInRange(1f,2f) * 600.0F, MathUtils.getRandomNumberInRange(40f,100f), 5.0F, 400.0F);
		I18nUtil.easyRippleOut(point, Speedo2, MathUtils.getRandomNumberInRange(1f,2f) * 600.0F, MathUtils.getRandomNumberInRange(90f,100f), 2.0F, 120.0F);
		I18nUtil.easyRippleOut(point, Speedo2, MathUtils.getRandomNumberInRange(1f,2.25f) * 500.0F, MathUtils.getRandomNumberInRange(85f,100f), -5F, 80.0F);
		I18nUtil.easyWaving(point, Speedo2, MathUtils.getRandomNumberInRange(1f,2.25f) * 700.0F, MathUtils.getRandomNumberInRange(55f,75f), 1.0F);

		MagicLensFlare.createSharpFlare(engine, projectile.getSource(), point, 50.0F, MathUtils.getRandomNumberInRange(1f,1.5f) * 950.0F, 45,
				new Color(125, 225, 136,175),
				new Color(99, 255, 255,175));

		//_____
		Float WhatDamage;
		if (shieldHit){
			WhatDamage = DAMAGE_WITH_SHIELD;
		}
		else{
			WhatDamage = DAMAGE_OUT_OF_SHIELD;
		}


		DamagingExplosionSpec boom = new DamagingExplosionSpec(
				0.1f,
				500,
				200,
				WhatDamage,
				WhatDamage/4,
				CollisionClass.PROJECTILE_NO_FF,
				CollisionClass.PROJECTILE_FIGHTER,
				2,
				5,
				5,
				25,
				new Color(186, 218, 174),
				new Color(18, 19, 18)
		);
		boom.setDamageType(DamageType.ENERGY);
		boom.setShowGraphic(false);
		boom.setSoundSetId("riftcascade_rift");

		//Float hitPot;
		if (shieldHit && target instanceof ShipAPI) {
			//hitPot = target.getHitpoints()-DAMAGE_WITH_SHIELD;
			//target.setHitpoints(hitPot);
			engine.spawnDamagingExplosion(boom, projectile.getSource(), point);
			if(Math.random()>0.5) {
				((ShipAPI) target).getFluxTracker().forceOverload(MathUtils.getRandomNumberInRange(2f, 7f));
			}

			int iNum = MathUtils.getRandomNumberInRange(15,25);
			for (int i = 0; i < iNum; i++) {
				Vector2f firstPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),target.getCollisionRadius()*1.5f);
				Vector2f SecPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),target.getCollisionRadius()*2.25f);
				engine.spawnEmpArcVisual(point, target, firstPoint, target,
						MathUtils.getRandomNumberInRange(40,60), // thickness
						new Color(95, 133, 19,255),
						new Color(227, 255, 204,255)
				);
				if(i>10){
					engine.spawnEmpArcVisual(firstPoint, target, SecPoint, target,
							MathUtils.getRandomNumberInRange(30,45), // thickness
							new Color(123, 213, 78,195),
							new Color(197, 255, 215,225)
					);
				}
			}
		}

		if (!shieldHit && target instanceof ShipAPI) {
			engine.spawnDamagingExplosion(boom, projectile.getSource(), point);

			int iNum = MathUtils.getRandomNumberInRange(15,25);
			for (int i = 0; i < iNum; i++) {
				Vector2f firstPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),target.getCollisionRadius()*0.25f);
				Vector2f SecPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),target.getCollisionRadius()*1.75f);
				Vector2f TriPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),target.getCollisionRadius()*3f);
				engine.spawnEmpArcVisual(point, target, firstPoint, target,
						MathUtils.getRandomNumberInRange(60,70), // thickness
						new Color(100, 143, 36,225),
						new Color(204, 255, 216,255)
				);
				engine.spawnEmpArcVisual(firstPoint, target, SecPoint, target,
						MathUtils.getRandomNumberInRange(45,65), // thickness
						new Color(123, 213, 78,195),
						new Color(197, 255, 215,225)
				);
				engine.spawnEmpArcVisual(SecPoint, target, TriPoint, target,
						MathUtils.getRandomNumberInRange(30,50), // thickness
						new Color(58, 238, 115,175),
						new Color(212, 243, 234,200)
				);
			}
		}
		//_____

		if(MagicRender.screenCheck(0.1f, point)){
			for (float i = 0; i <= 5; i++) {
				float particleSize = MathUtils.getRandomNumberInRange(PARTICLE_SIZE-2, PARTICLE_SIZE+2);
				Vector2f randSpawnPoint = MathUtils.getRandomPointOnCircumference(point, EXPLOSION_SIZE);
				Vector2f randExitVector = VectorUtils.getDirectionalVector(point, randSpawnPoint);
				randExitVector.scale(EXPLOSION_SIZE*2);
				engine.addHitParticle(randSpawnPoint, randExitVector, particleSize, PARTICLE_BRIGHTNESS, PARTICLE_DURATION, PARTICLE_COLOR);
			}

			engine.addSmoothParticle(point, new Vector2f(), 60, 2f, 0.1f, Color.orange);
			engine.spawnExplosion(point, new Vector2f(), new Color(255, 206, 188), 10 + (float)Math.random()*40, 0.25f);
		}
	}
}
