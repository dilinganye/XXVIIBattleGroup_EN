package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import java.awt.Color;
import java.util.List;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import static data.missions.SGB_20FAboveTheGround.MissionDefinition.colorNoiseBlend;

public class SGB_Missile_Reborn_Effect extends BaseEveryFrameCombatPlugin {

    protected DamagingProjectileAPI proj; //	进行一个定义
    private CombatEngineAPI engine = Global.getCombatEngine();
    private boolean ThisManly;
    protected IntervalUtil arcInterval = new IntervalUtil(0.17f, 0.23f);

    public SGB_Missile_Reborn_Effect(DamagingProjectileAPI proj, Boolean isManly){ //	进行一个绑定存储
        this.proj = proj;
        ThisManly = isManly;
        if(engine!=null) {
            engine.addPlugin(this); //	把自己加入到engine 这样才会运作
        }
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if(engine==null) return; //	别问
        if(proj==null||proj.isExpired()||!engine.isEntityInPlay(proj)){ //	射弹不存在时移除自身
            Global.getCombatEngine().removePlugin(this);
            return;
        }
        if(engine.isPaused()) return;
        if(proj.getProjectileSpecId().equals("SGB_Reborn_srm")){
            arcInterval.advance(amount);
            if (arcInterval.intervalElapsed()) {
                Vector2f pzs = VectorUtils.rotate(new Vector2f(0, 20f), proj.getFacing());
                Vector2f pzs_m = proj.getLocation();
                Global.getCombatEngine().addNebulaSmokeParticle(
                        new Vector2f(pzs.x+pzs_m.x,pzs.y+pzs_m.y),
                        pzs,
                        MathUtils.getRandomNumberInRange(30f, 60f),
                        MathUtils.getRandomNumberInRange(0.073f, 0.21f),
                        0.1f, 0.15f, 5f,
                        colorNoiseBlend(new Color(218, 199, 191, 200),
                                new Color(37, 35, 35, 175), (float) Math.random())
                );
            }

            if(proj.getElapsed()>=MathUtils.getRandomNumberInRange(1f, 2f)){
                Global.getCombatEngine().addNebulaSmokeParticle(
                        proj.getLocation(),
                        VectorUtils.rotate(new Vector2f(0,20f),proj.getFacing()),
                        MathUtils.getRandomNumberInRange(60f,95f),
                        MathUtils.getRandomNumberInRange(0.073f,0.21f),
                        0.1f, 0.15f,5f,
                        colorNoiseBlend(new Color(218, 199, 191,200),
                                new Color(37, 35, 35,175),(float) Math.random())
                );
                //Global.getLogger(this.getClass()).info("TEST - missile,missileID:" + missile.get);
                if(ThisManly){
                    CombatEntityAPI missile2 = engine.spawnProjectile(proj.getSource(), null, "SGB_Airbath_energy", proj.getLocation(), proj.getFacing()+45f, null);
                    CombatEntityAPI missile3 = engine.spawnProjectile(proj.getSource(), null, "SGB_Airbath_energy", proj.getLocation(), proj.getFacing()-45f, null);
                }
                CombatEntityAPI missile = engine.spawnProjectile(proj.getSource(), null, "SGB_Airbath_energy", proj.getLocation(), proj.getFacing(), proj.getVelocity());


                Global.getCombatEngine().removeEntity(proj);
            }
        }
    }
}