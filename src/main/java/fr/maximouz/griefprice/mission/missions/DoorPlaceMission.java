package fr.maximouz.griefprice.mission.missions;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class DoorPlaceMission extends Mission {

    public DoorPlaceMission(MissionType type) {
        super(type);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        if (event.isCancelled())
            return;

        Player player = event.getPlayer();

        if (!GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) && GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId()))
            return;

        Block block = event.getBlockPlaced();
        Material type = block.getType();

        if (type.toString().contains("DOOR")) {

            this.getProgression(player.getUniqueId()).addAmount(1);
            GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(0.05);

        } else if (type.toString().contains("FENCE") || type.toString().contains("GATE")) {

            this.getProgression(player.getUniqueId()).addAmount(1.2);
            GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(0.006);

        }

    }

}
