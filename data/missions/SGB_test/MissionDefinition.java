// Powered by Cat Magic

package data.missions.SGB_test;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;


public class MissionDefinition implements MissionDefinitionPlugin {

	static int missionType = 0;

	@Override
	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets so we can add ships and fighter wings to them.
		// In this scenario, the fleets are attacking each other, but
		// in other scenarios, a fleet may be defending or trying to escape
		api.initFleet(FleetSide.ENEMY, "TAR", FleetGoal.ATTACK, true);

		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.

		api.setFleetTagline(FleetSide.ENEMY, "Target");
		
		Global.getSoundPlayer().pauseCustomMusic();

		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Test");

		// Set up the player's fleet.
		
		// Set up the BLG fleet.

		//api.addToFleet(FleetSide.PLAYER, "SGB_Onslaught_Normal", FleetMemberType.SHIP, false);



		switch (missionType) {
			case 0:
			case 1:
			case 2:
			case 3:

				api.initFleet(FleetSide.PLAYER, "SGB", FleetGoal.ATTACK, false);
				api.setFleetTagline(FleetSide.PLAYER, "Shackles Garrison Ships");
		api.addToFleet(FleetSide.PLAYER, "SGB_Duplin_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ceaies_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Redmond_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Wiesios_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Overload", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Sumner_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Hydrargyri_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Hydrargyri_Overload", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Vulcanizing_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Hammies_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Hammies_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Posmous_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Posmous_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Whip_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Alicuty_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Alicuty_Coop", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Alicuty_DogFight", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Aligate_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Aligate_Coop", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Aligate_DogFight", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Aligate_Assault", FleetMemberType.SHIP, false);


		api.addToFleet(FleetSide.PLAYER, "SGB_Achilles_Artillery", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Achilles_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Davington_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Snapdragon_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Ogent_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Carving_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Carving_SD_Overload", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Carving_SD_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Falcon_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Falcon_2_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Eagle_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Eagle_2_Support", FleetMemberType.SHIP, false);


		api.addToFleet(FleetSide.PLAYER, "SGB_Ascaedy_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Anvil_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Anvil_Coop", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Anvil_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Anvil_Overload", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Stress_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Stress_2_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Onslaught_Normal", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_Coop", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_DogFight", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_Attack", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_2_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_2_Coop", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_2_DogFight", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ductility_2_Attack", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Battle_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Battle_Artillery", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Battle_2_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Fortress", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Forgnace_Fortress_AI", FleetMemberType.SHIP, false);


		api.addToFleet(FleetSide.PLAYER, "SGB_Redmond_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Anoyas_Cargo", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Anoyas_Tanker_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_SoftMelting_Fuel", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Buffalo_Normal", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Mould_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Quench_Fri_Commando", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Quench_Fri_Commando", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Quench_Fri_Commando", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Quench_Fri_Commando", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Quench_Clo_ACE", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.PLAYER, "SGB_Austenite_Wingman_Ship_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Austenite_Rocket", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Austenite_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Austenite_Carrier", FleetMemberType.SHIP, false);

				break;
			case 4:

				api.initFleet(FleetSide.PLAYER, "???", FleetGoal.ATTACK, false);
				api.setFleetTagline(FleetSide.PLAYER, "Unknown - How many time do you beat this mission?");
		api.addToFleet(FleetSide.PLAYER, "SGB_Undervoltage_AntiShield", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Undervoltage_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Undervoltage_Missile", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Undervoltage_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Undervoltage_AntiArmor", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Overcurrent_AntiShield", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Overcurrent_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Overcurrent_Missile", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Overcurrent_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Misalignment_AntiShield", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Misalignment_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Misalignment_Missile", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Misalignment_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_LargeGrain_AntiShield", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_LargeGrain_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_LargeGrain_Missile", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_LargeGrain_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_LargeGrain_AntiArmor", FleetMemberType.SHIP, false);


		api.addToFleet(FleetSide.PLAYER, "SGB_Carbonization_AntiShield_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Carbonization_Attack_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Carbonization_Missile_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Carbonization_Support_wing", FleetMemberType.FIGHTER_WING, false);

				break;
			default:
				break;
		}




		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "remnant_station2_Damaged", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);


		// Set up the map.
		float width = 10000f;
		float height = 10000f;
		api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);
		api.setBackgroundSpriteName("graphics/backgrounds/background1.jpg");

		float minX = -width / 2;
		float minY = -height / 2;

		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2f, 0f, 4000f, 5f, 50f, 50);
		api.addPlanet(-500f, 500f, 5f, StarTypes.YELLOW, 50f, true);

		api.addNebula(-400, 2100, 200f);
		missionType++;
		if (missionType > 4) {
			missionType = 0;}
	}
}