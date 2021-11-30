package fr.maximouz.griefprice.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PushPlayerEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public final Player victim;

    public PushPlayerEvent(Player player, Player victim) {
        super(player);
        this.victim = victim;
    }

    public Player getVictim() {
        return victim;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
