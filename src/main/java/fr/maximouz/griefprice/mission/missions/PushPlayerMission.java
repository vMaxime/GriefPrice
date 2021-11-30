package fr.maximouz.griefprice.mission.missions;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.events.PushPlayerEvent;
import fr.maximouz.griefprice.mission.Mission;
import fr.maximouz.griefprice.mission.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PushPlayerMission extends Mission {

    public PushPlayerMission(MissionType type) {
        super(type);
    }

    @EventHandler
    public void onPush(PushPlayerEvent event) {

        Player player = event.getPlayer();

        if (!GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) && GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId()))
            return;

        this.getProgression(player.getUniqueId()).addAmount(1);
        GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player.getUniqueId()).addPool(0.01);

    }

}