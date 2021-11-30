package fr.maximouz.griefprice.mission;

import fr.maximouz.griefprice.events.ProgressionChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class Progression {

    private final UUID uuid;
    private double amount;

    public Progression(UUID uuid, double amount) {
        this.uuid = uuid;
        this.amount = amount;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public double getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return new DecimalFormat("#.#").format(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
        Bukkit.getPluginManager().callEvent(new ProgressionChangeEvent(this));
    }

    public void addAmount(double amount) {
        setAmount(this.amount + amount);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
