package fr.maximouz.griefprice.commands;

import fr.maximouz.griefprice.GriefPrice;
import fr.maximouz.griefprice.mission.Progression;
import fr.maximouz.griefprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GriefPriceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender.isOp()) {

            if (args.length == 0) {

                sender.sendMessage("§c/" + label + " help");

            } else if (args.length == 1) {

                String arg = args[0];

                if (arg.equalsIgnoreCase("help")) {

                    sender.sendMessage("§e/" + label + " start §7commencer la partie");
                    sender.sendMessage("§e/" + label + " load §7commencer à partir de la sauvegarde");
                    sender.sendMessage("§e/" + label + " save §7sauvegarder la partie");
                    sender.sendMessage("§e/" + label + " setmin <count> §7minimum de joueurs pour commencer");
                    sender.sendMessage("§e/" + label + " classement §7classement de la partie");
                    sender.sendMessage("§e/" + label + " help");

                } else if (arg.equalsIgnoreCase("start") || arg.equalsIgnoreCase("load")) {

                    if (!GriefPrice.getInstance().getManager().hasStarted()) {

                        if (arg.equalsIgnoreCase("start")) {

                            long players = Bukkit.getOnlinePlayers().stream().filter(player -> !player.isOp()).count();
                            if (players >= GriefPrice.MIN_PLAYERS) {

                                GriefPrice.getInstance().getManager().start();

                            } else {

                                sender.sendMessage("§cIl n'y a pas assez de joueur pour commencer la partie.. (" + players + "/" + GriefPrice.MIN_PLAYERS + ")");
                                sender.sendMessage("§7§O/" + label + " setmin <count>");

                            }

                        } else
                            GriefPrice.getInstance().getManager().load();

                    } else {

                        sender.sendMessage("§cLa partie a déjà commencé.");

                    }

                } else if (arg.equalsIgnoreCase("save")) {

                    if (GriefPrice.getInstance().getManager().hasStarted()) {

                        sender.sendMessage("§eSauvegarde de la partie en cours..");
                        GriefPrice.getInstance().getManager().save();
                        sender.sendMessage("§aLa partie a bien été sauvegardée.");

                    } else {

                        sender.sendMessage("§cLa partie n'a pas commencé.");

                    }

                } else if (arg.equalsIgnoreCase("classement")) {

                    if (GriefPrice.getInstance().getManager().hasStarted() && GriefPrice.getInstance().getMissionManager().getCurrentMission() != null) {

                        int index = 0;

                        for (Progression progression : GriefPrice.getInstance().getMissionManager().getCurrentMission().getAliveProgressions()) {

                            sender.sendMessage("§e#" + ((index++) + 1) + " §f" + progression.getPlayer().getName() + "§b (" + progression.getFormattedAmount() + ")");

                        }

                    } else {

                        sender.sendMessage("§cLa partie n'a pas commencé.");

                    }

                }

            } else if (args.length == 2 && args[0].equalsIgnoreCase("setmin")) {

                if (GriefPrice.getInstance().getManager().hasStarted()) {
                    sender.sendMessage("§cLa partie a déjà commencé.");
                    return false;
                }

                String stringCount = args[1];

                try {

                    int count = Math.max(1, Integer.parseInt(stringCount));
                    GriefPrice.MIN_PLAYERS = count;
                    sender.sendMessage("§aLa partie pourra se lancer à §f" + count + "§a joueurs.");
                    GriefPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreboards().forEach(PlayerScoreboard::update);

                } catch (NumberFormatException ex) {

                    sender.sendMessage("§7" + stringCount + "§c n'est pas un nombre valide..");

                }

            }

        } else {

            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");

        }

        return false;
    }

}
