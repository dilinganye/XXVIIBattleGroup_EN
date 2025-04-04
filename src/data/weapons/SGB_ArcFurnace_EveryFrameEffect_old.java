package data.weapons;

import java.awt.Color;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicAnim;
import data.utils.sgb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class SGB_ArcFurnace_EveryFrameEffect_old implements EveryFrameWeaponEffectPlugin {
	//_____________
	static boolean  runOnce = false, firing = false, fired = false, midStoped = false;
	public final String ArcL_ID = "WS0004", ArcR_ID = "WS0005";
	private WeaponAPI ArcL, ArcR;
	protected IntervalUtil AnmiBack_Interval = new IntervalUtil(1.25f, 1.25f);
	float Arc_Height_L, Arc_Width_L;	float Arc_Height_R, Arc_Width_R;
	//_____________

	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) { //	进行一个特效
		if (engine.isPaused()) return;
		if (weapon == null) return;

		ShipAPI ship = weapon.getShip();
		if (ship == null) return;
		// Null判定-上 —————— 实际特效-下
		if(!runOnce){
			runOnce=true;
			if (weapon.getId().equals(ArcL_ID)) {
				ArcL = weapon;
				Arc_Width_L = weapon.getSprite().getWidth();
				Arc_Height_L = weapon.getSprite().getHeight();
			}
			if (weapon.getId().equals(ArcR_ID)) {
				ArcR = weapon;
				Arc_Width_R = weapon.getSprite().getWidth();
				Arc_Height_R = weapon.getSprite().getHeight();
			}
			return;
		}
		//_________IMPORTANT_Before_________
		Vector2f muzzle, muzzleSpeed;
		muzzle = MathUtils.getPoint(
				weapon.getLocation(),
				6, //	特效所需的相对武器核心位置
				weapon.getCurrAngle()
		);
		muzzleSpeed = MathUtils.getPoint(
				ZERO,
				10, //	特效所需的相对武器核心位置
				weapon.getCurrAngle()
		);
		//______Locations________
		//if(firing && weapon.getChargeLevel()<1){
		if(weapon.getChargeLevel()<1){
			//midStoped = true;
			//Add Fire Light & ammo out
			I18nUtil.easyWaving(muzzle, muzzleSpeed, 50, 3, 10);
			engine.addHitParticle(
					muzzle,
					weapon.getShip().getVelocity(),
					10,
					0.5f,
					1,
					new Color(239, 189, 139)
			);
			// 单独使用计时器的回收动画情况下[利用cargLv[CG]来实现补正效果]
			AnmiBack_Interval.advance(amount);
			float anmi_dura = AnmiBack_Interval.getIntervalDuration();
			float cargLv = weapon.getChargeLevel();
			float speedW = MagicAnim.smoothNormalizeRange(anmi_dura, 0f, 1f);
			float speedH = MagicAnim.smoothNormalizeRange(anmi_dura, 0f, 0.67f);

			float CurrLW = Arc_Width_L/2 +speedW*20f;
			float CurrRW = Arc_Width_R/2 -speedW*20f;
			float CurrLH = Arc_Height_L/2 +speedH*8f;
			float CurrRH = Arc_Height_R/2 +speedH*8f;
			ArcL.getSprite().setCenter(CurrLW,CurrLH);
			ArcR.getSprite().setCenter(CurrRW,CurrRH);
			//Anmi float
			if (!AnmiBack_Interval.intervalElapsed()) {	// Anmi had not been set in to ZERO
			}
		} else if (weapon.getChargeLevel()==1){
			fired=true;
			if(weapon.getId().equals(ArcL_ID)){	//确认左右-左
				if((weapon.getSprite().getWidth() != Arc_Width_L) ||	//确认武器动画到位/即不到位不许开火
						(weapon.getSprite().getHeight() != Arc_Width_L)){
					SGB_differentSideWeapon_FireSpec(weapon,muzzle, muzzleSpeed);
				}
			}
			if(weapon.getId().equals(ArcR_ID)){	//确认左右-右
				if((weapon.getSprite().getWidth() != Arc_Width_R) ||	//确认武器动画到位/即不到位不许开火
						(weapon.getSprite().getHeight() != Arc_Width_R)){
					SGB_differentSideWeapon_FireSpec(weapon,muzzle, muzzleSpeed);
				}
			}
		} else if(weapon.getChargeLevel()==0){
			firing=false;
			midStoped = false;
		}
		//	N.O.T.E
		//	开火后进入正常状态，当中止开火时，分为两个判定组，
		//	·一个组利用计时器模拟回收（不和ChargeDown等绑定）
		//	·另一个组使用新的ChargeUp进行判定
		//	从上述两组中取最大数值来进行特效操作
		//	综合最大值未归位时加快CG_UP的速度，武器不恢复弹药[归位时恢复好像更好做]
		//

		//	-=思维笔记=-
		//	目标：尽量符合观感[酷]，且具备一定误差规避效果
		//	已知中止开火时Charge会逐渐(后续简称CG)归零，即停止开火时会进行CG_DOWN的计时，并在继续开火时再跑一遍CG_UP
		//	所以应让CG_DOWN默认为0[我甚至想让他是负数]，CG_UP为正常动画时间数值，组二使用同正常动画时间数值的计时器

		//	Charge=0只代表武器间歇性停火，两组计时器的综合最大值才决定炮管实际动画位置
		//	所以当动画位置未彻底归零[收归舱内]时，应加快CG_UP的速度 - 或者说减小数值来保证武器有一定程度上的误差规避效果

		//	综合最大值未归零时武器不会恢复弹药，从而模拟必须收回才能装弹的效果
		//_____
		//	上述做完后再考虑烧烂枪管的事-

		if(fired){

		}
	}
	public void SGB_differentSideWeapon_FireSpec(WeaponAPI weapon,Vector2f muzzle,Vector2f muzzleSpeed) {
		weapon.setForceNoFireOneFrame(true);
		firing=true;
		midStoped = false;
		I18nUtil.easyWaving(muzzle, muzzleSpeed, 28, 3, 8);
	}
}