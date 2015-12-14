package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Plot;

public class PlotCommand {
	public static boolean delegate(CommandSender sender, String[] args) {
		switch (args[1].toLowerCase()) {
			case "list":
				list(sender, args);
				break;
			case "price":
				price(sender,args);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private static void list(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.plot.list")) {
			Citizen citizen = null;
			if (args.length > 1 && sender.hasPermission("cityzen.plot.list.others")) {
				citizen = Citizen.getCitizen(args[1]);
				if (citizen == null) {
					sender.sendMessage(Messaging.citizenNotFound(args[1]));
					return;
				}
			} else {
				citizen = Citizen.getCitizen(sender);
				if (citizen == null) {
					sender.sendMessage(Messaging.missingCitizenRecord());
					return;
				}
			}
			
			if (citizen.getAffiliation() != null && citizen.getPlots().size() > 0) {
				StringBuilder message = new StringBuilder(ChatColor.BLUE + "Plots owned by: " + ChatColor.GOLD + citizen.getName() + ChatColor.BLUE + ":");
				for (Plot p : citizen.getPlots()) {
					message.append(ChatColor.BLUE + "| " + p.getIdentifier() + ": (" + (int)p.getCorner1().getX() + "," + (int)p.getCorner1().getZ() + "),(" 
							+ (int)p.getCorner2().getX() + "," + (int)p.getCorner2().getX() + ") Height: " + p.getBaseHeight() + " Protection: " 
							+ p.getProtectionLevel() + " Creator: " + p.getCreator().getName() + " Co-Owners:");
					for (Citizen c : p.getOwners()) {
						if (!citizen.equals(c)) message.append(" " + c.getName());
					}
					message.append('\n');
				}
				sender.sendMessage(message.toString());
			} else {
				sender.sendMessage(ChatColor.RED + "No plots owned by " + citizen.getName());
			}
		} else sender.sendMessage(Messaging.noPerms("cityzen.plot.list"));
	}
	
	private static void price(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Citizen citizen = Citizen.getCitizen(sender);
			Plot plot = null;
			for (City c : City.getCities()) {
				for (Plot p : c.getPlots()) {
					if (p.isInPlot(citizen.getPassport().getLocation())) {
						plot = p;
						break;
					}
				}
				if (plot != null) break;
			}
			
			if (plot == null) {
				sender.sendMessage(ChatColor.RED + "You must be inside a plot to run this command");
				return;
			}
			if (args.length == 1) {
				if (sender.hasPermission("cityzen.plot.price.check")) {
					if (plot.isMega() || (plot.getOwners().size() > 0 && plot.getPrice() == 0)) sender.sendMessage(ChatColor.RED + "This plot is not for sale.");
					else sender.sendMessage(ChatColor.BLUE + "Price: " + ChatColor.GOLD + plot.getPrice());
				} else sender.sendMessage(Messaging.noPerms("cityzen.plot.price.check"));
			}
			else if (args[1].equalsIgnoreCase("set")) {
				if (sender.hasPermission("cityzen.plot.price.set")) {
					if (!plot.isMega() || plot.getOwners().contains(citizen) || ((citizen.isMayor() || citizen.isDeputy()) && citizen.getAffiliation().equals(plot.getAffiliation()))
							|| sender.hasPermission("cityzen.plot.price.set.others")) {
						try {
							plot.setPrice(Double.valueOf(args[2]));
							sender.sendMessage(ChatColor.BLUE + "The price of this plot is now " + plot.getPrice() + "." + (plot.getPrice() == 0 ? " This plot is not for sale." : ""));
							
						} catch(NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Price must be a number.");
						}
					} else sender.sendMessage(ChatColor.RED + "You cannot set a price for this plot.");
				} else sender.sendMessage(Messaging.noPerms("cityzen.plot.price.set"));
			} else sender.sendMessage(Messaging.invalidArguments("/plot price (set) (price)"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
}
