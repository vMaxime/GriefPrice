package fr.maximouz.griefprice.tasks;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.Progression;
import fr.maximouz.griefprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MissionEliminationTask extends BukkitRunnable {

    public MissionEliminationTask() {
        runTaskTimer(GriefPrice.getInstance(), 0L, 20L);
        GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::clearAndUpdate);
    }

    @Override
    public void run() {

        long nextEliminationAt = GriefPrice.getInstance().getMissionManager().getNextEliminationAt();
        Mission currentMission = GriefPrice.getInstance().getMissionManager().getCurrentMission();

        if (System.currentTimeMillis() >= nextEliminationAt) {

            long eliminationDelay = GriefPrice.getInstance().getMissionManager().getEliminationDelay();

            Progression lastProgression = currentMission.getProgressionAtPosition(currentMission.getAliveProgressions().size() - 1);
            GriefPrice.getInstance().getManager().eliminate(lastProgression.getUniqueId());
            GriefPrice.getInstance().getMissionManager().setNextEliminationAt(System.currentTimeMillis() + eliminationDelay);

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 2f, 2f));

        } else {

            GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::update);

            if (nextEliminationAt - System.currentTimeMillis() <= 3000) {

                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f));

            }

        }

    }

}
