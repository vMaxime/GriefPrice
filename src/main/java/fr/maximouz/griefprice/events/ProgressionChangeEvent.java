package fr.maximouz.griefprice.events;

import fr.maximouz.griefprice.mission.Progression;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProgressionChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Progression progression;

    public ProgressionChangeEvent(Progression progression) {
        this.progression = progression;
    }

    public Progression getProgression() {
        return progression;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
