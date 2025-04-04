package data.weapons;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicRender;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class SGB_Acidslay_AcidHit implements OnHitEffectPlugin {

	private boolean oneMoreCupOfAcid = false;
	private float oneMoreCupOfAcid_PeRCent = 0.45f,Max_DAMAGE = 250f;

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if(target instanceof MissileAPI)return;

		Vector2f Speedo =projectile.getVelocity();
		Vector2f SpeedT =target.getVelocity();
		Vector2f Speedo2 = new Vector2f(Speedo);
		Speedo2.setX(Speedo.getX()/8+SpeedT.getX()/4*3.75f);
		Speedo2.setY(Speedo.getY()/8+SpeedT.getY()/4*3.75f);

		float Angle =projectile.getFacing();

		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_smoke_2"),
				point,
				Speedo2,
				new Vector2f(32,64),
				new Vector2f(96,96),
				//angle,
				Angle+2*(float)Math.random()+90f,
				0,
				new Color(201, 239, 186,255),
				false,
				0.2f,
				0.0f,
				0.3f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_Spark01A"),
				point,
				Speedo2,
				new Vector2f(32,64),
				new Vector2f(64,96),
				//angle,
				Angle+2f*(float)Math.random()+90f,
				0,
				new Color(201, 239, 186,255),
				true,
				0,
				0.1f,
				0.25f
		);
		MagicRender.battlespace(
				Global.getSettings().getSprite("fx","SGB_Spark01A"),
				point,
				Speedo2,
				new Vector2f(32,64),
				new Vector2f(64,96),
				//angle,
				Angle+2*(float)Math.random()+90f,
				0,
				new Color(201, 239, 186,255),
				true,
				0.2f,
				0.0f,
				0.3f
		);

		engine.addHitParticle(point, new Vector2f(), 120, 2f, 0.5f, new Color(229, 253, 183,255));
		engine.addHitParticle(point, Speedo2, 100, 2f, 0.25f, new Color(254, 255, 231,255));
		engine.addSmoothParticle(point, new Vector2f(), 120, 2f, 0.1f, projectile.getProjectileSpec().getCoreColor());
		engine.spawnExplosion(point, new Vector2f(), new Color(133, 183, 143), 80 + (float)Math.random()*5, 0.25f);

		//if(target instanceof AsteroidAPI)return;

		//	引爆
		if(Math.random()<=oneMoreCupOfAcid_PeRCent){
			oneMoreCupOfAcid = true;
		}
		DamagingExplosionSpec Acid = new DamagingExplosionSpec(
				1.5f,
				60,
				40,
				Max_DAMAGE,
				Max_DAMAGE/4f,
				CollisionClass.PROJECTILE_NO_FF,
				CollisionClass.PROJECTILE_FIGHTER,
				10,
				5,
				3,
				10,
				projectile.getProjectileSpec().getCoreColor(),
				projectile.getProjectileSpec().getFringeColor()
		);
		Acid.setDamageType(DamageType.FRAGMENTATION);
		Acid.setShowGraphic(true);
		Acid.setSoundSetId("cryoflamer_hit_heavy");
		//KILL.setSoundSetId("rifttorpedo_explosion");
		engine.spawnDamagingExplosion(Acid, projectile.getSource(), point);
		if(oneMoreCupOfAcid){
			DamagingExplosionSpec Acid2 = new DamagingExplosionSpec(
					1.5f,
					60,
					40,
					Max_DAMAGE,
					Max_DAMAGE/4f,
					CollisionClass.PROJECTILE_NO_FF,
					CollisionClass.PROJECTILE_FIGHTER,
					10,
					11,
					5,
					25,
					projectile.getProjectileSpec().getCoreColor(),
					projectile.getProjectileSpec().getFringeColor()
			);
			Acid2.setDamageType(DamageType.FRAGMENTATION);
			Acid2.setShowGraphic(true);
			Acid2.setSoundSetId("cryoflamer_hit_heavy");
			engine.spawnDamagingExplosion(Acid2, projectile.getSource(), point);
		}

	}
}
