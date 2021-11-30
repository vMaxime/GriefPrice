package fr.maximouz.griefprice.scoreboard;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.GriefPricePlayer;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.Progression;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerScoreboard {

    private final BPlayerBoard board;
    private final GriefPricePlayer gPlayer;

    private final Team operatorTeam;
    private final Team subTeam;

    public PlayerScoreboard(Player player) {
        board = Netherboard.instance().createBoard(player, "§c§lGriefPrice");
        gPlayer = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player);

        Scoreboard scoreboard = board.getScoreboard();

        operatorTeam = scoreboard.registerNewTeam("0Operator");
        operatorTeam.setPrefix("Opérateur ");
        operatorTeam.setColor(ChatColor.RED);
        operatorTeam.setCanSeeFriendlyInvisibles(true);

        subTeam = scoreboard.registerNewTeam("1Sub");
        subTeam.setPrefix("Abonné ");
        subTeam.setColor(ChatColor.GRAY);
        subTeam.setCanSeeFriendlyInvisibles(false);

        updateTeams();
        update();

    }

    public BPlayerBoard getBoard() {
        return board;
    }

    public Team getOperatorTeam() {
        return operatorTeam;
    }

    public Team getSubTeam() {
        return subTeam;
    }

    public Player getPlayer() {
        return board.getPlayer();
    }

    public UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }

    public void update() {

        if (!GriefPrice.getInstance().getManager().hasStarted()) {

            long players = Bukkit.getOnlinePlayers().stream().filter(player -> !player.isOp()).count();
            String state = players >= GriefPrice.MIN_PLAYERS ? "§adu démarrage" : "§cdes abonnés";

            board.setAll(
                    "§1",
                    "§fAbonnés: " + (players >= GriefPrice.MIN_PLAYERS ? "§a" : "§7") + players + "/" + GriefPrice.MIN_PLAYERS,
                    "§fStatus: " + (players >= GriefPrice.MIN_PLAYERS ? "§a" : "§c") + "En attente",
                    state,
                    "§2"
            );

            return;
        }

        Mission currentMission = GriefPrice.getInstance().getMissionManager().getCurrentMission();

        if (currentMission == null) {

            String playerName = "N/A";
            String participation = "N/A";
            String shopPoints = "N/A";
            String progressions = "N/A";

            UUID winnerUid = GriefPrice.getInstance().getManager().getWinner();

            if (winnerUid != null) {

                OfflinePlayer winner = Bukkit.getOfflinePlayer(winnerUid);
                playerName = winner.getName();

                GriefPricePlayer gWinner = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(winnerUid);
                if (gWinner != null) {

                    double pool = gWinner.getPool();
                    double percent = pool > 0 ? (pool / GriefPrice.getInstance().getGriefPricePlayerManager().getTotalPools()) * 100 : 0;

                    participation = gWinner.getFormattedPool() + "€ §7§o(" + new DecimalFormat("#.#").format(percent) + "%)";
                    shopPoints = gWinner.getFormattedShopPoints();
                    progressions = GriefPrice.getInstance().getMissionManager().getTotalProgression(winnerUid);

                }

            }

            board.setAll(
                    "§1",
                    "§2§lFIN DE LA PARTIE",
                    "§7 ˣ Cagnotte: §e" + GriefPrice.getInstance().getGriefPricePlayerManager().getFormattedTotalPools() + "€",
                    "§2",
                    "§a§lGAGNANT",
                    "§aˣ " + playerName,
                    "§7 ˣ Participation: §e" + participation,
                    "§7 ˣ Points boutique: §6" + shopPoints,
                    "§7 ˣ Progressions: §b" + progressions,
                    "§3"
            );
            return;
        }

        boolean participating = GriefPrice.getInstance().getManager().isParticipating(getPlayer().getUniqueId());

        int line = 0;

        board.set("§1", line--);
        board.set("§7ˣ Cagnotte: §e" + GriefPrice.getInstance().getGriefPricePlayerManager().getFormattedTotalPools() + "€", line--);
        if (participating)
            board.set("§7ˣ Points boutique: §6" + gPlayer.getShopPoints(), line--);
        board.set("§2", line--);
        board.set("§c§lMISSION", line--);
        board.set("§7§o" + currentMission.getDescription()[0], line--);
        board.set("§7§o" + currentMission.getDescription()[1], line--);
        board.set("§3", line--);

        if (GriefPrice.getInstance().getMissionManager().getCurrentMissionEliminationTask() == null) {

            board.set("§3", line--);
            board.set("§7 ˣ Début dans : §e" + (TimeUnit.MILLISECONDS.toSeconds(GriefPrice.getInstance().getMissionManager().getCurrentMissionStartAt() - System.currentTimeMillis()) + 1) + "s", line--);
            board.set("§4", line);

            return;
        }

        board.set("§7ˣ Elimination dans §c" + (TimeUnit.MILLISECONDS.toSeconds(GriefPrice.getInstance().getMissionManager().getNextEliminationAt() - System.currentTimeMillis()) + 1) + "s", line--);
        board.set("§4", line--);

        int lastPosition = currentMission.getAliveProgressions().size() - 1;

        if (participating && !GriefPrice.getInstance().getManager().isEliminated(getUniqueId())) {

            int playerPosition = currentMission.getPosition(getUniqueId());
            Progression playerProgression = currentMission.getProgression(getUniqueId());

            if (playerPosition > 0) {

                //progression devant lui
                Progression frontProgression = currentMission.getProgressionAtPosition(playerPosition - 1);
                board.set("§e#" + (playerPosition /* ne pas -1 car les listes commencent à 0*/) + ". " + frontProgression.getPlayer().getName(), line--);
                board.set("  §e⇢ §b" + frontProgression.getFormattedAmount() + "§1", line--);

                // sa progression
                board.set("§a#" + (playerPosition + 1) + ". Vous", line--);
                board.set("  §a⇢ §b" + playerProgression.getFormattedAmount() + " §2", line--);

            } else {

                // sa progression
                board.set("§a#" + (playerPosition + 1) + ". Vous", line--);
                board.set("  §a⇢ §b" + playerProgression.getFormattedAmount() + "§3", line--);

                // progression derrière lui
                Progression backProgression = currentMission.getProgressionAtPosition(playerPosition + 1);
                board.set("§e#" + (playerPosition + 2) + ". " + backProgression.getPlayer().getName(), line--);
                board.set("  §e⇢ §b" + backProgression.getFormattedAmount() + " §4", line--);

            }

        } else {

            // spectateur
            Progression top1Progression = currentMission.getProgressionAtPosition(0);
            board.set("§e#1. " + top1Progression.getPlayer().getName(), line--);
            board.set("  §e⇢ §b" + top1Progression.getFormattedAmount() + "§5", line--);

            Progression topLastProgression = currentMission.getProgressionAtPosition(lastPosition);
            board.set("§e#" + (lastPosition + 1) + ". " + topLastProgression.getPlayer().getName(), line--);
            board.set("  §e⇢ §b" + topLastProgression.getFormattedAmount() + " §6", line--);

        }

        board.set("§5", line);

    }

    public void clear() {
        board.clear();
    }

    public void clearAndUpdate() {
        clear();
        update();
    }

    public void updateTeams() {

        Bukkit.getOnlinePlayers().forEach(target -> {

            if (target.isOp()) {

                subTeam.removeEntry(target.getName());
                operatorTeam.addEntry(target.getName());

            } else {

                operatorTeam.removeEntry(target.getName());
                subTeam.addEntry(target.getName());

            }

        });

    }

}
