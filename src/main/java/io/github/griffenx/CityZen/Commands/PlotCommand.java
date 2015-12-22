package io.github.griffenx.CityZen.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.CityZen;
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
			case "avail":
			case "available":
				available(sender, args);
				break;
			case "buy":
				buy(sender, args);
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
		if (CityZen.getPlugin().getConfig().getBoolean("useEconomy")){
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
		} else sender.sendMessage(Messaging.econDisabled());
	}
	
	private static void buy(CommandSender sender, String[] args) {
		if (CityZen.getPlugin().getConfig().getBoolean("useEconomy")) {
			if (sender.hasPermission("cityzen.plot.buy")) {
				if (sender instanceof Player) {
					Player user = (Player)sender;
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.getAffiliation() != null) {
							Plot plot = null;
							for (Plot p : citizen.getAffiliation().getPlots()) {
								if (p.isInPlot(user.getLocation())) {
									plot = p;
									break;
								}
							}
							if (plot != null) {
								if (citizen.getPlots().size() < citizen.getMaxPlots()) {
									if (plot.getPrice() > 0) {
										if (CityZen.econ.getBalance(CityZen.getPlugin().getServer().getOfflinePlayer(citizen.getUUID())) > plot.getPrice()) {
											CityZen.econ.withdrawPlayer(CityZen.getPlugin().getServer().getOfflinePlayer(citizen.getUUID()), plot.getPrice());
											plot.addOwner(citizen);
											citizen.addReputation(CityZen.getPlugin().getConfig().getLong("reputation.gainedOnBuyPlot"));
											
											List<Citizen> formerOwners = plot.getOwners();
											for (Citizen o : formerOwners) {
												o.sendMessage(ChatColor.BLUE + "Your plot centered at " + plot.getCenter() + " was sold to " + citizen.getName() + " for " 
														+ plot.getPrice() + " " + CityZen.econ.currencyNamePlural());
												CityZen.econ.depositPlayer(CityZen.getPlugin().getServer().getOfflinePlayer(o.getUUID()), plot.getPrice() / formerOwners.size());
												o.subReputation(CityZen.getPlugin().getConfig().getLong("reputation.lostOnSellPlot"));
												plot.removeOwner(o);
											}
											plot.setPrice(0);
											sender.sendMessage(ChatColor.BLUE + "You successfully purchased this plot for " + plot.getPrice() + " " + CityZen.econ.currencyNamePlural());
										} else sender.sendMessage(ChatColor.RED + "You cannot afford this plot. Price: " + plot.getPrice() + CityZen.econ.currencyNamePlural() 
											+ " You have: " + CityZen.econ.getBalance(CityZen.getPlugin().getServer().getOfflinePlayer(citizen.getUUID())) + CityZen.econ.currencyNamePlural());
									} else sender.sendMessage(ChatColor.RED + "This plot is not for sale.");
								} else sender.sendMessage(ChatColor.RED + "You have reached your maximum plot limit of " + citizen.getMaxPlots() + ". Get rid of plots or increase your limit before acquiring more.");
							} else sender.sendMessage(Messaging.noPlotFound());
						} else sender.sendMessage(Messaging.noAffiliation());
					} else sender.sendMessage(Messaging.missingCitizenRecord());
				} else sender.sendMessage(Messaging.playersOnly());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.buy"));
		} else sender.sendMessage(Messaging.econDisabled());
	}
	
	private static void available(CommandSender sender, String[] args) {
		if (sender.hasPermission("cityzen.plot.available")) {
			City city = null;
			if (args.length == 1) {
				if (sender instanceof Player) {
					Citizen citizen = Citizen.getCitizen(sender);
					if (citizen != null) {
						if (citizen.getAffiliation() != null) {
							city = citizen.getAffiliation();
						} else sender.sendMessage(Messaging.noAffiliation());
					} else sender.sendMessage(Messaging.missingCitizenRecord());
				} else sender.sendMessage(Messaging.playersOnly());
			} else {
				if (sender.hasPermission("cityzen.plot.available.others")) {
					city = City.getCity(args[1]);
					if (city == null) {
						sender.sendMessage(Messaging.cityNotFound(args[1]));
						return;
					}
				} else sender.sendMessage(Messaging.noPerms("cityzen.plot.available.others"));
			}
			
			if (city != null) {
				if (!city.isOpenPlotting()) {
					StringBuilder plotsList = new StringBuilder();
					int i = 1;
					for (Plot p : city.getPlots()) {
						if (p.getOwners().isEmpty()) {
							plotsList.append(ChatColor.BLUE + "| " + i + ". (" + p.getCenter().getBlockX() + "," + p.getCenter().getBlockZ() + ")\n");
							i++;
						}
					}
					if (i == 1) sender.sendMessage(ChatColor.RED + "No available plots found in " + city.getName() + ". Ask a city official to create some more.");
					else sender.sendMessage(new String[] {
								ChatColor.BLUE + "Open plots in " + city.getChatName() + ":",
								plotsList.toString()
						});
				} else sender.sendMessage(ChatColor.BLUE + "OpenPlotting is enabled in " + city.getChatName() + ". Empty plots will automatically wipe. Feel free to create your own plot.");
			}
		} else sender.sendMessage(Messaging.noPerms("cityzen.plot.available"));
	}
}
