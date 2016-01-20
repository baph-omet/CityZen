package io.github.griffenx.CityZen.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Util;
import io.github.griffenx.CityZen.Tasks.ClearMetadataTask;

public class CitizenCommand {
	public static boolean delegate(CommandSender sender, String[] args) {
		switch (args[0].toLowerCase()) {
			case "setrep":
			case "addrep":
			case "subrep":
				changerep(sender, args);
				break;
			case "remove":
				remove(sender, args);
				break;
			case "list":
				list(sender, args);
				break;
			case "top":
				top(sender, args);
				break;
			case "addplot":
			case "addplots":
				addplots(sender,args);
				break;
			case "setplot":
			case "setplots":
				setplots(sender, args);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private static void changerep(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.citizen.changerep")) {
			if (args.length >= 3) {
				Citizen citizen = Citizen.getCitizen(args[1]);
				if (citizen != null) {
					long reputation = citizen.getReputation();
					long amount = Long.parseLong(args[2]);
					String message = "";
					switch (args[0].toLowerCase()) {
						case "setrep":
							if (amount >= 0) {
								citizen.setReputation(amount);
							} else {
								sender.sendMessage(ChatColor.RED + "Cannot set a negative reputation.");
							}
							break;
						case "addrep":
							citizen.addReputation(amount);
							message += ChatColor.BLUE + "You increased the reputation of " + ChatColor.GOLD + citizen.getName() + ChatColor.BLUE 
									+ " by " + ChatColor.GOLD + (citizen.getReputation() - reputation) + "\n";
							break;
						case "subrep":
							citizen.subReputation(amount);
							message += ChatColor.BLUE + "You decreased the reputation of " + ChatColor.GOLD + citizen.getName() + ChatColor.BLUE 
									+ " by " + ChatColor.GOLD + (reputation - citizen.getReputation()) + "\n";
							break;
					}
					
					message += ChatColor.BLUE + "The reputation of " + ChatColor.GOLD + citizen.getName() + ChatColor.BLUE + " is now " + ChatColor.GOLD + citizen.getReputation();
					sender.sendMessage(message);
				} else sender.sendMessage(Messaging.citizenNotFound(args[1]));
			} else sender.sendMessage(Messaging.notEnoughArguments("/citizen " + args[0].toLowerCase() + " <citizen> <amount>"));
		} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.changerep"));
	}
	
	private static void remove(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.citizen.remove")) {
			if (args.length >=2) {
				Citizen citizen = Citizen.getCitizen(args[1]);
				if (citizen != null) {
					if (sender instanceof Player && (!((Player)sender).hasMetadata("deleteCitizenConfirm") 
							|| ((Player)sender).getMetadata("deleteCitizenConfirm").contains(new FixedMetadataValue(CityZen.getPlugin(), citizen.getName())))) {
						Player caller = (Player)sender;
						if (citizen.getUUID() != caller.getUniqueId()) {
							sender.sendMessage(ChatColor.RED + "Are you sure you want to delete the record for "
									+ citizen.getName() + "? It will be gone until they next log in. All record of"
									+ " this Citizen will be erased and any Plots they own may be wiped."
									+ " This action cannot be undone. Type the command again in the next 60 seconds to confirm.");
							caller.setMetadata("deleteCitizenConfirm", new FixedMetadataValue(CityZen.getPlugin(), citizen.getName()));
							new ClearMetadataTask((Metadatable) citizen.getPassport(),"deleteCitizenConfirm").runTaskLater(CityZen.getPlugin(), 20 * 60);
						} else sender.sendMessage(ChatColor.RED + "You cannot delete your own Citizen record.");
					} else {
						if (!citizen.isMayor()) {
							String name = citizen.getName();
							Citizen.deleteCitizen(citizen);
							sender.sendMessage(ChatColor.BLUE + "You deleted the Citizen record for " + name);
						} else sender.sendMessage(ChatColor.RED + "You cannot delete a Citizen who is a Mayor of a City. Change the mayor of " 
							+ citizen.getAffiliation().getName() + " and try again.");
					}
				} else sender.sendMessage(Messaging.citizenNotFound(args[1]));
			} else sender.sendMessage(Messaging.notEnoughArguments("/citizen remove <citizen>"));
		} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.remove"));
	}
	
