package fr.maximouz.griefprice.mission.missions;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.Utils;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class ReachLimitMission extends Mission {

    public ReachLimitMission(MissionType type) {
        super(type);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        if (event.isCancelled())
            return;

        Player player = event.getPlayer();

        if ((!GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) && GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId())) || event.getBlock().getLocation().getY() < 100)
            return;

        this.getProgression(player.getUniqueId()).addAmount(1);
        GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(1);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f);
        Location location = Utils.getRandomLocation(player.getLocation(), 10);
        location.setY(location.getWorld().getHighestBlockYAt(location));
        player.teleport(location);

        Bukkit.getOnlinePlayers().forEach(target -> {

            if (target.isOp() && !GriefPrice.getInstance().getManager().isParticipating(target.getUniqueId())) {
                target.sendMessage("§5" + player.getName() + " a atteint la couche 100 !");
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }

        });

    }

}
