//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package data.scripts.UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.campaign.specialist.intel.UNGP_SpecialistIntel;

import java.awt.*;

import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_NamelessDeadList extends UNGP_BaseRuleEffect implements UNGP_CampaignTag, UNGP_CombatTag {
    private static class crewLossRecorder{
        private float crewJustCombatNub=0;
        private float crewNub=0;
        private float crewLessDead=0;
        private float crewDeadThisDays=0;
        private float crewDeadThisMonth=0;
        private float crewDeadLIST=0;
        private boolean afterCombat = false;

    }

    public SGB_NamelessDeadList() {
    }
    public crewLossRecorder getRecorder(){
        crewLossRecorder recorder;
        if( Global.getSector().getMemoryWithoutUpdate().contains("$SGB_UNGP_CrewRecorder")) {
            recorder = (crewLossRecorder) Global.getSector().getMemoryWithoutUpdate().get("$SGB_UNGP_CrewRecorder");
        }
        else {
            recorder = new crewLossRecorder();
            Global.getSector().getMemoryWithoutUpdate().set("$SGB_UNGP_CrewRecorder", recorder);
        }
        return recorder;
    }
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        crewLossRecorder recorder = getRecorder();
        recorder.crewLessDead = this.getValueByDifficulty(0, difficulty);
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return index == 0 ? difficulty.getLinearValue(10F, 10F) : 0.0F;
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
            return "" + this.getValueByDifficulty(index, difficulty);
        } else if (index == 1) {
            return "" + this.getValueByDifficulty(0, difficulty) * 2.0F;
        } else {
            return index == 2 ? "10%" : super.getDescriptionParams(index, difficulty);
        }
    }

    public void advanceInCombat(CombatEngineAPI engine, float amount) {
        crewLossRecorder recorder = getRecorder();

        if(engine != null && !recorder.afterCombat){
            recorder.afterCombat = true;
        }
    }

    @Override
    public void applyEnemyShipInCombat(float v, ShipAPI shipAPI) {

    }

    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        crewLossRecorder recorder = getRecorder();
        MutableShipStatsAPI PlayerStats = ship.getMutableStats();
        PlayerStats.getCrewLossMult().modifyPercent(this.buffID, -recorder.crewLessDead);
    }
    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        crewLossRecorder recorder = getRecorder();
        if (params.isOneMonthPassed()){
            recorder.crewDeadThisMonth = 0;
        }
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
            //float crew = (float)(player.getCargo().getCrew() + player.getCargo().getMarines());
        if(!recorder.afterCombat){
            recorder.crewNub = (float)(player.getCargo().getCrew());
        }
        else if (recorder.afterCombat) {
            recorder.crewJustCombatNub = (float)(player.getCargo().getCrew());

            recorder.crewDeadThisDays = recorder.crewNub-recorder.crewJustCombatNub;
            recorder.crewDeadThisMonth += recorder.crewNub-recorder.crewJustCombatNub;
            recorder.crewDeadLIST += recorder.crewNub-recorder.crewJustCombatNub;
            int crewGone = (int) recorder.crewDeadThisDays;

            float rngDifference = MathUtils.getRandomNumberInRange(1.0F, 2.5F);
            float organs = crewGone * (rngDifference / 2.0F);

            if (recorder.crewDeadThisDays > 0.0F) {
                UNGP_SpecialistIntel.RuleMessage message;
                if (player.getCargo().getCrew() > 0) {
                    player.getCargo().removeCrew((int)(crewGone * rngDifference));
                    player.getCargo().removeMarines((int)(crewGone / rngDifference));
                    message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"" + (int)(crewGone * rngDifference), "" + (int)(crewGone / rngDifference), "" + (int)organs});
                    message.send();
                } else if (player.getCargo().getCrew() == 0) {
                    player.getCargo().removeMarines((int) crewGone);
                    message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"no", "" + (int) crewGone, "" + (int) organs});
                    message.send();
                }
                player.getCargo().addCommodity("organs", organs);

            }recorder.afterCombat = false;


        }
        /*
        if (params.isOneDayPassed()) {
            CampaignFleetAPI player = Global.getSector().getPlayerFleet();
            //float crew = (float)(player.getCargo().getCrew() + player.getCargo().getMarines());
            float crew = (float)(player.getCargo().getCrew());
            if(!this.afterCombat){
                this.crewNub = crew;
            }
            else if (this.afterCombat) {
                this.crewJustCombatNub = crew;
            }
            if(this.crewNub != this.crewJustCombatNub && this.afterCombat){
                this.crewDeadThisDays = crewNub-crewJustCombatNub;
                this.crewDeadThisMonth += crewNub-crewJustCombatNub;
                this.crewDeadLIST += crewNub-crewJustCombatNub;
                int crewGone = (int) this.crewDeadThisDays;

                float rngDifference = MathUtils.getRandomNumberInRange(1.0F, 2.5F);
                float organs = crewGone * (rngDifference / 2.0F);

                if (crew > 0.0F) {
                    UNGP_SpecialistIntel.RuleMessage message;
                    if (player.getCargo().getCrew() > 0) {
                        player.getCargo().removeCrew((int)(crewGone * rngDifference));
                        player.getCargo().removeMarines((int)(crewGone / rngDifference));
                        message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"" + (int)(crewGone * rngDifference), "" + (int)(crewGone / rngDifference), "" + (int)organs});
                        message.send();
                    } else if (player.getCargo().getCrew() == 0) {
                        player.getCargo().removeMarines((int) crewGone);
                        message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"no", "" + (int) crewGone, "" + (int) organs});
                        message.send();
                    }
                    player.getCargo().addCommodity("organs", organs);

                }
                this.afterCombat = false;
            }
        }
         */
    }

    public boolean addIntelTips(TooltipMakerAPI imageTooltip) {
        crewLossRecorder recorder = getRecorder();
        Color highlight = Misc.getHighlightColor();
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        int crewDeadMonthly = (int) recorder.crewDeadThisMonth;
        int crewDeadALL = (int) recorder.crewDeadLIST;

        String crew;
        if (player.getCargo().getCrew() > 0) {
            crew = txt("SGB_UNGP_DeadList_About") + player.getCargo().getCrew() + txt("SGB_UNGP_DeadList_Crews");
        } else {
            crew = txt("SGB_UNGP_DeadList_NonAlive");
        }

        imageTooltip.addPara(txt("SGB_UNGP_DeadList_HavingCrew"), 0.0F, highlight, crew);
        imageTooltip.addPara(txt("SGB_UNGP_DeadList_DeadCrewThisMonth"), 1.0F, highlight, String.valueOf(crewDeadMonthly));
        imageTooltip.addPara(txt("SGB_UNGP_DeadList_DeadCrewLIST"), 1.0F, highlight, String.valueOf(crewDeadALL));
        return true;
    }

}
