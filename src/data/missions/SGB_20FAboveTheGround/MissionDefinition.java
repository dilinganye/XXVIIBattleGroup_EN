package data.missions.SGB_20FAboveTheGround;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Iterator;
import java.util.List;


public class MissionDefinition implements MissionDefinitionPlugin {


	@Override
	public void defineMission(MissionDefinitionAPI api) {
		// Set up the fleets so we can add ships and fighter wings to them.
		api.initFleet(FleetSide.PLAYER, "PDF", FleetGoal.ATTACK, false, 30);
		api.initFleet(FleetSide.ENEMY, "SOB", FleetGoal.ATTACK, true, 30);
		api.setFleetTagline(FleetSide.PLAYER, "行星防务稽查队");
		api.setFleetTagline(FleetSide.ENEMY, "婊子养的政治犯");

		api.addBriefingItem("提示：战斗距离地表20英尺高的地方");
		api.addBriefingItem("一旦失去动力，将立刻坠毁");

		// Set up the player's fleet.

		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_M_F_Assault", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_M_F_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.PLAYER, "SGB_Ares_M_F_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.PLAYER, "SGB_Suprav_Rav", FleetMemberType.SHIP, false);




		// Mark player flagship as essential
		//api.defeatOnShipLoss("SGB Aaunosy");

		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "SGB_Agrav_Convoy", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "SGB_Argion_M_F_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);
		api.addToFleet(FleetSide.ENEMY, "SGB_Argion_M_F_Assault", FleetMemberType.SHIP, false).getCaptain().setPersonality(Personalities.AGGRESSIVE);


		// Set up the map.
		float width=Global.getSettings().getScreenWidth();
		float height=Global.getSettings().getScreenHeight();
		api.initMap(-width * 1.5f, width * 1.5f, -height * 10f, height * 1.5f);
		api.setBackgroundSpriteName("graphics/backgrounds/background2.jpg");

		float minX = -width / 2;
		float minY = -height / 2;

		/*for (int i = 0; i < 12; i++) {
			float x = (float) Math.random() * width - width / 2;
			float y = (float) Math.random() * height;

			if (x > -6000 && y > -2000) continue;
			float radius = 600f + (float) Math.random() * 1000f;
			api.addNebula(x, y, radius);
		}*/

