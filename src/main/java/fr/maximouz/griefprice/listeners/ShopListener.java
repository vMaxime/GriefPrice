package fr.maximouz.griefprice.listeners;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.GriefPricePlayer;
import fr.maximouz.griefprice.events.DynamiteExplodeEvent;
import fr.maximouz.griefprice.events.PushPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equalsIgnoreCase("§7Boutique")) {

            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();

            if (item != null) {

                GriefPricePlayer gPlayer = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player);

                if (gPlayer == null) {
                    player.sendMessage("§cVous ne participez pas à l'event..");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2f, 2f);
                    return;
                }

                ItemStack boughtItem = new ItemStack(Material.STICK);
                ItemMeta meta = boughtItem.getItemMeta();
                meta.setDisplayName("§5Bâton recul X");
                meta.setLore(Collections.singletonList("§7§oUtilisation unique."));

                if (item.getType() == Material.STICK) {

                    if (check(player, gPlayer, 10)) {

                        player.sendMessage("§aVous avez acheté un §5Bâton recul X§a !");
                        Bukkit.getOnlinePlayers().forEach(target -> {

                            if (target != player && target.isOp())
                                target.sendMessage("§f" + player.getName() + "§a a acheté un §5Bâton recul X§a !");


                        });
                        meta.addEnchant(Enchantment.KNOCKBACK, 10, true);

                    } else {

                        return;

                    }

                } else if (item.getType() == Material.TNT) {

                    if (check(player, gPlayer, 50)) {

                        player.sendMessage("§aVous avez acheté une §4§lDynamite§r§a !");
                        Bukkit.getOnlinePlayers().forEach(target -> {

                            if (target != player && target.isOp())
                                target.sendMessage("§f" + player.getName() + "§a a acheté une §4§lDynamite§r§a !");


                        });
                        boughtItem.setType(Material.TNT);
                        meta.setDisplayName("§4§lDynamite");
                        meta.setLore(Arrays.asList("§7§oUtilisation unique.", "§7§oDétonation instantanée."));

                    } else {

                        return;

                    }

                } else {

                    if (check(player, gPlayer, 25)) {

                        String name = item.getItemMeta().getDisplayName();
                        player.sendMessage("§aVous avez acheté une §5" + name + "§r§a !");
                        Bukkit.getOnlinePlayers().forEach(target -> {

                            if (target != player && target.isOp())
                                target.sendMessage("§f" + name +"§a a acheté une §5" + name + "§r§a !");


                        });
                        boughtItem.setType(item.getType());
                        meta.setDisplayName(name);
                        meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
                        meta.setLore(Collections.singletonList("§7§oUtilisation limitée."));

                    } else {

                        return;

                    }

                }

                boughtItem.setItemMeta(meta);
                player.getInventory().addItem(boughtItem);

            }

        }

    }

    public boolean check(Player player, GriefPricePlayer gPlayer, long price) {

        if (gPlayer.getShopPoints() >= price) {

            if (player.getInventory().firstEmpty() != -1) {

                gPlayer.withdrawShopPoints(price);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 2f, 2f);
                return true;

            } else {

                player.sendMessage("§cVotre inventaire est plein..");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2f, 2f);

            }

        } else {

            player.sendMessage("§cVous n'avez pas assez de §6Points boutique§c..");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2f, 2f);

        }

        return false;

    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager().getType() != EntityType.PLAYER || event.getEntity().getType() != EntityType.PLAYER)
            return;

        Player victim = (Player) event.getEntity();

        if (!GriefPrice.getInstance().getManager().isParticipating(victim.getUniqueId()) || GriefPrice.getInstance().getManager().isEliminated(victim.getUniqueId()))
            return;

        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();

        Bukkit.getPluginManager().callEvent(new PushPlayerEvent(player, victim));
        Bukkit.getOnlinePlayers().forEach(target -> {

            if (target.isOp())
                target.sendMessage("§cOUCH! §f" + victim.getName() + "§c s'est fait pousser par §f" + player.getName() + "§c !");

        });

        if (item.getType() == Material.STICK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§5Bâton recul X")) {

            event.setCancelled(false);
            event.setDamage(0);

            if (item.getAmount() > 1)
                item.setAmount(item.getAmount() - 1);
            else
                player.getInventory().setItemInMainHand(null);

        }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.isCancelled())
            return;

        Block block = event.getBlock();

        if (block.getType() == Material.TNT) {

            Player player = event.getPlayer();
            ItemStack item = event.getItemInHand();

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§4§lDynamite")) {

                Bukkit.getOnlinePlayers().forEach(target -> {

                    if (target.isOp())
                        target.sendMessage("§cBOUM! §f" + player.getName() + "§c a posé une dynamite !");

                });

                block.setType(Material.AIR);
                TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                tnt.setCustomName("§4§lDynamite");
                tnt.setCustomNameVisible(true);
                tnt.setYield(8);
                tnt.setFuseTicks(30);
                tnt.setSource(player);

            }

        }

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {

        if (event.getEntity().getType() != EntityType.PRIMED_TNT || !GriefPrice.getInstance().getManager().hasStarted() || GriefPrice.getInstance().getMissionManager().getCurrentMission() == null)
            return;

        TNTPrimed tnt = (TNTPrimed) event.getEntity();

        if (tnt.getCustomName() == null || !tnt.getCustomName().equalsIgnoreCase("§4§lDynamite") || tnt.getSource() == null || tnt.getSource().getType() != EntityType.PLAYER)
            return;

        Player player = (Player) tnt.getSource();

        List<Block> blocks = new ArrayList<>(event.blockList());

        for (Block block : blocks) {

            if (block.getType() == Material.AIR || GriefPrice.getInstance().getMissionManager().isPlacedByPlayer(block))
                event.blockList().remove(block);

        }

        Bukkit.getPluginManager().callEvent(new DynamiteExplodeEvent(player, event.blockList()));

    }

}
