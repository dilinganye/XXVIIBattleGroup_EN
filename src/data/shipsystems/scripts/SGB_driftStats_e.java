package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.util.MagicAnim;

import java.awt.*;

public class SGB_driftStats_e extends BaseShipSystemScript {

    private final Integer TURN_ACC_BUFF = 100;
    private final Integer TURN_RATE_BUFF = 400;
    private final Integer ACCEL_BUFF = 400;
    private final Integer DECCEL_BUFF = 200;
    private final Integer SPEED_BUFF = 150;
    private final Integer TIME_BUFF = 1000;
    private final Integer DAMAGE_BUFF = 50;
    private boolean hadBeenDone = false;
    private boolean systemOut = false;
    
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        
        float effect = 4/3 * Math.min(1, Math.max(0, MagicAnim.smoothReturnNormalizeRange(effectLevel, 0, 1)/2 + MagicAnim.smoothReturnNormalizeRange(effectLevel*1.5f, 0, 1)/2 + MagicAnim.smoothReturnNormalizeRange(effectLevel*2, 0, 1)/2));
        
        //visual effect
        ShipAPI ship = (ShipAPI) stats.getEntity();
        /*
        if(ship!=null){
            if(effectLevel>0){
                ship.setCollisionClass(CollisionClass.NONE);
            } else {
                if(ship.isFighter()){
                    ship.setCollisionClass(CollisionClass.FIGHTER);
                } else {
                    ship.setCollisionClass(CollisionClass.SHIP);
                }
            }
        }
         */
        if(effectLevel >= 0.75){
            hadBeenDone = true;
        }
        if(state == State.OUT){
            systemOut = true;
            hadBeenDone = false;
        }
        if(state == State.IN){
            systemOut = false;
        }
        if(ship!=null && !hadBeenDone && !systemOut){
            ship.setJitterUnder(
                    ship, 
                    Color.CYAN,
                    0.5f*effect,
                    5, 
                    5+5f*effect, 
                    5+10f*effect
            );
            if(Math.random()>0.9f){
                ship.addAfterimage(new Color(155, 218, 60, 105), 0, 0, -ship.getVelocity().x, -ship.getVelocity().y, 5+50*effect, 0, 0, 2*effect, false, false, false);
            }  
            
            if(!stats.getTimeMult().getPercentMods().containsKey(id)){
                //Global.getSoundPlayer().playSound("diableavionics_drift", 1, 1.66f, ship.getLocation(), ship.getVelocity());
                

                ship.setPhased(true);
//                for (WeaponAPI w : ship.getAllWeapons()){
//                    if(w.getChargeLevel()==1){
//                        w.setRemainingCooldownTo(w.getCooldown());
//                    }
//                }
            } else if(ship.isPhased()){
                ship.setPhased(false);
            }
        }
        if(!hadBeenDone && !systemOut) {
            stats.getTurnAcceleration().modifyPercent(id, TURN_ACC_BUFF * effect);
            stats.getMaxTurnRate().modifyPercent(id, TURN_RATE_BUFF * effect);

            stats.getMaxSpeed().modifyPercent(id, SPEED_BUFF * effect);
            stats.getAcceleration().modifyPercent(id, ACCEL_BUFF);
            stats.getDeceleration().modifyPercent(id, DECCEL_BUFF);

            stats.getTimeMult().modifyPercent(id, TIME_BUFF * effect);
            stats.getCRLossPerSecondPercent().modifyFlat(id, -stats.getCRLossPerSecondPercent().flatBonus);
        }
        if (hadBeenDone && !systemOut) {
            stats.getMaxTurnRate().unmodify(id);
            stats.getTurnAcceleration().unmodify(id);

            stats.getMaxSpeed().unmodify(id);
            stats.getAcceleration().unmodify(id);
            stats.getDeceleration().unmodify(id);

            stats.getTimeMult().unmodify(id);
            stats.getCRLossPerSecondPercent().unmodify(id);
            stats.getEnergyWeaponDamageMult().modifyPercent(id, DAMAGE_BUFF);
        }
        
        
    }
    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        systemOut = true;
        hadBeenDone = false;
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        
        stats.getTimeMult().unmodify(id);
        stats.getCRLossPerSecondPercent().unmodify(id);
        stats.getEnergyWeaponDamageMult().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (!systemOut) {
            if (index == 0 && hadBeenDone) {
                return new StatusData("ENERGY DAMAGE + " + DAMAGE_BUFF + "%", false);
            }
            if (index == 1 && !hadBeenDone) {
                return new StatusData("-==>>>", false);
            }
        }
        return null;
    }
}