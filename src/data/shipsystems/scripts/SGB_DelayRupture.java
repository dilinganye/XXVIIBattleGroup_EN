package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.MoteControlScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicAnim;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static data.missions.SGB_20FAboveTheGround.MissionDefinition.colorNoiseBlend;

public class SGB_DelayRupture extends BaseShipSystemScript {

	private boolean runOnce = false;
	private WeaponAPI SCK;
	private final IntervalUtil timer = new IntervalUtil(0.05f, 0.1f);

	private static final Map<Integer, Float> LAUCH_ANGLE = new HashMap<>(4);

	public final static float MAX_SPEED_BONUS =100f, MAX_TIME_SPEED = 10f;
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
				if (w.getSlot().getId().equals("Shock")) {
					SCK = w;
				}
			}
			runOnce = true;
		}

		float amount = engine.getElapsedInLastFrame();
		timer.advance(amount);

		if (state == ShipSystemStatsScript.State.IN) {
			ship.setJitterUnder(ship, new Color(230, 255, 0, 69), Math.min((effectLevel + 0.5f), 1f), 2, 0f, 10f);

		}
		fireInterval.advance(amount);
			if (fireInterval.intervalElapsed()) {
				for (int i = 0; i < 3; i++) {
					engine.spawnEmpArcVisual(
							MathUtils.getRandomPointInCircle(SCK.getLocation(),ship.getCollisionRadius()/1.725f+effectLevel*ship.getCollisionRadius()*1.25f),
							null,
							new Vector2f(SCK.getLocation()),
							null,
							MathUtils.getRandomNumberInRange(15f, 30f),
							new Color(65, 129, 57, 255),
							new Color(255, 255, 255, 255));
				}
			}

		if (timer.intervalElapsed()) {
			Global.getCombatEngine().addNebulaSmokeParticle(
							SCK.getLocation(),
							VectorUtils.rotate(new Vector2f(0,20f),SCK.getCurrAngle()),
							Math.max(ship.getCollisionRadius(), 18f)
									* MathUtils.getRandomNumberInRange(0.7f,1.75f),
							MathUtils.getRandomNumberInRange(0.073f,0.21f),
							0.25f, 0.45f,3.5f,
							colorNoiseBlend(new Color(134, 59, 98,200),
									new Color(66, 225, 222,175),(float) Math.random())
			);
		} else {
				timer.setElapsed(0);
		}

		float effect = Math.min(1, Math.max(0, MagicAnim.smoothReturnNormalizeRange(1f-effectLevel, 0, 1)));
		if (state == ShipSystemStatsScript.State.OUT) {
			ship.setJitterUnder(ship, new Color(212, 246, 66, 215), 0.5f*effect, 10, 5+20f*effect, 5+10f*effect);
		};
		if (state == ShipSystemStatsScript.State.OUT || state == State.IN) {
			if(ship!=null) {
				// Are you the player?————
				boolean player = false;
				if (stats.getEntity() instanceof ShipAPI) {
					player = ship == Global.getCombatEngine().getPlayerShip();
					id = id + "_" + ship.getId();
				} else {
					return;
				}
				// So I might give ya more time
				float shipTimeMult = 1f + (MAX_TIME_SPEED - 1f) * effectLevel;
				stats.getTimeMult().modifyMult(id, shipTimeMult);
				if (player) {
					Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
				} else {
					Global.getCombatEngine().getTimeMult().unmodify(id);
				}
				// ————————
				ship.setJitterUnder(ship, Color.CYAN, 0.5f*effect, 5, 5+5f*effect, 5+10f*effect);
				if(Math.random()>0.9f){
					ship.addAfterimage(new Color(230, 255, 0, 69), 0, 0, -ship.getVelocity().x, -ship.getVelocity().y, 5+50*effect, 0, 0, 2*effect, false, false, false);
				}
				/*if(!stats.getTimeMult().getPercentMods().containsKey(id)){
					ship.setPhased(true);
				} else if(ship.isPhased()){
					ship.setPhased(false);
				}
				 */
				if (fireInterval.intervalElapsed()) {
					Global.getCombatEngine().addNebulaSmokeParticle(
							SCK.getLocation(),
							VectorUtils.rotate(new Vector2f(0, 20f), SCK.getCurrAngle()),
							Math.max(ship.getCollisionRadius(), 18f)
									* MathUtils.getRandomNumberInRange(0.7f, 1.75f),
							MathUtils.getRandomNumberInRange(0.073f, 0.21f),
							0.1f, 0.15f, 5f,
							colorNoiseBlend(new Color(218, 212, 191, 200),
									new Color(66, 48, 89, 175), (float) Math.random())
					);
				}
			}
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
		ShipAPI ship = null;
		boolean player = false;
		timer.setElapsed(0);
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
	}
}