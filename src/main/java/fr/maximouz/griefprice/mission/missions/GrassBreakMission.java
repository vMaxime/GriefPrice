package fr.maximouz.griefprice.mission.missions;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.events.DynamiteExplodeEvent;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class GrassBreakMission extends Mission {

    public GrassBreakMission(MissionType type) {
        super(type);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (event.isCancelled())
            return;

        Player player = event.getPlayer();

        Material blockType = event.getBlock().getType();
        validate(blockType, player);

    }

    @EventHandler
    public void onDynamiteExplode(DynamiteExplodeEvent event) {

        Player player = event.getPlayer();

        for (Block block : event.getBlocks())
            validate(block.getType(), player);

    }

    public void validate(Material blockType, Player player) {

        if (!GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) || GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId()))
            return;

        if (blockType == Material.GRASS_BLOCK || blockType == Material.GRASS_PATH || blockType == Material.DIRT || blockType == Material.COARSE_DIRT) {

            this.getProgression(player.getUniqueId()).addAmount(1);
            GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(0.01);

        } else if (blockType == Material.GRASS) {

            this.getProgression(player.getUniqueId()).addAmount(0.1);
            GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(0.005);

        }

    }


}
