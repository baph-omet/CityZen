package io.github.griffenx.CityZen.Commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Util;
import io.github.griffenx.CityZen.Tasks.ClearMetadataTask;

public class CityCommand {
	private static final Plugin plugin = CityZen.getPlugin();

	public static boolean delegate(CommandSender sender, String[] args) {
		switch (args[0].toLowerCase()) {
		case "join":
			join(sender, args);
			break;
		case "list":
			list(sender, args);
			break;
		case "top":
			top(sender, args);
			break;
		case "create":
			create(sender, args);
			break;
		case "leave":
			leave(sender, args);
			break;
		case "info":
			info(sender, args);
			break;
		case "evict":
			evict(sender, args);
			break;
		case "dissolve":
			dissolve(sender, args);
			break;
		case "ban":
			ban(sender, args);
			break;
		case "unban":
		case "pardon":
			pardon(sender, args);
			break;
		case "approve":
		case "accept":
			accept(sender, args);
			break;
		case "deny":
			deny(sender, args);
			break;
		case "banlist":
			banlist(sender, args);
			break;
		case "location":
		case "distance":
			distance(sender, args);
			break;
		case "who":
		case "whois":
		case "people":
		case "visitors":
			visitors(sender, args);
			break;
		case "alert":
			alert(sender, args);
			break;
		case "citizens":
			citizens(sender, args);
			break;
		default:
			sender.sendMessage(Messaging.noSuchSubcommand(args[0]));
			return false;
		}
		return true;
	}

