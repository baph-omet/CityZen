package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.ProtectionLevel;
import io.github.griffenx.CityZen.Util;

public class CitySetCommand {
	public static boolean delegate(CommandSender sender, String[] args) {
		if (args.length >= 3) {
			City city = null;
			boolean admin = false;
			
			String foundCityName = Util.findCityName(args, 2);
			
			if (foundCityName != null && sender.hasPermission("cityzen.city.set.others")) {
				city = City.getCity(foundCityName);
				admin = true;
				if (city == null) sender.sendMessage(Messaging.cityNotFound());
			} else {
				if (sender.hasPermission("cityzen.city.set")) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.isMayor()) {
							city = citizen.getAffiliation();
						}
					}
				}
			}
			
			if (city != null) {
				String value = args[2];
				switch (args[1].toLowerCase()) {
					case "name":
						String cityName;
						if (admin) cityName = Util.collapseArgsWithoutCityName(args, 2, city.getName());
						else cityName = Util.collapseArguments(args, 2);
						StringBuilder namingConflicts = new StringBuilder();
						for (String s : CityZen.getPlugin().getConfig().getStringList("cityNameFilter")) {
							if (cityName.toString().contains(s)) namingConflicts.append(ChatColor.RED + "- \"" + s.trim() + "\"\n");
						}
						if (namingConflicts.length() == 0) {
							city.setName(cityName.toString());
							sender.sendMessage(ChatColor.BLUE + "This City is now known as " + city.getChatName());
						} else sender.sendMessage(ChatColor.RED + "Unable to rename city. \"" + cityName.toString() + "\" contains the following blocked word(s):\n" + namingConflicts.toString() 
							+ "Please try again with these words omitted.");
						break;
					case "slogan":
						String slogan;
						if (admin) slogan = Util.collapseArgsWithoutCityName(args, 2, city.getName());
						else slogan = Util.collapseArguments(args, 2);
						StringBuilder sloganConflicts = new StringBuilder();
						for (String s : CityZen.getPlugin().getConfig().getStringList("cityNameFilter")) {
							if (slogan.contains(s)) sloganConflicts.append(ChatColor.RED + "- \"" + s.trim() + "\"\n");
						}
						if (sloganConflicts.length() == 0) {
							city.setSlogan(slogan.toString());
							sender.sendMessage(ChatColor.BLUE + "The slogan for " + city.getChatName() + ChatColor.BLUE + " is now " 
									+ ChatColor.WHITE + slogan);
						} else sender.sendMessage(ChatColor.RED + "Unable to set slogan. \"" + slogan + "\" contains the following blocked word(s):\n" + sloganConflicts.toString() 
							+ "Please try again with these words omitted.");
						break;
					case "color":
						ChatColor color;
						if (value.length() <= 2) {
							color = ChatColor.getByChar(value.charAt(value.length() - 1));
						} else {
							color = ChatColor.valueOf(value.toUpperCase());
						}
						if (color != null) {
							city.setColor(color.getChar());
							sender.sendMessage(ChatColor.BLUE + "The color for " + city.getChatName() + ChatColor.BLUE + " is now "
									+ color.name());
							break;
						} else {
							sender.sendMessage(ChatColor.RED + "Could not interpret " + value + " as a color.");
							break;
						}
					case "maxplotsize":
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
					case "minplotsize":
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
					case "freejoin":
						boolean freejoin = Boolean.parseBoolean(value);
						city.setFreeJoin(freejoin);
						sender.sendMessage(ChatColor.BLUE + "FreeJoin for " + city.getChatName() + ChatColor.BLUE + " is now set to " + freejoin);
						break;
					case "openplotting":
						boolean openPlotting = Boolean.parseBoolean(value);
						city.setOpenPlotting(openPlotting);
						sender.sendMessage(ChatColor.BLUE + "FreeJoin for " + city.getChatName() + ChatColor.BLUE + " is now set to " + openPlotting);
						break;
					case "wipeplots":
						boolean wipePlots = Boolean.parseBoolean(value);
						city.setWipePlots(wipePlots);
						sender.sendMessage(ChatColor.BLUE + "WipePlots for " + city.getChatName() + ChatColor.BLUE + " is now set to " + wipePlots);
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
					case "protection":
						switch (args[2].toLowerCase()) {
							case "0":
							case "none":
							case "pu":
							case "pub":
							case "public":
								city.setProtectionLevel(ProtectionLevel.PUBLIC);
								sender.sendMessage(ChatColor.BLUE + "You set the protection level for " + city.getChatName()
									+ ChatColor.BLUE + " to PUBLIC. Any player can now build in the Plot buffers.");
								break;
							case "1":
							case "c":
							case "co":
							case "com":
							case "comm":
							case "communal":
								city.setProtectionLevel(ProtectionLevel.COMMUNAL);
								sender.sendMessage(ChatColor.BLUE + "You set the protection level for " + city.getChatName()
									+ ChatColor.BLUE + " to COMMUNAL. Any Citizen of this City can now build in the Plot buffers.");
								break;
							case "2":
							case "p":
							case "pr":
							case "pro":
							case "prot":
							case "protected":
								city.setProtectionLevel(ProtectionLevel.PROTECTED);
								sender.sendMessage(ChatColor.BLUE + "You set the protection level for " + city.getChatName()
									+ ChatColor.BLUE + " to PROTECTED. Only City officials can now build in the Plot buffers.");
								break;
							default:
								sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" is not a protection level.");
						}
						break;
					default:
						sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" is not a configurable property.");
						break;
				}
			} else sender.sendMessage(Messaging.cityNotFound());
		} else {
			sender.sendMessage(Messaging.notEnoughArguments("/city set <property> <value>"));
		} return true;
	}
}
