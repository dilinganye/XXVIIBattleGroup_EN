package data.missions.SGB_TheLongestDay;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.combat.EscapeRevealPlugin;
import com.fs.starfarer.api.impl.hullmods.CompromisedStructure;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.dark.shaders.post.PostProcessShader;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector3f;


import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints.CIVILIAN;
import static com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints.FREIGHTER;
import static com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent.SkillPickPreference.ANY;


public class MissionDefinition implements MissionDefinitionPlugin {


	@Override
	public void defineMission(MissionDefinitionAPI api) {
		Global.getSoundPlayer().playCustomMusic(1,1,"faction_sbg_sf_encounter_hostile_3",true);
		// Set up the fleets so we can add ships and fighter wings to them.
		api.initFleet(FleetSide.PLAYER, "SGB", FleetGoal.ESCAPE, false, 15);
		api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true, 30);
		Global.getCombatEngine().setMaxFleetPoints(FleetSide.ENEMY, 300);;
		api.setFleetTagline(FleetSide.PLAYER, "Andelon & Erubolie and SGB Evacuation Fleet - Remnants of the Transport Escort");
		api.setFleetTagline(FleetSide.ENEMY, "Unknown bounty team?- Everyone know it's those damn Tri-Tachyon bastards");

		api.addBriefingItem("Tip: Your flagship is a special ship and also your own ship - don't die with it;");
		//Don't be obsessed with war, prioritize ensuring your own evacuation;
		api.addBriefingItem("All non  ships' CR will not quickly drop below 45.");
		api.addBriefingItem("The enemys are thirst for your blood, Prepare for the worst outcome!");

		//add Person
		FactionAPI SGB = Global.getSettings().createBaseFaction("SGB");
		PersonAPI Andelon = SGB.createRandomPerson(FullName.Gender.MALE);
		Andelon.getStats().setSkillLevel("helmsmanship", 2.0F);
		Andelon.getStats().setSkillLevel("combat_endurance", 2.0F);
		Andelon.getStats().setSkillLevel("damage_control", 1.0F);
		Andelon.getStats().setSkillLevel("target_analysis", 2.0F);
		Andelon.getStats().setSkillLevel("systems_expertise", 2.0F);
		Andelon.getStats().setSkillLevel("ballistic_mastery", 1.0F);
		Andelon.getStats().setSkillLevel("tactical_drills", 1.0F);
		Andelon.getStats().setSkillLevel("wolfpack_tactics", 1.0F);
		Andelon.getStats().setSkillLevel("crew_training", 1.0F);
		Andelon.getStats().setLevel(6);
		Andelon.setFaction("SGB");
		Andelon.setPersonality("aggressive");
		Andelon.getName().setFirst("Andelon");
		Andelon.getName().setLast("Neins");
		Andelon.getName().setGender(FullName.Gender.MALE);
		Andelon.setPortraitSprite(Global.getSettings().getSpriteName("intel", "SGB_SF_Andelon"));

		PersonAPI Erubolie = SGB.createRandomPerson(FullName.Gender.MALE);
		Erubolie.getStats().setSkillLevel("helmsmanship", 2.0F);
		Erubolie.getStats().setSkillLevel("combat_endurance", 2.0F);
		Erubolie.getStats().setSkillLevel("damage_control", 1.0F);
		Erubolie.getStats().setSkillLevel("target_analysis", 1.0F);
		Erubolie.getStats().setSkillLevel("missile_specialization", 2.0F);
		Erubolie.getStats().setSkillLevel("wolfpack_tactics", 1.0F);
		Erubolie.getStats().setSkillLevel("navigation", 1.0F);
		Erubolie.getStats().setSkillLevel("flux_regulation", 1.0F);
		Erubolie.getStats().setSkillLevel("field_repairs", 1.0F);
		Erubolie.getStats().setLevel(6);
		Erubolie.setFaction("SGB");
		Erubolie.setPersonality("cautious");
		Erubolie.getName().setFirst("Erubolie");
		Erubolie.getName().setLast("Yabnury");
		Erubolie.getName().setGender(FullName.Gender.MALE);
		Erubolie.setPortraitSprite(Global.getSettings().getSpriteName("intel", "SGB_SF_Erubolie"));

