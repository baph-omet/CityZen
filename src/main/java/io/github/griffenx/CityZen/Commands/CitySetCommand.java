package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.Messaging;

public class CitySetCommand {
	public static boolean delegate(CommandSender sender, String[] args) {
		if (args.length >= 3) {
			City city = null;
			if (args.length == 3) {
				if (sender.hasPermission("cityzen.city.set")) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.isMayor() || citizen.isDeputy()) {
							city = citizen.getAffiliation();
						}
					}
				}
			} else {
				if (sender.hasPermission("cityzen.city.set.others")) {
					if (args[1].equalsIgnoreCase("slogan")) city = City.getCity(args[args.length - 1]);
					else city = City.getCity(args[3]);
					if (city == null) sender.sendMessage(Messaging.cityNotFound(args[3]));
				}
			} if (city != null) {
				String value = args[2];
				switch (args[1].toLowerCase()) {
					case "name":
						city.setName(value);
						sender.sendMessage(ChatColor.BLUE + "This City is now known as " + city.getChatName());
						break;
					case "slogan":
						String slogan = "";
						for (int i = 2; i < args.length - 1; i++) {
							slogan += args[i];
						}
						city.setSlogan(slogan);
						sender.sendMessage(ChatColor.BLUE + "The slogan for " + city.getChatName() + ChatColor.BLUE + " is now " 
								+ ChatColor.WHITE + slogan);
						break;
					case "color":
						ChatColor color = ChatColor.getByChar(value);
						if (color != null) {
							city.setColor(color.getChar());
							sender.sendMessage(ChatColor.BLUE + "The color for " + city.getChatName() + ChatColor.BLUE + " is now "
									+ color.toString());
							break;
						} else {
							sender.sendMessage(ChatColor.RED + "Could not interpret " + value + " as a color.");
							break;
						}
					case "maxPlotSize":
						int size;
						try {
							size = Integer.parseInt(value);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Could not interpret " + value + " as a number.");
							break;
						}
						city.setMaxPlotSize(size);
						sender.sendMessage(ChatColor.BLUE + "The maximum plot size for " + city.getChatName() + ChatColor.BLUE + " is now " + value);
						break;
					case "minPlotSize":
						int minSize;
						try {
							minSize = Integer.parseInt(value);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Could not interpret " + value + " as a number.");
							break;
						}
						city.setMaxPlotSize(minSize);
						sender.sendMessage(ChatColor.BLUE + "The minimum plot size for " + city.getChatName() + ChatColor.BLUE + " is now " + value);
						break;
					case "freeJoin":
						boolean freejoin = Boolean.parseBoolean(value);
						city.setFreeJoin(freejoin);
						sender.sendMessage(ChatColor.BLUE + "FreeJoin for " + city.getChatName() + ChatColor.BLUE + " is now set to " + freejoin);
						break;
					case "openPlotting":
						boolean openPlotting = Boolean.parseBoolean(value);
						city.setOpenPlotting(openPlotting);
						sender.sendMessage(ChatColor.BLUE + "FreeJoin for " + city.getChatName() + ChatColor.BLUE + " is now set to " + openPlotting);
						break;
					case "mayor":
						Citizen mayor = Citizen.getCitizen(value);
						if (mayor != null) {
							if (city.equals(mayor.getAffiliation()) && !mayor.isMayor()) {
								city.setMayor(mayor);
								sender.sendMessage(ChatColor.BLUE + "The Mayor of " + city.getChatName() 
									+ ChatColor.BLUE + " is now " + ChatColor.GOLD + mayor.getName());
							}
						}
						break;
					default:
						sender.sendMessage(ChatColor.RED + value + " is not a configurable property.");
						break;
				}
			}
		} else {
			sender.sendMessage(Messaging.notEnoughArguments("/city set <property> <value>"));
		} return true;
	}
}
