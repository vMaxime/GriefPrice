package fr.maximouz.griefprice.managers;

import fr.maximouz.griefprice.GriefPricePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class GriefPricePlayerManager {

    private final List<GriefPricePlayer> griefPricePlayers;
    private double totalPools;

    public GriefPricePlayerManager() {
        griefPricePlayers = new ArrayList<>();
        totalPools = 0d;
    }

    public List<GriefPricePlayer> getGriefPricePlayers() {
        return griefPricePlayers;
    }

    public GriefPricePlayer getGriefPricePlayer(UUID uuid) {
        return getGriefPricePlayers().stream()
                .filter(griefPricePlayer -> griefPricePlayer.getUniqueId().equals(uuid))
                .findAny()
                .orElse(null);
    }

    public GriefPricePlayer getGriefPricePlayer(Player player) {
        return getGriefPricePlayer(player.getUniqueId());
    }

    public void updateTotalPools() {
        AtomicReference<Double> total = new AtomicReference<>(0d);
        getGriefPricePlayers().forEach(griefPricePlayer -> total.set(total.get() + griefPricePlayer.getPool()));
        this.totalPools = total.get();
    }

    public double getTotalPools() {
        return totalPools;
    }

    public String getFormattedTotalPools() {
        return new DecimalFormat("#.##").format(getTotalPools());
    }

    public Long getTotalShopPoints() {
        AtomicReference<Long> total = new AtomicReference<>(0L);
        getGriefPricePlayers().forEach(griefPricePlayer -> total.set(total.get() + griefPricePlayer.getShopPoints()));
        return total.get();
    }

}
