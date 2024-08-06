package data.missions.SGB_Retaliate;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.combat.EscapeRevealPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.util.vector.Vector2f;


import java.awt.*;


public class MissionDefinition implements MissionDefinitionPlugin {


	@Override
	public void defineMission(MissionDefinitionAPI api) {
		Global.getSoundPlayer().playCustomMusic(1, 1, "XXVII_SP_Momentum", true);
		// Set up the fleets so we can add ships and fighter wings to them.
		api.initFleet(FleetSide.PLAYER, "SGB", FleetGoal.ATTACK, false, 30);
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 30);
		api.setFleetTagline(FleetSide.PLAYER, "Shackles Garrison - Topaz Revenge Fleet");
		api.setFleetTagline(FleetSide.ENEMY, "Tri-Tachyon Ampoist Research Fortress");

		api.addBriefingItem("Tip: Try using cruise missiles to forcefully open the enemy space station's shield system");
		api.addBriefingItem("Destroy enemy and send Tri-Tachyon back to the Stone Age");
		//This contains a large amount of scientific research achievements of Tri-Tachyon.
		api.addBriefingItem("Campaign Score>50 is considered successful");

		// Set up the player's fleet.
		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Battle_Artillery", FleetMemberType.SHIP, "SGB Topa Stone", true);
		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Battle_Artillery", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Stress_2_Assault", FleetMemberType.SHIP, "SGB YellowMelt", false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.PLAYER, "SGB_Stress_2_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_2_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Falcon_2_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Falcon_2_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Eagle_2_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Eagle_2_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Eagle_2_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ascaedy_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ascaedy_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.PLAYER, "SGB_Ogent_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.STEADY);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ogent_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.STEADY);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ogent_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.STEADY);
		api.addToFleet(FleetSide.PLAYER, "SGB_Posmous_Support", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.STEADY);
		api.addToFleet(FleetSide.PLAYER, "SGB_Posmous_Support", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.STEADY);

		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Wiesios_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.PLAYER, "SGB_Wiesios_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.PLAYER, "SGB_Wiesios_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.PLAYER, "SGB_Argion_Bomber_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Argion_Bomber_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Argion_Bomber_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Agatha_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Agatha_Coop_wing", FleetMemberType.FIGHTER_WING, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);

		// Mark player flagship as essential
		//api.defeatOnShipLoss("SGB Aaunosy");

		// Set up the enemy fleet.
		//api.addToFleet(FleetSide.ENEMY, "station3_hightech_Retaliate", FleetMemberType.SHIP, "Ampoist BattleBase", true).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "station3_hightech_Retaliate", FleetMemberType.SHIP, "Ampoist BattleBase", false);

		api.addToFleet(FleetSide.ENEMY, "paragon_Retaliate", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "astral_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "aurora_Retaliate", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "aurora_Retaliate", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "aurora_Retaliate", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		//api.addToFleet(FleetSide.ENEMY, "aurora_Retaliate", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.ENEMY, "hyperion_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hyperion_Strike", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.ENEMY, "wolf_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);


		// Set up the map.
		float width = 18000f;
		float height = 12000f;
		api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);
		api.setBackgroundSpriteName("graphics/backgrounds/background2.jpg");

		float minX = -width / 2;
		float minY = -height / 2;

		for (int i = 0; i < 200; i++) {
			float x = (float) Math.random() * width - width / 2;
			float y = (float) Math.random() * height;

			if (x > -6000 && y > -2000) continue;
			float radius = 600f + (float) Math.random() * 1000f;
			api.addNebula(x, y, radius);
		}
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2f, 0f, 4000f, 5f, 50f, 50);
		api.addPlanet(-2728f, 5850f, 2500f, StarTypes.BLUE_GIANT, 100f);
		api.setBackgroundGlowColor(new Color(37, 38, 45, 100));
		api.addPlanet(3950, -1750, 750f, StarTypes.WHITE_DWARF, 60f);

		api.setHyperspaceMode(true);
		api.addNebula(-400, 2100, 200f);
		api.addPlugin(new Plugin());
		api.addObjective(minX + width * 0.25f, minY + height * 0.5f, "nav_buoy");
		api.addObjective(minX + width * 0.75f, minY + height * 0.5f, "nav_buoy");

	}

	public static final class Plugin extends BaseEveryFrameCombatPlugin {
		private final Vector2f LEFT = new Vector2f();
		private final Vector2f RIGHT = new Vector2f();
		private boolean reallyStarted = false;
		private static int Nub = 0;//对话到哪儿了
		private final IntervalUtil Before_Combat_Words_TIMER = new IntervalUtil(3.0F, 8.0F);
		private final IntervalUtil Before_Combat_Words_TIMER2 = new IntervalUtil(40.0F, 50.0F);

		public void init(CombatEngineAPI engine) {
			this.LEFT.set(-(engine.getMapWidth() / 2.0F), (engine.getMapHeight() / 3.0F));
			this.RIGHT.set(engine.getMapWidth() / 2.0F, (engine.getMapHeight() / 3.0F));
		}

		public void advance(float amount) {
			Global.getSoundPlayer().playCustomMusic(1, 1, "XXVII_SP_Momentum", true);

			if (!this.reallyStarted) {
				this.reallyStarted = true;
				Global.getSoundPlayer().playCustomMusic(1, 1, "XXVII_SP_Momentum", true);
			}
		}
	}
}