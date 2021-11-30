package fr.maximouz.griefprice.managers;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.events.*;
import fr.maximouz.griefprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerScoreboardManager implements Listener {

    private final List<PlayerScoreboard> playerScoreboards;

    public PlayerScoreboardManager() {
        playerScoreboards = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(this, GriefPrice.getInstance());

    }

    public List<PlayerScoreboard> getPlayerScoreboards() {
        return playerScoreboards;
    }

    public PlayerScoreboard getPlayerScoreboard(Player player) {
        return getPlayerScoreboards().stream()
                .filter(playerScoreboard -> playerScoreboard.getUniqueId() == player.getUniqueId())
                .findAny()
                .orElse(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        playerScoreboards.add(new PlayerScoreboard(player));

        playerScoreboards.forEach(PlayerScoreboard::updateTeams);
        playerScoreboards.forEach(PlayerScoreboard::update);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLaterAsynchronously(GriefPrice.getInstance(), () -> {

            // mettre à jour les scoreboards (teams, lines)
            for (PlayerScoreboard playerScoreboard : playerScoreboards) {

                playerScoreboard.getOperatorTeam().removeEntry(player.getName());
                playerScoreboard.getSubTeam().removeEntry(player.getName());
                playerScoreboard.update();

            }

            // supprimer le scoreboard du joueur qui se déconnecte
            PlayerScoreboard playerScoreboard = getPlayerScoreboard(player);
            playerScoreboard.getBoard().delete();
            playerScoreboards.remove(playerScoreboard);

        }, 1L);

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onStart(GriefPriceStartEvent event) {

        GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::clearAndUpdate);

    }

    @EventHandler
    public void onOp(OpEvent event) {

        if (!event.isOnline())
            return;

        Player player = event.getPlayer();
        player.setDisplayName("§c" + player.getName());

        playerScoreboards.forEach(PlayerScoreboard::updateTeams);
        playerScoreboards.forEach(PlayerScoreboard::update);

    }

    @EventHandler
    public void onDeOp(DeOpEvent event) {

        if (!event.isOnline())
            return;

        Player player = event.getPlayer();
        player.setDisplayName("§7" + player.getName());

        playerScoreboards.forEach(PlayerScoreboard::updateTeams);
        playerScoreboards.forEach(PlayerScoreboard::update);

    }

    @EventHandler
    public void onProgressionChange(ProgressionChangeEvent event) {

        playerScoreboards.forEach(PlayerScoreboard::update);

    }

}
