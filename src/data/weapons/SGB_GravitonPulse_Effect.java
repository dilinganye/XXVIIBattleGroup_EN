package data.weapons;

import java.awt.*;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import data.utils.sgb.SGB_Color;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.TimeoutTracker;
import org.magiclib.util.MagicAnim;

import static data.weapons.SGB_ArcFurnace_EveryFrameEffect.colorBlend;

public class SGB_GravitonPulse_Effect implements BeamEffectPlugin {

	public static float EFFECT_DUR = 1f;
	
	public static float DAMAGE_PERCENT_ONE = 5f;
	public static float DAMAGE_PERCENT_TWO = 8f;
	public static float DAMAGE_PERCENT_THREE = 10f;
	private float width = 0;
	
	protected boolean wasZero = true;
	private boolean runOnce=false;
	private Color fringe;
	private final IntervalUtil timer = new IntervalUtil(0.45f,0.5f);
	float t = 0;
	float rate = 4; //变化速率 2/武器开火时间
	int w = 1;
	
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (engine.isPaused()) {
			return;
		}

		if(!runOnce){
			fringe = beam.getFringeColor();
			width = beam.getWidth();
			runOnce=true;
		}
		t += amount * rate * w;
		//Global.getLogger(this.getClass()).info("IAMMTHESTOMOFTHEDOOOOOOOSSSSSSEA - t:" + t);
		if (t >= 1) {
			t = 1;
			w = -1;
		}
		if (t <= 0) {
			t = 0;
			w = 1;
		}
		float theWidth = (float) FastTrig.sin(Math.toRadians(Math.max(0.05f,t)*90));
		//beam.setWidth(width*theWidth);
		beam.setWidth(width*theWidth);

		timer.advance(amount);
		Color finalLight = colorBlend(fringe,
				new Color(255, 0, 0), Math.min(timer.getElapsed()*2,1));
		beam.setFringeColor(finalLight);
		float RandomNum = (float)Math.random();
		if (MagicRender.screenCheck(0.1f, beam.getFrom()) && RandomNum > 0.75f ) {
			engine.addHitParticle(beam.getFrom(), beam.getSource().getVelocity(), 10f + 20f * theWidth + (float) Math.random() * 5, 1, 0.2f, beam.getCoreColor());
			engine.addHitParticle(beam.getTo(), new Vector2f(), 2f + 33f * theWidth, 1, 0.2f, beam.getFringeColor());

		}

		if(timer.getElapsed()>0.3f){
			beam.getDamage().setForceHardFlux(true);
		}
		else{
			beam.getDamage().setForceHardFlux(false);
		}
		//Global.getLogger(this.getClass()).info("IAMMTHESTOMOFTHEDOOOOOOOSSSSSSEA - width:" + width);
		//Global.getLogger(this.getClass()).info("IAMMTHESTOMOFTHEDOOOOOOOSSSSSSEA - theWidth:" + theWidth);


		CombatEntityAPI target = beam.getDamageTarget();
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f && beam.getWeapon() != null) {

			float dur = beam.getDamage().getDpsDuration();


			// needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
			if (!wasZero) dur = 0;
			wasZero = beam.getDamage().getDpsDuration() <= 0;
			
			// beam tick, apply damage modifier effect if needed
			if (dur > 0) {
				boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());
				if (hitShield) {
					ShipAPI ship = (ShipAPI) target;
					if (!ship.hasListenerOfClass(GravitonBeamDamageTakenMod.class)) {
						ship.addListener(new GravitonBeamDamageTakenMod(ship));
					}
					List<GravitonBeamDamageTakenMod> listeners = ship.getListeners(GravitonBeamDamageTakenMod.class);
					if (listeners.isEmpty()) return; // ???
					
					GravitonBeamDamageTakenMod listener = listeners.get(0);
					listener.notifyHit(beam.getWeapon());
				}
			}
		}
	}
	

	public static String DAMAGE_MOD_ID = "gravitonbeam_dam_mod";

	public static class GravitonBeamDamageTakenMod implements AdvanceableListener {
							//implements DamageTakenModifier, AdvanceableListener {
		protected ShipAPI ship;
		protected TimeoutTracker<WeaponAPI> recentHits = new TimeoutTracker<WeaponAPI>();
		public GravitonBeamDamageTakenMod(ShipAPI ship) {
			this.ship = ship;
			//ship.addListener(new GravitonBeamDamageTakenModRemover(ship));
		}
		
		public void notifyHit(WeaponAPI w) {
			recentHits.add(w, EFFECT_DUR, EFFECT_DUR);
			
		}
		
		public void advance(float amount) {
			recentHits.advance(amount);
			
			int beams = recentHits.getItems().size();

			float bonus = 0;
			if (beams == 1) {
				bonus = DAMAGE_PERCENT_ONE;
			} else if (beams == 2) {
				bonus = DAMAGE_PERCENT_TWO;
			} else if (beams >= 3) {
				bonus = DAMAGE_PERCENT_THREE;
			}
			
			if (bonus > 0) {
				ship.getMutableStats().getShieldDamageTakenMult().modifyMult(DAMAGE_MOD_ID, 1f + bonus * 0.01f);
			} else {
				ship.removeListener(this);
				ship.getMutableStats().getShieldDamageTakenMult().unmodify(DAMAGE_MOD_ID);
			}
		}

		public String modifyDamageTaken(Object param,
								   		CombatEntityAPI target, DamageAPI damage,
								   		Vector2f point, boolean shieldHit) {
			return null;
		}

	}

}
