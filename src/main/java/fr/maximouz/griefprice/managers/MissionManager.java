package fr.maximouz.griefprice.managers;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.Utils;
import fr.maximouz.griefprice.events.GriefPriceStartEvent;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import fr.maximouz.griefprice.mission.Progression;
import fr.maximouz.griefprice.tasks.MissionEliminationTask;
import fr.maximouz.griefprice.tasks.MissionStartTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MissionManager implements Listener {

    private final List<Mission> missions;
    private Mission currentMission;
    private long currentMissionStartAt;
    private long eliminationDelay;
    private long nextEliminationAt;

    private int currentMissionAliveGoal;

    private MissionStartTask currentMissionStartTask;
    private MissionEliminationTask currentMissionEliminationTask;


    private final List<Location> currentMissionPlacedBlocks;

    public MissionManager() {
        missions = new ArrayList<>();
        currentMission = null;
        eliminationDelay = 30000;
        currentMissionPlacedBlocks = new ArrayList<>();

        for (MissionType type : MissionType.values())
            missions.add(type.getNewMissionInstance());

        Bukkit.getPluginManager().registerEvents(this, GriefPrice.getInstance());
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public Mission getMission(MissionType type) {
        return missions.stream()
                .filter(mission -> mission.getType().equals(type))
                .findAny()
                .orElse(null);
    }

    public Mission getNextMission() {
        if (currentMission != null) {
            int index = missions.indexOf(currentMission);

            if (index + 1 >= missions.size()) return null;
            else return missions.get(index + 1);

        } else {
            return missions.get(0);
        }
    }

    public Mission getCurrentMission() {
        return currentMission;
    }

    public void setCurrentMission(Mission currentMission) {
        this.currentMission = currentMission;
    }

    public long getCurrentMissionStartAt() {
        return currentMissionStartAt;
    }

    public void setCurrentMissionStartAt(long currentMissionStartAt) {
        this.currentMissionStartAt = currentMissionStartAt;
    }

    public long getEliminationDelay() {
        return eliminationDelay;
    }

    public void setEliminationDelay(long eliminationDelay) {
        this.eliminationDelay = eliminationDelay;
    }

    public long getNextEliminationAt() {
        return nextEliminationAt;
    }

    public void setNextEliminationAt(long nextEliminationAt) {
        this.nextEliminationAt = nextEliminationAt;
    }

    public String getTotalProgression(UUID uuid) {

        double total = 0d;

        for (Mission mission : missions) {

            Progression progression = mission.getProgression(uuid);

            if (progression != null)
                total += progression.getAmount();

        }

        return new DecimalFormat("#.#").format(total);

    }

    public String getTotalProgression(Player player) {
        return getTotalProgression(player.getUniqueId());
    }

    public MissionStartTask getCurrentMissionStartTask() {
        return currentMissionStartTask;
    }

    public void setCurrentMissionStartTask(MissionStartTask task) {
        this.currentMissionStartTask = task;
    }

    public MissionEliminationTask getCurrentMissionEliminationTask() {
        return currentMissionEliminationTask;
    }

    public void setCurrentMissionEliminationTask(MissionEliminationTask task) {
        this.currentMissionEliminationTask = task;
    }

    public int getCurrentMissionAliveGoal() {
        return currentMissionAliveGoal;
    }

    public void setCurrentMissionAliveGoal(int currentMissionAliveGoal) {
        this.currentMissionAliveGoal = currentMissionAliveGoal;
    }

    public List<Location> getCurrentMissionPlacedBlocks() {
        return currentMissionPlacedBlocks;
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onStart(GriefPriceStartEvent event) {

        setCurrentMission(getNextMission());
        startCurrentMission();

    }

    public void nextMission() {

        if (currentMission != null) {

            if (currentMissionStartTask != null) {
                currentMissionStartTask.cancel();
                setCurrentMissionStartTask(null);
            }

            if (currentMissionEliminationTask != null) {
                currentMissionEliminationTask.cancel();
                setCurrentMissionEliminationTask(null);
            }

            HandlerList.unregisterAll(currentMission);

        }

        Mission nextMission = getNextMission();

        if (nextMission != null) {

            setCurrentMission(nextMission);
            startCurrentMission();

        } else {

            GriefPrice.getInstance().getManager().stop();

        }

    }

    private void startCurrentMission() {

        GriefPrice.getInstance().getManager().getAlivePlayers().forEach(uuid -> currentMission.initProgression(uuid, 0));

        currentMissionStartAt = System.currentTimeMillis() + 10000;
        // reset des blocs placés par les joueurs entre chaque mission
        currentMissionPlacedBlocks.clear();

        int aliveCount = GriefPrice.getInstance().getManager().getAliveCount();
        currentMissionAliveGoal = aliveCount > 85
                ? 85
                : aliveCount > 70
                    ? 70
                    : aliveCount > 55
                        ? 55
                        : aliveCount > 40
                            ? 40
                            : aliveCount > 25
                                ? 25
                                : aliveCount > 10
                                    ? 10
                                    : 1;

        if (eliminationDelay != 15000 && aliveCount - currentMissionAliveGoal <= 10)
            eliminationDelay = 15000;

        nextEliminationAt = currentMissionStartAt + eliminationDelay;
        setCurrentMissionStartTask(new MissionStartTask());

        Bukkit.broadcastMessage("§9§m                                                           ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§3§lMission"));
        Bukkit.broadcastMessage("§7 ˣ Vous devez: §b" + currentMission.getDescription()[0]);
        Bukkit.broadcastMessage("§b   " + currentMission.getDescription()[1]);
        Bukkit.broadcastMessage("§7 ˣ Début dans §c" + TimeUnit.MILLISECONDS.toSeconds(currentMissionStartAt -  System.currentTimeMillis()) + "s");
        Bukkit.broadcastMessage("§9§m                                                           ");

    }

    public void cancelMissionsTask() {
        if (currentMissionStartTask != null)
            currentMissionStartTask.cancel();
        if (currentMissionEliminationTask != null)
            currentMissionEliminationTask.cancel();
    }

    public boolean isPlacedByPlayer(Location location) {
        return currentMissionPlacedBlocks.contains(location);
    }

    public boolean isPlacedByPlayer(Block block) {
        return isPlacedByPlayer(block.getLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (currentMission == null)
            return;

        Player player = event.getPlayer();
        if (GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) && !GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId())) {
            GriefPrice.getInstance().getManager().eliminate(player.getUniqueId());
            event.setQuitMessage("§7" + player.getName() + "§c s'est déconnecté.");
        }

    }

}
