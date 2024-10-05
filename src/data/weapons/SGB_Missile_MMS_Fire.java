package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class SGB_Missile_MMS_Fire implements EveryFrameWeaponEffectPlugin {
        
    private boolean firing=false;
    private float  recoil=0;

    private final Color PARTICLE_COLOR = new Color(241, 203, 182);
    private final float PARTICLE_SIZE = 4f;
    private final float PARTICLE_BRIGHTNESS = 0.98f;
    private final float PARTICLE_DURATION = 0.5f;

    private final float EXPLOSION_SIZE = 30f;


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        
        if(engine.isPaused() || weapon.getShip().getOriginalOwner()==-1){return;}
        if(weapon.getId().equals("SGB_Shackles_MissileLauncher")
                && weapon.getShip() != null
                && weapon.getShip().getHullSpec().getBaseHullId().equals("SGB_Whip")){
        }
        if(weapon.getChargeLevel()==1 && weapon.getAmmo()<0f){

            Vector2f muzzle;

            //recoil
            recoil=Math.min(1, recoil+0.33f);

            //muzzle non hidden weapon
            if(MagicRender.screenCheck(0.1f, weapon.getLocation())){

                for(DamagingProjectileAPI p : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 150)) {
                    if(p.getWeapon()!=weapon)continue;
                    engine.addHitParticle(p.getLocation(), weapon.getShip().getVelocity(), 50, 0.5f, 0.33f, new Color(200, 96, 0, 32));
                }
                muzzle = weapon.getLocation();

                engine.addHitParticle(
                        muzzle,
                        weapon.getShip().getVelocity(),
                        30,
                        0.5f,
                        1,
                        Color.green
                );
                engine.addHitParticle(
                        muzzle,
                        weapon.getShip().getVelocity(),
                        30,
                        0.5f,
                        0.3f,
                        Color.red
                );
                for (float i = 0; i <= 5; i++) {
                    float particleSize = MathUtils.getRandomNumberInRange(PARTICLE_SIZE - 2, PARTICLE_SIZE + 2);
                    Vector2f randSpawnPoint = MathUtils.getRandomPointOnCircumference(muzzle, EXPLOSION_SIZE);
                    Vector2f randExitVector = VectorUtils.getDirectionalVector(muzzle, randSpawnPoint);
                    engine.addHitParticle(
                            randSpawnPoint,
                            randExitVector,
                            particleSize,
                            PARTICLE_BRIGHTNESS,
                            PARTICLE_DURATION,
                            PARTICLE_COLOR);
                }
        if(firing && weapon.getChargeLevel()<1){
            Global.getSoundPlayer().playSound("SGB_Missile_L_reload", 1, 1, weapon.getLocation(), weapon.getShip().getVelocity());
            firing=false;
        } else if (weapon.getChargeLevel()==1){
            firing=true;
        }
    }
}}}