package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;

public class CityZenCommand {
	public static void delegate(CommandSender sender, String[] args) {
		if (args.length > 0) {
			switch (args[0].toLowerCase().substring(0,1)) {
				case "h":
					help(sender, args);
					break;
				case "r":
					reload(sender);
					break;
				case "s":
					save(sender);
					break;
			}
		} else {
			info(sender);
		}
	}
	
	private static void save(CommandSender sender) {
		if (sender.hasPermission("cityzen.save")) {
			CityZen.getPlugin().saveConfig();;
			CityZen.cityConfig.save();
			CityZen.citizenConfig.save();
			CityZen.rewardConfig.save();
		} else sender.sendMessage(Messaging.noPerms("cityzen.save"));
	}
	
	private static void reload(CommandSender sender) {
		if (sender.hasPermission("cityzen.reload")) {
			CityZen.getPlugin().reloadConfig();;
			CityZen.cityConfig.reload();
			CityZen.citizenConfig.reload();
			CityZen.rewardConfig.reload();
		} else sender.sendMessage(Messaging.noPerms("cityzen.reload"));
	}
	
	private static void info(CommandSender sender) {
		if (sender.hasPermission("cityzen.info")) {
			Plugin plugin = CityZen.getPlugin();
			StringBuilder message = new StringBuilder(ChatColor.BLUE + "" + ChatColor.BOLD + "=== CityZen: The Premier City Management Plugin ===\n");
			message.append(ChatColor.RESET + "" + ChatColor.BLUE + "| Version: " + plugin.getDescription().getVersion() + " Author: iamvishnu (iamvishnu.tumblr.com)\n");
			message.append("| Download, get help, and learn about the plugin on BukkitDev: ");
			message.append("| For a list of commands, try \"" + ChatColor.WHITE + "/cityzen help" + ChatColor.BLUE + "\"");
			//TODO: Add BukkitDev link
			sender.sendMessage(message.toString());
		} else sender.sendMessage(Messaging.noPerms("cityzen.info"));
	}
	
	private static void help(CommandSender sender, String[] args) {
		//TODO: show help of the specified category
		String category = "none";
		if (args.length > 1) {
			StringBuilder cat = new StringBuilder(args[1]);
			for (int i=2;i<args.length;i++) {
				cat.append(" " + args[i]);
			}
			category = cat.toString();
		}
		
		String[] categories = {
				"none",
				"basic",
				"city",
				
		};
	}
}
