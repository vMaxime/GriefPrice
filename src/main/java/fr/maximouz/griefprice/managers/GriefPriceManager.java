package fr.maximouz.griefprice.managers;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.GriefPricePlayer;
import fr.maximouz.griefprice.Utils;
import fr.maximouz.griefprice.events.GriefPriceStartEvent;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import fr.maximouz.griefprice.mission.Progression;
import fr.maximouz.griefprice.scoreboard.PlayerScoreboard;
import fr.maximouz.griefprice.tasks.MissionStartTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GriefPriceManager {

    private final List<UUID> participatingPlayers;
    private final List<UUID> alivePlayers;
    private long startedAt;

    private UUID winner;

    public GriefPriceManager() {

        participatingPlayers = new ArrayList<>();
        alivePlayers = new ArrayList<>();
        startedAt = -1;
        winner = null;

    }

    public boolean hasStarted() {
        return startedAt != -1;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - getStartedAt();
    }

    public boolean isEliminated(UUID uuid) {
        if (!participatingPlayers.contains(uuid))
            throw new Error("Checking if a player is eliminated while he is not participating");
        return !alivePlayers.contains(uuid);
    }

    public void eliminate(UUID uuid) {
        alivePlayers.remove(uuid);
        // retirer le joueur des progressions en vie dans la mission
        Progression progression = GriefPrice.getInstance().getMissionManager().getCurrentMission().getProgression(uuid);
        GriefPrice.getInstance().getMissionManager().getCurrentMission().getAliveProgressions().remove(progression);
        Player player = Bukkit.getPlayer(uuid);

        String playerName;

        Bukkit.broadcastMessage("§4§m                                                           ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§4§lElimination"));

        if (player != null) {

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(player.getLocation().add(0, 3, 0));

            playerName = player.getName();

            GriefPricePlayer gPlayer = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player);

            double pool = gPlayer.getPool();
            double percent = pool > 0 ? (pool / GriefPrice.getInstance().getGriefPricePlayerManager().getTotalPools()) * 100 : 0;

            Bukkit.broadcastMessage("§cˣ " + playerName);
            Bukkit.broadcastMessage("§7 ˣ Participation: §e" + gPlayer.getFormattedPool() + "€ §7§o(" + new DecimalFormat("#.#").format(percent) + "%)");
            Bukkit.broadcastMessage("§7 ˣ Points boutique: §6" + gPlayer.getFormattedShopPoints());
            Bukkit.broadcastMessage("§7 ˣ Progressions: §b" + GriefPrice.getInstance().getMissionManager().getTotalProgression(player));

        } else {

            playerName = Bukkit.getOfflinePlayer(uuid).getName();
            Bukkit.broadcastMessage("§7 ˣ Joueur: §f" + playerName);

        }

        Bukkit.broadcastMessage("§4§m                                                           ");


        int aliveGoal = GriefPrice.getInstance().getMissionManager().getCurrentMissionAliveGoal();

        if (GriefPrice.getInstance().getMissionManager().getEliminationDelay() != 15000 && getAliveCount() - aliveGoal <= 10)
            GriefPrice.getInstance().getMissionManager().setEliminationDelay(15000);

        if (getAliveCount() <= aliveGoal) {

            if (getAliveCount() == 1) {

                GriefPrice.getInstance().getManager().stop();

            } else {

                GriefPrice.getInstance().getMissionManager().nextMission();
                GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::clearAndUpdate);

            }

        } else {

            GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::update);

        }

    }

    public boolean isParticipating(UUID uuid) {
        return participatingPlayers.contains(uuid);
    }

    public List<UUID> getParticipatingPlayers() {
        return participatingPlayers;
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public int getAliveCount() {
        return alivePlayers.size();
    }

    public UUID getWinner() {
        return winner;
    }

    public void start() {

        startedAt = System.currentTimeMillis();

        GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayers().forEach(griefPricePlayer -> {

            Player player = griefPricePlayer.getPlayer();

            if (player == null)
                return;

            player.setGameMode(player.isOp() ? GameMode.CREATIVE : GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setFoodLevel(20);

            if (!player.isOp()) {
                participatingPlayers.add(griefPricePlayer.getUniqueId());
                alivePlayers.add(griefPricePlayer.getUniqueId());
            }

        });

        Bukkit.getPluginManager().callEvent(new GriefPriceStartEvent(this));

    }

    public void save() {

        FileConfiguration config = GriefPrice.getInstance().getConfig();
        MissionManager missionManager = GriefPrice.getInstance().getMissionManager();
        Mission currentMission = GriefPrice.getInstance().getMissionManager().getCurrentMission();

        GriefPrice.getInstance().getLogger().info("saving game data..");
        // données de la partie en cours
        config.set("save.saved_at", System.currentTimeMillis());
        config.set("save.started_at", startedAt);
        config.set("save.winner", winner != null ? winner.toString() : "null");
        config.set("save.current_mission", currentMission != null ? currentMission.getType().toString() : "null");
        config.set("save.current_mission_start_at", missionManager.getCurrentMissionStartAt());
        config.set("save.elimination_delay", missionManager.getEliminationDelay());
        config.set("save.current_mission_alive_goal", missionManager.getCurrentMissionAliveGoal());
        if (currentMission != null)
            config.set("save.next_elimination_in", missionManager.getNextEliminationAt() - System.currentTimeMillis());
        GriefPrice.getInstance().getLogger().finest("game data saved.");
        // joueurs
        GriefPrice.getInstance().getLogger().info("saving players..");
        for (UUID uuid : getParticipatingPlayers()) {

            try {

                GriefPricePlayer gPlayer = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(uuid);

                if (gPlayer == null)
                    continue;

                Player player = gPlayer.getPlayer();

                String key = "save.players." + uuid.toString();
                config.set(key + ".alive", !isEliminated(uuid));
                if (player != null)
                    config.set(key + ".location", Utils.locationToString(player.getLocation()));
                config.set(key + ".shop_points", gPlayer.getShopPoints());
                config.set(key + ".pool", gPlayer.getPool());

                for (Mission mission : missionManager.getMissions()) {

                    Progression progression = mission.getProgression(uuid);
                    if (progression != null) {

                        config.set(key + ".missions." + mission.getType().toString(), progression.getAmount());

                    }

                }

            } catch (Exception ex) {

                ex.printStackTrace();
                GriefPrice.getInstance().getLogger().severe("could'nt save player " + uuid.toString());

            }

        }
        GriefPrice.getInstance().getLogger().finest("players saved.");
        // blocs posés par les joueurs
        GriefPrice.getInstance().getLogger().info("saving current mission placed blocks..");
        List<String> blocks = new ArrayList<>();
        for (Location location : missionManager.getCurrentMissionPlacedBlocks())
            blocks.add(Utils.locationToString(location));
        config.set("save.blocks", blocks);
        GriefPrice.getInstance().getLogger().finest("current mission placed blocks saved.");

        GriefPrice.getInstance().saveConfig();

    }

    public void load() {

        FileConfiguration config = GriefPrice.getInstance().getConfig();
        MissionManager missionManager = GriefPrice.getInstance().getMissionManager();

        long difference = System.currentTimeMillis() - config.getLong("save.saved_at");
        GriefPrice.getInstance().getLogger().info("difference of " + TimeUnit.MILLISECONDS.toSeconds(difference) + "s.");

        if (!config.contains("save"))
            return;

        startedAt = config.getLong("save.started_at") + difference;
        String stringWinner = config.getString("save.winner");
        if (!stringWinner.equals("null"))
            winner = UUID.fromString(stringWinner);

        MissionType missionType = MissionType.getFromString(config.getString("save.current_mission"));
        if (missionType != null) {

            missionManager.setCurrentMissionStartAt(config.getLong("save.current_mission_start_at") + difference);
            missionManager.setEliminationDelay(config.getLong("save.elimination_delay"));
            missionManager.setCurrentMissionAliveGoal(config.getInt("save.current_mission_alive_goal"));
            missionManager.setNextEliminationAt(System.currentTimeMillis() + config.getLong("save.next_elimination_in"));

            missionManager.setCurrentMission(missionManager.getMission(missionType));

        }

        for (String block : config.getStringList("save.blocks"))
            missionManager.getCurrentMissionPlacedBlocks().add(Utils.locationFromString(block));

        for (String key : config.getConfigurationSection("save.players").getKeys(false)) {

            ConfigurationSection playerSection = config.getConfigurationSection("save.players." + key);

            UUID uuid = UUID.fromString(key);
            GriefPricePlayer gPlayer = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(uuid);

            if (gPlayer == null) {

                gPlayer = new GriefPricePlayer(uuid);
                GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayers().add(gPlayer);

            }

            Player player = gPlayer.getPlayer();

            boolean alive = playerSection.getBoolean("alive");

            participatingPlayers.add(uuid);
            if (alive && player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                alivePlayers.add(uuid);
                if (playerSection.contains("location"))
                    player.teleport(Utils.locationFromString(playerSection.getString("location")));
            }
            gPlayer.addShopPoints(playerSection.getLong("shop_points"));
            gPlayer.addPool(playerSection.getDouble("pool"));

            // progressions
            for (String stringMissionType : config.getConfigurationSection("save.players." + uuid.toString() + ".missions").getKeys(false)) {

                MissionType type = MissionType.getFromString(stringMissionType);
                if (type == null)
                    continue;
                Mission mission = GriefPrice.getInstance().getMissionManager().getMission(type);
                mission.initProgression(uuid, config.getDouble("save.players." + uuid.toString() + ".missions." + stringMissionType));

            }

        }

        missionManager.getMissions().forEach(Mission::sortProgressions);
        Mission currentMission = GriefPrice.getInstance().getMissionManager().getCurrentMission();

        // partie terminée
        if (currentMission == null) {

            Bukkit.broadcastMessage("§2§m                                                           ");
            Bukkit.broadcastMessage(Utils.getCenteredText("§a§lGagnant"));

            if (winner != null) {

                OfflinePlayer opWinner = Bukkit.getOfflinePlayer(winner);
                Bukkit.broadcastMessage("§f ˣ " + opWinner.getName());

                GriefPricePlayer gWinner = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(winner);

                if (gWinner != null) {

                    double pool = gWinner.getPool();
                    double percent = pool > 0 ? (pool / GriefPrice.getInstance().getGriefPricePlayerManager().getTotalPools()) * 100 : 0;

                    Bukkit.broadcastMessage("§7 ˣ Participation: §e" + gWinner.getFormattedPool() + "€ §7§o(" + new DecimalFormat("#.#").format(percent) + "%)");
                    Bukkit.broadcastMessage("§7 ˣ Points boutique: §6" + gWinner.getFormattedShopPoints());
                    Bukkit.broadcastMessage("§7 ˣ Progressions: §b" + GriefPrice.getInstance().getMissionManager().getTotalProgression(winner));
                    Bukkit.broadcastMessage("§2§m                                                           ");

                }

            }

        } else {

            Bukkit.broadcastMessage("§9§m                                                           ");
            Bukkit.broadcastMessage(Utils.getCenteredText("§3§lMission"));
            Bukkit.broadcastMessage("§7 ˣ Vous devez: §b" + currentMission.getDescription()[0]);
            Bukkit.broadcastMessage("§b   " + currentMission.getDescription()[1]);
            long startIn = TimeUnit.MILLISECONDS.toSeconds(missionManager.getCurrentMissionStartAt() - System.currentTimeMillis());
            Bukkit.broadcastMessage("§7 ˣ Début de la mission " + (startIn > 0 ? "dans §c" + startIn + "s§7." : "§cmaintenant§7 !"));
            Bukkit.broadcastMessage("§9§m                                                           ");

            missionManager.setCurrentMissionStartTask(new MissionStartTask());

        }

        for (Player player : Bukkit.getOnlinePlayers())
            if (!isParticipating(player.getUniqueId()) || isEliminated(player.getUniqueId())) {
                player.setGameMode(player.isOp() ? GameMode.CREATIVE : GameMode.SPECTATOR);
                if (!player.isOp())
                    player.sendMessage("§7§oLa partie a déjà commencé, vous êtes spectateur.");
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }

    }

    public void stop() {

        if (GriefPrice.getInstance().getMissionManager().getCurrentMissionEliminationTask() != null) {

            GriefPrice.getInstance().getMissionManager().getCurrentMissionEliminationTask().cancel();
            GriefPrice.getInstance().getMissionManager().setCurrentMissionEliminationTask(null);

        }

        Bukkit.broadcastMessage("§2§m                                                           ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§a§lGagnant"));
        String winnerName = "N/A";
        String participation = "N/A";
        String shopPoints = "N/A";
        String progressions = "N/A";

        UUID winnerUniqueId = alivePlayers.get(0);

        if (winnerUniqueId != null) {

            OfflinePlayer winner = Bukkit.getOfflinePlayer(winnerUniqueId);

            this.winner = winnerUniqueId;

            winnerName = winner.getName();

            GriefPricePlayer gWinner = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(winnerUniqueId);

            if (gWinner != null) {

                double pool = gWinner.getPool();
                double percent = pool > 0 ? (pool / GriefPrice.getInstance().getGriefPricePlayerManager().getTotalPools()) * 100 : 0;

                participation = gWinner.getFormattedPool() + "€ §7§o(" + new DecimalFormat("#.#").format(percent) + "%)";
                shopPoints = gWinner.getFormattedShopPoints();
                progressions = GriefPrice.getInstance().getMissionManager().getTotalProgression(winnerUniqueId);

            }

        }

        Bukkit.broadcastMessage("§aˣ " + winnerName);
        Bukkit.broadcastMessage("§7 ˣ Participation: §e" + participation);
        Bukkit.broadcastMessage("§7 ˣ Points boutique: §6" + shopPoints);
        Bukkit.broadcastMessage("§7 ˣ Progressions: §b" + progressions);
        Bukkit.broadcastMessage("§2§m                                                           ");

        GriefPrice.getInstance().getMissionManager().setCurrentMission(null);
        GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::clearAndUpdate);

    }

}
