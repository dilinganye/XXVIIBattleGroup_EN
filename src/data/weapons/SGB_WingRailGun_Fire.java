package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import data.hullmods.SGB_Achilles_FrontTurret;
import data.scripts.util.MagicRender;
import java.awt.Color;

import data.utils.sgb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class SGB_WingRailGun_Fire implements EveryFrameWeaponEffectPlugin, OnFireEffectPlugin {

    boolean runOnce = false;
    public static class SGB_WingRailGun_Fire_Range implements WeaponBaseRangeModifier {
        private  final  ShipAPI ship;
        public SGB_WingRailGun_Fire_Range(ShipAPI ship) {
            this.ship = ship;
        }
        public static SGB_WingRailGun_Fire_Range getInstance(ShipAPI ship){
            if(ship.hasListenerOfClass(SGB_WingRailGun_Fire.SGB_WingRailGun_Fire_Range.class)){
                return ship.getListeners(SGB_WingRailGun_Fire.SGB_WingRailGun_Fire_Range.class).get(0);
            }
            else{
                SGB_WingRailGun_Fire.SGB_WingRailGun_Fire_Range listener = new SGB_WingRailGun_Fire_Range(ship);
                ship.addListener(listener);
                return listener;
            }
        }
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getShip() == ship && ship.isFighter() && ship.getWing() != null && ship.getWing().getSource() != null) {
                return ship.getWing().getSource().getShip().getCollisionRadius()*2;
            }
            return 0f;
        }
    }
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if(engine.isPaused() || weapon.getShip().getOriginalOwner()==-1){return;}
        if(!runOnce){
            SGB_WingRailGun_Fire_Range.getInstance(weapon.getShip());
            runOnce = true;
        }
        //Thinking about how to let the wing got more range, which same as the collection range of the carrier.
        if(weapon.getShip() != null && weapon.getShip().isFighter()) {
        }

        if(weapon.getChargeLevel()==1) {
            //recoil
            Float vectorAS = 1F; //左
            Vector2f EmptyAmmoVector = new Vector2f(MathUtils.getRandomNumberInRange(-40,-20), vectorAS * 100f );
            Vector2f ammoVector = MathUtils.getRandomPointInCircle(VectorUtils.rotate(EmptyAmmoVector,weapon.getCurrAngle()),10);

            Vector2f weaponAddVector = new Vector2f(-1,vectorAS * 8f); //武器开火所在位置
            Vector2f USE_weaponVector = VectorUtils.rotate(weaponAddVector,weapon.getCurrAngle());
            Vector2f ammoPositionVector = new Vector2f(weapon.getLocation().x + USE_weaponVector.x,
                    weapon.getLocation().y + USE_weaponVector.y);

            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "SGB_Ammo"),
                    ammoPositionVector,
                    ammoVector,
                    new Vector2f(7, 8),
                    new Vector2f(-1, -1),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    new Color(255, 233, 190, 252),
                    true,
                    0.2f,
                    1f,
                    0.3f
            );
            if (MagicRender.screenCheck(0.1f, weapon.getLocation())) {
                for (DamagingProjectileAPI p : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 150)) {
                    for(int i=0; i<10; i++) {
                        Vector2f drift = MathUtils.getRandomPointInCone(weapon.getShip().getVelocity(),
                                MathUtils.getRandomNumberInRange(25, 70),
                                weapon.getCurrAngle() - 5, weapon.getCurrAngle() + 5);

                        if (p.getWeapon() != weapon) continue;
                        engine.addHitParticle(
                                p.getLocation(),
                                drift,
                                10f,
                                0.5f,
                                1f,
                                new Color(206, 255, 253, 55));
                    }
                }
            }
        }
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        Vector2f Fire_weaponAddVector = new Vector2f(-1, 15f); //武器云雾位置
        Vector2f Fire_USE_weaponVector = VectorUtils.rotate(Fire_weaponAddVector,weapon.getCurrAngle());
        // Wave
        I18nUtil.easyWaving(projectile.getLocation(), projectile.getVelocity(), 80, 3, 10);
        engine.addNebulaParticle(
                projectile.getLocation(),
                Fire_USE_weaponVector,
                MathUtils.getRandomNumberInRange(20, 40),
                2,
                0.1f,
                0.3f,
                1f,
                new Color(147, 201, 199,100)
        );
        // recoil on ship
        Vector2f.add(weapon.getShip().getVelocity(), MathUtils.getPoint(
                        new Vector2f(),
                        300,
                        weapon.getCurrAngle()+180),
                weapon.getShip().getVelocity()
        );
        weapon.getShip().setAngularVelocity(
                weapon.getShip().getAngularVelocity() + MathUtils.getRandomNumberInRange(90f,120f));
    }
}