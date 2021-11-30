package fr.maximouz.griefprice.commands;

import fr.maximouz.griefprice.events.DeOpEvent;
import fr.maximouz.griefprice.events.OpEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.isOp()) {

            sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return false;

        }

        if (args.length == 1) {

            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if (target != null) {

                if (label.equalsIgnoreCase("op")) {

                    if (!target.isOp()) {

                        target.setOp(true);
                        Bukkit.getOperators().forEach(operator -> {
                            if (operator.isOnline())
                                operator.getPlayer().sendMessage("§f" + targetName + "§7 a été promu opérateur par §f" + sender.getName() + "§7.");
                        });
                        Bukkit.getPluginManager().callEvent(new OpEvent(target));

                    } else {

                        sender.sendMessage("§f" + targetName + "§c est déjà op.");

                    }

                } else {

                    if (target.isOp()) {

                        target.setOp(false);
                        Bukkit.getOperators().forEach(operator -> {
                            if (operator.isOnline())
                                operator.getPlayer().sendMessage("§f" + targetName + "§7 a été déop par §f" + sender.getName() + "§7.");
                        });
                        Bukkit.getPluginManager().callEvent(new DeOpEvent(target));

                    } else {

                        sender.sendMessage("§f" + targetName + "§c n'est pas op..");

                    }

                }

            } else {

                sender.sendMessage("§cLe joueur §f" + targetName + "§c n'existe pas..");

            }

            return true;
        }

        sender.sendMessage("§cSyntaxe: §7/op <joueur>");

        return false;
    }

}
