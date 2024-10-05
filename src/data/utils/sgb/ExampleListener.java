package data.utils.sgb;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;

import java.awt.*;

public class ExampleListener implements AdvanceableListener {

    private ShipAPI ship;//附着的舰船

    private float elapsed = 0f;

    private float MAX_TIME = 10f;

    private boolean shieldHit = false;

    public ExampleListener(ShipAPI ship,boolean shieldHit){
        MAX_TIME = (shieldHit)?3f:10f;
        this.shieldHit = shieldHit;
        //可以把减益放在这里
        //如果要实现一定时间的变色 可以使用
        ship.fadeToColor(this,new Color(100,100,100,100),2f,6f,2f);
    }
    @Override
    public void advance(float amount) {
        if(isExpired()){
            clean();
            ship.removeListener(this);
            return;
        }
        //也可以把减益放在这里

    }

    private boolean isExpired(){//在何时这个效果应该移除
        return !ship.isAlive()||elapsed>=MAX_TIME;
    }

    private void clean(){//可以在这里清理施加的debuff 以及一些结束时应该做的事
        //纯粹只是好看所以框起来

    }
}
