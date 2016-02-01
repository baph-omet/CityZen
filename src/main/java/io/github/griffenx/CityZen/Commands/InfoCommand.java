package io.github.griffenx.CityZen.Commands;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;

public class InfoCommand {
	
	private static final Plugin plugin = CityZen.getPlugin();
	private static final Logger log = plugin.getLogger();
	
	public static void reputation(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				if (sender.hasPermission("cityzen.reputation")) {
					Citizen snd = Citizen.getCitizen((Player) sender);
					if (snd != null) {
						sender.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + ", Your Reputation is:");
						sender.sendMessage(ChatColor.GOLD + "" + snd.getReputation());
					} else {
						sender.sendMessage(Messaging.missingCitizenRecord());
					}
				} else {
					sender.sendMessage(Messaging.noPerms("cityzen.reputation"));
				}
			} else {
				sender.sendMessage(Messaging.playersOnly());
			}
			return;
		} else {
			if (sender.hasPermission("cityzen.reputation.others")) {
				Citizen target = Citizen.getCitizen(args[0]);
				if (target != null) {
					sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has a Reputation of:");
					sender.sendMessage(ChatColor.GOLD + "" + target.getReputation());
				} else {
					sender.sendMessage(Messaging.citizenNotFound(args[0]));
				}
				
			} else {
				sender.sendMessage(Messaging.noPerms("cityzen.reputation.others"));
			}
			return;
		}
	}
	
	public static void passport(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				if (sender.hasPermission("cityzen.passport")) {
					Citizen target = Citizen.getCitizen(sender);
					if (target != null) {
						log.info("Fetched passport for " + target.getName());
						String[] messages = {
								ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getServer().getServerName() + ChatColor.RESET + ChatColor.RED + " OFFICIAL PASSPORT",
								ChatColor.BLUE + "| Username: " + ChatColor.WHITE + (target.getPassport().isOnline() ? target.getPlayer().getDisplayName() : target.getPassport().getName()),
								ChatColor.BLUE + "| City: " + (target.getAffiliation() != null ? target.getAffiliation().getChatName()
										+ ChatColor.BLUE + (target.isMayor() ? " (Mayor)" : (target.isDeputy() ? " (Deputy)" : ""))
										+ "\n" + ChatColor.BLUE + "| Plots Owned: " + ChatColor.WHITE + target.getPlots().size() + "/" + target.getMaxPlots()
										: "None"),
								ChatColor.BLUE + "| Date Issued: " + ChatColor.WHITE + target.getIssueDate("dd MMM yyyy"),
								ChatColor.BLUE + "| Current Reputation: " + ChatColor.GOLD + target.getReputation(),
								ChatColor.BLUE + "| Max Reputation: " + ChatColor.RED + target.getMaxReputation(),
						};
						sender.sendMessage(messages);
					} else sender.sendMessage(Messaging.missingCitizenRecord());
				} else sender.sendMessage(Messaging.noPerms("cityzen.passport"));
			} else sender.sendMessage(Messaging.playersOnly());
		} else {
			if (sender.hasPermission("cityzen.passport.others")) {
				Citizen target = Citizen.getCitizen(args[0]);
				if (target != null) {
					String[] messages = {
							ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getServer().getServerName() + ChatColor.RESET + ChatColor.RED + " OFFICIAL PASSPORT",
							ChatColor.BLUE + "| Username: " + ChatColor.WHITE + (target.getPassport().isOnline() ? target.getPlayer().getDisplayName() : target.getPassport().getName()),
							ChatColor.BLUE + "| City: " + (target.getAffiliation() != null ? target.getAffiliation().getChatName()
									+ ChatColor.BLUE + (target.isMayor() ? " (Mayor)" : (target.isDeputy() ? " (Deputy)" : ""))
									+ "\n" + ChatColor.BLUE + "| Plots Owned: " + ChatColor.WHITE + target.getPlots().size() + "/" + target.getMaxPlots()
									: "None"),
							ChatColor.BLUE + "| Date Issued: " + ChatColor.WHITE + target.getIssueDate("dd MMM yyyy"),
							ChatColor.BLUE + "| Current Reputation: " + ChatColor.GOLD + target.getReputation(),
							ChatColor.BLUE + "| Max Reputation: " + ChatColor.RED + target.getMaxReputation(),
					};
					sender.sendMessage(messages);
				} else sender.sendMessage(Messaging.citizenNotFound(args[0]));
			} else sender.sendMessage(Messaging.noPerms("cityzen.passport.others"));
		}
	}
	
	public static void alert(CommandSender sender) {
		if (sender instanceof Player) {
			Citizen citizen = Citizen.getCitizen(sender);
			if (citizen != null) {
				List<String> alerts = citizen.getAlerts();
				if (alerts.size() > 0) {
					StringBuilder alertText = new StringBuilder(ChatColor.GOLD + "Alerts for " + citizen.getName() + ":\n");
					for (String a : alerts) {
						alertText.append(ChatColor.BLUE + "| " + ChatColor.WHITE + a);
					}
					alertText.append(ChatColor.BLUE + "\nAll alerts delivered. Have a nice day!");
					sender.sendMessage(alertText.toString());
					citizen.clearAlerts();
				} else sender.sendMessage(ChatColor.BLUE + "No pending alerts to display.");
			} else sender.sendMessage(Messaging.missingCitizenRecord());
		} else sender.sendMessage(Messaging.playersOnly());
	}
}
