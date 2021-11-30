package fr.maximouz.griefprice.mission;

import fr.maximouz.griefprice.events.ProgressionChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Mission implements Listener {

    private final MissionType type;

    private final List<Progression> progressions;
    private final List<Progression> aliveProgressions;

    public Mission(MissionType type) {
        this.type = type;
        this.progressions = new ArrayList<>();
        this.aliveProgressions = new ArrayList<>();
    }

    public MissionType getType() {
        return type;
    }

    public String[] getDescription() {
        return type.getDescription();
    }

    public List<Progression> getProgressions() {
        return progressions;
    }

    public List<Progression> getAliveProgressions() {
        return aliveProgressions;
    }

    public void sortProgressions() {
        aliveProgressions.sort((progression1, progression2) -> Double.compare(progression2.getAmount(), progression1.getAmount()));
    }

    public Progression getProgression(UUID uuid) {
        return getProgressions().stream()
                .filter(progression -> progression.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public double getAllProgressionAmount() {
        AtomicReference<Double> amount = new AtomicReference<>(0d);
        progressions.forEach(progression -> amount.set(amount.get() + progression.getAmount()));
        return amount.get();
    }

    public void initProgression(UUID uuid, double amount) {
        Progression progression = new Progression(uuid, amount);
        progressions.add(progression);
        aliveProgressions.add(progression);
    }

    public Progression getProgressionAtPosition(int position) {
        if (position < 0)
            return null;
        return getAliveProgressions().get(position);
    }

    public int getPosition(Progression progression) {
        return getAliveProgressions().indexOf(progression);
    }

    public int getPosition(UUID uuid) {
        return getPosition(getProgression(uuid));
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onProgressionChange(ProgressionChangeEvent event) {

        sortProgressions();

    }

}
