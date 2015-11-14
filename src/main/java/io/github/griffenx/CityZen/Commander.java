package io.github.griffenx.CityZen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.chat.Chat;

public class Commander implements CommandExecutor {
	private final CityZen plugin;
	
	public Commander(CityZen plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		String cmdName = command.getName().toLowerCase();
		
		switch (cmdName) {
			case "psp":
			case "passport":
				return passportCommand(sender,command,args);
			case "rep":
			case "reputation":
				return reputationCommand(sender,command,args);
			case "ctz":
			case "citizen":
				return citizenCommand(sender, command, args);
			case "cty":
			case "city":
				return cityCommand(sender, command, args);
			case "plt":
			case "plot":
				return plotCommand(sender, command, args);
		}
		return false;
	}
	
	private String noPermMessage(String node) {
		return ChatColor.RED + "You don't have permission to do that. Required permission: " + node;
	}
	
	private boolean reputationCommand(CommandSender sender, Command command, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				if (sender.hasPermission("cityzen.reputation")) {
					Citizen snd = Citizen.getCitizen((Player) sender);
					if (snd != null) {
						sender.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + ", Your Reputation is:");
						sender.sendMessage(ChatColor.GOLD + "" + snd.getReputation());
					} else {
						sender.sendMessage(ChatColor.RED + "That's weird. Your Citizen record doesn't exist.");
						sender.sendMessage(ChatColor.RED + "Try logging in again. If that doesn't work, contact an admin.");
					}
				} else {
					sender.sendMessage(noPermMessage("cityzen.reputation"));
				}
			} else {
				sender.sendMessage("Consoles and Command Blocks have no reputation to look up. Try adding a player name instead.");
				sender.sendMessage("Usage: /" + command.getName() + " <player>");
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
					sender.sendMessage(ChatColor.RED + "Couldn't find a Citizen named \"" + args[0] + ".\"");
				}
				
			} else {
				sender.sendMessage(noPermMessage("cityzen.reputation.others"));
			}
			return true;
		}
	}
	
	private boolean passportCommand(CommandSender sender, Command command, String[] args) {
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
					sender.sendMessage(noPermMessage("cityzen.passport"));
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
				sender.sendMessage(noPermMessage("cityzen.passport.others"));
			}
		}
		return true;
	}
	
	private boolean citizenCommand(CommandSender sender, Command command, String[] args) {
		
	}
	
	private boolean cityCommand(CommandSender sender, Command command, String[] args) {
		
	}
	
	private boolean plotCommand(CommandSender sender, Command command, String[] args) {
		
	}
}
