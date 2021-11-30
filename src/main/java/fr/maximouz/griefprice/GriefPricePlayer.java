package fr.maximouz.griefprice;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class GriefPricePlayer {

    private final UUID uuid;
    private long shopPoints;
    private double pool;
    private boolean vanished;

    public GriefPricePlayer(UUID uuid, long shopPoints, double pool, boolean vanished) {
        this.uuid = uuid;
        this.shopPoints = shopPoints;
        this.pool = pool;
        this.vanished = vanished;
    }

    public GriefPricePlayer(UUID uuid) {
        this(uuid, 0L, 0, false);
    }

    public GriefPricePlayer(Player player) {
        this(player.getUniqueId());
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    public long getShopPoints() {
        return shopPoints;
    }

    public String getFormattedShopPoints() {
        return new DecimalFormat("#.#").format(shopPoints);
    }

    public void addShopPoints(long amount) {
        this.shopPoints += amount;
    }

    public void withdrawShopPoints(long amount) {
        this.shopPoints -= amount;
    }

    public double getPool() {
        return pool;
    }

    public String getFormattedPool() {
        return new DecimalFormat("#.##").format(pool);
    }

    public void addPool(double amount) {
        this.pool += amount;
        GriefPrice.getInstance().getGriefPricePlayerManager().updateTotalPools();
    }

    public void withdrawPool(double amount) {
        this.pool -= amount;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void vanish() {
        Player player = getPlayer();

        if (player == null)
            return;

        Bukkit.getOnlinePlayers().forEach(target -> {

            if (target != player && !target.isOp() && target.canSee(player))
                target.hidePlayer(GriefPrice.getInstance(), player);

        });
        vanished = true;
    }

    public void unVanish() {
        Player player = getPlayer();

        if (player == null)
            return;

        Bukkit.getOnlinePlayers().forEach(target -> {

            if (target != player && !target.canSee(player))
                target.showPlayer(GriefPrice.getInstance(), player);

        });
        vanished = false;
    }

}