		api.setBackgroundGlowColor(new Color(101, 88, 77, 100));
		api.addPlugin(new Plugin());
	}

	private static final int ARENA_SIZE=1000;
	private static boolean runOnce=false;
	static ShipAPI player=null;
	static ShipAPI enemy=null;
	private float timer = 0;

	public class Plugin extends BaseEveryFrameCombatPlugin {
		private final Vector2f LEFT = new Vector2f();
		private final Vector2f RIGHT = new Vector2f();
		private final Vector2f BELLOW = new Vector2f();
		private CombatEngineAPI engine;
		private int Nub = 0;//对话到哪儿了
		private final IntervalUtil Before_Combat_Words_TIMER = new IntervalUtil(3.0F, 8.0F);
		private final IntervalUtil Before_Combat_Words_TIMER2 = new IntervalUtil(40.0F, 50.0F);
		private IntervalUtil interval = new IntervalUtil(0.025F, 0.06F);
		private boolean WV1 = false,RF1 = false,
		WV2 = false,WV3 = false,WV4 = false,RF2 = false,WV5 = false;
		@Override
		public void init(CombatEngineAPI engine) {
			timer=0; // 利用时间来记录进场
			float screenX=Global.getSettings().getScreenWidth()*1.5f;
			float screenY=Global.getSettings().getScreenHeight()*1.5f;
			float ratio = screenX/screenY;
			//find low left corner
			float lly=-ARENA_SIZE/2-40;
			float llx=lly*ratio;
			engine.getViewport().set(llx, lly,(ARENA_SIZE*2+80)*ratio, ARENA_SIZE*2+80);
			engine.getViewport().setCenter(new Vector2f());
			//engine.getViewport().setExternalControl(true);
			//-height * 10f, height * 1.5f

			//side spawn zone
			this.LEFT.set(-(screenX / 2.0F), (screenY / 3.0F));
			this.RIGHT.set(screenX / 2.0F, (screenY / 3.0F));
		}

		public void advance(float amount, List<InputEventAPI> events) {
			if(engine == null && Global.getCombatEngine()!= null){
				Global.getLogger(this.getClass()).info("SGB_20FAbove=-=-=-=-=-=-=-=-=-=-=" +"*** engine == null");
				engine = Global.getCombatEngine();
			}
			if(engine != null) {
				//bellow spawn zone
				this.BELLOW.set(MathUtils.getRandomNumberInRange(
						-Global.getSettings().getScreenWidth(),Global.getSettings().getScreenWidth())
				,-Global.getSettings().getScreenHeight()*10f);
				Iterator ESi = engine.getShips().iterator();

				//_____________________
				//__EVERY ONE'S STATS__
				//_____________________
				if (!runOnce) {
					//Add special runOnce code latter, like some mission_key check.

					boolean checkEnemy = false, checkPlayer = false;

					if (enemy == null && !engine.getFleetManager(FleetSide.ENEMY).getDeployedCopy().isEmpty()) {
						for (int i = 0; i < engine.getFleetManager(FleetSide.ENEMY).getDeployedCopy().size(); i++) {
							enemy = engine.getFleetManager(FleetSide.ENEMY).getShipFor(engine.getFleetManager(FleetSide.ENEMY).getDeployedCopy().get(i));

							enemy.getMutableStats().getWeaponRangeThreshold().modifyMult("SGB_CGB", 1.5f);
							enemy.getMutableStats().getMissileWeaponRangeBonus().modifyMult("SGB_CGB", 2f);

							if (enemy.getHullSpec().getHullSize().equals("FIGHTER") ||
									enemy.getHullSpec().getHullSize().equals("FRIGATE")) {
								enemy.setCollisionClass(CollisionClass.FIGHTER);
							}
						}
						checkEnemy = true;
					}

					if (player == null && !engine.getFleetManager(FleetSide.PLAYER).getDeployedCopy().isEmpty()) {
						for (int i = 0; i < engine.getFleetManager(FleetSide.ENEMY).getDeployedCopy().size(); i++) {
							player = engine.getFleetManager(FleetSide.PLAYER).getShipFor(engine.getFleetManager(FleetSide.PLAYER).getDeployedCopy().get(0));
							Vector2f location = player.getLocation();
							location.x = 0;
							location.y = -512;

							player.getMutableStats().getWeaponRangeThreshold().modifyMult("SGB_CGB", 1.5f);
							player.getMutableStats().getEngineDamageTakenMult().modifyMult("SGB_CGB", 0.5f);

							if (player.getHullSpec().getHullSize().equals("FIGHTER") ||
									player.getHullSpec().getHullSize().equals("FRIGATE")) {
								player.setCollisionClass(CollisionClass.FIGHTER);
							}
						}
						checkPlayer = true;
					}

					runOnce = (checkEnemy && checkPlayer);
				}

				if(runOnce) {
					//_____________________
					//__STATE OF MISSIONS__
					//_____________________
					List<ShipAPI> shipList = Global.getCombatEngine().getShips();
					boolean StatePASS = false;
					for (ShipAPI Tship : shipList){
						if(!Tship.isAlly() && !Tship.isAlive()){
							StatePASS = true;
						}
					}
					if (!RF1) {
						for (int i = 0; i < 3; i++) {
							ShipAPI ally = engine.getFleetManager(0).spawnShipOrWing("SGB_Ares_M_F_Assault",
									LEFT,
									90.0F,
									8.0F);
							ally.setName("MPs.04" + i + "A");
						}
						for (int i = 4; i < 5; i++) {
							ShipAPI ally = engine.getFleetManager(0).spawnShipOrWing("SGB_Ares_M_F_Assault",
									RIGHT,
									-90.0F,
									8.0F);
							ally.setName("MPs.04" + i + "A");
						}
						RF1 = true;
					}
					if (!WV1) {
						for (int i = 4; i < 8; i++) {
							ShipAPI enemy = engine.getFleetManager(1).spawnShipOrWing("SGB_Ares_M_F_Assault",
									BELLOW,
									0.0F,
									2.0F);
							enemy.setName("RoT.Fa1" + i);
						}
						WV1 = true;
					}
					if (WV1 && StatePASS && !WV2){
						for (int i = 7; i < 8; i++) {
							ShipAPI enemy = engine.getFleetManager(1).spawnShipOrWing("SGB_GravTank_Hover",
									BELLOW,
									0.0F,
									10.0F);
							enemy.setName("RoT.Se7" + i);
						}
						WV2 = true;
					}
					//Global.getSoundPlayer().playCustomMusic(1, 1, "faction_sbg_market_neutral", true);
				}
				if (!engine.isPaused()) {
					this.interval.advance(amount);
					if (this.interval.intervalElapsed()){
						Iterator i$ = engine.getShips().iterator();

						while(i$.hasNext()) {
							ShipAPI ship = (ShipAPI) i$.next();
							if (!ship.isAlive() || ship.isHulk() || ship.getEngineController().isDisabled()) {
								shouldBurn(ship);
							}
						}
					}
					if (Global.getCombatEngine().getPlayerShip() != null && Global.getCombatEngine().getPlayerShip().isAlive()){
						Global.getCombatEngine().getPlayerShip().getMutableStats().getTimeMult().modifyMult("SGB_CGB", 1.15f);
						Global.getCombatEngine().getTimeMult().modifyMult("SGB_CGB", 0.82f);
					} else {
						Global.getCombatEngine().getTimeMult().unmodify("SGB_CGB");
					}
				}
				engine.setDoNotEndCombat(true);
			}
		}
	}

	public void init(CombatEngineAPI engine) {
		engine = engine;
	}

	//_____________________
	//____THE FALL UNIT____
	//_____________________
	private static void shouldBurn(ShipAPI ship){
		if(ship!=null){
			if(ship.getVelocity().getX() <= 1322f && !ship.isAlive()){
				Vector2f.add(ship.getVelocity(), MathUtils.getPoint(
								new Vector2f(),
								54,
								MathUtils.getRandomNumberInRange(-95,-85)),
						ship.getVelocity()
				);
				//ship.splitShip()
				if(ship.getAngularVelocity() <= 193.2f) {
					ship.setAngularVelocity(ship.getAngularVelocity() + MathUtils.getRandomNumberInRange(15f, 45f));
				}
				/*
				Global.getCombatEngine().addSmokeParticle(
						ship.getLocation(),
						new Vector2f(0,Math.min(-532f,-ship.getVelocity().y + MathUtils.getRandomNumberInRange(30f,100f))),
						Math.max(ship.getCollisionRadius(), 30f)
								* MathUtils.getRandomNumberInRange(1f,3f),
						0.5f,6f,
						colorNoiseBlend(new Color(197, 182, 147,50),
								new Color(199, 174, 174,100),(float) Math.random())
				);*/
				float sizeSmoke = Math.max(ship.getCollisionRadius(), 30f)
						* MathUtils.getRandomNumberInRange(0.33f,1.125f);
				MagicRender.battlespace(
						Global.getSettings().getSprite("fx", "SGB_Va_Smoke"),
						ship.getLocation(),
						new Vector2f(0,Math.min(-532f,-ship.getVelocity().y + MathUtils.getRandomNumberInRange(30f,100f))),
						new Vector2f(sizeSmoke, sizeSmoke),
						new Vector2f(sizeSmoke*1.97f, sizeSmoke*2.21f),
						//angle,
						360 * (float) Math.random(),
						0,
						colorNoiseBlend(new Color(197, 182, 147,50),
								new Color(199, 174, 174,100),(float) Math.random()),
						false,
						0.1f,
						1f,
						2f
				);
				if(Math.random() >= 0.9f) {
					Global.getCombatEngine().addHitParticle(
							ship.getLocation(),
							new Vector2f(0, -MathUtils.getRandomNumberInRange(750f,900f)),
							Math.max(ship.getCollisionRadius(), 30f)
									* MathUtils.getRandomNumberInRange(0.5f, 3f),
							1f, 2f,
							colorBlend(new Color(255, 99, 99, 185),
									new Color(241, 82, 14, 100), (float) Math.random())
					);
				}
				Global.getCombatEngine().addNebulaSmokeParticle(
						ship.getLocation(),
						new Vector2f(0,Math.min(-332f,-ship.getVelocity().y + 50f)),
						Math.max(ship.getCollisionRadius(), 30f)
								* MathUtils.getRandomNumberInRange(0.7f,2.25f),
						MathUtils.getRandomNumberInRange(0.073f,0.21f),
						0.1f, 0.15f,5f,
						colorNoiseBlend(new Color(255, 103, 28,200),
								new Color(255, 225, 168,175),(float) Math.random())
				);
				Global.getCombatEngine().addNebulaSmokeParticle(
						ship.getLocation(),
						new Vector2f(0,-520f),
						Math.max(ship.getCollisionRadius(), 30f)
								* MathUtils.getRandomNumberInRange(0.7f,1.75f),
						MathUtils.getRandomNumberInRange(0.073f,0.21f),
						0.1f, 0.15f,5f,
						colorNoiseBlend(new Color(218, 199, 191,200),
								new Color(37, 35, 35,175),(float) Math.random())
				);
			}
			else { // >350f || isAlive
				//生成爆炸
			}
		}
	}
	public static Color colorBlend(Color a, Color b, float amount) {
		float conjAmount = 1f - amount;
		return new Color(
				(int) Math.max(0, Math.min(255,
						a.getRed() * conjAmount +
								b.getRed() * amount)),
				(int) Math.max(0,
						Math.min(255,
								a.getGreen() * conjAmount +
										b.getGreen() * amount)),
				(int) Math.max(0,
						Math.min(255,
								a.getBlue() * conjAmount +
										b.getBlue() * amount)),
				(int) Math.max(0,
						Math.min(255,
								a.getAlpha() * conjAmount +
										b.getAlpha() * amount))
		);
	}
	public static Color colorNoiseBlend(Color a, Color b, float amount) {
		float conjAmount = 1f - amount;
		float randAmount = (float) (Math.random() * 0.1f);
		return new Color(
				(int) Math.max(0, Math.min(255, (1f-randAmount) *
								(a.getRed() * conjAmount + b.getRed() * amount)
						+ randAmount * (250 * Math.random())
				)),
				(int) Math.max(0, Math.min(255, (1f-randAmount) *
										(a.getGreen() * conjAmount + b.getGreen() * amount)
										+ randAmount * (250 * Math.random())
						)),
				(int) Math.max(0, Math.min(255, (1f-randAmount) *
										(a.getBlue() * conjAmount + b.getBlue() * amount)
										+ randAmount * (250 * Math.random())
						)),
				(int) Math.max(0, Math.min(255, (1f-randAmount) *
										(a.getAlpha() * conjAmount + b.getAlpha() * amount)
										+ randAmount * (250 * Math.random())
						))
		);
	}

}