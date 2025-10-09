package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.missions.SGB_20FAboveTheGround.MissionDefinition.colorNoiseBlend;

public class SGB_HeatReactor implements EveryFrameWeaponEffectPlugin {

    float Defacing = 0, movingRate = 22;
    float NormalTurnRate = 40, overloadTurnRate = 80,  //转速
            turnRate = 0, moving = 0;
    int isLeft = 1; Boolean isMid = false, runOnce = false, shouldOpen = false;   //用来识别左右
    private IntervalUtil LightInterval = new IntervalUtil(0.07f, 0.25f);
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        ShipAPI ship = weapon.getShip();
        if (!ship.isAlive()) {
            return;
        }
        if(!runOnce){
            if(weapon.getSlot().getLocation().y == 0){
                isMid = true;
            }else{
                isLeft = weapon.getSlot().getLocation().y > 0 ? 1 : -1;  //y大于0的武器槽在武器左边,正负乘子为1，y小于0意味着在右边，乘子为-1
            }
            runOnce = true;
        }

        //何时开启散热:
        if(ship.getFluxTracker().isOverloaded()){
            shouldOpen = true;
            turnRate = overloadTurnRate;
            //Global.getLogger(this.getClass()).info("TEST - isOverloaded,shouldOpen:" + shouldOpen);
        } else if (ship.getFluxTracker().isVenting()) {
            shouldOpen = true;
            turnRate = NormalTurnRate;
            //Global.getLogger(this.getClass()).info("TEST - isOverloaded,isVenting:" + shouldOpen);
        } else if (moving>=0) {
            shouldOpen = false;

        } else{
            shouldOpen = false;
            turnRate = 0;
        }

        if(shouldOpen){
            moving += amount * movingRate;
            //Global.getLogger(this.getClass()).info("TEST - shouldOpen,moving:" + moving);
        }else {
            moving -= amount * movingRate*2f;
            //Global.getLogger(this.getClass()).info("TEST - shouldOpenElse,moving:" + moving);
        }
        if(moving!=0) {
            moving = Math.max(Math.min(turnRate, moving), 0);
           // Global.getLogger(this.getClass()).info("TEST - moving!=0,moving:" + moving);
        }

        if(moving>=0){
            if(Math.random()>=0.665f) {
                engine.addSmoothParticle(
                        weapon.getLocation(),
                        MathUtils.getRandomPointInCircle(
                                VectorUtils.rotate(new Vector2f(0, isLeft * moving * 2.75f), ship.getFacing()),
                                moving * 1.5f),
                        Math.min(moving, 40f) * MathUtils.getRandomNumberInRange(0.2f, 1f),
                        MathUtils.getRandomNumberInRange(0.25f, 0.75f),
                        MathUtils.getRandomNumberInRange(0.32f, 0.82f),
                        MathUtils.getRandomNumberInRange(0.8f, 1.2f),
                        colorNoiseBlend(new Color(149, 196, 87, 75),
                                new Color(255, 28, 28, 175), (float) Math.random())
                );
            }
            if(Math.random()>=0.32f) {

                Global.getCombatEngine().addNebulaSmokeParticle(
                        weapon.getLocation(),
                        VectorUtils.rotate(new Vector2f(0, isLeft * moving * 3f), ship.getFacing()),
                        Math.min(moving, 20f) * MathUtils.getRandomNumberInRange(0.2f, 0.67f), //删除Math.min会变成烟雾风暴极点
                        Math.min(moving, 25f) * MathUtils.getRandomNumberInRange(0.7f, 1f),
                        MathUtils.getRandomNumberInRange(0.01f, 0.32f),
                        MathUtils.getRandomNumberInRange(0.15f, 0.82f),
                        MathUtils.getRandomNumberInRange(0.8f, 1.2f),
                        colorNoiseBlend(new Color(210, 201, 186, 175),
                                new Color(49, 21, 21, 43), (float) Math.random())
                );
            }
        }
        //Global.getLogger(this.getClass()).info("TEST - Done,moving:" + moving);
       // Global.getLogger(this.getClass()).info("TEST - Done,turnRate:" + turnRate);
       // Global.getLogger(this.getClass()).info("TEST - Done,setCurrAngle:" + isLeft*moving + ship.getFacing());
        weapon.setCurrAngle(isLeft*moving + ship.getFacing());
    }
}
