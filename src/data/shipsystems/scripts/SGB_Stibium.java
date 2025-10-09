package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.MoteControlScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static data.missions.SGB_20FAboveTheGround.MissionDefinition.colorNoiseBlend;

public class SGB_Stibium extends BaseShipSystemScript {

	private int fireamount = 6; private float roLeft = -15, roRight = 15;
	private boolean runOnce = false, hasfire = false;
	private WeaponAPI FLW, FRW, SCK;
	private final IntervalUtil timer = new IntervalUtil(0.05f, 0.1f);

	private static final Map<Integer, Float> LAUCH_ANGLE = new HashMap<>(4);

	public final static float MAX_SPEED_BONUS =75f;
	private IntervalUtil fireInterval = new IntervalUtil(0.5f, 1.8f);


	@Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine.isPaused() || engine == null) {
			return;
		}

		ShipAPI ship = (ShipAPI) stats.getEntity();
		if (ship == null) {
			return;
		}

		if (!ship.isAlive()) {
			return;
		}

		if (!runOnce) {
			for (WeaponAPI w : ship.getAllWeapons()) {
				switch (w.getSlot().getId()) {
					case "FLW":
						FLW = w;
						break;
					case "FRW":
						FRW = w;
						break;
					case "Shock":
						SCK = w;
						break;
				}
			}
			runOnce = true;
		}

		float amount = engine.getElapsedInLastFrame();
		timer.advance(amount);

		if (state == ShipSystemStatsScript.State.IN) {
			ship.setJitterUnder(ship, new Color(230, 255, 0, 69), Math.min((effectLevel + 0.5f), 1f), 2, 0f, 10f);

		}

		for (ShipAPI Tship : engine.getShips()) {
			fireInterval.advance(amount);
			if (fireInterval.intervalElapsed()) {
				if ((Tship != ship) && (Tship.getOwner() != ship.getOwner())) {
					if (MathUtils.getDistance(ship, Tship) <= 1000) {
						for (int i = 0; i < 5; i++) {
							engine.spawnEmpArc(
									ship, SCK.getLocation(), ship, Tship,
									DamageType.ENERGY,
									MathUtils.getRandomNumberInRange(100f, 200f),
									1000, // emp
									100000f, // max range
									"mote_attractor_targeted_ship",
									MathUtils.getRandomNumberInRange(15f, 28f), // thickness
									//new Color(100,165,255,255),
									new Color(220, 217, 158, 255),
									new Color(255, 255, 255, 255)
							);
						}
					}
					else {
						for (int i = 0; i < 3; i++) {
							engine.spawnEmpArcVisual(
									SCK.getLocation(),
									SCK.getShip(),
									new Vector2f(SCK.getLocation()),
									null,
									MathUtils.getRandomNumberInRange(15f, 30f),
									new Color(220, 217, 158, 255),
									new Color(255, 255, 255, 255));
						}
					}
				}
			}
		}

		if (timer.intervalElapsed()) {
			if (fireamount > 0 && !hasfire) {
				float goingNumber = 300f;
				if (ship.getVariant().hasHullMod("unstable_injector")){
					goingNumber = 750f;
				}
				if (fireamount % 2 == 0 && FLW != null) {
						engine.spawnProjectile(ship, null, "SGB_Airbath", FLW.getLocation(), ship.getFacing() + roLeft, null);
					Global.getCombatEngine().addNebulaSmokeParticle(
							FLW.getLocation(),
							VectorUtils.rotate(new Vector2f(0,20f),FLW.getCurrAngle()),
							Math.max(ship.getCollisionRadius(), 18f)
									* MathUtils.getRandomNumberInRange(0.7f,1.75f),
							MathUtils.getRandomNumberInRange(0.073f,0.21f),
							0.1f, 0.15f,5f,
							colorNoiseBlend(new Color(218, 199, 191,200),
									new Color(37, 35, 35,175),(float) Math.random())
					);

						ship.getVelocity().set(VectorUtils.rotate(new Vector2f(goingNumber, 0), ship.getFacing() + 180f));
					Global.getSoundPlayer().playSound("SGB_Missile_L_fire", 1, 1, FLW.getLocation(), ship.getVelocity());
					roLeft = roLeft+15;
				} else {
						engine.spawnProjectile(ship, null, "SGB_Airbath", FRW.getLocation(), ship.getFacing() + roRight, null);
						Global.getCombatEngine().addNebulaSmokeParticle(
							FRW.getLocation(),
							VectorUtils.rotate(new Vector2f(0,20f),FRW.getCurrAngle()),
							Math.max(ship.getCollisionRadius(), 18f)
									* MathUtils.getRandomNumberInRange(0.7f,1.75f),
							MathUtils.getRandomNumberInRange(0.073f,0.21f),
							0.1f, 0.15f,5f,
							colorNoiseBlend(new Color(218, 199, 191,200),
									new Color(37, 35, 35,175),(float) Math.random())
						);
						ship.getVelocity().set(VectorUtils.rotate(new Vector2f(goingNumber, 0), ship.getFacing() + 180f));
					Global.getSoundPlayer().playSound("SGB_Missile_L_fire", 1, 1, FRW.getLocation(), ship.getVelocity());
					roRight = roRight-15;
				}
				fireamount--;
			} else {
				hasfire = true;
				timer.setElapsed(0);
				fireamount = 6;
				roLeft = -15;
				roRight = 15;
			}
		}

		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().modifyFlat(id, MAX_SPEED_BONUS);
			stats.getAcceleration().modifyPercent(id, 200f);
			stats.getDeceleration().modifyPercent(id, 200f);
			stats.getTurnAcceleration().modifyFlat(id, 30f);
			stats.getTurnAcceleration().modifyPercent(id, 200f);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);
			ship.setJitterUnder(ship, Color.orange, Math.min((effectLevel + 0.5f), 1f), 2, 0f, 10f);
		}
	}

	@Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		timer.setElapsed(0);
		hasfire = false;
		fireamount = 6;
		roLeft = -15;
		roRight = 15;
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
	}
}