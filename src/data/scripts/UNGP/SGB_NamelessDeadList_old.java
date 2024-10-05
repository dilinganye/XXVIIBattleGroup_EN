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
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.campaign.specialist.intel.UNGP_SpecialistIntel;

import java.awt.*;

import static data.utils.sgb.SGB_stringsManager.txt;

public class SGB_NamelessDeadList_old extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {
    private float crewJustCombatNub, crewNub, crewLessDead,crewDeadThisDays,crewDeadThisMonth,crewDeadLIST;
    private boolean afterCombat = false;

    public SGB_NamelessDeadList_old() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.crewLessDead = this.getValueByDifficulty(0, difficulty);
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
        if(engine != null && !this.afterCombat){
            this.afterCombat = true;
        }
    }

    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        MutableShipStatsAPI PlayerStats = ship.getMutableStats();
        PlayerStats.getCrewLossMult().modifyPercent(this.buffID, -this.crewLessDead);
    }
    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        if (params.isOneMonthPassed()){
            this.crewDeadThisMonth = 0;
        }
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
            //float crew = (float)(player.getCargo().getCrew() + player.getCargo().getMarines());
        if(!this.afterCombat){
            this.crewNub = (float)(player.getCargo().getCrew());
        }
        else if (this.afterCombat) {
            this.crewJustCombatNub = (float)(player.getCargo().getCrew());

            this.crewDeadThisDays = crewNub-crewJustCombatNub;
            this.crewDeadThisMonth += crewNub-crewJustCombatNub;
            this.crewDeadLIST += crewNub-crewJustCombatNub;
            int crewGone = (int) this.crewDeadThisDays;

            float rngDifference = MathUtils.getRandomNumberInRange(1.0F, 2.5F);
            float organs = crewGone * (rngDifference / 2.0F);

            if (crewDeadThisDays > 0.0F) {
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

            }this.afterCombat = false;


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
        Color highlight = Misc.getHighlightColor();
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        int crewDeadMonthly = (int) this.crewDeadThisMonth;
        int crewDeadALL = (int) this.crewDeadLIST;

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