package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import data.utils.sgb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.shipsystems.scripts.SGB_AsAboveSoBelow.getRandomBlendColor;

public class SGB_MatealDisper_FireEffect implements EveryFrameWeaponEffectPlugin {
    static boolean  runOnce = false, hidden = false, fired = false;
    Vector2f ZERO = new Vector2f(0,0);
    protected IntervalUtil chargeInterval = new IntervalUtil(0.09f, 0.45f),
            AfterFireSmokeChargeInterval = new IntervalUtil(0.035f, 0.15f); //	充能特效进行时

    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (engine.isPaused() || weapon.getShip().getOriginalOwner() == -1) {
            return;
        }

        if (!runOnce) {
            runOnce = true;
            if (weapon.getSlot().isHidden()) {
                hidden = true;
            }
            return;
        }

        if (!hidden) {
            //______Locations________
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
            //-----------
            if(weapon.getChargeLevel() == 0){
                fired = false;
            }
            if(weapon.getChargeLevel() == 1){
                fired = true;
                ///________Wave__________
                I18nUtil.easyWaving(muzzle, muzzleSpeed, 28, 5, 8);
            }
            if (!fired && weapon.getChargeLevel() > 0 && weapon.getChargeLevel() < 1) {
                float chargeLevel = weapon.getChargeLevel();

                AfterFireSmokeChargeInterval.advance(amount);
                ///________Smoke Sprite__________
                if (AfterFireSmokeChargeInterval.intervalElapsed()) {
                    for (int i = 0; i < MathUtils.getRandomNumberInRange(4,8); i++) {
                        MagicRender.battlespace(
                                Global.getSettings().getSprite("fx", "SGB_Va_Smoke"),
                                muzzle,
                                MathUtils.getRandomPointInCircle(weapon.getShip().getVelocity(),10f),
                                new Vector2f(40, 40),
                                new Vector2f(20, 20),
                                //angle,
                                360 * (float) Math.random(),
                                0,
                                new Color(16, 1, 1, 47),
                                false,
                                0.1f,
                                1f,
                                1f
                        );
                    }
                    MagicRender.battlespace(
                            Global.getSettings().getSprite("fx", "SGB_Va_Explosion_ring"),
                            muzzle,
                            weapon.getShip().getVelocity(),
                            new Vector2f(10, 10),
                            new Vector2f(60, 60),
                            //angle,
                            360 * (float) Math.random(),
                            0,
                            new Color(133, 19, 19, 175),
                            false,
                            0f,
                            0f,
                            0.17f
                    );
                    MagicRender.battlespace(
                            Global.getSettings().getSprite("fx", "SGB_Va_Explosion"),
                            muzzle,
                            weapon.getShip().getVelocity(),
                            new Vector2f(20, 20),
                            new Vector2f(18, 18),
                            //angle,
                            360 * (float) Math.random(),
                            0,
                            new Color(255, 201, 201, 255),
                            true,
                            0.03f,
                            0.05f,
                            0.02f
                    );
                }
                chargeInterval.advance(amount);
                if (chargeInterval.intervalElapsed()) {
                    ///_______EMP__________
                    Vector2f arcLocation;
                    arcLocation = MathUtils.getPoint(
                            weapon.getLocation(),
                            MathUtils.getRandomNumberInRange(6f, 28f), //
                            weapon.getCurrAngle()
                    );
                    engine.spawnEmpArcVisual(MathUtils.getRandomPointInCircle(arcLocation,
                                    MathUtils.getRandomNumberInRange(10f * chargeLevel, 80f * chargeLevel)),
                            weapon.getShip(),
                            muzzle, weapon.getShip(),
                            chargeLevel * 10f * MathUtils.getRandomNumberInRange(0.8f, 2.72f), // thickness
                            new Color(133, 19, 19, 255),
                            new Color(255, 204, 210, 255)
                    );
                }
            }
            if(fired && weapon.getChargeLevel() > 0 && weapon.getChargeLevel() < 1){
                float chargeLevel = weapon.getChargeLevel();

                AfterFireSmokeChargeInterval.advance(amount);
                if (AfterFireSmokeChargeInterval.intervalElapsed()) {
                    engine.addHitParticle(weapon.getLocation(),ZERO,
                            0+MathUtils.getRandomNumberInRange(30f,40f),1f,
                            0+MathUtils.getRandomNumberInRange(0.35f,0.5f),
                            0+MathUtils.getRandomNumberInRange(0.9f,1.2f),
                            getRandomBlendColor( new Color(253, 127, 138, 175),  new Color(93, 16, 98, 175))
                    );
                }
                chargeInterval.advance(amount);
                if (chargeInterval.intervalElapsed()) {
                    for (int i = 0; i < MathUtils.getRandomNumberInRange(1,3); i++) {
                        ///_______EMP__________
                        Vector2f arcLocation;
                        arcLocation = MathUtils.getPoint(
                                weapon.getLocation(),
                                MathUtils.getRandomNumberInRange(18f,25f) * chargeLevel,
                                weapon.getCurrAngle()
                        );
                        engine.spawnEmpArcVisual(MathUtils.getRandomPointInCircle(arcLocation,
                                        MathUtils.getRandomNumberInRange(10f * chargeLevel, 80f * chargeLevel)),
                                weapon.getShip(),
                                muzzle, weapon.getShip(),
                                chargeLevel * 10f * MathUtils.getRandomNumberInRange(0.8f, 2.72f), // thickness
                                new Color(133, 19, 19, 255),
                                new Color(255, 204, 210, 255)
                        );
                    }
                }
            }
        }
    }
}