	public static void list(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 1) {
			if (sender instanceof Player) {
				if (sender.hasPermission("cityzen.citizen.list")) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						city = citizen.getAffiliation();
						if (city == null) sender.sendMessage(Messaging.noAffiliation());
					}
				} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.list"));
			} else sender.sendMessage(Messaging.playersOnly());
		} else {
			if (sender.hasPermission("cityzen.citizen.list.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) sender.sendMessage(Messaging.cityNotFound());
			} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.list.others"));
		}
		if (city != null) {
			Vector<Citizen> citizens = new Vector<Citizen>();
			for (Citizen c : city.getCitizens()) {
				if (!c.isMayor() && !city.getDeputies().contains(c)) citizens.add(c);
			}
			String playerList = "";
			
			sender.sendMessage(ChatColor.BLUE + "Citizens of " + city.getChatName());
			sender.sendMessage(ChatColor.RED + "Mayor: " + ChatColor.GOLD + city.getMayor().getName());
			if (city.getDeputies().size() > 0) {
				sender.sendMessage(ChatColor.GOLD + "Deputies:\n");
				for (Citizen c : city.getDeputies()) {
					playerList += ChatColor.RED + c.getName() + " ";
				}
				sender.sendMessage(playerList);
				playerList = "";
			}
			if (citizens.size() > 0) {
				sender.sendMessage(ChatColor.GOLD + "Citizens:\n");
				for (Citizen c : citizens) {
					playerList += ChatColor.BLUE + c.getName() + " ";
				}
				sender.sendMessage(playerList);
				playerList = "";
			}
		}
	}
	
	public static void top(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.citizen.top")) {
			City city = null;
			if (args.length >= 4) city = City.getCity(args[3]);
			
			String category = "";
			String invalidCategoryMessage = "";
			String[] categories = {"reputation","age"};
			if (args.length >= 3) {
				for (String s : categories) {
					invalidCategoryMessage += s + " ";
					if (s.equalsIgnoreCase(args[2])) {
						category = s;
						break;
					}
				}
				if (category.length() == 0) {
					sender.sendMessage(ChatColor.RED + "No category called " + args[2] + ". Valid categories:\n" + invalidCategoryMessage);
					return;
				}
			} else {
				category = "reputation";
			}
			
			int amount;
			if (args.length >= 2) {
				try {
					amount = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					amount = 10;
				}
			} else {
				amount = 10;
			}
			if (amount > 10) amount = 10;
			
			List<Citizen> citizens;
			if (city == null) citizens = Citizen.getCitizens();
			else citizens = city.getCitizens();
			
			List<Long> values = new ArrayList<Long>();
			// Sort by type
			for (Citizen c : citizens) {
				switch (category) {
					case "reputation":
						values.add(c.getReputation());
						break;
					case "age":
						Date issueDate = c.getIssueDate();
						values.add(issueDate.getTime());
						break;
				}
			}
			Collections.sort(values);
			if (!category.equals("age")) Collections.reverse(values);
			
			sender.sendMessage(ChatColor.BLUE + "Top " + ChatColor.GOLD + amount + ChatColor.BLUE + " Citizens by " + category + ":");
			for (int i = 0; i < amount; i++) {
				for (Citizen c : citizens) {
					if (values.get(i) != null && values.get(i) == c.getReputation()) {
						sender.sendMessage(ChatColor.BLUE + "| " + i + ". " + ChatColor.RED + values.get(i)
							+ ChatColor.BLUE + " - " + c.getName());
						citizens.remove(c);
					}
				}
			}
		} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.top"));
	}
	
	private static void addplots(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.citizen.addplots")) {
			if (args.length > 2) {
				Citizen citizen = Citizen.getCitizen(args[1]);
				if (citizen != null) {
					int amount;
					try {
						amount = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Amount must be a number.");
						return;
					}
					
					if (amount > 0) {
						citizen.setMaxPlots(citizen.getMaxPlots() + amount);
						sender.sendMessage(ChatColor.BLUE + "Max Plots increased by " + amount + " for " + citizen.getName());
					} else sender.sendMessage(ChatColor.RED + "Amount must be greater than 0");
				} else sender.sendMessage(Messaging.citizenNotFound(args[1]));
			} else sender.sendMessage(Messaging.notEnoughArguments("/citizen addplots <citizen> <amount>"));
		} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.addplots"));
	}
	
	private static void setplots(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.citizen.setplots")) {
			if (args.length > 2) {
				Citizen citizen = Citizen.getCitizen(args[1]);
				if (citizen != null) {
					int amount;
					try {
						amount = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Amount must be a number.");
						return;
					}
					
					if (amount >= 0) {
						citizen.setMaxPlots(amount);
						sender.sendMessage(ChatColor.BLUE + "Max Plots increased by " + amount + " for " + citizen.getName());
					} else sender.sendMessage(ChatColor.RED + "Amount must be at least 0");
				} else sender.sendMessage(Messaging.citizenNotFound(args[1]));
			} else sender.sendMessage(Messaging.notEnoughArguments("/citizen setplots <citizen> <amount>"));
		} else sender.sendMessage(Messaging.noPerms("cityzen.citizen.setplots"));
	}
}
