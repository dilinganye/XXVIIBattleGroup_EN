package data.scripts.UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BattleObjectiveAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Iterator;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class SGB_UNGP_TakeHighLand extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float range;
    private float damage;

    public SGB_UNGP_TakeHighLand() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.range = this.getValueByDifficulty(0, difficulty);
        this.damage = this.getValueByDifficulty(1, difficulty);
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
            return index == 0 ? (float)((int)difficulty.getLinearValue(1000.0F, 1000.0F)) : 1.0F;}
        else{
            return index == 1 ? (float)((int)difficulty.getLinearValue(5.0F, 10.0F)) : 1.0F;}
    }

    public void advanceInCombat(CombatEngineAPI engine, float amount) {
    }

    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
    }

    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        Vector2f shipLoc = ship.getLocation();
        boolean takeEffect = false;
        Iterator i$ = Global.getCombatEngine().getObjectives().iterator();

        while(i$.hasNext()) {
            BattleObjectiveAPI tmp = (BattleObjectiveAPI)i$.next();
            if (MathUtils.isWithinRange(tmp.getLocation(), shipLoc, this.range)) {
                takeEffect = true;
                break;
            }
        }

        MutableShipStatsAPI stats = ship.getMutableStats();
        if (takeEffect) {
            stats.getMissileDamageTakenMult().modifyPercent(this.buffID, this.damage);
            stats.getProjectileDamageTakenMult().modifyPercent(this.buffID, this.damage);
            if (ship == engine.getPlayerShip()) {
                engine.maintainStatusForPlayerShip(this.rule, "graphics/icons/mission_marker.png", this.rule.getName(), this.rule.getExtra1(), true);
            }
        } else {
            stats.getMissileDamageTakenMult().unmodify(this.buffID);
            stats.getProjectileDamageTakenMult().unmodify(this.buffID);
        }

    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
            return this.getFactorString(this.getValueByDifficulty(index, difficulty));
        } else {
            return index == 1 ? this.getFactorString(this.getValueByDifficulty(1, difficulty))+"%" : null;
        }
    }
}
