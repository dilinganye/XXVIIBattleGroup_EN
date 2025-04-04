package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
//import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.util.MagicAnim;

import java.awt.Color;

public class SGB_driftStats extends BaseShipSystemScript {

    private final Integer TURN_ACC_BUFF = 100;
    private final Integer TURN_RATE_BUFF = 400;
    private final Integer ACCEL_BUFF = 400;
    private final Integer DECCEL_BUFF = 200;
    private final Integer SPEED_BUFF = 150;
    private final Integer TIME_BUFF = 1000;
    
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float shipTimeMult = Math.max(Math.min(effectLevel/2,
                        1f), //最大值
                0.5f); //最小值
        float effect = Math.min(1, Math.max(0, MagicAnim.smoothReturnNormalizeRange(effectLevel, 0, 1)/2 + MagicAnim.smoothReturnNormalizeRange(effectLevel*1.5f, 0, 1)/2 + MagicAnim.smoothReturnNormalizeRange(effectLevel*2, 0, 1)/2));
        
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

        if(ship!=null){
            // Are you the player?————
            boolean player = false;
            if (stats.getEntity() instanceof ShipAPI) {
                player = ship == Global.getCombatEngine().getPlayerShip();
                id = id + "_" + ship.getId();
            } else {
                return;
            }
            // So I might give ya more time
            if (player) {
                Global.getCombatEngine().getTimeMult().modifyMult(id,shipTimeMult);
            } else {
                Global.getCombatEngine().getTimeMult().unmodify(id);
            }

            if(player && effect == 0){
                Global.getCombatEngine().getTimeMult().unmodify(id);
            }
            // ————————


            ship.setJitterUnder(
                    ship, 
                    Color.CYAN,
                    0.5f*effect,
                    5, 
                    5+5f*effect, 
                    5+10f*effect
            );
            if(Math.random()>0.9f){
                ship.addAfterimage(new Color(230, 255, 0, 69), 0, 0, -ship.getVelocity().x, -ship.getVelocity().y, 5+50*effect, 0, 0, 2*effect, false, false, false);
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

        stats.getTurnAcceleration().modifyPercent(id, TURN_ACC_BUFF * effect);
        stats.getMaxTurnRate().modifyPercent(id, TURN_RATE_BUFF * effect);

        stats.getMaxSpeed().modifyPercent(id, SPEED_BUFF * effect);
        stats.getAcceleration().modifyPercent(id, ACCEL_BUFF);
        stats.getDeceleration().modifyPercent(id, DECCEL_BUFF);

        stats.getTimeMult().modifyPercent(id, TIME_BUFF * effect);
        stats.getCRLossPerSecondPercent().modifyFlat(id, -stats.getCRLossPerSecondPercent().flatBonus);
        
        
    }
    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        
        stats.getTimeMult().unmodify(id);
        stats.getCRLossPerSecondPercent().unmodify(id);
        if(ship != null) {
            // Are you the player?————
            boolean player = false;
            if (stats.getEntity() instanceof ShipAPI) {
                player = ship == Global.getCombatEngine().getPlayerShip();
                id = id + "_" + ship.getId();
            } else {
                return;
            }
            // So I might give ya more time
            if (player) {
                Global.getCombatEngine().getTimeMult().unmodify(id);
            }
            // ————————
        }
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("-==>>>", false);
        }
        return null;
    }
}