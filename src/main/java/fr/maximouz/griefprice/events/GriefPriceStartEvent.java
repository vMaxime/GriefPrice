package fr.maximouz.griefprice.events;

import fr.maximouz.griefprice.managers.GriefPriceManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GriefPriceStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GriefPriceManager manager;

    public GriefPriceStartEvent(GriefPriceManager manager) {
        this.manager = manager;
    }

    public GriefPriceManager getManager() {
        return manager;
    }

    public long getStartedAt() {
        return manager.getStartedAt();
    }

    public long getTimePassed() {
        return manager.getTimePassed();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
