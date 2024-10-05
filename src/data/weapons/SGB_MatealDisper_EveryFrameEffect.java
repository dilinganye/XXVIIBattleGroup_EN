package data.weapons;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.impl.combat.RealityDisruptorChargeGlow.isProjectileExpired;
import static com.fs.starfarer.api.util.Misc.ZERO;
import static data.shipsystems.scripts.SGB_AsAboveSoBelow.getRandomBlendColor;

public class SGB_MatealDisper_EveryFrameEffect extends BaseEveryFrameCombatPlugin {

	private CombatEngineAPI engine = Global.getCombatEngine();
	protected DamagingProjectileAPI proj; //	进行一个定义
	protected IntervalUtil arcInterval = new IntervalUtil(0.17f, 0.23f),
			hitPInterval = new IntervalUtil(0.15f, 0.4f); //	还是计时器好用 - 对电弧以及物品的特效进行

	public SGB_MatealDisper_EveryFrameEffect(DamagingProjectileAPI proj){ //	进行一个绑定存储
		this.proj = proj;
		if(engine!=null)
			engine.addPlugin(this); //	把自己加入到engine 这样才会运作
	}
	public void advance(float amount, List<InputEventAPI> events) { //	进行一个特效

		if(engine==null) return; //	别问
		if(proj==null||proj.isExpired()||!engine.isEntityInPlay(proj)){ //	射弹不存在时移除自身
			Global.getCombatEngine().removePlugin(this);
			return;
		}
		if(engine.isPaused()) return;
		// Null判定-上 —————— 实际特效-下
		arcInterval.advance(amount);
		if (arcInterval.intervalElapsed()) {
			engine.spawnEmpArcVisual(MathUtils.getRandomPointInCircle(proj.getLocation(),
							MathUtils.getRandomNumberInRange(10f,127f)),
					proj.getSource(),
					proj.getLocation(), proj.getSource(),
					10f * MathUtils.getRandomNumberInRange(2.72f,0.8f), // thickness
					new Color(91, 19, 133, 255),
					new Color(235, 204, 255, 255)
			);
		}

		hitPInterval.advance(amount);
		if (hitPInterval.intervalElapsed()) {
			engine.addHitParticle(proj.getLocation(),proj.getVelocity(),
					0+MathUtils.getRandomNumberInRange(30f,40f),1f,
					0+MathUtils.getRandomNumberInRange(0.35f,0.5f),
					0+MathUtils.getRandomNumberInRange(0.9f,1.2f),
					getRandomBlendColor(proj.getProjectileSpec().getFringeColor(), proj.getProjectileSpec().getFringeColor())
			);
		}
	}
}