package io.github.griffenx.CityZen.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.CityZen;

public class CityCommand {
	private static final Plugin plugin = CityZen.getPlugin();
	
	public static boolean join(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.city.join")) {
				
				String cityName = "";
				for (int i = 1; i < args.length; i++){
					cityName += args[i];
				}
				City city = City.getCity(cityName);
				if (city != null) {
					
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						
						City preexistingWaitlist = citizen.getWaitlistedCity();
						if (preexistingWaitlist == null) {
							
							if (city.isFreeJoin()) {
								
								city.addCitizen(citizen);
								sender.sendMessage("You successfully joined " + cityName + "!");
								
								long rep = plugin.getConfig().getLong("gainedOnJoinCity");
								citizen.addReputation(rep);
								sender.sendMessage("You gained " + rep + " Reputation for joining!");
								
							} else {
								city.addWaitlist(citizen);
								sender.sendMessage(city.getChatName() + ChatColor.BLUE + " does not allow Free Join."
										+ " A request to join this City has been sent instead and will be reviewed by an official of that City."
										+ " To cancel this request, type \"/city cancel\"");
								
								String alertMessage = citizen.getName() + " has requested to join " + city.getName() 
									+ ". Type \"/city approve " + citizen.getName() + "\" or \"/city deny " + citizen.getName() + "\"";
								city.getMayor().addAlert(alertMessage);
								for (Citizen d : city.getDeputies()) d.addAlert(alertMessage);
							}
						} else {
							sender.sendMessage(ChatColor.RED + "You are already on the waitlist for " + preexistingWaitlist.getChatName()
								+ ChatColor.RED + ". Please cancel your request there or wait for it to be denied before attempting to join another city.");
						}
					} else {
						sender.sendMessage(InfoCommand.missingCitizenRecordMessage());
					}
				} else {
					sender.sendMessage(InfoCommand.cityNotFoundMessage(cityName));
				}
			} else {
				sender.sendMessage(InfoCommand.noPermMessage("cityzen.city.join"));
			}
		} else {
			sender.sendMessage(InfoCommand.playersOnlyMessage());
		}
		return true;
	}
	
	public static boolean list(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.list")) {
			List<City> cities = City.getCities();
			if (cities.size() > 0) {
				int pageNumber;
				try {
					pageNumber = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					pageNumber = 1;
				}
				
				// Puts together the page of cities to display
				int numberOfPages = (int) Math.ceil(cities.size() / 5);
				if (pageNumber < 1) pageNumber = 1;
				else if (pageNumber > numberOfPages) pageNumber = numberOfPages;
				
				String[][] listCities = new String[numberOfPages][5];
				for (int page = 0; page < numberOfPages; page++) {
					for (int c = 0; c < 5; c++) {
						try {
							City city = cities.get((page * 5) + c);
							listCities[page][c] = city.getChatName() + ChatColor.BLUE + city.getSlogan();
						} catch (IndexOutOfBoundsException e) {
							listCities[page][c] = null;
						}
					}
				}
				sender.sendMessage(ChatColor.RED + "Cities on " + ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getServer().getServerName() 
						+ ChatColor.RESET + "" + ChatColor.RED + "(" + pageNumber + "/" + listCities);
				sender.sendMessage(listCities[pageNumber - 1]);
				if (numberOfPages > 1 && pageNumber < numberOfPages) sender.sendMessage(ChatColor.BLUE + "Type \"/cities list " + (pageNumber + 1) + "\" to view the next page.");
				sender.sendMessage(ChatColor.BLUE + "See more info about a city with \"/city info <City Name...>\"");
			} else {
				sender.sendMessage(ChatColor.BLUE + "There are no Cities on this server.");
			}
		} else {
			sender.sendMessage(InfoCommand.noPermMessage("cityzen.city.list"));
		}
		return true;
	}
}
