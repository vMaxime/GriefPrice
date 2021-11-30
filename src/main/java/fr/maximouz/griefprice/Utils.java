package fr.maximouz.griefprice;

import org.bukkit.*;
import org.bukkit.util.ChatPaginator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Utils {

    public static String getCenteredText(String base) {
        StringBuilder builder = new StringBuilder();
        int startPos = getPosCenter(ChatColor.stripColor(base));

        for (int i = 0; i < startPos; i++) {
            builder.append(" ");
        }

        builder.append(base);

        return builder.toString();
    }

    private static int getPosCenter(String text) {
        return (ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH / 2) - (text.length() / 2);
    }

    public static Location getRandomLocation(Location loc1, Location loc2) {
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        return new Location(loc1.getWorld(), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
    }

    public static Location getRandomLocation(Location center, int radius) {
        int maxX = center.getBlockX() + radius;
        int minX = center.getBlockX() - radius;

        int maxZ = center.getBlockZ() + radius;
        int minZ = center.getBlockZ() - radius;

        Random r = new Random();

        int ix = r.nextInt(Math.max(Math.abs(maxX - minX), 1)) + minX;
        double x = ix + 0.5;
        int iz = r.nextInt(Math.max(Math.abs(maxZ - minZ), 1)) + minZ;
        double z = iz + 0.5;

        return new Location(center.getWorld(), x, center.getWorld().getHighestBlockYAt(ix, iz), z);
    }

    private static double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static Location locationFromString(String string) {

        String[] parts = string.split(Pattern.quote(","));

        World world = Bukkit.getWorld(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);

        float yaw = 0;
        float pitch = 0;

        if (parts.length == 6) {
            yaw = Float.parseFloat(parts[4]);
            pitch = Float.parseFloat(parts[5]);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

}
