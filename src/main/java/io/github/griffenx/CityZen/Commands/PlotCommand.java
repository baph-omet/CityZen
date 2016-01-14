package io.github.griffenx.CityZen.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.CityZen;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Plot;
import io.github.griffenx.CityZen.ProtectionLevel;

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
				buy(sender);
				break;
			case "claim":
				claim(sender);
				break;
			case "del":
			case "destroy":
			case "delete":
				delete(sender);
				break;
			case "leave":
			case "abandon":
				abandon(sender);
				break;
			case "kick":
			case "remove":
				remove(sender,args);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private static void select(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.select")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					City city = citizen.getAffiliation();
					if (city != null) {
						if (city.isOpenPlotting() || citizen.isCityOfficial()) {
							if (citizen.getPlots().size() < citizen.getMaxPlots() || citizen.isCityOfficial()) {
								switch (args[0].toLowerCase()) {
									case "s":
									case "se":
									case "sel":
									case "sele":
									case "selec":
									case "select":
										if (citizen.getPassport().hasMetadata("plotSelectEnabled")) {
											citizen.getPassport().removeMetadata("plotSelectEnabled", CityZen.getPlugin());
											citizen.getPassport().removeMetadata("plotSelectCorners", CityZen.getPlugin());
											sender.sendMessage(ChatColor.BLUE + "Plot selection disabled.");
										} else {
											citizen.getPassport().setMetadata("plotSelectEnabled", new FixedMetadataValue(CityZen.getPlugin(), true));
											sender.sendMessage(ChatColor.BLUE + "Plot selection enabled. Select corners with \"/plot pos1\" and \"/plot pos2\". "
													+ "Disable plot selection with \"/plot select\"");
										}
										break;
									case "1":
									case "pos1":
										// Format: world,x1,y1,z1;world,x2,y2,z2
										Location pos = ((Player) sender).getLocation();
										for (City c : City.getCities()) for (Plot p : c.getPlots()) if (p.isInPlot(pos)) {
											sender.sendMessage(ChatColor.RED + "This location is inside an existing plot. Try a different location.");
											return;
										}
										String[] positions;
										if (citizen.getPassport().hasMetadata("plotSelectCorners")) {
											positions = citizen.getPassport().getMetadata("plotSelectCorners").get(0).asString().split(";");
											Location pos2 = new Location(CityZen.getPlugin().getServer().getWorld(positions[1].split(",")[0]),
													Double.parseDouble(positions[1].split(",")[1]),Double.parseDouble(positions[1].split(",")[2]),
													Double.parseDouble(positions[1].split(",")[3]));
											
											positions[0] = pos.getWorld().getName() + "," + pos.getBlockX() + "," + pos.getBlockZ();
											citizen.getPassport().setMetadata("plotSelectCorners", new FixedMetadataValue(CityZen.getPlugin(), String.join(";", positions)));
										} else {
											
										}
										break;
								}
							} else sender.sendMessage(ChatColor.RED + "You cannot select plots. You have too many plots already.");
						} else sender.sendMessage(ChatColor.RED + city.getName() + " does not allow OpenPlotting, meaning only City officials can create Plots.");
					} else sender.sendMessage(Messaging.noAffiliation());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.select"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void selectCorner(CommandSender sender) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.select")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					
				}
			}
		}
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
	
	private static void buy(CommandSender sender) {
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
	
	private static void claim(CommandSender sender) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.claim")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					City city = citizen.getAffiliation();
					if (city != null) {
						if (!city.isOpenPlotting()) {
							Plot plot = null;
							for (Plot p : city.getPlots()) {
								if (p.isInPlot(citizen.getPassport().getLocation())) plot = p;
							}
							if (plot != null) {
								if (plot.getOwners().size() == 0) {
									if (citizen.getPlots().size() < citizen.getMaxPlots()) {
										plot.addOwner(citizen);
										long rep = CityZen.getPlugin().getConfig().getLong("reputation.gainedOnClaimPlot");
										citizen.addReputation(rep);
										citizen.sendMessage(ChatColor.BLUE + "You successfully claimed this plot, gaining " + ChatColor.GOLD + rep + " Reputation.");
									} else sender.sendMessage(Messaging.tooManyPlots());
								} else sender.sendMessage(ChatColor.RED + "This plot is currently owned, meaning it is not available to claim.");
							} else sender.sendMessage(ChatColor.RED + "You must be inside a plot in your city to use this command.");
						} else sender.sendMessage(ChatColor.RED + city.getName() + " has OpenPlotting enabled. Empty plots are automatically wiped, so there's no need to claim them.");
					} else sender.sendMessage(Messaging.noAffiliation());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.claim"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void delete(CommandSender sender) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.delete")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					City city = citizen.getAffiliation();
					if (sender.hasPermission("cityzen.plot.delete.others")) {
						for (City c : City.getCities()) {
							if (c.isInCity((Player)sender)) city = c;
						}
					}
					if (city != null) {
						Plot plot = city.getPlot(sender);
						if (plot != null) {
							if (sender.hasPermission("cityzen.plot.delete.others") || citizen.isCityOfficial() || plot.getOwners().contains(citizen)) {
								long rep = CityZen.getPlugin().getConfig().getLong("reputation.lostOnLeavePlot");
								for (Citizen c : plot.getOwners()) {
									c.sendMessage(ChatColor.BLUE + "A plot you own centered at (" + plot.getCenter().getBlockX() + "," 
											+ plot.getCenter().getBlockZ() + ") was deleted by " + sender.getName() + ". You lost " +
											ChatColor.GOLD + rep + " Reputation.");
									c.subReputation(rep);
								}
								plot.wipe();
								plot.delete();
								if (!plot.getOwners().contains(citizen)) sender.sendMessage(ChatColor.BLUE + "You successfully deleted this plot.");
							} else sender.sendMessage(ChatColor.RED + "You don't have permission to delete this plot. Only Admins, "
									+ "city officials, and owners of the plot can delete it.");
						} else sender.sendMessage(Messaging.noPlotFound());
					} else sender.sendMessage(Messaging.noAffiliation());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.delete"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void abandon(CommandSender sender) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.abandon")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					City city = citizen.getAffiliation();
					if (city != null) {
						Plot plot = city.getPlot(sender);
						if (plot != null) {
							if (plot.getOwners().contains(citizen)) {
								long rep = CityZen.getPlugin().getConfig().getLong("reputation.lostOnLeavePlot");
								for (Citizen c : plot.getOwners()) {
									if (!c.equals(citizen)) c.sendMessage(ChatColor.BLUE + citizen.getName() + " left ownership of your plot centered at (" + plot.getCenter().getBlockX() + "," 
											+ plot.getCenter().getBlockZ() + ").");
								}
								boolean isAbandoned = true;
								if (plot.getOwners().size() == 0) {
									if (city.isOpenPlotting()) {
										plot.wipe();
										plot.delete();
									} else isAbandoned = true;
								}
								citizen.subReputation(rep);
								sender.sendMessage(ChatColor.BLUE + "You successfully abandoned this plot." + (isAbandoned ? " This plot is now abandoned, meaning anyone can claim it." : "") 
										+ " You lost " + ChatColor.GOLD + rep + " Reputation.");
							} else sender.sendMessage(ChatColor.RED + "You cannot abandon this plot, since you don't own it.");
						} else sender.sendMessage(Messaging.noPlotFound());
					} else sender.sendMessage(Messaging.noAffiliation());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.abandon"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void invite(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.invite")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					City city = citizen.getAffiliation();
					if (city != null) {
						Plot plot = city.getPlot(sender);
						if (plot != null) {
							if (plot.getOwners().contains(citizen)) {
								if (args.length > 1) {
									Citizen target = Citizen.getCitizen(args[1]);
									if (target != null) {
										if (target.getPassport().isOnline()) {
											if (target.getPlots().size() < target.getMaxPlots()) {
												//TODO: Send target an invitation
												// After 2 minutes, the invitation should expire
											} else sender.sendMessage(ChatColor.RED + target.getName() + " cannot own any more plots.");
										} else {
											target.sendMessage(citizen.getName() + " wanted to invite you to a plot, but you were offline.");
											sender.sendMessage(ChatColor.BLUE + target.getName() + " was offline, but they were notified that you want to add them to your plot. Try again when they're online.");
										}
									} else sender.sendMessage(Messaging.citizenNotFound(args[1]));
								} else sender.sendMessage(Messaging.notEnoughArguments("/plot invite <Citizen>"));
							} else sender.sendMessage(Messaging.notPlotOwner());
						} else sender.sendMessage(Messaging.noPlotFound());
					} else sender.sendMessage(Messaging.noAffiliation());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.invite"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void modifyowners(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.modifyowners")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					City city = citizen.getAffiliation();
					if (sender.hasPermission("cityzen.plot.modifyowners.others")) {
						for (City c : City.getCities()) {
							if (c.isInCity((Player)sender)) city = c;
						}
					}
					if (city != null) {
						Plot plot = city.getPlot(sender);
						if (plot != null) {
							if (citizen.isCityOfficial() || sender.hasPermission("cityzen.plot.modifyowners.others")) {
								if (args.length > 1) {
									Citizen target = Citizen.getCitizen(args[1]);
									if (target != null && plot.getOwners().contains(target)) {
										if (args[0].toLowerCase().contains("add")) {
											if (target.getPlots().size() < target.getMaxPlots()) {
												for (Citizen c : plot.getOwners()) {
													c.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " was added to your plot centered at (" 
														+ plot.getCenter().getBlockX() + "," + plot.getCenter().getBlockZ() + ") by " + sender.getName());
												}
												plot.addOwner(target);
												target.sendMessage(ChatColor.BLUE + "You were added to a plot centered at (" 
													+ plot.getCenter().getBlockX() + "," + plot.getCenter().getBlockZ() + ") by " + sender.getName());
											} else {
												sender.sendMessage(ChatColor.RED + target.getName() + " cannot own any more plots.");
											}
										} else {
											plot.removeOwner(target);
												for (Citizen c : plot.getOwners()) {
													c.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " was removed from your plot centered at (" 
														+ plot.getCenter().getBlockX() + "," + plot.getCenter().getBlockZ() + ") by " + sender.getName());
												}
												target.sendMessage(ChatColor.BLUE + "You were removed from a plot centered at (" 
													+ plot.getCenter().getBlockX() + "," + plot.getCenter().getBlockZ() + ") by " + sender.getName());
										}
									} else sender.sendMessage(ChatColor.RED + "This plot is not owned by a Citizen named " + args[1]);
								} else sender.sendMessage(Messaging.notEnoughArguments("/plot " + args[0] + " <Citizen>"));
							} else sender.sendMessage(Messaging.notCityOfficial());
						} else sender.sendMessage(Messaging.noPlotFound());
					} else sender.sendMessage(Messaging.cityNotFound());
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.modifyowners"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void info(CommandSender sender) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.info")) {
				Plot plot = null;
				for (City c : City.getCities()) {
					if (c.isInCity(sender)) {
						plot = c.getPlot(sender);
						break;
					}
				}
				if (plot != null) {
					StringBuilder message = new StringBuilder();
					message.append(ChatColor.BLUE + "This Plot:\n");
					message.append(ChatColor.BLUE + "Affiliation: " + plot.getAffiliation().getChatName() + " ID: " + plot.getIdentifier() + "\n");
					if (plot.isMega()) message.append(ChatColor.BLUE + "MegaPlot\n");
					message.append(ChatColor.BLUE + "Corner 1: (" + plot.getCorner1().getBlockX() + "," + plot.getCorner1().getBlockZ() + ")\n");
					message.append(ChatColor.BLUE + "Corner 2: (" + plot.getCorner2().getBlockX() + "," + plot.getCorner2().getBlockZ() + ")\n");
					message.append(ChatColor.BLUE + "Center: " + plot.getCenterCoords() + "\n");
					message.append(ChatColor.BLUE + "Size: " + (int)Math.sqrt(plot.getArea()) + " Area: " + plot.getArea() + "\n");
					message.append(ChatColor.BLUE + "Base Height:" + plot.getBaseHeight());
					message.append(ChatColor.BLUE + "Protection: " + plot.getProtectionLevel().toString());
					message.append(ChatColor.BLUE + "Owners:\n");
					for (Citizen o : plot.getOwners()) message.append(ChatColor.BLUE + "- " + o.getName() + "\n");
					message.append(ChatColor.BLUE + "Creator: " + plot.getCreator().getName() + "\n");
					if (CityZen.getPlugin().getConfig().getBoolean("useEconomy") && plot.getPrice() > 0) message.append(ChatColor.BLUE + 
						"Price: " + ChatColor.GREEN + plot.getPrice() + " " + CityZen.econ.currencyNamePlural());
				} else sender.sendMessage(Messaging.noPlotFound());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.info"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
	
	private static void setprotection(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("cityzen.plot.setprotection")) {
				Citizen citizen = Citizen.getCitizen(sender);
				if (citizen != null) {
					Plot plot = City.getCity(sender).getPlot(sender);
					if (plot != null) {
						if (plot.getOwners().contains(citizen) || citizen.isCityOfficial() || sender.hasPermission("cityzen.plot.setprotection.others")) {
							switch (args[2].toLowerCase()) {
								case "0":
								case "none":
								case "pu":
								case "pub":
								case "public":
									plot.setProtectionLevel(ProtectionLevel.PUBLIC);
									sender.sendMessage(ChatColor.BLUE + "You set the protection level for this plot to PUBLIC. "
											+ "Any player can now build in this plot.");
									break;
								case "1":
								case "c":
								case "co":
								case "com":
								case "comm":
								case "communal":
									plot.setProtectionLevel(ProtectionLevel.COMMUNAL);
									sender.sendMessage(ChatColor.BLUE + "You set the protection level for this plot to COMMUNAL. "
											+ "Any Citizen of this City can now build in this plot.");
									break;
								case "2":
								case "p":
								case "pr":
								case "pro":
								case "prot":
								case "protected":
									plot.setProtectionLevel(ProtectionLevel.PROTECTED);
									sender.sendMessage(ChatColor.BLUE + "You set the protection level for this plot to PROTECTED. "
											+ "Only City officials can now build in this plot.");
									break;
								default:
									sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" is not a protection level.");
							}
						} else sender.sendMessage(Messaging.notPlotOwner());
					} else sender.sendMessage(Messaging.noPlotFound()); 
				} else sender.sendMessage(Messaging.missingCitizenRecord());
			} else sender.sendMessage(Messaging.noPerms("cityzen.plot.setprotection"));
		} else sender.sendMessage(Messaging.playersOnly());
	}
}
