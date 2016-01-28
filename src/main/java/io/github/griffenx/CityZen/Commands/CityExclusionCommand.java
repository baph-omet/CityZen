package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Util;

public class CityExclusionCommand {
	public static boolean delegate(CommandSender sender, String[] args) {
		if (args.length > 1) {
			switch (args[1].toLowerCase()) {
				case "mode":
					mode(sender,args);
					break;
				case "add":
					add(sender,args);
					break;
				case "remove":
					remove(sender,args);
					break;
				case "list":
					list(sender,args);
					break;
				default:
					sender.sendMessage(Messaging.noSuchSubcommand(args[1]));
					return false;
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "Not enough arguments. Type \"/cityzen help city exclusion\" for help with this command.");
			return false;
		}
	}
	
	private static void mode(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 4) {
			if (sender.hasPermission("cityzen.city.exclusion.mode")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					if (!(citizen.isDeputy() || citizen.isMayor())) {
						city = citizen.getAffiliation();
					} else sender.sendMessage(Messaging.notCityOfficial());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.mode"));
		} else {
			if (sender.hasPermission("cityzen.city.exclusion.mode.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) sender.sendMessage(Messaging.cityNotFound());
			} else sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.mode.others"));
		}
		if (city == null) return;
		else {
			switch (args[2].toLowerCase()) {
				case "b":
				case "bl":
				case "black":
				case "blacklist":
				case "blacklisted":
					city.setBlockExclusion(true);
					city.setWhitelisted(false);
					sender.sendMessage(ChatColor.BLUE + "Block Exclusion for " + city.getChatName() 
						+ ChatColor.BLUE + "has been set to " + ChatColor.GOLD + " BLACKLIST" 
						+ ChatColor.BLUE + ". Use \"/city exclusion list\" to see which materials are not allowed in plots.");
					break;
				case "w":
				case "wl":
				case "white":
				case "whitelist":
				case "whitelisted":
					city.setBlockExclusion(true);
					city.setWhitelisted(true);
					sender.sendMessage(ChatColor.BLUE + "Block Exclusion for " + city.getChatName() 
					+ ChatColor.BLUE + "has been set to " + ChatColor.GOLD + " WHITELIST" 
					+ ChatColor.BLUE + ". Use \"/city exclusion list\" to see which materials are allowed in plots.");
					break;
				case "":
				case "n":
				case "no":
				case "none":
				case "off":
				case "diable":
				case "disabled":
					city.setBlockExclusion(false);
					city.setWhitelisted(false);
					sender.sendMessage(ChatColor.BLUE + "Block Exclusion for " + city.getChatName() 
					+ ChatColor.BLUE + "has been set to " + ChatColor.GOLD + " NONE" 
					+ ChatColor.BLUE + ". All materials are now allowed in plots.");
					break;
			}
		}
	}
	
	private static void add(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 4) {
			if (sender.hasPermission("cityzen.city.exclusion.add")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					if (!(citizen.isDeputy() || citizen.isMayor())) {
						city = citizen.getAffiliation();
					} else sender.sendMessage(Messaging.notCityOfficial());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.add"));
		} else {
			if (sender.hasPermission("cityzen.city.exclusion.add.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) sender.sendMessage(Messaging.cityNotFound());
			} else sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.add.others"));
		}
		if (city == null) return;
		else {
			Material material = Material.getMaterial(args[2].toUpperCase());
			if (material != null) {
				if (!city.getBlacklist().contains(material)) {
					city.addBlock(material);
					sender.sendMessage(ChatColor.BLUE + "You successfully added " + ChatColor.GOLD + material.toString() 
						+ ChatColor.BLUE + " to the exclusion list for " + city.getChatName());
				} else sender.sendMessage(ChatColor.RED + material.toString() + " is already on the exclusion list for " + city.getName());
			} else sender.sendMessage(ChatColor.RED + "No material found called " + args[2] + ".");
		}
	}
	
	private static void remove(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 4) {
			if (sender.hasPermission("cityzen.city.exclusion.remove")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					if (!(citizen.isDeputy() || citizen.isMayor())) {
						city = citizen.getAffiliation();
					} else sender.sendMessage(Messaging.notCityOfficial());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.remove"));
		} else {
			if (sender.hasPermission("cityzen.city.exclusion.remove.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) sender.sendMessage(Messaging.cityNotFound());
			} else sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.remove.others"));
		}
		if (city == null) return;
		else {
			Material material = Material.getMaterial(args[2].toUpperCase());
			if (material != null) {
				if (city.getBlacklist().contains(material)) {
					city.removeBlock(material);
					sender.sendMessage(ChatColor.BLUE + "You successfully removed " + ChatColor.GOLD + material.toString() 
						+ ChatColor.BLUE + " from the exclusion list for " + city.getChatName());
				} else sender.sendMessage(ChatColor.RED + material.toString() + " is not on the exclusion list for " + city.getName());
			} else sender.sendMessage(ChatColor.RED + "No material found called " + args[2] + ".");
		}
	}
	
	private static void list(CommandSender sender,String[] args) {
		City city = null;
		if (args.length == 2) {
			if (sender.hasPermission("cityzen.city.exclusion.list")) {
				if (sender instanceof Player) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						city = citizen.getAffiliation();
						if (city == null) {
							sender.sendMessage(Messaging.noAffiliation());
							return;
						}
					} else {
						sender.sendMessage(Messaging.missingCitizenRecord());
						return;
					}
				} else {
					sender.sendMessage(Messaging.playersOnly());
					return;
				}
			} else {
				sender.sendMessage(Messaging.noPerms("cityzen.city.exclusion.list"));
				return;
			}
		} else {
			if (sender.hasPermission("cityzen.city.exclusion.list.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) {
					sender.sendMessage(Messaging.cityNotFound());
					return;
				}
			}
		} if (city == null) return;
		if (city.isBlockExclusion()) {
			sender.sendMessage(ChatColor.GOLD + "Materials Excluded" + ChatColor.BLUE + " in " + city.getChatName() 
				+ ChatColor.BLUE + " (Mode: " + (city.isWhitelisted() ? "Whitelist" : "Blacklist") + ")");
			for (Material m : city.getBlacklist()) {
				sender.sendMessage(ChatColor.GOLD + m.toString());
			}
		} else sender.sendMessage(ChatColor.RED + "Block Exclusion is disabled in " + city.getName());
	}
}
