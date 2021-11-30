package fr.maximouz.griefprice.commands;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.GriefPricePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            if (sender.isOp()) {

                Player player = (Player) sender;
                GriefPricePlayer griefPricePlayer = GriefPrice.getInstance().getGriefPricePlayerManager().getGriefPricePlayer(player);

                if (griefPricePlayer != null) {

                    if (griefPricePlayer.isVanished()) {

                        griefPricePlayer.unVanish();
                        player.sendMessage("§eVous êtes visible aux yeux des joueurs.");

                    } else {

                        griefPricePlayer.vanish();
                        player.sendMessage("§aVous êtes invisible aux yeux des joueurs.");

                    }

                } else {

                    player.sendMessage("§cVous n'avez pas été corréctement initialisé par le jeu pour faire cette commande, essayez de vous reconnecter..");

                }

            } else {

                sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");

            }

        } else {

            sender.sendMessage("Commande utilisable en tant que joueur uniquement");

        }

        return false;

    }


}
