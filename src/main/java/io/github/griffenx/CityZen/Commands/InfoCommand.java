package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;

public class InfoCommand {
	
	private static final Plugin plugin = CityZen.getPlugin();
	
	public static boolean reputation(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				if (sender.hasPermission("cityzen.reputation")) {
					Citizen snd = Citizen.getCitizen((Player) sender);
					if (snd != null) {
						sender.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + ", Your Reputation is:");
						sender.sendMessage(ChatColor.GOLD + "" + snd.getReputation());
					} else {
						sender.sendMessage(Messaging.missingCitizenRecordMessage());
					}
				} else {
					sender.sendMessage(Messaging.noPermMessage("cityzen.reputation"));
				}
			} else {
				sender.sendMessage(Messaging.playersOnlyMessage());
			}
			return true;
		} else {
			if (sender.hasPermission("cityzen.reputation.others")) {
				@SuppressWarnings("deprecation")
				Citizen target = Citizen.getCitizen(plugin.getServer().getOfflinePlayer(args[0]));
				if (target != null) {
					sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has a Reputation of:");
					sender.sendMessage(ChatColor.GOLD + "" + target.getReputation());
				} else {
					sender.sendMessage(Messaging.citizenNotFoundMessage(args[0]));
				}
				
			} else {
				sender.sendMessage(Messaging.noPermMessage("cityzen.reputation.others"));
			}
			return true;
		}
	}
	
	public static boolean passport(CommandSender sender, Command command, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				if (sender.hasPermission("cityzen.passport")) {
					Citizen target = Citizen.getCitizen(sender);
					if (target != null) {
						String[] messages = {
								ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getServer().getServerName() + ChatColor.RESET + ChatColor.RED + " OFFICIAL PASSPORT",
								ChatColor.BLUE + "| Username: " + ChatColor.WHITE + target.getPassport().getDisplayName(),
								ChatColor.BLUE + "| City: " + (target.getAffiliation() != null ? target.getAffiliation()
										+ (target.isMayor() ? " (Mayor)" : (target.isDeputy() ? " (Deputy)" : ""))
										+ "\n" + ChatColor.BLUE + "| Plots Owned: " + ChatColor.WHITE + target.getPlots().size()
										: "None"),
								ChatColor.BLUE + "| Date Issued: " + ChatColor.WHITE + target.getIssueDate("dd MMM yyyy"),
								ChatColor.BLUE + "| Current Reputation: " + ChatColor.GOLD + target.getReputation(),
								ChatColor.BLUE + "| Max Reputation: " + ChatColor.RED + target.getMaxReputation(),
						};
						sender.sendMessage(messages);
					} else {
						sender.sendMessage(ChatColor.RED + "Couldn't find your passport. Your Citizen record may not exist.");
						sender.sendMessage(ChatColor.RED + "Contact an admin for help with this issue.");
					}
				} else {
					sender.sendMessage(Messaging.noPermMessage("cityzen.passport"));
				}
			} else {
				sender.sendMessage("Consoles and Command Blocks have no passport to look up. Try adding a player name instead.");
				sender.sendMessage("Usage: /" + command.getName() + " <player>");
			}
		} else {
			if (sender.hasPermission("cityzen.passport.others")) {
				Citizen target = Citizen.getCitizen(args[0]);
				if (target != null) {
					String[] messages = {
							ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getServer().getServerName() + ChatColor.RESET + ChatColor.RED + " OFFICIAL PASSPORT",
							ChatColor.BLUE + "| Username: " + ChatColor.WHITE + target.getPassport().getDisplayName(),
							ChatColor.BLUE + "| City: " + (target.getAffiliation() != null ? target.getAffiliation()
									+ (target.isMayor() ? " (Mayor)" : (target.isDeputy() ? " (Deputy)" : ""))
									+ "\n" + ChatColor.BLUE + "| Plots Owned: " + ChatColor.WHITE + target.getPlots().size()
									: "None"),
							ChatColor.BLUE + "| Date Issued: " + ChatColor.WHITE + target.getIssueDate("dd MMM yyyy"),
							ChatColor.BLUE + "| Current Reputation: " + ChatColor.GOLD + target.getReputation(),
							ChatColor.BLUE + "| Max Reputation: " + ChatColor.RED + target.getMaxReputation(),
					};
					sender.sendMessage(messages);
				} else {
					sender.sendMessage(ChatColor.RED + "Couldn't find a Citizen named \"" + args[0] + ".\" That Citizen record may not exist.");
				}
			} else {
				sender.sendMessage(Messaging.noPermMessage("cityzen.passport.others"));
			}
		}
		return true;
	}
}
