package data.weapons;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class SGB_Missile_Weapon_Roller implements OnHitEffectPlugin {

	private final Color PARTICLE_COLOR = new Color(182, 241, 214);
	private final float PARTICLE_SIZE = 90f;
	private final float PARTICLE_BRIGHTNESS = 1f;
	private final float PARTICLE_DURATION = 0.75f;

	private final float EXPLOSION_SIZE = 130f;

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		if(target instanceof MissileAPI || target instanceof AsteroidAPI)return;
		if(shieldHit)return;
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
				new Vector2f(192,192),
				//angle,
				360*(float)Math.random(),
				0,
				new Color(239, 186, 208,255),
				true,
				0,
				0.1f,
				0.15f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_smoke_01"),
				point,
				Speedo2,
				new Vector2f(32,32),
				new Vector2f(192,192),
				//angle,
				Angle+2*(float)Math.random(),
				0,
				Color.white,
				true,
				0.1f,
				0.0f,
				0.25f
		);
		if(MagicRender.screenCheck(0.1f, point)){
			for (float i = 0; i <= 5; i++) {
				float particleSize = MathUtils.getRandomNumberInRange(PARTICLE_SIZE-2, PARTICLE_SIZE+2);
				Vector2f randSpawnPoint = MathUtils.getRandomPointOnCircumference(point, EXPLOSION_SIZE);
				Vector2f randExitVector = VectorUtils.getDirectionalVector(point, randSpawnPoint);
				randExitVector.scale(EXPLOSION_SIZE*2);
				engine.addHitParticle(randSpawnPoint, randExitVector, particleSize, PARTICLE_BRIGHTNESS, PARTICLE_DURATION, PARTICLE_COLOR);
			}

			engine.addSmoothParticle(point, new Vector2f(), 60, 2f, 0.1f, Color.orange);
			engine.spawnExplosion(point, new Vector2f(), new Color(188, 255, 229), 10 + (float)Math.random()*40, 0.25f);
		}
		for (float i = 0; i <= 20; i++) {
			engine.spawnEmpArc(projectile.getSource(), point, null, target,
					DamageType.ENERGY,
					10,
					200f,
					200f,
					"tachyon_lance_emp_impact",
					20f, // thickness
					new Color(25, 155, 140,255),
					new Color(255, 245, 223,255));
		}
	}
}
