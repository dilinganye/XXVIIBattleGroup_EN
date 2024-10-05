package data.missions.SGB_CleavedSky;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
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
		Global.getSoundPlayer().playCustomMusic(1, 1, "XXVII_EnemyWings", true);
		// Set up the fleets so we can add ships and fighter wings to them.
		api.initFleet(FleetSide.PLAYER, "HSS", FleetGoal.ATTACK, false, 30);
		api.initFleet(FleetSide.ENEMY, "SGB", FleetGoal.ATTACK, true, 30);
		api.setFleetTagline(FleetSide.PLAYER, "Pollux Strike Force XIV Intercept Fleet");
		api.setFleetTagline(FleetSide.ENEMY, "Shackles Garrison XXVII Neck Hook Revenge Fleet");

		api.addBriefingItem("Tip: The enemy is a giant carrier formation, please do your best to ensure your air superiority");
		api.addBriefingItem("Enemy fleet has gone berserk, their actions are extremely aggressive.");
		//make sure to cripple enough of their ships to bring them back to their senses
		api.addBriefingItem("Score of this campaign > 25 is considered as successful");

		// Set up the player's fleet.

		api.addToFleet(FleetSide.PLAYER, "onslaught_xiv_Elite", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.PLAYER, "onslaught_xiv_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "legion_xiv_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "legion_xiv_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "legion_xiv_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "legion_xiv_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "dominator_XIV_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "dominator_XIV_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "falcon_xiv_Escort", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "falcon_xiv_Escort", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "falcon_xiv_Escort", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "condor_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "condor_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "vanguard_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "vanguard_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "vanguard_Attack", FleetMemberType.SHIP, false);




		// Mark player flagship as essential
		//api.defeatOnShipLoss("SGB Aaunosy");

		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "SGB_Ductility_2_Attack", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ductility_2_DogFight", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ductility_Attack", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ductility_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.ENEMY, "SGB_Anvil_Coop", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Anvil_Coop", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Anvil_Attack", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.ENEMY, "SGB_Ascaedy_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Aligate_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Aligate_Coop", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Aligate_DogFight", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Aligate_DogFight", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);

		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Ares_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Agatha_Assault_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Agatha_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Agatha_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Agatha_Coop_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Agatha_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Agatha_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Axio_DogFight_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "SGB_Axio_DogFight_wing", FleetMemberType.FIGHTER_WING, false);


		// Set up the map.
		float width = 19500f;
		float height = 18000f;
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
		api.addAsteroidField(minX, minY + height / 2f, 0f, 4000f, 5f, 50f, 25);
		// Main Star
		api.addPlanet(8250f, 1250f, 3120f, StarTypes.YELLOW, 350f);
		// Add an asteroid field
		api.addAsteroidField(minX + width * 0.55f, minY + height * 0.55f, 70, 3000f,
				20f, 70f, 200);
		api.addAsteroidField(minX + width * 0.55f, minY + height * 0.55f, 70, 5000f,
				20f, 70f, 200);
		api.addAsteroidField(minX + width * 0.55f, minY + height * 0.55f, 70, 7000f,
				20f, 70f, 100);
		// Main Star
		api.addPlanet(-850, -6950, 1100f, "barren-desert", 200f);


		api.setBackgroundGlowColor(new Color(101, 88, 77, 100));
		api.setHyperspaceMode(true);
		api.addNebula(2400, -2100, 500f);
		api.addNebula(6400, 3150, 200f);
		api.addPlugin(new Plugin());

		api.addObjective(minX + width * 0.8f - 1000, minY + height * 0.4f, "nav_buoy");
		//api.addObjective(minX + width * 0.8f - 1000, minY + height * 0.6f, "nav_buoy");
		//api.addObjective(minX + width * 0.3f + 1000, minY + height * 0.3f, "comm_relay");
		api.addObjective(minX + width * 0.3f + 1000, minY + height * 0.7f, "comm_relay");
		//api.addObjective(minX + width * 0.5f, minY + height * 0.5f, "sensor_array");
		api.addObjective(minX + width * 0.2f + 1000, minY + height * 0.5f, "sensor_array");

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