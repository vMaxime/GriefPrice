package fr.maximouz.griefprice.listeners;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.GriefPricePlayer;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler (priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {

        event.setJoinMessage(null);

        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);

        Netherboard.instance().createBoard(player, "§4§lGriefPrice");

        if (GriefPrice.getInstance().getManager().hasStarted()) {

            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§7La partie a déjà commencé, vous êtes spéctateur.");

        } else {

            GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayers().add(new GriefPricePlayer(player));

        }

        // Cacher les joueurs en vanish ou voir les joueurs non vanish
        GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayers().forEach(griefPricePlayerTarget -> {

            Player target = griefPricePlayerTarget.getPlayer();

            if (target == null)
                return;

            if (griefPricePlayerTarget.isVanished() && player.canSee(target))
                player.hidePlayer(GriefPrice.getInstance(), target);
            else if (!griefPricePlayerTarget.isVanished() && !player.canSee(target))
                player.showPlayer(GriefPrice.getInstance(), target);

        });
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {

        event.setQuitMessage(null);

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        if (player.isOp()) {
            event.setFormat("§cOpérateur %s:§f %s");
            return;
        }

        event.setFormat("§7%s: %s");

        if (!GriefPrice.getInstance().getManager().hasStarted() || GriefPrice.getInstance().getMissionManager().getCurrentMission() == null)
            return;

        if (!GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) || GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId())) {

            event.setCancelled(true);

            Bukkit.getOnlinePlayers().forEach(target -> {

                // Si la cible est op ou aussi éliminée elle pourra reçevoir le message
                if (target.isOp() || !GriefPrice.getInstance().getManager().isParticipating(player.getUniqueId()) || GriefPrice.getInstance().getManager().isEliminated(player.getUniqueId())) {

                    target.sendMessage("§7[Spéctateur] " + player.getName() + ": " + event.getMessage());

                }

            });
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (event.getEntity().getType() == EntityType.PLAYER) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);
        }

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        event.setCancelled(true);

    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!event.getPlayer().isOp() && (!GriefPrice.getInstance().getManager().hasStarted() || !GriefPrice.getInstance().getManager().isParticipating(uuid)))
            event.setCancelled(true);
        else if (GriefPrice.getInstance().getMissionManager().isPlacedByPlayer(event.getBlock())) {

            if (player.isOp()) {
                GriefPrice.getInstance().getMissionManager().getCurrentMissionPlacedBlocks().remove(event.getBlock().getLocation());
                return;
            }

            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVous ne pouvez pas casser ce bloc, il a été posé par un joueur.");

        } else if (player.getInventory().getItemInMainHand().getType() == Material.AIR && GriefPrice.getInstance().getManager().isParticipating(uuid) && !GriefPrice.getInstance().getManager().isEliminated(uuid))
            GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(uuid).addShopPoints(1);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean started = GriefPrice.getInstance().getManager().hasStarted();
        boolean participating = GriefPrice.getInstance().getManager().isParticipating(uuid);

        if (!player.isOp() && (!started || !participating))
            event.setCancelled(true);
        else if (participating && !GriefPrice.getInstance().getManager().isEliminated(uuid) && GriefPrice.getInstance().getMissionManager().getCurrentMission() != null)
            GriefPrice.getInstance().getMissionManager().getCurrentMissionPlacedBlocks().add(event.getBlock().getLocation());

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {

        if (!event.getEntity().isOp() && !GriefPrice.getInstance().getManager().hasStarted())
            event.setCancelled(true);

    }

}