		PersonAPI SGB_Normal = SGB.createRandomPerson(FullName.Gender.MALE);
		SGB_Normal.getStats().setSkillLevel("helmsmanship", 1.0F);
		SGB_Normal.getStats().setSkillLevel("combat_endurance", 2.0F);
		SGB_Normal.getStats().setSkillLevel("sensors", 1.0F);
		SGB_Normal.getStats().setSkillLevel("polarized_armor", 1.0F);
		SGB_Normal.getStats().setLevel(2);
		SGB_Normal.setPersonality("timid");
		SGB_Normal.setPortraitSprite(Global.getSettings().getSpriteName("intel", "SGB_Flag"));

		PersonAPI SGB_Battle = OfficerManagerEvent.createOfficer(
				Global.getSector().getFaction("SGB"),
				3,
				ANY,
				true,
				(CampaignFleetAPI)null,
				true,
				true,
				1,
				new Random());
		SGB_Battle.getStats().setSkillLevel("combat_endurance", 2.0F);
		SGB_Battle.setPersonality("cautious");
		SGB_Battle.setPortraitSprite(Global.getSettings().getSpriteName("intel", "SGB_Flag"));

		PersonAPI ISS = OfficerManagerEvent.createOfficer(
				Global.getSector().getFaction("tritachyon"),
				5,
				ANY,
				true,
				(CampaignFleetAPI)null,
				true,
				true,
				2,
				new Random());
		ISS.setPersonality("aggressive");
		ISS.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TRI_ISS_Mercenaries"));


		// Set up the player's fleet.
		api.addToFleet(FleetSide.PLAYER, "SGB_Austenite_Assault", FleetMemberType.SHIP, "SGB The-Guillotines ACE", true).setCaptain(Andelon);
		api.addToFleet(FleetSide.PLAYER, "SGB_Austenite_Rocket", FleetMemberType.SHIP, "SGB The-Guillotines IV·I", false).setCaptain(Erubolie);

		//api.addToFleet(FleetSide.PLAYER, "SGB_Ascaedy_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Anvil_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Aligate_Attack", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);

		//api.addToFleet(FleetSide.PLAYER, "SGB_Ogent_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Ogent_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Posmous_Support", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Hammies_Assault", FleetMemberType.SHIP, false);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Hammies_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);
		api.addToFleet(FleetSide.PLAYER, "SGB_Felix_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);

		api.addToFleet(FleetSide.PLAYER, "SGB_Wiesios_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);
		api.addToFleet(FleetSide.PLAYER, "SGB_Wiesios_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "SGB_Duplin_Assault", FleetMemberType.SHIP, false).setCaptain(SGB_Battle);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Duplin_Assault", FleetMemberType.SHIP, false);
		//api.addToFleet(FleetSide.PLAYER, "SGB_Duplin_Assault", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.PLAYER, "colossus_Standard", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "colossus_Standard", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_Buffalo_Normal", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_Buffalo_Normal", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_Buffalo_Normal", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_Buffalo_Normal", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);

		api.addToFleet(FleetSide.PLAYER, "SGB_Anoyas_Tanker_Support", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_Anoyas_Tanker_Support", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);

		//api.addToFleet(FleetSide.PLAYER, "prometheus_Super", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_SoftMelting_Fuel", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_SoftMelting_Fuel", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_SoftMelting_Fuel", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "SGB_SoftMelting_Fuel", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);

		api.addToFleet(FleetSide.PLAYER, "apogee_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "brawler_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "brawler_tritachyon_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "buffalo_tritachyon_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "doom_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "hound_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "mora_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "nebula_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "shade_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "shrike_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "sunde_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "tarsus_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);
		api.addToFleet(FleetSide.PLAYER, "vanguard_LongWay", FleetMemberType.SHIP, false).setCaptain(SGB_Normal);



		// Mark player flagship as essential
		api.defeatOnShipLoss("SGB The-Guillotines ACE");

		// Set up the enemy fleet.
		//api.addToFleet(FleetSide.ENEMY, "astral", FleetMemberType.SHIP, true).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, true).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "apogee_Balanced", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "eradicator_Overdriven", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "eradicator_Overdriven", FleetMemberType.SHIP, false).setCaptain(ISS);
		//api.addToFleet(FleetSide.ENEMY, "eradicator_Strike", FleetMemberType.SHIP, false).setCaptain(ISS);

		api.addToFleet(FleetSide.ENEMY, "aurora_Assault", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "aurora_Assault", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "aurora_Assault", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "aurora_Assault", FleetMemberType.SHIP, false).setCaptain(ISS);

		api.addToFleet(FleetSide.ENEMY, "harbinger_Strike", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "harbinger_Strike", FleetMemberType.SHIP, false).setCaptain(ISS);

		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false).setCaptain(ISS);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);




		// Set up the map.
		float width = 16000f;
		float height = 40000f;
		api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);
		api.setBackgroundSpriteName("graphics/backgrounds/hyperspace_bg_cool.jpg");

