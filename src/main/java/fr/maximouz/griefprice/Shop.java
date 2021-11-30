package fr.maximouz.griefprice;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Shop {

    public static void open(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 9, "§7Boutique GriefPrice");

        int index = 0;

        for (ItemStack item : getItems()) {

            inventory.setItem(index++, item);

        }

        player.openInventory(inventory);

    }

    private static ItemStack[] getItems() {

        ItemStack[] items = new ItemStack[5];

        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9Bâton recul X");
        meta.setLore(Arrays.asList("§7§oUtilisation unique.", "", "§7Prix: §610PB"));
        meta.addEnchant(Enchantment.KNOCKBACK, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        items[0] = item;

        item = new ItemStack(Material.WOODEN_SHOVEL);
        meta = item.getItemMeta();
        meta.setDisplayName("§5Super pelle");
        meta.setLore(Arrays.asList("§7§oUtilisation limitée.", "", "§7Prix: §625PB"));
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        items[1] = item;

        item = new ItemStack(Material.WOODEN_AXE);
        meta = item.getItemMeta();
        meta.setDisplayName("§5Super hache");
        meta.setLore(Arrays.asList("§7§oUtilisation limitée.", "", "§7Prix: §625PB"));
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        items[2] = item;

        item = new ItemStack(Material.WOODEN_PICKAXE);
        meta = item.getItemMeta();
        meta.setDisplayName("§5Super pioche");
        meta.setLore(Arrays.asList("§7§oUtilisation limitée.", "", "§7Prix: §625PB"));
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        items[3] = item;

        item = new ItemStack(Material.TNT);
        meta = item.getItemMeta();
        meta.setDisplayName("§4§lDynamite");
        meta.setLore(Arrays.asList("§7§oUtilisation unique.", "§7§oDétonation instantanée.", "", "§7Prix: §650PB"));
        meta.addEnchant(Enchantment.DIG_SPEED, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        items[4] = item;

        return items;

    }

}
