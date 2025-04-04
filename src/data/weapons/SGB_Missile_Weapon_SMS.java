package data.weapons;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import data.utils.sgb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class SGB_Missile_Weapon_SMS implements OnHitEffectPlugin {

	private final Color PARTICLE_COLOR = new Color(241, 221, 182);
	private final float PARTICLE_SIZE = 2.5f;
	private final float PARTICLE_BRIGHTNESS = 0.98f;
	private final float PARTICLE_DURATION = 0.5f;

	private final float EXPLOSION_SIZE = 25f;

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		if(target instanceof MissileAPI || target instanceof AsteroidAPI)return;
		Vector2f Speedo =projectile.getVelocity();
		Vector2f Speedo2 = Speedo;
		Speedo2.setX(Speedo.getX()/8);;
		Speedo2.setY(Speedo.getY()/8);
		float Angle =projectile.getFacing();
		I18nUtil.easyWaving(point, ZERO, 50, 3, 10);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_plasma_red"),
				point,
				Speedo2,
				new Vector2f(64,96),
				new Vector2f(96,160),
				//angle,
				360*(float)Math.random(),
				0,
				new Color(186, 239, 231,255),
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
				new Vector2f(102,102),
				//angle,
				Angle+2*(float)Math.random(),
				0,
				Color.white,
				true,
				0.1f,
				0.0f,
				0.25f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_smoke_2"),
				point,
				Speedo2,
				new Vector2f(32,32),
				new Vector2f(102,102),
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
			engine.spawnExplosion(point, new Vector2f(), new Color(255, 222, 202), 10 + (float)Math.random()*40, 0.25f);
		}
	}
}
