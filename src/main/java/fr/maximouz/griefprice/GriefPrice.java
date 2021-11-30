package fr.maximouz.griefprice;

import fr.maximouz.griefprice.commands.GriefPriceCommand;
import fr.maximouz.griefprice.commands.OpCommand;
import fr.maximouz.griefprice.commands.ShopCommand;
import fr.maximouz.griefprice.commands.VanishCommand;
import fr.maximouz.griefprice.listeners.PlayerListener;
import fr.maximouz.griefprice.listeners.ShopListener;
import fr.maximouz.griefprice.managers.GriefPriceManager;
import fr.maximouz.griefprice.managers.GriefPricePlayerManager;
import fr.maximouz.griefprice.managers.MissionManager;
import fr.maximouz.griefprice.managers.PlayerScoreboardManager;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;

public class GriefPrice extends JavaPlugin {

    private static GriefPrice INSTANCE;
    public static Location CENTER;
    public static int MIN_PLAYERS;

    private GriefPricePlayerManager griefPricePlayerManager;
    private GriefPriceManager griefPriceManager;
    private PlayerScoreboardManager playerScoreboardManager;
    private MissionManager missionManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        // config
        CENTER = Utils.locationFromString(getConfig().getString("center"));
        MIN_PLAYERS = 100;

        INSTANCE = this;

        // game rules
        getServer().getWorlds().forEach(world -> {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, true);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setTime(0);
        });

        // worldborder
        WorldBorder worldBorder = CENTER.getWorld().getWorldBorder();
        worldBorder.setCenter(CENTER);
        worldBorder.setSize(280);

        // managers
        griefPricePlayerManager = new GriefPricePlayerManager();
        playerScoreboardManager = new PlayerScoreboardManager();
        griefPriceManager = new GriefPriceManager();
        missionManager = new MissionManager();

        // listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);

        // commands
        getCommand("griefprice").setExecutor(new GriefPriceCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("op").setExecutor(new OpCommand());
        getCommand("shop").setExecutor(new ShopCommand());

    }

    @Override
    public void onDisable() {

        missionManager.cancelMissionsTask();

    }

    public static GriefPrice getInstance() {
        return INSTANCE;
    }

    public GriefPricePlayerManager getGriefPricePlayerManager() {
        return griefPricePlayerManager;
    }

    public GriefPriceManager getManager() {
        return griefPriceManager;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public PlayerScoreboardManager getPlayerScoreboardManager() {
        return playerScoreboardManager;
    }

}
