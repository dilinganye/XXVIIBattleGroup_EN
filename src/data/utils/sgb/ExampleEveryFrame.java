package data.utils.sgb;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.List;

public class ExampleEveryFrame extends BaseEveryFrameCombatPlugin {

    private DamagingProjectileAPI proj;//存储绑定的射弹

    private String KEY = "EEE";

    private IntervalUtil interval = new IntervalUtil(0.5f,0.5f);

    private CombatEngineAPI engine = Global.getCombatEngine();

    public ExampleEveryFrame(DamagingProjectileAPI proj){
        this.proj = proj;
        if(engine!=null)
        engine.addPlugin(this);//把自己加入到engine 这样才会运作
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if(engine==null) return;//别问
        if(proj==null||proj.isExpired()||!engine.isEntityInPlay(proj)){//射弹不存在时移除自身
            Global.getCombatEngine().removePlugin(this);
            return;
        }
        if(engine.isPaused()) return;
        interval.advance(amount);
        if(interval.intervalElapsed()){
            //do something
        }

    }
}
