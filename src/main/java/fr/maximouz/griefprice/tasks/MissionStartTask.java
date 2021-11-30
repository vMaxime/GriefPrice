package fr.maximouz.griefprice.tasks;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class MissionStartTask extends BukkitRunnable {

    public MissionStartTask() {
        runTaskTimer(GriefPrice.getInstance(), 0L, 20L);
    }

    @Override
    public void run() {

        Mission currentMission = GriefPrice.getInstance().getMissionManager().getCurrentMission();
        long startAt = GriefPrice.getInstance().getMissionManager().getCurrentMissionStartAt();

        if (System.currentTimeMillis() >= startAt) {

            Bukkit.getPluginManager().registerEvents(currentMission, GriefPrice.getInstance());

            GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::clearAndUpdate);

            GriefPrice.getInstance().getMissionManager().setCurrentMissionStartTask(null);
            GriefPrice.getInstance().getMissionManager().setCurrentMissionEliminationTask(new MissionEliminationTask());

            cancel();

        } else {

            GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::update);

        }

    }

}
