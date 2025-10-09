package data.weapons;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import data.utils.sgb.I18nUtil;
import data.utils.sgb.SGB_BoxBasedUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import static com.fs.starfarer.api.util.Misc.ZERO;
import static data.missions.SGB_20FAboveTheGround.MissionDefinition.colorNoiseBlend;

public class SGB_Missile_Weapon_SMS implements OnHitEffectPlugin {

	private final float PARTICLE_SIZE = 2.5f;
	private final float PARTICLE_BRIGHTNESS = 0.98f;
	private final float PARTICLE_DURATION = 0.5f;

	private final float EXPLOSION_SIZE = 25f;

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if(target instanceof MissileAPI || target instanceof AsteroidAPI)return;
		if(projectile.getProjectileSpecId().equals("SGB_Reborn_srm_energy")){
			if(target instanceof ShipAPI){
				ShipAPI tShip = (ShipAPI) target;
				float theFluxAdd = projectile.getHitpoints();
				boolean hardFlux = true;

				if(shieldHit){
					theFluxAdd = theFluxAdd/2f;
					hardFlux = false;
				}
				tShip.getFluxTracker().increaseFlux(theFluxAdd,hardFlux);

				engine.addFloatingText(point, String.valueOf((int) theFluxAdd), 50f,
						new Color(180, 241, 239, 255), target, 0f, 0f);
			}
			if(SGB_BoxBasedUtil.hadMagicLib()){
				MagicRender.battlespace(
						Global.getSettings().getSprite("fx","SGB_Wave"),
						point,
						ZERO,
						new Vector2f(270,270),
						new Vector2f(-470,-470),
						//angle,
						360*(float)Math.random(),
						30,
						new Color(13, 7, 199,125),
						true,
						0.1f,
						0.25f,
						0.75f
				);
				MagicRender.battlespace(
						Global.getSettings().getSprite("fx","SGB_smoke_2"),
						MathUtils.getRandomPointOnCircumference(point,90f),
						ZERO,
						new Vector2f(75,75),
						new Vector2f(600,600),
						//angle,
						projectile.getFacing()+2*(float)Math.random(),
						2,
						Color.white,
						false,
						0.1f,
						0.2f,
						1.25f
				);
			}int iNum = MathUtils.getRandomNumberInRange(4,8);
			for (int i = 0; i < iNum; i++) {
				Vector2f firstPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),iNum*9.5f);
				Vector2f SecPoint = MathUtils.getRandomPointOnCircumference(target.getLocation(),iNum*5.25f);
				engine.spawnEmpArcVisual(point, target, firstPoint, target,
						MathUtils.getRandomNumberInRange(40,60), // thickness
						new Color(78, 116, 213,195),
						new Color(205, 197, 255,225)
				);
				if(i>6){
					engine.spawnEmpArcVisual(firstPoint, target, SecPoint, target,
							MathUtils.getRandomNumberInRange(30,45), // thickness
							new Color(78, 116, 213,195),
							new Color(205, 197, 255,225)
					);
					Global.getCombatEngine().addNebulaSmokeParticle(
							firstPoint,ZERO,
							iNum*25,
							1.25f,
							0.74f,
							0.73f,
							1.22f,
							new Color(149, 137, 255,106)
					);
					Global.getCombatEngine().addNebulaSmokeParticle(
							SecPoint,ZERO,
							iNum*18,
							0.7f,
							0.42f,
							0.4f,
							1.22f,
							new Color(173, 167, 227,76)
					);
				}
			}
		}


		Color PARTICLE_COLOR = new Color(241, 221, 182);
		boolean isRailChain = projectile.getProjectileSpecId().equals("SGB_RailChain_shot");
		if(isRailChain){PARTICLE_COLOR = new Color(127, 170, 224);}

		Vector2f Speedo =projectile.getVelocity();
		Vector2f Speedo2 = Speedo;
		Speedo2.setX(Speedo.getX()/8);;
		Speedo2.setY(Speedo.getY()/8);
		float Angle =projectile.getFacing();
		I18nUtil.easyWaving(point, ZERO, 50, 3, 10);
		if(!projectile.getProjectileSpecId().equals("SGB_Reborn_srm") && !projectile.getProjectileSpecId().equals("SGB_Reborn_srm_energy")){
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
				0.125f,
				0.172f
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
			MagicRender.battlespace(
					Global.getSettings().getSprite("fx","SGB_plasma_red"),
					point,
					Speedo2,
					new Vector2f(80,80),
					new Vector2f(189,189),
					//angle,
					360*(float)Math.random(),
					0,
					new Color(186, 239, 231,255),
					true,
					0,
					0.1f,
					0.15f
			);
		}
		if(MagicRender.screenCheck(0.1f, point)){
			for (float i = 0; i <= 5; i++) {
				float particleSize = MathUtils.getRandomNumberInRange(PARTICLE_SIZE-2, PARTICLE_SIZE+2);
				Vector2f randSpawnPoint = MathUtils.getRandomPointOnCircumference(point, EXPLOSION_SIZE);
				Vector2f randExitVector = VectorUtils.getDirectionalVector(point, randSpawnPoint);
				randExitVector.scale(EXPLOSION_SIZE*2);
				engine.addHitParticle(randSpawnPoint, randExitVector, particleSize, PARTICLE_BRIGHTNESS, PARTICLE_DURATION, PARTICLE_COLOR);
			}
			if (isRailChain) {
				engine.addSmoothParticle(point, new Vector2f(), 60, 2f, 0.1f, Color.orange);
				engine.spawnExplosion(point, new Vector2f(), new Color(255, 222, 202), 10 + (float) Math.random() * 40, 0.25f);
			}else{
				engine.addSmoothParticle(point, new Vector2f(), 60, 2f, 0.1f, Color.cyan);
				engine.spawnExplosion(point, new Vector2f(), new Color(202, 255, 217), 10 + (float) Math.random() * 40, 0.25f);
			}
		}
	}
}
