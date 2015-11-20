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
	
	public static boolean delegate(CommandSender sender, String[] args) {
		switch (args[0].toLowerCase()) {
			case "join":
				join(sender,args);
				break;
			case "list":
				list(sender,args);
				break;
			case "top":
				top(sender,args);
				break;
			case "leave":
				leave(sender,args);
				break;
			case "info":
				info(sender,args);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private static void join(CommandSender sender, String[] args) {
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
	}
	
	private static void list(CommandSender sender, String[] args) {
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
	}
	
	private static boolean top(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.top")) {
			String numberOfResults = 10;
			try {
				numberOfResults = Integer.parse(args[1]);
			} catch (NumberFormatException e) {
				numberOfResults = 10;
			}
			
			if (numberOfResults < 1) numberOfResults = 1;
			else if (numberOfResults > 10) numberOfResults = 10;
			List<City> cities = City.getCities();
			if (cities.size() < numberOfResults) numberOfResults = cities.size();
			
			String sortType = "Reputation";
			
			if (args[2].length == 0 || args[2].equalsIgnoreCase("reputation")) {
				sortType = "Reputation";
			} else if (args[2].equalsIgnoreCase("citizens") || args[2].equalsIgnoreCase("population")) {
				sortType = "Population";
			} else if (args[2].equalsIgnoreCase("date") || args[2].equalsIgnoreCase("age")) {
				sortType = "Age";
			}
			
			List<long> values = new ArrayList();
			// Sort by type
			for (City c : cities) {
				switch (sortType) {
					case "Reputation":
						values.add(c.getReputation());
						break;
					case "Population":
						values.add((long) c.getCitizens().size());
						break;
					case "Age":
						Date foundingDate = c.getFoundingDate();
						//TODO: Check this
						values.add(foundingDate.Milliseconds);
						break;
				}
			}
			Collections.sort(values)
			if (!sortType.equals("Age")) Collections.reverse(values);
			
			sender.sendMessage(ChatColor.RED + "Top " + ChatColor.GOLD + numberOfResults + " Cities by " + sortType + ":");
			for (int i = 0; i < numberOfResults; i++) {
				for (City c : cities) {
					if (values.get(i) != null && values.get(i) == c.getReputation()) {
						sender.sendMessage(ChatColor.BLUE + "| " + i + ". " + ChatColor.RED + values.get(i)
							+ ChatColor.BLUE + " - " + c.getChatName());
						cities.remove(c);
					}
				}
			}
		} else {
			sender.sendMessage(InfoCommand.noPermMessage("cityzen.city.top");
		}
		return true;
	}
	
	private static void create(CommandSender sender, String[] args) {
		if (args.length > 1) {
			if (sender.hasPermission("cityzen.city.create") {
				if (Character.isAlphabetic(args[1].charAt(0))) {
					City newCity;
					String cityName = "";
					for (int i = 1; i < args.length; i++) {
						cityName += args[i];
					}
					if (sender instanceof Player) {
						Citizen creator = Citizen.getCitizen(sender);
						if (creator != null) {
							if (creator.getAffilitaion() == null && !creator.isWaitlisted()) {
								newCity = City.createCity(cityName,creator);
								if (newCity != null) creator.addReputation(CityZen.getConfig().getLong("reputation.gainedOnCreateCity"));
							} else {
								sender.sendMessage(ChatColor.RED + "You cannot create a City if you are already a Citizen of a City, or are on the Waitlist for a City.");
								return;
							}
						} else {
							sender.sendMessage(InfoCommand.citizenNotFoundMessage());
							return;
						}
					} else newCity = City.createCity(cityName);
					if (newCity != null) sender.sendMessage(ChatColor.BLUE + "Congratulations! You founded " + ChatColor.GOLD + cityName);
					else sender.sendMessage(ChatColor.RED + "A city already exists by the name " + ChatColor.GOLD + cityName + ChatColor.RED + ". Please try again with a unique name.");
				} else sender.sendMessage(ChatColor.RED + "City names must start with a letter. Please use a valid City name."); 
			} else sender.sendMessage(InfoCommand.noPermMessage("cityzen.city.create"));
		} else sender.sendMessage(ChatColor.RED + "Not enough arguments. Usage: \"/city create <City Name...>\"");
	}
	
	private static void leave(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Citizen citizen = Citizen.getCitizen(sender);
			if (sender.hasPermission("cityzen.city.leave")) {
				City aff = citizen.getAffiliation();
				if (aff != null) {
					long rep = citizen.getReputation();
					aff.removeCitizen(citizen);
					sender.sendMessage(ChatColor.BLUE + "You have left " + ChatColor.GOLD + aff.getChatName() + ChatColor.BLUE + ". "
						+ (CityZen.getConfig().getInt("lostOnLeaveCityPercent") > 0 ? " You lost " + ChatColor.GOLD + (rep - citizen.getReputation())
						+ " Reputation" + ChatColor.BLUE + ". You now have " + ChatColor.GOLD + citizen.getReputation() + " Reputation" + ChatColor.BLUE + "." : "");
				} else sender.sendMessage(ChatColor.RED + "You have no city to leave.");
			} else sender.sendMessage(InfoCommand.noPermMessage("cityzen.city.leave"));
		} else sender.sendMessage(InfoCommand.playersOnlyMessage());
	}
	
	private static void info(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.info") {
			City city;
			if (sender instanceof Player && args.length == 1) {
				Citizen citizen = Citizen.getCitizen(sender);
				city = citizen.getAffiliation();
				if (city == null) {
					sender.sendMessage(ChatColor.RED + "You are not a Citizen of any City."
						+ " Please specify a City to look up. Useage: \"/city info <City>\"");
					return;
				}
			} else if (args.length > 1) {
				city = getCity()
			} else {
				sender.sendMessage(ChatColor.RED + "You must specify a City in order to run this command from Console."
					+ "Useage:\"/city info <City>\"");
				return;
			}
			
			int deps = city.getDeputies().size();
			String[] messages = {
				ChatColor.BLUE + "==============================",
				ChatColor.GOLD + "     " + city.getChatName(),
				ChatColor.BLUE + "| Slogan: " + ChatColor.WHITE + "\"" + city.getSlogan() + "\"",
				ChatColor.BLUE + "| Date Founded: " + ChatColor.WHITE + city.getFoundingDate("dd MMM yyyy"),
				ChatColor.BLUE + "| Population: " + ChatColor.WHITE + city.getCitizens().size(),
				ChatColor.BLUE + "| Reputation: " + ChatColor.GOLD + city.getReputation(),
				ChatColor.BLUE + "| Mayor: " + ChatColor.GOLD + city.getMayor().getName() 
					+ (deps > 0 ? "(" + deps + " Deput" + (deps > 1 ? "ies" : "y") + ")" : ""),
				ChatColor.BLUE + "| Plots: " + ChatColor.WHITE + city.getPlots().size(),
				ChatColor.BLUE + "| FreeJoin: " + city.isFreeJoin() + " OpenPlotting: " + city.isOpenPlotting() + " BlockExclusion: " 
					+ (city.isBlockExclusion() ? (city.isWhitelisted() ? "Whitelist" : "Blacklist") : "None")
			};
			
			sender.sendMessage(messages);
		} else sender.sendMessage(noPermMessage("cityzen.city.info"));
	}
}