		float minX = -width / 2;
		float minY = -height / 2;

		for (int i = 0; i < 300; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height;

			float radius = 600f + (float) Math.random() * 400f;
			api.addNebula(x, y, radius);
		}
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2f, 0f, 4000f, 5f, 50f, 50);
		api.setBackgroundGlowColor(new Color(33, 47, 94, 100));

		api.setHyperspaceMode(true);
		api.addNebula(-400, 2100, 200f);
		api.addObjective(minX + width * 0.5f, minY + height * 0.5f, "nav_buoy");
		api.addObjective(minX + width * 0.6f, minY + height * 0.75f, "nav_buoy");
		api.addObjective(minX + width * 0.7f, minY + height * 0.25f, "nav_buoy");

	}

	public static final class Plugin extends BaseEveryFrameCombatPlugin {
		private boolean reallyStarted = false;
		private boolean started = false;

		public Plugin() {
		}

		public void advance(float amount, List<InputEventAPI> events) {
			if (!this.started) {
				this.started = true;
			} else {
				if (!this.reallyStarted) {
					this.reallyStarted = true;
					StandardLight system = new StandardLight();
					system.setType(3);
					system.setDirection((Vector3f)(new Vector3f(-1.0F, -1.0F, -0.2F)).normalise());
					system.setIntensity(0.0F);
					system.setSpecularIntensity(2.0F);
					system.setColor(new Color(80, 85, 192, 139));
					system.makePermanent();
					LightShader.addLight(system);
					system = new StandardLight();
					system.setType(3);
					system.setDirection((Vector3f)(new Vector3f(0.0F, 0.0F, -1.0F)).normalise());
					system.setIntensity(0.75F);
					system.setSpecularIntensity(0.0F);
					system.setColor(new Color(48, 70, 157, 208));
					system.makePermanent();
					LightShader.addLight(system);
					PostProcessShader.setSaturation(false, 1.1F);
					PostProcessShader.setLightness(false, 0.9F);
					PostProcessShader.setContrast(false, 1.1F);
					PostProcessShader.setNoise(false, 0.1F);
				}

				if (!Global.getCombatEngine().isPaused()) {
					Iterator AllShips = Global.getCombatEngine().getShips().iterator();

					while(AllShips.hasNext()) {
						ShipAPI ship = (ShipAPI)AllShips.next();
						float CR = 45;
						CR = ship.getCurrentCR();
						String id = ship.getId();
						MutableShipStatsAPI stats =ship.getMutableStats();
						if (!ship.getVariant().getHullMods().contains("safetyoverrides")) {
							if (CR <= 45f){
								stats.getCRLossPerSecondPercent().modifyPercent(id, -99f);
							} else{
								stats.getCRLossPerSecondPercent().modifyPercent(id, -10f);
							}}
						else{
							if (CR <= 45f){
								stats.getCRLossPerSecondPercent().modifyPercent(id, -20f);
							} else{
								stats.getCRLossPerSecondPercent().modifyPercent(id, -10f);
							}}
					}
					while(AllShips.hasNext()) {
						ShipAPI ship = (ShipAPI) AllShips.next();
						String id = ship.getId();
						MutableShipStatsAPI stats =ship.getMutableStats();
						if (!ship.getVariant().getHints().contains("CIVILIAN") && !ship.getVariant().getHints().contains("FREIGHTER")){
							stats.getSuppliesToRecover().modifyMult(id, 0.5f);
						}
						if (!ship.getCaptain().getFleet().isPlayerFleet()){
							stats.getSuppliesToRecover().modifyMult(id, 0.1f);
						}
					}

				}


			}
		}
	}

	public void init(CombatEngineAPI engine) {
	}
}
