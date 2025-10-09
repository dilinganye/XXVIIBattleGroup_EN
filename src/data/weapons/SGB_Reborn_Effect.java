package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import data.utils.sgb.SGB_BoxBasedUtil;
import data.utils.sgb.SGB_Color;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

import static data.missions.SGB_20FAboveTheGround.MissionDefinition.colorNoiseBlend;

public class SGB_Reborn_Effect implements EveryFrameWeaponEffectPlugin, EveryFrameWeaponEffectPluginWithAdvanceAfter, OnFireEffectPlugin {
    public final String Reborn_Top_ID = "SGB_Soueifiner_Shield";

    private WeaponAPI Reborn_Top;
    private IntervalUtil missileFireInterval = new IntervalUtil(0.1f, 0.8f);
    private IntervalUtil tipsInterval = new IntervalUtil(1f, 2f);

    private float Reborn_Top_Height,Reborn_Top_Width, numbMoving, reFireDelay,ammoPerSec;
    private final float THEMOVEPIXEL = 12,THEMOVESPEEDY = 2, IntervalFloat = 5;
    private boolean runOnce = false, openGate = false, smokeByGate = false;
    private ShipAPI ship;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (!runOnce || ship == null) { // Get data
            ship = weapon.getShip();
            List<WeaponAPI> weapons = ship.getAllWeapons();
            for (WeaponAPI w : weapons) {
                if (w.getId().equals(Reborn_Top_ID)) {
                    Reborn_Top = w;
                    Reborn_Top_Height = w.getSprite().getHeight();
                    Reborn_Top_Width = w.getSprite().getWidth();
                }
            }
            reFireDelay = weapon.getRefireDelay();
            ammoPerSec = weapon.getAmmoTracker().getAmmoPerSecond();
            runOnce = true;
        }

    }

    @Override
    public void advanceAfter(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (Global.getCombatEngine().isPaused() || ship == null) {
            return;
        }

        float fluxLevel = ship.getFluxTracker().getFluxLevel();
        boolean isVenting = ship.getFluxTracker().isOverloadedOrVenting() && ship.getFluxTracker().getFluxLevel()>=0.02f, isOverload = ship.getFluxTracker().isOverloaded();
        openGate = (ship.getFluxTracker().getFluxLevel() > 0.5f);

        float ADDNumber = fluxLevel * THEMOVESPEEDY;
        if(openGate || isVenting){
            numbMoving += Math.min(ADDNumber,1);}
        else{
            numbMoving -= Math.min(ADDNumber,1);
            smokeByGate = true;
        }

        numbMoving = Math.min(Math.max(numbMoving,0),THEMOVEPIXEL);

        float movingPixelY = Reborn_Top_Height / 2 + numbMoving;

        Reborn_Top.getSprite().setCenter(Reborn_Top_Width/2, movingPixelY);

        boolean weaponOnline = (numbMoving >= THEMOVEPIXEL * 3 / 4);
        weapon.setForceNoFireOneFrame(!weaponOnline);

        Color cloudColor = colorNoiseBlend(new Color(218, 199, 191,200),
                new Color(37, 35, 35,175),(float) Math.random());
        Color cloudColorSoft = colorNoiseBlend(new Color(218, 199, 191,125),
                new Color(37, 35, 35,75),(float) Math.random());
        if (weaponOnline
                && smokeByGate
        ){
            Global.getCombatEngine().addNebulaSmokeParticle(
                    weapon.getLocation(),
                    VectorUtils.rotate(new Vector2f(0,20f),ship.getFacing()),
                    MathUtils.getRandomNumberInRange(30f,60f),
                    MathUtils.getRandomNumberInRange(0.073f,0.21f),
                    0.1f, 0.15f,5f,
                    cloudColor
            );
            smokeByGate = false;
        } else if (smokeByGate) {
            smokeByGate = false;
        }
        if(ship.getFluxLevel()>=0.25){
            tipsInterval.advance(amount * Math.max(ship.getFluxLevel(),0.63f)*100/63  /1.75f);
            if (missileFireInterval.intervalElapsed()) {
                for (int i = 0; i < 2; i++) {
                    if (SGB_BoxBasedUtil.hadMagicLib()) {
                        MagicRender.battlespace(
                                Global.getSettings().getSprite("fx", "SGB_smoke_1"),
                                weapon.getLocation(),
                                MathUtils.getRandomPointInCircle(weapon.getShip().getVelocity(),50f),
                                new Vector2f(25, 25),
                                new Vector2f(45, 45),
                                //angle,
                                360 * (float) Math.random(),
                                0,
                                colorNoiseBlend(cloudColorSoft,
                                        new Color(166, 93, 20,42), (float) Math.random()),
                                false,
                                0.3f,
                                0.1f,
                                1f
                        );
                    }
                }
            }
        }
        if(isVenting && ship.getFluxLevel()>=0.25){
            float overload;
            if(isOverload){
                overload = 2*IntervalFloat;
            }
            else{
                overload = 1*IntervalFloat;
            }
            missileFireInterval.advance(amount * Math.max(ship.getFluxLevel(),0.5f)*overload);
            if (missileFireInterval.intervalElapsed()) {
                DamagingProjectileAPI missile = (DamagingProjectileAPI) engine.spawnProjectile(weapon.getShip(), weapon, weapon.getId(), weapon.getLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                SGB_Missile_Reborn_Effect e = new SGB_Missile_Reborn_Effect(missile,false);
                if(SGB_BoxBasedUtil.hadMagicLib()){
                    MagicRender.battlespace(
                            Global.getSettings().getSprite("fx", "SGB_smoke_01"),
                            weapon.getLocation(),
                            weapon.getShip().getVelocity(),
                            new Vector2f(85, 85),
                            new Vector2f(-120, -120),
                            //angle,
                            360 * (float) Math.random(),
                            0,
                            Color.white,
                            true,
                            0.1f,
                            1f,
                            0.25f
                    );
                }
                ship.getFluxTracker().decreaseFlux(100f);
            }
            weapon.getAmmoTracker().setAmmoPerSecond(ammoPerSec+Math.max(ship.getFluxLevel(),0.5f)*2f);
        }
        else{weapon.getAmmoTracker().setAmmoPerSecond(ammoPerSec);}
    }

    @Override //	使用OnFire为projectile挂载一个每帧以实现效果
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        SGB_Missile_Reborn_Effect e = new SGB_Missile_Reborn_Effect(projectile,true);
        //projectile.getShield().isOn();
        if(SGB_BoxBasedUtil.hadMagicLib()){
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "SGB_smoke_01"),
                    weapon.getLocation(),
                    weapon.getShip().getVelocity(),
                    new Vector2f(85, 85),
                    new Vector2f(-120, -120),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    Color.white,
                    true,
                    0.1f,
                    1f,
                    0.25f
            );
        }
    }
}
