package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.ai.SGB_Attack_MissileAI;
import data.scripts.ai.SGB_PD_MissileAI;
import data.scripts.world.SGBModGen;
import exerelin.campaign.SectorManager;
import data.weapons.SGB_Quench;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

import java.awt.*;


public class SGBModPlugin extends BaseModPlugin {

    private static void initXIModPlugin() {
        //Nex compatibility setting, if there is no nex or corvus mode(Nex), just generate the system
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            new SGBModGen().generate(Global.getSector());
        }
    }

    @Override
    public void onNewGame(){
        initXIModPlugin();
    }
    public static boolean hasGraphicsLib;
Color cl =new Color (197, 196, 186,255);

    public void onApplicationLoad() throws Exception {
        boolean hasLazyLib = Global.getSettings().getModManager().isModEnabled("lw_lazylib");
        if (!hasLazyLib) {
            throw new RuntimeException("Requires LazyLib!");
        }
        hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");

        if (hasGraphicsLib) {
            initGraphicsLib();
        }
    }
    private static void initGraphicsLib() {
        ShaderLib.init();
        if (ShaderLib.areShadersAllowed() && ShaderLib.areBuffersAllowed()) {
            //LightData.readLightDataCSV("data/lights/SGB_light_data.csv");
            TextureData.readTextureDataCSV("data/lights/SGB_texture_data.csv");
        }

    }
    Color test1 = new Color(236, 246, 186,255);
    Color test2 = new Color(131, 108, 95,35);
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case "SGB_Quench":
                return new PluginPick(new SGB_Quench(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "XXVII_type_7_f_srm":
                return new PluginPick(new SGB_PD_MissileAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "SGB_Axio_Rocket":
            case "XXVII_den_Felix_L":
            case "XXVII_den_Felix_R":
            case "SGB_Axio_Rocket_2":
            case "XXVII_den_Redmond_L":
            case "XXVII_den_Redmond_R":
            case "XXVII_den_Anvil_M":
            case "XXVII_den_Onslaught_RL":
            case "XXVII_den_Onslaught_LL":
            case "SGB_Austenite_Weapon_Rocket_L":
            case "SGB_Austenite_Weapon_Rocket_R":
                return new PluginPick(new SGB_Attack_MissileAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);

            default:
                return null;
        }
    }
}
