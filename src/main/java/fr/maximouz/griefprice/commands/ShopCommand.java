package fr.maximouz.griefprice.commands;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (GriefPrice.getInstance().getManager().hasStarted() || player.isOp()) {

                Shop.open(player);

            }

        } else {

            sender.sendMessage("Commande utilisable en tant que joueur..");

        }

        return false;
    }

}
