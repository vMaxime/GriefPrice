package fr.maximouz.griefprice.mission.missions;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.events.DynamiteExplodeEvent;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DynamitePlaceMission extends Mission {

    public DynamitePlaceMission(MissionType type) {
        super(type);
    }

    @EventHandler
    public void onExplode(DynamiteExplodeEvent event) {

        Player player = event.getPlayer();

        if (!GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) || GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId()))
            return;

        this.getProgression(player.getUniqueId()).addAmount(1);
        GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(1);

    }

}
