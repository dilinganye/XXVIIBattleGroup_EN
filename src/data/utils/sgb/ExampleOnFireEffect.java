package data.utils.sgb;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class ExampleOnFireEffect implements OnFireEffectPlugin {
    //使用OnFire为projectile挂载一个每帧以实现效果
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ExampleEveryFrame e = new ExampleEveryFrame(projectile);
    }
}
