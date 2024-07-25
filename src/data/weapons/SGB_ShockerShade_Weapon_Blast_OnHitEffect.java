package data.weapons;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.utils.sgb.SGB_Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class SGB_ShockerShade_Weapon_Blast_OnHitEffect implements OnHitEffectPlugin {


	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		float RandomNum = (float)Math.random();
		if (!shieldHit && target instanceof ShipAPI) {
			
			float emp = projectile.getEmpAmount();
			float dam = projectile.getDamageAmount();

			engine.addHitParticle(point, new Vector2f(), 10 + (float)Math.random()*5, 1, 0.05f, SGB_Color.SGBpurple);
			
			engine.spawnEmpArc(projectile.getSource(), point, target, target,
							   DamageType.ENERGY, 
							   dam/3,
							   emp, // emp
							   100000f, // max range 
							   "tachyon_lance_emp_impact",
							   10f * MathUtils.getRandomNumberInRange(0.5f,1.5f), // thickness
							   new Color(133, 108, 19,255),
							   new Color(255, 245, 204,255)
							   );
			engine.spawnEmpArc(projectile.getSource(), point, target, target,
					DamageType.ENERGY,
					dam/3,
					emp*1.5f, // emp
					100000f, // max range
					"tachyon_lance_emp_impact",
					10f * MathUtils.getRandomNumberInRange(0.75f,1.75f), // thickness
					new Color(133, 124, 19,255),
					new Color(255, 251, 204,255)
			);
			engine.spawnEmpArc(projectile.getSource(), point, target, target,
					DamageType.ENERGY,
					dam/3,
					emp*2, // emp
					100000f, // max range
					"tachyon_lance_emp_impact",
					10f * MathUtils.getRandomNumberInRange(1f,2f), // thickness
					new Color(133, 101, 19,255),
					new Color(255, 237, 204,255)
			);
			
			//engine.spawnProjectile(null, null, "plasma", point, 0, new Vector2f(0, 0));
		}
		if (shieldHit && target instanceof ShipAPI) {

			ShipAPI ship = (ShipAPI) target;
			float pierceChance = ((ShipAPI)target).getHardFluxLevel() - 0.1f;
			pierceChance *= ship.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);

			boolean piercedShield = (float) Math.random() < pierceChance;
			//piercedShield = true;

			if (piercedShield) {
				float emp = projectile.getEmpAmount();
				float dam = projectile.getDamageAmount();
				engine.spawnEmpArcPierceShields(
						projectile.getSource(),
						point,
						projectile.getDamageTarget(), projectile.getDamageTarget(),
						DamageType.ENERGY,
						dam/3, // damage
						emp, // emp
						100000f, // max range
						"tachyon_lance_emp_impact",
						10f * MathUtils.getRandomNumberInRange(1f,2f), // thickness
						projectile.getProjectileSpec().getFringeColor(),
						projectile.getProjectileSpec().getCoreColor()
				);

				engine.spawnEmpArcPierceShields(
						projectile.getSource(),
						point,
						projectile.getDamageTarget(), projectile.getDamageTarget(),
						DamageType.ENERGY,
						dam/3, // damage
						emp, // emp
						100000f, // max range
						"tachyon_lance_emp_impact",
						10f * MathUtils.getRandomNumberInRange(1f,2f), // thickness
						projectile.getProjectileSpec().getFringeColor(),
						projectile.getProjectileSpec().getCoreColor()
				);
			}

			Vector2f to1 = MathUtils.getRandomPointOnCircumference(point,150f*RandomNum);
			Vector2f to2 = MathUtils.getRandomPointOnCircumference(point,200f*(float)Math.random());
			Vector2f to3 = MathUtils.getRandomPointOnCircumference(point,75f*RandomNum);

			engine.spawnEmpArcVisual(projectile.getLocation(), target, to1, target,
					10f * RandomNum, // thickness
					new Color(133, 108, 19,255),
					new Color(255, 245, 204,255)
			);
			engine.spawnEmpArcVisual(projectile.getLocation(), target, to2, target,
					10f * RandomNum, // thickness
					new Color(133, 118, 19,255),
					new Color(255, 248, 204,255)
			);
			engine.spawnEmpArcVisual(projectile.getLocation(), target, to3, target,
					10f * RandomNum, // thickness
					new Color(133, 101, 19,255),
					new Color(255, 237, 204,255)
			);
		}
	}
}
