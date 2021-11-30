package fr.maximouz.griefprice.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer offlinePlayer;

    public OpEvent(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    public Player getPlayer() {
        return offlinePlayer.getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
