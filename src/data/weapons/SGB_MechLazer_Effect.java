package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.TimeoutTracker;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

import static data.weapons.SGB_ArcFurnace_EveryFrameEffect.colorBlend;

public class SGB_MechLazer_Effect implements BeamEffectPlugin {

	private float width = 0;

	protected boolean wasZero = true;
	private boolean runOnce = false;
	private Color fringe;
	private final IntervalUtil timer = new IntervalUtil(0.45f, 0.5f);
	float t = 0;
	float rate = 4; //变化速率 2/武器开火时间
	int w = 1;


	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (engine.isPaused()) {
			return;
		}

		if (!runOnce) {
			fringe = beam.getFringeColor();
			width = beam.getWidth();
			runOnce = true;
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
		float theWidth = (float) FastTrig.sin(Math.toRadians(Math.max(0.05f, t) * 90));
		//beam.setWidth(width*theWidth);
		beam.setWidth(width * theWidth);

		timer.advance(amount);
		Color finalLight = colorBlend(fringe,
				new Color(255, 221, 0), Math.min(timer.getElapsed() * 2, 1));
		beam.setFringeColor(finalLight);
		float RandomNum = (float) Math.random();
		if (MagicRender.screenCheck(0.1f, beam.getFrom()) && RandomNum > 0.9f) {
			engine.addHitParticle(beam.getFrom(), beam.getSource().getVelocity(), 2f + 5f * theWidth + (float) Math.random() * 5, 0.2f, 0.2f, beam.getFringeColor());
			engine.addHitParticle(beam.getTo(), new Vector2f(), 2f + 25f * theWidth, 1, 0.2f, beam.getFringeColor());

		}

		if (timer.getElapsed() > 0.3f) {
			beam.getDamage().setForceHardFlux(true);
		} else {
			beam.getDamage().setForceHardFlux(false);
		}
	}
}
