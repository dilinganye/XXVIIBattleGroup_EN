//by Tartiflette, Anti-missile missile AI: precise and able to randomly choose a target between nearby enemy missiles.
//feel free to use it, credit is appreciated but not mandatory
//V2 done
package data.scripts.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicRender;
import data.scripts.util.MagicTargeting;
import java.awt.Color;
import java.util.List;

import data.utils.sgb.SGB_Color;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class SGB_PD_MissileAI implements MissileAIPlugin, GuidedMissileAI {
//MagicMissileAI
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private Vector2f lead = new Vector2f();
//    private float timer=0, delay=0.05f;
    //data
    private final float MAX_SPEED;
//    private final int SEARCH_RANGE = 1000;
    private final float DAMPING = 0.05f;
    private final int NUM_PARTICLES = 20;

    public SGB_PD_MissileAI(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        MAX_SPEED = missile.getMaxSpeed()*1.25f; //slight over lead
    }

    @Override
    public void advance(float amount) {
        
        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }
        
        if (Global.getCombatEngine().isPaused() || missile.isFading() || missile.isFizzling()){
            return;
        }
        
        // if there is no target, assign one
        if (target == null 
                || !Global.getCombatEngine().isEntityInPlay(target)
                || target.getOwner()==missile.getOwner()
                ) {
            missile.giveCommand(ShipCommand.ACCELERATE);
            
            //shuffle targets when there are several anti-missiles around            
            boolean random=false;
            
            List <MissileAPI> near = CombatUtils.getMissilesWithinRange(missile.getLocation(), 100);
            if(!near.isEmpty()){
                for(MissileAPI m : near){
                    if(m==missile)continue;
                    if(m.getOwner()!=missile.getOwner())continue;
                    if(m.getWeaponSpec()!=null && m.getWeaponSpec().getWeaponId().equals(missile.getWeaponSpec().getWeaponId())){
                        random=true;
                        break;
                    }
                }
            }
        
            //assign target
            if(random){
                //multiple antimissiles, spread around
                setTarget(
                        MagicTargeting.randomMissile(
                                missile,
                                MagicTargeting.missilePriority.RANDOM,
                                missile.getLocation(),
                                missile.getFacing(),
                                360,
                                (int)(missile.getWeapon().getRange()*1.5f*(missile.getMaxFlightTime()-missile.getFlightTime())/missile.getMaxFlightTime())
                        )
                );
            } else {
                //this is the only antimissile in flight, better aim for the most dangerous ordinance around
                setTarget(
                        MagicTargeting.randomMissile(
                                missile,
                                MagicTargeting.missilePriority.DAMAGE_PRIORITY,
                                missile.getLocation(),
                                missile.getFacing(),
                                360,
                                (int)(missile.getWeapon().getRange()*1.5f*(missile.getMaxFlightTime()-missile.getFlightTime())/missile.getMaxFlightTime())
                        )
                );
            }
            return;
        }
        
        //finding lead point to aim to    
        float dist = MathUtils.getDistanceSquared(missile.getLocation(), target.getLocation());
        if (dist<2500){
            proximityFuse();
            return;
        }
        lead = AIUtils.getBestInterceptPoint(
                missile.getLocation(),
                MAX_SPEED,
                target.getLocation(),
                target.getVelocity()
        );
        if (lead == null ) {
            lead = target.getLocation(); 
        }
                
        //best velocity vector angle for interception
        float correctAngle = VectorUtils.getAngle(
                        missile.getLocation(),
                        lead
                );
        /*
        //velocity angle correction        
        float offCourseAngle = MathUtils.getShortestRotation(
                VectorUtils.getFacing(missile.getVelocity()),
                correctAngle
                );
        
        float correction = MathUtils.getShortestRotation(                
                correctAngle,
                VectorUtils.getFacing(missile.getVelocity())+180
                ) 
                * 0.5f * //oversteer
                (float)((FastTrig.sin(MathUtils.FPI/90*(Math.min(Math.abs(offCourseAngle),45))))); //damping when the correction isn't important
        
        //modified optimal facing to correct the velocity vector angle as soon as possible
        correctAngle += correction;
        */
        
        float correction = MathUtils.getShortestRotation(VectorUtils.getFacing(missile.getVelocity()),correctAngle);
        if(correction>0){
            correction= -11.25f * ( (float)Math.pow(FastTrig.cos(MathUtils.FPI*correction/90)+1, 2) -4 );
        } else {
            correction= 11.25f * ( (float)Math.pow(FastTrig.cos(MathUtils.FPI*correction/90)+1, 2) -4 );
        }
        correctAngle+= correction;        
        
        //turn the missile
        float aimAngle = MathUtils.getShortestRotation(missile.getFacing(), correctAngle);
        if (aimAngle < 0) {
            missile.giveCommand(ShipCommand.TURN_RIGHT);
        } else {
            missile.giveCommand(ShipCommand.TURN_LEFT);
        }
        if (Math.abs(aimAngle)<45){
            missile.giveCommand(ShipCommand.ACCELERATE);
        }
        
        // Damp angular velocity if we're getting close to the target angle
        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * DAMPING) {
            missile.setAngularVelocity(aimAngle / DAMPING);
        }
    }
    
    /*
    private CombatEntityAPI findRandomMissileWithinRange(MissileAPI missile){        
        
        CombatEntityAPI theTarget = AIUtils.getNearestEnemyMissile(missile);
        
        //Check if there is a missile nearby. If not, find another target.
        if (theTarget==null || !MathUtils.isWithinRange(theTarget, missile, SEARCH_RANGE*1.5f)){
            theTarget = AIUtils.getNearestEnemy(missile);
            if (theTarget==null || !MathUtils.isWithinRange(theTarget, missile, SEARCH_RANGE*1.5f)){
                return null;
            } else {
                return theTarget;
            }
        }
        
        //If there are missiles around, let's find the best one.
        WeaponAPI weapon = missile.getWeapon();
        Map<Integer, MissileAPI> PRIORITYLIST = new HashMap<>();
        Map<Integer, MissileAPI> OTHERSLIST = new HashMap<>();
        int i=1, u=1;      
        List<MissileAPI> potentialTargets = AIUtils.getNearbyEnemyMissiles(missile, SEARCH_RANGE);
        
        for(MissileAPI m : potentialTargets){
            if(Math.abs(
                    MathUtils.getShortestRotation(
                            weapon.getCurrAngle(),
                            VectorUtils.getAngle(
                                    weapon.getLocation(),
                                    m.getLocation()
                                )
                            )
                        )<10 
                    ){
                PRIORITYLIST.put(u, m);
                u++;
            } else {
                OTHERSLIST.put(i, m);
                i++;
            }
        }
        
        if(!PRIORITYLIST.containsValue((MissileAPI)theTarget)){      
            if (!PRIORITYLIST.isEmpty()){
                int chooser=Math.round((float)Math.random()*(i-1)+0.5f);
                theTarget=PRIORITYLIST.get(chooser);
            } else if (!OTHERSLIST.isEmpty()){                    
                int chooser=Math.round((float)Math.random()*(u-1)+0.5f);
                theTarget=OTHERSLIST.get(chooser);
            }
        }
        return theTarget;
    }
    */
    
    void proximityFuse(){
        engine.applyDamage(
                target,
                target.getLocation(),
                missile.getDamageAmount(),
                DamageType.FRAGMENTATION,
                0f,
                false,
                false,
                missile.getSource()
        );
        DamagingExplosionSpec boom = new DamagingExplosionSpec(
                0.1f,
                60,
                50,
                missile.getDamageAmount(),
                50,
                CollisionClass.PROJECTILE_NO_FF,
                CollisionClass.PROJECTILE_FIGHTER,
                2,
                5,
                5,
                25,
                new Color(150, 225,1),
                new Color(200, 130,25)
        );
        boom.setDamageType(DamageType.FRAGMENTATION);
        boom.setShowGraphic(false);
        boom.setSoundSetId("explosion_flak");
        engine.spawnDamagingExplosion(boom, missile.getSource(), missile.getLocation());

        Vector2f Speedo =missile.getVelocity();
        Vector2f Speedo2 = Speedo;
        Speedo2.setX(Speedo.getX()/8);;
        Speedo2.setY(Speedo.getY()/8);
        float Angle =missile.getFacing();
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","SGB_plasma_red"),
                missile.getLocation(),
                Speedo2,
                new Vector2f(64,96),
                new Vector2f(96,160),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(197, 239, 186,255),
                true,
                0,
                0.1f,
                0.15f
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","SGB_smoke_01"),
                missile.getLocation(),
                Speedo2,
                new Vector2f(32,32),
                new Vector2f(96,96),
                //angle,
                Angle+2*(float)Math.random(),
                0,
                Color.white,
                true,
                0.1f,
                0.0f,
                0.25f
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","SGB_Spark01A"),
                missile.getLocation(),
                Speedo2,
                new Vector2f(16,16),
                new Vector2f(32,64),
                //angle,
                Angle+2f*(float)Math.random()+90f,
                0,
                new Color(112, 97, 82,255),
                true,
                0.05f,
                0.1f,
                0.3f
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","SGB_Spark01A"),
                missile.getLocation(),
                Speedo2,
                new Vector2f(16,16),
                new Vector2f(16,64),
                //angle,
                Angle+2f*(float)Math.random()+90f,
                0,
                new Color(170, 182, 127,255),
                true,
                0,
                0.1f,
                0.15f
        );
        if(MagicRender.screenCheck(0.1f, missile.getLocation())){
            engine.addHitParticle(
                missile.getLocation(),
                new Vector2f(),
                100,
                1,
                0.25f,
                    SGB_Color.SGBred
            );
            for (int i=0; i<NUM_PARTICLES; i++){
                float axis = (float)Math.random()*360;
                float range = (float)Math.random()*100;
                engine.addHitParticle(
                    MathUtils.getPointOnCircumference(missile.getLocation(), range/5, axis),
                    MathUtils.getPointOnCircumference(new Vector2f(), range, axis),
                    2+(float)Math.random()*2,
                    1,
                    1+(float)Math.random(),
                        SGB_Color.SGBpurple
                );
            }

            if(missile.getWeapon().getId().equals("SGB_Shackles_MissileLauncher")
                    && missile.getWeapon().getShip() != null
                    && missile.getWeapon().getShip().getHullSpec().getBaseHullId().equals("SGB_Whip")){
                engine.applyDamage(
                        missile,
                        missile.getLocation(),
                        missile.getHitpoints() * 0.26f,
                        DamageType.ENERGY,
                        0f,
                        false,
                        false,
                        missile
                );
            }
            else {
                engine.applyDamage(
                        missile,
                        missile.getLocation(),
                        missile.getHitpoints() * 0.55f,
                        DamageType.ENERGY,
                        0f,
                        false,
                        false,
                        missile
                );
            }
        } else {
            engine.removeEntity(missile);
        }
    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }
        public void init(CombatEngineAPI engine) {
    }
}
