package io.github.griffenx.CityZen.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Util;

public class CityDeputyCommand {
	
	public static boolean delegate(CommandSender sender, String[] args) {
		if (args.length > 1) {
			switch (args[1].toLowerCase()) {
				case "list":
					list(sender,args);
					break;
				case "add":
					add(sender,args);
					break;
				case "remove":
					remove(sender,args);
					break;
				default:
					sender.sendMessage(Messaging.noSuchSubcommand(args[1]));
					return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	private static void list(CommandSender sender,String[] args) {
		City city = null;
		if (args.length == 2) {
			if (sender.hasPermission("cityzen.city.deputy.list")) {
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
				sender.sendMessage(Messaging.noPerms("cityzen.city.deputy.list"));
				return;
			}
		} else {
			if (sender.hasPermission("cityzen.city.deputy.list.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) {
					sender.sendMessage(Messaging.cityNotFound());
					return;
				}
			}
		} if (city == null) return;
		
		if (city.getDeputies().size() > 0) {
			sender.sendMessage(ChatColor.GOLD + "Deputies" + ChatColor.BLUE + " of " + city.getChatName());
			for (Citizen d : city.getDeputies()) {
				sender.sendMessage(ChatColor.GOLD + d.getName());
			}
		} else sender.sendMessage(city.getChatName() + ChatColor.BLUE + " has no Deputies.");
	}
	
	private static void add(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 2) {
			sender.sendMessage(Messaging.notEnoughArguments("/city deputy add <Citizen>"));
			return;
		} else if (args.length == 3) {
			if (sender.hasPermission("cityzen.city.deputy.add")) {
				if (sender instanceof Player) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.isMayor()) {
							city = citizen.getAffiliation();
						} else {
							sender.sendMessage(Messaging.notMayor());
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
				sender.sendMessage("cityzen.city.deputy.add");
				return;
			}
		} else {
			if (sender.hasPermission("cityzen.city.deputy.add.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) {
					sender.sendMessage(Messaging.cityNotFound());
					return;
				}
			} else {
				sender.sendMessage(Messaging.noPerms("cityzen.city.deputy.add.others"));
				return;
			}
		} if (city != null) {
			Citizen target = Citizen.getCitizen(args[2]);
			if (target != null) {
				if (target.getAffiliation() != null && city.equals(target.getAffiliation())) {
					if (!target.isMayor() && !target.isDeputy()) {
						city.addDeputy(target);
						sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " is now a deputy of " + city.getChatName());
						target.sendMessage(ChatColor.GOLD + "Congratulations!" + ChatColor.BLUE + " You are now a Deputy of " + city.getChatName());
					} else sender.sendMessage(ChatColor.RED + target.getName() + " is already an official of " + city.getName());
				} else sender.sendMessage(ChatColor.RED + target.getName() + " is not a Citizen of " + city.getName());
			} else sender.sendMessage(Messaging.citizenNotFound(args[2]));
		}
	}
	
	private static void remove(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 2) {
			sender.sendMessage(Messaging.notEnoughArguments("/city deputy remove <Citizen>"));
			return;
		} else if (args.length == 3) {
			if (sender.hasPermission("cityzen.city.deputy.remove")) {
				if (sender instanceof Player) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.isMayor()) {
							city = citizen.getAffiliation();
						} else {
							sender.sendMessage(Messaging.notMayor());
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
				sender.sendMessage("cityzen.city.deputy.remove");
				return;
			}
		} else {
			if (sender.hasPermission("cityzen.city.deputy.remove.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) {
					sender.sendMessage(Messaging.cityNotFound());
					return;
				}
			} else {
				sender.sendMessage(Messaging.noPerms("cityzen.city.deputy.remove.others"));
				return;
			}
		} if (city != null) {
			Citizen target = Citizen.getCitizen(args[2]);
			if (target != null) {
				if (target.getAffiliation() != null && city.equals(target.getAffiliation())) {
					if (target.isDeputy()) {
						city.removeDeputy(target);
						sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " is no longer a deputy of " + city.getChatName());
						target.sendMessage(ChatColor.BLUE + "You are no longer a Deputy of " + city.getChatName());
					} else sender.sendMessage(ChatColor.RED + target.getName() + " is not a Deputy of " + city.getName());
				} else sender.sendMessage(ChatColor.RED + target.getName() + " is not a Citizen of " + city.getName());
			} else sender.sendMessage(Messaging.citizenNotFound(args[2]));
		}
	}
}