	private static void join(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.city.join")) {

				final String cityName = Util.findCityName(args);
				final City city = City.getCity(cityName);
				if (city != null) {

					final Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {

						if (citizen.getAffiliation() == null) {

							final City preexistingWaitlist = citizen.getWaitlistedCity();
							if (preexistingWaitlist == null) {

								if (city.isFreeJoin()) {

									city.addCitizen(citizen);
									sender.sendMessage(ChatColor.BLUE + "You successfully joined " + city.getChatName()
											+ ChatColor.BLUE + "!");
									for (final Player p : CityZen.getPlugin().getServer().getOnlinePlayers())
										if (!p.equals(sender))
											p.sendMessage(((Player) sender).getDisplayName() + ChatColor.BLUE
													+ " has joined " + city.getChatName());

									final long rep = plugin.getConfig().getLong("reputation.gainedOnJoinCity");
									sender.sendMessage(ChatColor.BLUE + "You gained " + ChatColor.GOLD + rep
											+ " Reputation" + ChatColor.BLUE + " for joining!");

								} else {
									city.addWaitlist(citizen);
									sender.sendMessage(city.getChatName() + ChatColor.BLUE
											+ " does not allow Free Join."
											+ " A request to join this City has been sent instead and will be reviewed by an official of that City."
											+ " To cancel this request, type \"/city cancel\"");

									final String alertMessage = citizen.getName() + " has requested to join "
											+ city.getName() + ". Type \"/city accept " + citizen.getName()
											+ "\" or \"/city deny " + citizen.getName() + "\"";
									city.getMayor().addAlert(alertMessage);
									for (final Citizen d : city.getDeputies())
										d.addAlert(alertMessage);
								}
							} else
								sender.sendMessage(ChatColor.RED + "You are already on the waitlist for "
										+ preexistingWaitlist.getChatName() + ChatColor.RED
										+ ". Please cancel your request there or wait for it to be denied before attempting to join another city.");
						} else
							sender.sendMessage(ChatColor.RED + "You are already a resident of "
									+ citizen.getAffiliation().getName()
									+ ". You cannot join another city without first leaving your current city.");
					} else
						sender.sendMessage(Messaging.missingCitizenRecord());
				} else
					sender.sendMessage(Messaging.cityNotFound());
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.join"));
		} else
			sender.sendMessage(Messaging.playersOnly());
	}

	private static void list(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.list")) {
			final List<City> cities = City.getCities();
			if (cities.size() > 0) {
				int pageNumber;
				if (args.length == 1)
					pageNumber = 1;
				else
					try {
						pageNumber = Integer.parseInt(args[1]);
					} catch (final NumberFormatException e) {
						pageNumber = 1;
					}
				// Puts together the page of cities to display
				final int numberOfPages = (int) Math.ceil(cities.size() / 5.0);
				if (pageNumber < 1)
					pageNumber = 1;
				else if (pageNumber > numberOfPages)
					pageNumber = numberOfPages;

				final String[][] listCities = new String[numberOfPages][5];
				for (int page = 0; page < numberOfPages; page++)
					for (int c = 0; c < 5; c++)
						try {
							final City city = cities.get((page * 5) + c);
							if (city != null)
								listCities[page][c] = ChatColor.BLUE + "| " + ChatColor.WHITE + city.getChatName()
										+ ChatColor.WHITE + " - \"" + city.getSlogan() + "\"";
						} catch (final IndexOutOfBoundsException e) {
							listCities[page][c] = null;
						}
				sender.sendMessage(ChatColor.AQUA + "Cities on " + ChatColor.GOLD + "" + ChatColor.BOLD
						+ plugin.getServer().getName() + ChatColor.RESET + "" + ChatColor.AQUA + " (" + pageNumber + "/"
						+ numberOfPages + ")");
				for (final String m : listCities[pageNumber - 1])
					if (m != null)
						sender.sendMessage(m);
				if (numberOfPages > 1 && pageNumber < numberOfPages)
					sender.sendMessage(
							ChatColor.BLUE + "Type \"/city list " + (pageNumber + 1) + "\" to view the next page.");
				sender.sendMessage(ChatColor.BLUE + "See more info about a city with \"/city info <City Name...>\"");
			} else
				sender.sendMessage(ChatColor.BLUE + "There are no Cities on this server.");
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.list"));
	}

	private static boolean top(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.top")) {
			int numberOfResults = 10;
			if (args.length > 1)
				try {
					numberOfResults = Integer.parseInt(args[1]);
				} catch (final NumberFormatException e) {
					numberOfResults = 10;
				}

			if (numberOfResults < 1)
				numberOfResults = 1;
			else if (numberOfResults > 10)
				numberOfResults = 10;
			final List<City> cities = City.getCities();
			if (cities.size() < numberOfResults)
				numberOfResults = cities.size();

			String sortType = "Reputation";
			if (args.length > 2) {
				if (args[2].length() == 0 || args[2].equalsIgnoreCase("reputation"))
					sortType = "Reputation";
				else if (args[2].equalsIgnoreCase("citizens") || args[2].equalsIgnoreCase("population"))
					sortType = "Population";
				else if (args[2].equalsIgnoreCase("date") || args[2].equalsIgnoreCase("age"))
					sortType = "Age";
			} else
				sortType = "Reputation";

			final List<Long> values = new ArrayList<Long>();
			// Sort by type
			for (final City c : cities)
				switch (sortType) {
				case "Reputation":
					values.add(c.getReputation());
					break;
				case "Population":
					values.add((long) c.getCitizens().size());
					break;
				case "Age":
					final Date foundingDate = c.getFoundingDate();
					values.add(foundingDate.getTime());
					break;
				}
			Collections.sort(values);
			if (!sortType.equals("Age"))
				Collections.reverse(values);

			sender.sendMessage(ChatColor.BLUE + "Top " + ChatColor.GOLD + numberOfResults + ChatColor.BLUE
					+ " Cities by " + sortType + ":");
			for (int i = 0; i < numberOfResults; i++)
				for (int j = 0; j < cities.size(); j++)
					if (values.get(i) != null) {
						long comparison = cities.get(j).getReputation();
						if (sortType.equalsIgnoreCase("age"))
							comparison = cities.get(j).getFoundingDate().getTime();
						else if (sortType.equalsIgnoreCase("population"))
							comparison = cities.get(j).getCitizens().size();
						if (values.get(i) == comparison) {
							sender.sendMessage(ChatColor.BLUE + "| " + (i + 1) + ". " + ChatColor.RED
									+ (sortType.equalsIgnoreCase("age")
											? new SimpleDateFormat("yyyy-MM-dd").format(new Date(values.get(i)))
											: values.get(i))
									+ ChatColor.BLUE + " - " + cities.get(j).getChatName());
							cities.remove(j);
							j--;
						}
					}
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.top"));
		return true;
	}

	private static void create(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			final Citizen citizen = Citizen.getCitizen(sender);
			if (citizen != null) {
				if (args.length > 1) {
					if (sender.hasPermission("cityzen.city.create")) {
						if (Character.isAlphabetic(args[1].charAt(0))) {
							City newCity;
							final String cityName = Util.collapseArguments(args, 1);
							final StringBuilder namingConflicts = new StringBuilder();
							for (final String s : CityZen.getPlugin().getConfig().getStringList("cityNameFilter"))
								if (cityName.contains(s))
									namingConflicts.append(ChatColor.RED + "- \"" + s.trim() + "\"\n");
							if (namingConflicts.length() == 0) {
								if (sender instanceof Player) {
									final Citizen creator = Citizen.getCitizen(sender);
									if (creator == null) {
										sender.sendMessage(Messaging.missingCitizenRecord());
										return;
									}
									if ((creator.getAffiliation() != null) || creator.isWaitlisted()) {
										sender.sendMessage(ChatColor.RED
												+ "You cannot create a City if you are already a Citizen of a City, or are on the Waitlist for a City.");
										return;
									}
									newCity = City.createCity(cityName, creator);
									if (newCity != null)
										creator.addReputation(CityZen.getPlugin().getConfig()
												.getLong("reputation.gainedOnCreateCity"));
								} else
									newCity = City.createCity(cityName, citizen);
								if (newCity != null)
									sender.sendMessage(ChatColor.BLUE + "Congratulations! You founded " + ChatColor.GOLD
											+ cityName);
								else
									sender.sendMessage(ChatColor.RED + "A city already exists by the name "
											+ ChatColor.GOLD + cityName + ChatColor.RED
											+ ". Please try again with a unique name.");
							} else
								sender.sendMessage(ChatColor.RED + "Unable to create city. \"" + cityName
										+ "\" contains the following blocked word(s):\n" + namingConflicts.toString()
										+ "Please try again with these words omitted.");
						} else
							sender.sendMessage(ChatColor.RED
									+ "City names must start with a letter. Please use a valid City name.");
					} else
						sender.sendMessage(Messaging.noPerms("cityzen.city.create"));
				} else
					sender.sendMessage(ChatColor.RED + "Not enough arguments. Usage: \"/city create <City Name...>\"");
			} else
				sender.sendMessage(Messaging.missingCitizenRecord());
		} else
			sender.sendMessage(Messaging.playersOnly());
	}

	private static void leave(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			final Citizen citizen = Citizen.getCitizen(sender);
			if (sender.hasPermission("cityzen.city.leave")) {
				final City aff = citizen.getAffiliation();
				if (aff != null) {
					if (!citizen.isMayor()) {
						final long rep = citizen.getReputation();
						aff.removeCitizen(citizen);
						sender.sendMessage(ChatColor.BLUE + "You have left " + ChatColor.GOLD + aff.getChatName()
								+ ChatColor.BLUE + ". "
								+ (CityZen.getPlugin().getConfig().getInt("lostOnLeaveCityPercent") > 0
										? " You lost " + ChatColor.GOLD + (rep - citizen.getReputation())
												+ " Reputation" + ChatColor.BLUE + ". You now have " + ChatColor.GOLD
												+ citizen.getReputation() + " Reputation" + ChatColor.BLUE + "."
										: ""));
					} else
						sender.sendMessage(ChatColor.RED
								+ "The Mayor of a City cannot leave. Either pass on your title or delete your City.");
				} else
					sender.sendMessage(ChatColor.RED + "You have no city to leave.");
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.leave"));
		} else
			sender.sendMessage(Messaging.playersOnly());
	}

	private static void info(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.info")) {
			City city;
			if (sender instanceof Player && args.length == 1) {
				final Citizen citizen = Citizen.getCitizen(sender);
				city = City.getCity(sender);
				if (city == null) {
					if (citizen == null) {
						sender.sendMessage(ChatColor.RED + "You are not a Citizen of any City."
								+ " Please specify a City to look up. Useage: \"/city info <City>\"");
						return;
					}
					city = citizen.getAffiliation();
					if (city == null) {
						sender.sendMessage(ChatColor.RED + "You are not a Citizen of any City."
								+ " Please specify a City to look up. Useage: \"/city info <City>\"");
						return;
					}
				}
			} else if (args.length > 1)
				city = City.getCity(Util.findCityName(args));
			else {
				sender.sendMessage(ChatColor.RED + "You must specify a City in order to run this command from Console."
						+ "Useage:\"/city info <City>\"");
				return;
			}
			if (city != null) {
				final int deps = city.getDeputies().size();
				final String[] messages = { ChatColor.BLUE + "==============================",
						ChatColor.GOLD + "     " + city.getChatName(),
						ChatColor.BLUE + "| Slogan: " + ChatColor.WHITE + "\"" + city.getSlogan() + "\"",
						ChatColor.BLUE + "| Date Founded: " + ChatColor.WHITE + city.getFoundingDate("dd MMM yyyy"),
						ChatColor.BLUE + "| Population: " + ChatColor.WHITE + city.getCitizens().size(),
						ChatColor.BLUE + "| Reputation: " + ChatColor.GOLD + city.getReputation(),
						ChatColor.BLUE + "| Max Reputation: " + ChatColor.RED + city.getMaxReputation(),
						ChatColor.BLUE + "| Mayor: " + ChatColor.GOLD + city.getMayor().getName()
								+ (deps > 0 ? " (" + deps + " Deput" + (deps > 1 ? "ies" : "y") + ")" : ""),
						ChatColor.BLUE + "| Plots: " + ChatColor.WHITE + city.getPlots().size() + "/"
								+ Math.round(CityZen.getPlugin().getConfig().getDouble("plotDensity")
										* city.getCitizens().size()),
						ChatColor.BLUE + "| FreeJoin: " + ChatColor.WHITE + city.isFreeJoin(),
						ChatColor.BLUE + "| OpenPlotting: " + ChatColor.WHITE + city.isOpenPlotting(),
						ChatColor.BLUE + "| WipePlots: " + ChatColor.WHITE + city.isWipePlots(),
						ChatColor.BLUE + "| Protection: " + ChatColor.WHITE + city.getProtectionLevel().toString(),
						ChatColor.BLUE + "| Plot Sizes: " + ChatColor.WHITE + city.getMinPlotSize() + ChatColor.BLUE
								+ " to " + ChatColor.WHITE + city.getMaxPlotSize(),
						ChatColor.BLUE + "| BlockExclusion: " + ChatColor.WHITE
								+ (city.isBlockExclusion() ? (city.isWhitelisted() ? "Whitelist" : "Blacklist")
										: "None") };

				sender.sendMessage(messages);
			} else
				sender.sendMessage(Messaging.cityNotFound());
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.info"));
	}

	private static void evict(CommandSender sender, String[] args) {
		Citizen citizen = null;
		if (sender instanceof Player)
			citizen = Citizen.getCitizen(sender);
		if (sender.hasPermission("cityzen.city.evict")
				|| (citizen != null && (citizen.isMayor() || citizen.isDeputy()))) {
			if (args.length > 1) {
				final Citizen target = Citizen.getCitizen(args[1]);
				if (target != null) {
					if ((citizen != null && (citizen.isMayor() || citizen.isDeputy()))
							&& !target.getAffiliation().equals(citizen.getAffiliation())) {
						sender.sendMessage(ChatColor.RED + "You can only evict players from your City.");
						return;
					}
					final City city = target.getAffiliation();
					if (city != null) {
						if (!target.isMayor()) {
							final long rep = target.getReputation();
							city.removeCitizen(target, true);
							if (target.getPlayer().isOnline()) {
								final String message = ChatColor.RED + "You were evicted from " + city.getChatName()
										+ ChatColor.RED + " by " + sender.getName() + ". You lost " + ChatColor.GOLD
										+ (rep - target.getReputation()) + ChatColor.RED
										+ " Reputation. You have been removed from ownership of all plots"
										+ " and may not rejoin this City unless approved by a City official.";
								target.getPlayer().sendMessage(message);
							} else
								target.addAlert("[CityZen] You were evicted from " + city.getName() + " by "
										+ sender.getName() + ". You lost " + (rep - target.getReputation())
										+ " Reputation. You have been removed from ownership of all plots"
										+ " and may not rejoin this City unless approved by a City official.");
							sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " was evicted from "
									+ city.getChatName());
						} else
							sender.sendMessage(ChatColor.RED + "You cannot evict the Mayor.");
					} else
						sender.sendMessage(Messaging.noAffiliation(target));
				} else
					sender.sendMessage(Messaging.citizenNotFound(args[1]));
			} else
				sender.sendMessage(
						ChatColor.RED + "Not enough arguments. Please specify a Citizen to evict from their City.");
		} else
			sender.sendMessage(ChatColor.RED
					+ "You must either be a City official or have permission node cityzen.city.evict to run this command.");
	}

	private static void dissolve(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (sender.hasPermission("cityzen.city.dissolve")) {
				if (sender instanceof Player) {
					final Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.isMayor()) {
							if (citizen.getPlayer().hasMetadata("deleteConfirm")) {
								final City city = citizen.getAffiliation();
								citizen.getPlayer().removeMetadata("deleteConfirm", CityZen.getPlugin());
								final List<Citizen> refugees = city.getCitizens();
								citizen.getAffiliation().delete();
								for (final Citizen r : refugees)
									if (r.getPlayer().isOnline())
										r.getPlayer().sendMessage(ChatColor.RED + "Your city has been deleted."
												+ " You have not lost any reputation from this and are free to join another city.");
									else
										r.addAlert("Your city has been deleted."
												+ " You have not lost any reputation from this and are free to join another city.");
								sender.sendMessage(ChatColor.BLUE + "Your city has been completely deleted!");
							} else {
								sender.sendMessage(ChatColor.RED + "Are you sure you want to delete "
										+ citizen.getAffiliation().getChatName() + ChatColor.RED
										+ "? It will be gone forever (a long time). This action cannot be reversed. Type the command"
										+ " again in the next 60 seconds to confirm.");
								citizen.getPlayer().setMetadata("deleteConfirm",
										new FixedMetadataValue(CityZen.getPlugin(), "asked"));
								new ClearMetadataTask(citizen.getPlayer(), "deleteConfirm")
										.runTaskLater(CityZen.getPlugin(), 20 * 60);
							}
						} else
							sender.sendMessage(Messaging.notMayor());
					} else
						sender.sendMessage(Messaging.missingCitizenRecord());
				} else
					sender.sendMessage(ChatColor.RED
							+ "To use this command as Console or Command block, please specify a City to dissolve.");
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.dissolve"));
		} else if (sender.hasPermission("cityzen.city.dissolve.others")) {
			final City city = City.getCity(Util.findCityName(args));
			if (city != null) {
				final List<Citizen> refugees = city.getCitizens();
				sender.sendMessage(ChatColor.BLUE + "You deleted the City " + city.getChatName());
				city.delete();
				for (final Citizen r : refugees)
					if (r.getPlayer().isOnline())
						r.getPlayer().sendMessage(ChatColor.RED + "Your city has been deleted."
								+ " You have not lost any reputation from this and are free to join another city.");
					else
						r.addAlert("Your city has been deleted."
								+ " You have not lost any reputation from this and are free to join another city.");
			} else
				sender.sendMessage(Messaging.cityNotFound());
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.dissolve.others"));
	}

	private static void ban(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 2) {
			if (sender.hasPermission("cityzen.city.ban")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You have to specify a city to use this command"
							+ " from console or a command block. Useage:\"/city ban <player> (city)");
					return;
				}
				final Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					city = citizen.getAffiliation();
					if (city == null) {
						sender.sendMessage(Messaging.noAffiliation());
						return;
					}
					if (!(citizen.isMayor() || citizen.isDeputy())) {
						sender.sendMessage(Messaging.notCityOfficial());
						return;
					}
				}
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.ban"));
		} else if (sender.hasPermission("cityzen.city.ban.others")) {
			city = City.getCity(Util.findCityName(args));
			if (city == null) {
				sender.sendMessage(Messaging.cityNotFound());
				return;
			}
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.ban.others"));
		if (city != null) {
			final Citizen target = Citizen.getCitizen(args[1]);
			if (target != null) {
				if (!city.isBanned(target)) {
					city.ban(target);
					sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has been banned from "
							+ city.getChatName());
				} else
					sender.sendMessage(ChatColor.RED + "That player is already banned from " + city.getChatName());
			} else
				sender.sendMessage(Messaging.citizenNotFound(args[1]));
		}
	}

	private static void pardon(CommandSender sender, String[] args) {
		City city = null;
		if (args.length == 2) {
			if (sender.hasPermission("cityzen.city.pardon")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You have to specify a city to use this command"
							+ " from console or a command block. Useage:\"/city ban <player> (city)");
					return;
				}
				final Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					city = citizen.getAffiliation();
					if (city == null) {
						sender.sendMessage(Messaging.noAffiliation());
						return;
					}
					if (!(citizen.isMayor() || citizen.isDeputy())) {
						sender.sendMessage(Messaging.notCityOfficial());
						return;
					}
				}
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.pardon"));
		} else if (sender.hasPermission("cityzen.city.pardon.others")) {
			city = City.getCity(Util.findCityName(args));

			if (city == null) {
				sender.sendMessage(Messaging.cityNotFound());
				return;
			}
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.pardon.others"));
		if (city != null) {
			final Citizen target = Citizen.getCitizen(args[1]);
			if (target != null) {
				if (city.isBanned(target)) {
					city.pardon(target);
					sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has been pardoned from "
							+ city.getChatName());
				} else
					sender.sendMessage(ChatColor.RED + "That player is not banned from " + city.getChatName());
			} else
				sender.sendMessage(Messaging.citizenNotFound(args[1]));
		}
	}

	private static void accept(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.accept")) {
			if (sender instanceof Player) {
				final Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					final City city = citizen.getAffiliation();
					if (city != null) {
						if (citizen.isMayor() || citizen.isDeputy()) {
							if (args.length > 1) {
								final Citizen target = Citizen.getCitizen(args[1]);
								if (target != null) {
									if (city.isInWaitlist(target)) {
										final long rep = target.getReputation();
										city.addCitizen(target);
										city.removeWaitlist(target);
										sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE
												+ " is now a Citizen of " + city.getChatName());
										target.sendMessage(ChatColor.GOLD + "Congratulations!" + ChatColor.BLUE
												+ " Your request to join " + city.getChatName() + ChatColor.BLUE
												+ " has been accepted. You are now an official Citizen"
												+ " of this City."
												+ (target.getReputation() > rep
														? " You gained " + ChatColor.GOLD
																+ (target.getReputation() - rep) + " Reputation."
														: ""));
									} else
										sender.sendMessage(ChatColor.RED + target.getName()
												+ " is not on the waitlist for " + city.getChatName());
								} else
									sender.sendMessage(Messaging.citizenNotFound(args[2]));
							} else
								sender.sendMessage(ChatColor.RED
										+ "Not enough arguments. Please specify a player whose join request to accept.");
						} else
							sender.sendMessage(Messaging.notCityOfficial());
					} else
						sender.sendMessage(Messaging.noAffiliation());
				} else
					sender.sendMessage(Messaging.missingCitizenRecord());
			} else
				sender.sendMessage(Messaging.playersOnly());
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.accept"));
	}

	private static void deny(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.city.accept")) {
			if (sender instanceof Player) {
				final Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					final City city = citizen.getAffiliation();
					if (city != null) {
						if (citizen.isMayor() || citizen.isDeputy()) {
							if (args.length > 1) {
								final Citizen target = Citizen.getCitizen(args[2]);
								if (target != null) {
									if (city.isInWaitlist(target)) {
										city.removeWaitlist(target);
										sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE
												+ " has been removed from the waitlist for " + city.getChatName()
												+ ChatColor.BLUE
												+ ". If you want to prevent this Citizen from reapplying, you can ban them"
												+ " with /city ban <Citizen>");
										target.sendMessage(ChatColor.BLUE + "Sorry, your request to join "
												+ city.getChatName() + ChatColor.BLUE + " has been denied.");
									} else
										sender.sendMessage(ChatColor.RED + target.getName()
												+ " is not on the waitlist for " + city.getChatName());
								} else
									sender.sendMessage(Messaging.citizenNotFound(args[2]));
							} else
								sender.sendMessage(ChatColor.RED
										+ "Not enough arguments. Please specify a player whose join request to accept.");
						} else
							sender.sendMessage(Messaging.notCityOfficial());
					} else
						sender.sendMessage(Messaging.noAffiliation());
				} else
					sender.sendMessage(Messaging.missingCitizenRecord());
			} else
				sender.sendMessage(Messaging.playersOnly());
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.accept"));
	}

	private static void banlist(CommandSender sender, String[] args) {
		City city = null;
		if (args.length <= 2) {
			if (!sender.hasPermission("cityzen.city.banlist")) {
				sender.sendMessage(Messaging.noPerms("cityzen.city.banlist"));
				return;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(Messaging.playersOnly());
				return;
			}
			final Citizen citizen = Citizen.getCitizen(sender);
			if (citizen == null) {
				sender.sendMessage(Messaging.missingCitizenRecord());
				return;
			}
			if (!(citizen.isMayor() || citizen.isDeputy())) {
				sender.sendMessage(Messaging.notCityOfficial());
				return;
			}
			city = citizen.getAffiliation();
			if (city == null) {
				sender.sendMessage(Messaging.noAffiliation());
				return;
			}
		} else if (sender.hasPermission("cityzen.city.banlist.others")) {
			city = City.getCity(Util.findCityName(args));
			if (city == null) {
				sender.sendMessage(Messaging.cityNotFound(Util.collapseArguments(args, 2)));
				return;
			}
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.banlist.others"));
		if (city != null) {

			int pageNumber;
			if (args.length > 1)
				try {
					pageNumber = Integer.parseInt(args[1]);
					if (pageNumber < 1)
						pageNumber = 1;
				} catch (final NumberFormatException e) {
					pageNumber = 1;
				}
			else
				pageNumber = 1;

			final List<Citizen> banlist = city.getBanlist();

			if (banlist.size() > 0) {
				final int numberOfPages = (int) Math.ceil(banlist.size() / 5.0);

				final String[][] pages = new String[numberOfPages][5];

				for (int page = 0; page < numberOfPages; page++)
					for (int i = 0; i < 5; i++)
						try {
							final Citizen banee = banlist.get(page * 5 + i);
							pages[page][i] = ChatColor.RED + banee.getName();
						} catch (final IndexOutOfBoundsException e) {
							pages[page][i] = null;
						}

				sender.sendMessage(ChatColor.RED + "Citizens banned from " + city.getChatName() + ChatColor.RED + " ("
						+ pageNumber + "/" + numberOfPages + ")");
				for (final String s : pages[pageNumber - 1])
					if (s != null)
						sender.sendMessage(s);
				if (numberOfPages > 1 && pageNumber < numberOfPages)
					sender.sendMessage(
							ChatColor.BLUE + "Type \"/city banlist " + (pageNumber + 1) + "\" to view the next page.");
			} else
				sender.sendMessage(ChatColor.BLUE + "No Citizens are banned from " + city.getChatName());
		}
	}

	private static void distance(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			City city = null;
			if (args.length == 1) {
				if (!sender.hasPermission("cityzen.city.distance")) {
					sender.sendMessage(Messaging.noPerms("cityzen.city.distance"));
					return;
				}
				final Citizen citizen = Citizen.getCitizen(sender);
				if (citizen == null) {
					sender.sendMessage(Messaging.missingCitizenRecord());
					return;
				}
				city = citizen.getAffiliation();
				if (city == null) {
					sender.sendMessage(Messaging.noAffiliation());
					return;
				}
			} else if (sender.hasPermission("cityzen.city.distance.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) {
					sender.sendMessage(Messaging.cityNotFound());
					return;
				}
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.distance.others"));
			if (city != null)
				if (city.getCenter() != null) {
					final Location position = ((Player) sender).getLocation();
					final Location cityLocation = city.getCenter().asLocation();
					final double x = position.getX();
					final double z = position.getZ();
					final double cityX = cityLocation.getX();
					final double cityZ = cityLocation.getZ();
					final double distance = Math.sqrt(Math.pow(x - cityX, 2.0) + Math.pow(z - cityZ, 2.0));
					final StringBuilder direction = new StringBuilder();
					if (cityZ - z > 0.0)
						direction.append("South");
					else if (cityZ - z < 0.0)
						direction.append("North");
					// else direction += "Due ";
					if (cityX - x > 0.0)
						direction.append("East");
					else if (cityX - x < 0.0)
						direction.append("West");
					// else direction += "ward";

					sender.sendMessage(ChatColor.BLUE + "Distance to the center of " + city.getChatName()
							+ ChatColor.BLUE + String.format(" (%1$.2f,%2$.2f):\n", cityX, cityZ)
							+ String.format("| %1$.2f Blocks ", distance)
							+ direction.append(" of your location").toString());
				} else
					sender.sendMessage(ChatColor.RED + "Could not locate the center of " + city.getName()
							+ ". This is probably because the City doesn't have any plots yet.");
		} else
			sender.sendMessage(Messaging.playersOnly());
	}

	private static void visitors(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			City city = null;
			if (args.length == 1) {
				if (!sender.hasPermission("cityzen.city.visitors")) {
					sender.sendMessage(Messaging.noPerms("cityzen.city.visitors"));
					return;
				}
				final Citizen citizen = Citizen.getCitizen(sender);
				if (citizen == null) {
					sender.sendMessage(Messaging.missingCitizenRecord());
					return;
				}
				city = citizen.getAffiliation();
				if (city == null) {
					sender.sendMessage(Messaging.noAffiliation());
					return;
				}
			} else if (sender.hasPermission("cityzen.city.visitors.others")) {
				city = City.getCity(Util.findCityName(args));
				if (city == null) {
					sender.sendMessage(Messaging.cityNotFound());
					return;
				}
			} else
				sender.sendMessage(Messaging.noPerms("cityzen.city.visitors.others"));
			if (city != null) {
				Player mayor = null;
				final Vector<Player> deputies = new Vector<Player>();
				final Vector<Player> citizens = new Vector<Player>();
				final Vector<Player> visitors = new Vector<Player>();
				for (final Player p : plugin.getServer().getOnlinePlayers())
					if (city.isInCity(p.getLocation().getX(), p.getLocation().getZ())) {
						final Citizen citizen = Citizen.getCitizen(p);
						if (city.equals(citizen.getAffiliation())) {
							if (citizen.isMayor())
								mayor = p;
							else if (citizen.isDeputy())
								deputies.add(p);
							else
								citizens.add(p);
						} else
							visitors.add(p);
					}

				if (mayor != null || deputies.size() > 0 || citizens.size() > 0 || visitors.size() > 0) {
					String playerList = "";

					sender.sendMessage(ChatColor.BLUE + "Players currently located in " + city.getChatName());
					if (mayor != null)
						sender.sendMessage(ChatColor.RED + "Mayor: " + ChatColor.RESET + mayor.getDisplayName());
					if (deputies.size() > 0) {
						sender.sendMessage(ChatColor.YELLOW + "Deputies:\n");
						for (final Player p : deputies)
							playerList += p.getDisplayName() + " ";
						sender.sendMessage(playerList);
						playerList = "";
					}
					if (citizens.size() > 0) {
						sender.sendMessage(ChatColor.GREEN + "Citizens:\n");
						for (final Player p : citizens)
							playerList += p.getDisplayName() + " ";
						sender.sendMessage(playerList);
						playerList = "";
					}
					if (visitors.size() > 0) {
						sender.sendMessage(ChatColor.BLUE + "Visitors:\n");
						for (final Player p : visitors)
							playerList += p.getDisplayName() + " ";
						sender.sendMessage(playerList);
						playerList = "";
					}

				} else
					sender.sendMessage(ChatColor.BLUE + "Nobody is in " + city.getChatName());
			}
		} else
			sender.sendMessage(Messaging.playersOnly());
	}

	private static void alert(CommandSender sender, String[] args) {
		City city = null;
		final String cityName = Util.findCityName(args);
		boolean admin = false;
		if (cityName != null && sender.hasPermission("cityzen.city.alert.others")) {
			city = City.getCity(Util.findCityName(args));

			if (city == null) {
				sender.sendMessage(Messaging.cityNotFound());
				return;
			}
			admin = true;
		} else if (sender.hasPermission("cityzen.city.alert")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You have to specify a city to use this command"
						+ " from console or a command block. Useage:\"/city alert <message...> (city)");
				return;
			}
			final Citizen citizen = Citizen.getCitizen(sender);
			if (citizen != null) {
				city = citizen.getAffiliation();
				if (city == null) {
					sender.sendMessage(Messaging.noAffiliation());
					return;
				}
				if (!(citizen.isMayor() || citizen.isDeputy())) {
					sender.sendMessage(Messaging.notCityOfficial());
					return;
				}
			}
		} else
			sender.sendMessage(Messaging.noPerms("cityzen.city.alert"));
		if (city != null)
			if (args.length > 1) {
				String alert = "";
				if (admin)
					alert = Util.collapseArgsWithoutCityName(args, 1, city.getName());
				else
					alert = Util.collapseArguments(args, 1);
				for (final Citizen c : city.getCitizens())
					c.addAlert(sender.getName() + ": " + alert);
				sender.sendMessage(ChatColor.BLUE + "Alert sent: " + alert);
			} else
				sender.sendMessage(Messaging.notEnoughArguments("/city alert <Message>"));
	}

	private static void citizens(CommandSender sender, String[] args) {
		String perm = "cityzen.city.members";
		if (!sender.hasPermission(perm)) {
			sender.sendMessage(Messaging.noPerms(perm));
			return;
		}

		City city = null;
		if (args.length > 1) {
			perm = "cityzen.city.members.others";
			if (!sender.hasPermission(perm)) {
				sender.sendMessage(Messaging.noPerms(perm));
				return;
			}

			final String name = Util.findCityName(args, 1);
			city = City.getCity(name);
			if (city == null)
				sender.sendMessage(Messaging.cityNotFound(name));
		} else if (sender instanceof Player)
			city = City.getCity(sender);

		if (city == null) {
			sender.sendMessage(Messaging.cityNotFound());
			return;
		}

		if (sender instanceof Player) {
			final Citizen citizen = Citizen.getCitizen(sender);
			if (citizen == null) {
				sender.sendMessage(Messaging.missingCitizenRecord());
				return;
			}

			perm = "cityzen.city.members.others";
			if (city != citizen.getAffiliation() && !sender.hasPermission(perm)) {
				sender.sendMessage(Messaging.noPerms(perm));
				return;
			}
		}

		final StringBuilder builder = new StringBuilder(
				ChatColor.BLUE + "Members of " + city.getChatName() + "\n" + ChatColor.YELLOW);
		final List<String> names = new ArrayList<String>();
		for (final Citizen c : city.getCitizens())
			names.add(c.getName());
		builder.append(String.join(", ", names));

		sender.sendMessage(builder.toString());
	}
}
