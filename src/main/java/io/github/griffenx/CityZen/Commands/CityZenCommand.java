package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Util;

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
				default:
					sender.sendMessage(Messaging.noSuchSubcommand(args[0]));
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
			sender.sendMessage(ChatColor.BLUE + "CityZen configs saved!");
		} else sender.sendMessage(Messaging.noPerms("cityzen.save"));
	}
	
	private static void reload(CommandSender sender) {
		if (sender.hasPermission("cityzen.reload")) {
			CityZen.getPlugin().reloadConfig();;
			CityZen.cityConfig.reload();
			CityZen.citizenConfig.reload();
			CityZen.rewardConfig.reload();
			sender.sendMessage(ChatColor.BLUE + "CityZen configs reloaded!");
		} else sender.sendMessage(Messaging.noPerms("cityzen.reload"));
	}
	
	private static void info(CommandSender sender) {
		Plugin plugin = CityZen.getPlugin();
		StringBuilder message = new StringBuilder(ChatColor.GOLD+ "" + ChatColor.BOLD + "=== CityZen: The Premier City Management Plugin ===\n");
		message.append(ChatColor.RESET + "" + ChatColor.BLUE + "| Version: " + ChatColor.WHITE + plugin.getDescription().getVersion() 
				+ ChatColor.BLUE + " Author: " + ChatColor.WHITE + "iamvishnu" + ChatColor.GRAY + " ( iamvishnu.tumblr.com )\n");
		message.append(ChatColor.BLUE + "| Download, get help, and learn about the plugin on GitHub:\n|" + ChatColor.GRAY + "    https://github.com/griffenx/CityZen/wiki\n");
		message.append(ChatColor.BLUE + "| For a list of commands, try \"" + ChatColor.WHITE + "/cityzen help" + ChatColor.BLUE + "\"");
		//TODO: Add BukkitDev link
		sender.sendMessage(message.toString());
	}
	
	private static void help(CommandSender sender, String[] args) {
		String category = "none";
		if (args.length > 1) {
			category = Util.collapseArguments(args, 1);
		}
		String[] categories = {
				"basic",
				"city",
				"city management",
				"city set",
				"city exclusion",
				"citizen",
				"plot",
				"plot management",
				"rewards"
		};
		StringBuilder message = new StringBuilder();
		if (category.equals("none")) {
			message.append(ChatColor.RED + "===CityZen Help: Categories===\n" + ChatColor.GRAY 
				+ "| To get help with CityZen commands, type\n\"" + ChatColor.RED + "/cityzen help <category>" 
				+ ChatColor.GRAY + "\"\n where <category> is one of the following:\n");
			for (String s : categories) message.append(ChatColor.GRAY + "| " + s.substring(0,1).toUpperCase() + s.substring(1) + "\n");
			message.append(ChatColor.GRAY + "Command Syntax: /label <requiredArgument> (optionalArgument)\n    (chooseThis | orThat) <argumentSupportsSpaces...>\n");
			message.append(ChatColor.GRAY + "Key: (C) Citizens of Cities only, (P) Plot owners only, (O) City officials only, (M) Mayors only");
		} else {
			String[] basic = {
				" - cityzen - Show basic plugin info",
				" - cityzen help (category) - Show all useable commands by category",
				"reload - cityzen reload - Reload config files from disk",
				"save - cityzen save - Save changes to config files",
				"reputation - reputation - Show your reputation",
				"reputation.others - reputation (Citizen) - Show the reputation of the specified Citizen",
				"passport - passport - Show all your Citizen data",
				"passport.others - passport (Citizen) - Show all the Citizen data for the specified Citizen",
				" - alert - Show all your pending alerts and dismisses them"
			};
			String[] city = {
				"city.list - city list - List all cities that exist on this server",
				"city.info - city info (city) - Show detailed information on the specified City",
				"city.top - city top (number) (reputation | citizens | age) - Show top Cities with the specified criteria",
				"city.visitors - city visitors - Show which players are currently located in your City (C)",
				"city.visitors.others - city visitors <City> - Show which players are currently located inside the specified City",
				"city.create - city create <name> - Found a new City",
				"city.join - city join <city> - Join the specified City",
				"city.leave - city leave - Leave your current City (C)",
				"city.deputy.list - city deputy list - Lists all Deputies of your City (C)",
				"city.deputy.list.others - city deputy list <City> - Lists all deputies of the specified City",
				"city.alert city - alert <message...> - Sends an alert message to all members of your City (O)",
				"city.alert.others city - alert <message...> <City> - Sends an alert message to all members of the specified City"
			};
			String[] citymgmt = {
				"city.evict - city evict <Citizen> - Forcibly remove the specified Citizen from your City (O)",
				"city.dissolve - city dissolve - Permanently deletes your City (M)",
				"city.dissolve.others - city dissolve <City> - Permanently delete the specified City",
				"city.accept - city accept <Citizen> - Accept the request to join your city of the specified Citizen (O)",
				"city.deny - city deny <Citizen> - Deny the request to join your city of the specified Citizen (O)",
				"city.ban - city ban <Citizen> - Block the specified Citizen from joining your City or sending join requests (O)",
				"city.ban.others - city ban <Citizen> <City> - Block the specified Citizen from joining the specified City or sending join requests",
				"city.pardon - city pardon <Citizen> - Remove the specified Citizen from your City's banlist (O)",
				"city.pardon.others - city pardon <Citizen> <City> - Remove the specified Citizen from the banlist of the specified City",
				"city.banlist - city banlist (page) - Show Citizens banned from joining your City (O)",
				"city.banlist.others - city banlist (page) <City> - Show Citizens banned from joining the specified City",
				"city.deputy.add - city deputy add <Citizen> - Promotes a Citizen of your City to a Deputy (M)",
				"city.deputy.add.others - city deputy add <Citizen> <City> - Promotes a Citizen of the specified City to a Deputy",
				"city.deputy.remove - city deputy remove <Citizen> - Demotes the specified Deputy of your City (M)",
				"city.deputy.remove.others - city deputy remove <Citizen> <City> - Demotes the specified Deputy of the specified City"
			};
			String[] cityset = {
				"city.set - city set name <name...> - Set a new name for your City (M)",
				"city.set.others - city set name <name...> <City> - Set a new name for the specified City",
				"city.set - city set slogan <slogan...> - Set a new slogan for your City (M)",
				"city.set.others - city set slogan <slogan...> <City> - Set a new slogan for the specified City",
				"city.set - city set color <colorCode> - Set a new color for the name of your City (M)",
				"city.set.others - city set color <colorCode> <City> - Set a new color for the name of the specified City",
				"city.set - city set maxPlotSize <size> - Set the new max plot size for your City (M)",
				"city.set.others - city set maxPlotSize <size> <City> - Set the new max plot size for the specified City",
				"city.set - city set minPlotSize <size> - Set the new min plot size for your City (M)",
				"city.set.others - city set minPlotSize <size> <City> - Set the new min plot size for the specified City",
				"city.set - city set freeJoin <true | false> - Set whether or not any player can join your City (M)",
				"city.set.others - city set freeJoin <true | false> <City> - Set whether or not any player can join the selected City",
				"city.set - city set openPlotting <true | false> - Set whether or not any Citizen of your City can create Plots (M)",
				"city.set.others - city set openPlotting <true | false> <City> - Set whether or not any Citizen of the specified City can create Plots",
				"city.set - city set wipePlots <true | false> - Toggle automatically wiping Plots in this City",
				"city.set.others - city set wipePlots <true | false> <City> - Toggle automatically wiping Plots in the specified City",
				"city.set - city set mayor <Citizen> - Change the Mayor of your City to a new Citizen (M)",
				"city.set.others - city set mayor <Citizen> <City> - Change the Mayor of the specified City to a new Citizen",
				"city.set - city set protection <level> - Sets protection level of Plot buffers in your City (M)",
				"city.set.others - city set protection <level> <City> - Sets protection level of Plot buffers in the specified City"
			};
			String[] cityexclusion = {
				"city.exclusion.mode - city exclusion mode <blacklist | whitelist | none> - Set the block exclusion mode of your City (O)",
				"city.exclusion.mode.others - city exclusion mode <blacklist | whitelist | none> <City> - Set the block exclusion mode of the specified City",
				"city.exclusion.add - city exclusion add <material> - Add a material to the exclusion list for your City (O)",
				"city.exclusion.add.others - city exclusion add <material> <City> - Add a material to the exclusion list for the specified City",
				"city.exclusion.remove - city exclusion remove <material> - Remove a material from the exclusion list for your City (O)",
				"city.exclusion.remove.others - city exclusion remove <material> <City> - Remove a material from the exclusion list for your City",
				"city.exclusion.list - city exclusion list - List all excluded materials for your City (O)",
				"city.exclusion.list.others - city exclusion list <City> - List all excluded materials for the specified City"
			};
			String[] citizen = {
				"citizen.top - citizen top (number) (reputation | age) (City) - List top Citizens with the specified criteria",
				"citizen.list - citizen list - Show a list of Citizens of your City (C)",
				"citizen.list.others - citizen list <City> - Show a list of Citizens of the specified City",
				"citizen.changerep - citizen setrep <Citizen> <value> - Set the reputation of the specified Citizen",
				"citizen.changerep - citizen addrep <Citizen> <value> - Increase the reputation of the specified Citizen",
				"citizen.changerep - citizen subrep <Citizen> <value> - Decrease the reputation of the specified Citizen",
				"citizen.remove - citizen remove <Citizen> - Delete the record of the specified Citizen",
				"citizen.addplots - citizen addplots <Citizen> <amount> - Increase the number of plots this Citizen can own",
				"citizen.setplots - citizen setplots <Citizen> <amount> - Set the number of plots this Citizen can own"
			};
			String[] plot = {
				"plot.info - plot info - Show information about the Plot in which you're standing",
				"plot.select - plot select - Toggle Plot selection mode (C)",
				"plot.select - plot pos1 - Select the first corner of a new Plot (C)",
				"plot.select - plot pos2 - Select the second corner of a new Plot (C)",
				"plot.create - plot create - Create a new Plot from your selection (C)",
				"plot.move - plot move <plotID> - Move an existing Plot to your selection (P)",
				"plot.list - plot list - List all Plots you own (C)",
				"plot.list.others - plot list <Citizen> - List all Plots owned by the specified Citizen",
				"plot.available - plot available - List all open Plots in your City (C)",
				"plot.available.others - plot available <City> - List all open Plots in the specified City",
				"plot.abandon - plot abandon - Remove yourself from ownership of the Plot in which you're standing (P)",
				"plot.delete - plot delete - Delete the Plot in which you're standing (P)",
				"plot.delete.others - plot delete - Delete any Plot in which you're standing"
			};
			String[] plotmanagement = {
				"plot.invite - plot invite <Citizen> - Invite the specified Citizen to be an owner of this Plot (P)",
				"plot.accept - plot accept - Accepts a Plot invitation",
				"plot.deny - plot deny - Denies Plot invitation",
				"plot.claim - plot claim - Claim the Plot in which you're standing if it's available (C)",
				"plot.buy - plot buy - Buy the Plot in which you're standing if it's for sale (C)",
				"plot.price - plot price - Check the price of the Plot in which you're standing",
				"plot.price.set - plot price set <amount> - Set the price of a Plot you own (P)",
				"plot.price.set.others - plot price set <amount> - Set the price of any Plot",
				"plot.modifyowners - plot addowner <Citizen> - Set owner of a Plot in your City (O)",
				"plot.modifyowners.others - plot addowner <Citizen> - Set owner of any Plot",
				"plot.modifyowners - plot removeowner <Citizen> - Remove owner of a Plot in your City (O)",
				"plot.modifyowners.others - plot removeowner <Citizen> - Remove owner of any Plot",
				"plot.setprotection - plot setprotection <level> - Set the protection level of a Plot you own (P)",
				"plot.setprotection.others - plot setprotection <level> - Set the protection level of any Plot",
				"plot.wipe - plot wipe - Wipe the plot in which you're standing"
			};
			String[] rewards = {
				"rewards.list - cityzen rewards list - List all registered rewards",
				"rewards.list.others - cityzen rewards list (Citizen | City) - Show all rewards earned by a certain Citizen or City",
				"rewards.add - cityzen rewards add <Citizen | City> <initialRep> <intervalRep> <isBroadcast> <command...> - Add a new reward.",
				"rewards.message - cityzen rewards message <index> <message...> - Set a message to be broadcast when a reward is disbursed.",
				"rewards.remove - cityzen rewards remove <index> - Remove a specified reward"
			};
			
			String[] catarray = null;
			switch (category.toLowerCase()) {
				case "basic": catarray = basic; break;
				case "city": catarray = city; break;
				case "city management": catarray = citymgmt; break;
				case "city set": catarray = cityset; break;
				case "city exclusion": catarray = cityexclusion; break;
				case "citizen": catarray = citizen; break;
				case "plot": catarray = plot; break;
				case "plot management": catarray = plotmanagement; break;
				case "rewards": catarray = rewards; break;
				default:
					sender.sendMessage(ChatColor.RED + "Category not found. Type \"/cityzen help\" for a list of help categories.");
					return;
			}
			
			int numberOfUseableCommands = 0;
			message.append(ChatColor.RED + "===CityZen Help: " + category.toUpperCase() + "===");
			for (String s : catarray) {
				String[] command = s.split(" - ");
				if (command[0].length() == 0 || sender.hasPermission("cityzen." + command[0])) {
					message.append("\n" + ChatColor.GRAY + "| " + ChatColor.RED + "/" + command[1] + ChatColor.GRAY + " - " + command[2]);
					numberOfUseableCommands++;
				}
			}
			if (numberOfUseableCommands == 0) {
				message.append(ChatColor.GRAY + "You do not have permission to use any commands in this category. Sorry!");
			}
		}
		
		sender.sendMessage(message.toString());
	}
}
