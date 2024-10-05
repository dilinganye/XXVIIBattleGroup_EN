package data.scripts.UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BattleObjectiveAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Iterator;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class SGB_UNGP_PeopleFireWork extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float addExplodeRange, addExplode;

    public SGB_UNGP_PeopleFireWork() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.addExplodeRange = this.getValueByDifficulty(0, difficulty);
        this.addExplode = this.getValueByDifficulty(1, difficulty);
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
        return index == 0 ? (float)((int)difficulty.getLinearValue(25.0F, 50.0F)) : 1.0F;}
        else{
        return index == 1 ? (float)((int)difficulty.getLinearValue(50.0F, 100.0F)) : 1.0F;}
    }

    public void advanceInCombat(CombatEngineAPI engine, float amount) {
    }

    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
        MutableShipStatsAPI EnemyStats = enemy.getMutableStats();
        EnemyStats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyPercent(this.buffID, this.addExplodeRange);
        EnemyStats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyPercent(this.buffID, this.addExplode);
    }

    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        MutableShipStatsAPI PlayerStats = ship.getMutableStats();
        PlayerStats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyPercent(this.buffID, this.addExplodeRange);
        PlayerStats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyPercent(this.buffID, this.addExplode);
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
            return this.getFactorString(this.getValueByDifficulty(index, difficulty))+"%";
        } else {
            return index == 1 ? this.getFactorString(this.getValueByDifficulty(1, difficulty))+"%" : null;
        }
    }
}
