package io.github.griffenx.CityZen.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.City;
import io.github.griffenx.CityZen.Messaging;
import io.github.griffenx.CityZen.Reputable;
import io.github.griffenx.CityZen.Reward;

public class CityZenRewardCommand {
    public static boolean delegate(CommandSender sender, String[] args) {
        if (args.length > 1) {
            switch (args[1].toLowerCase().substring(0,1)) {
                case "l":
                	list(sender, args);
                    break;
                case "a":
                	add(sender, args);
                    break;
                case "m":
                    message(sender, args);
                    break;
                case "d":
                case "r":
                    remove(sender, args);
                    break;
                default:
                	sender.sendMessage(Messaging.noSuchSubcommand(args[1]));
                    return false;
            }
            return true;
        } return false;
    }
    
    private static void list(CommandSender sender,String[] args) {
    	List<Reward> rewards = null;
        if (args.length == 2) {
            if (sender.hasPermission("cityzen.rewards.list")) {
                sender.sendMessage(ChatColor.BLUE + "Reputation Rewards:");
                rewards = Reward.getRewards();
            } else sender.sendMessage(Messaging.noPerms("cityzen.rewards.list"));
        } else {
            if (sender.hasPermission("cityzen.rewards.list.others")) {
                Reputable target = Citizen.getCitizen(args[2]);
                if (args.length > 3 || target == null) {
                    StringBuilder cityName = new StringBuilder(args[2]);
                    for (int i=3;i<args.length;i++) cityName.append(" " + args[i]);
                    target = City.getCity(cityName.toString());
                }
                if (target != null) {
                    rewards = target.getRewards();
                }
            } else sender.sendMessage(Messaging.noPerms("cityzen.rewards.list.others"));
        }
        if (rewards != null) {
            for (Reward r : Reward.getRewards()) {
                sender.sendMessage(ChatColor.BLUE + "| (ID:" + r.getID() + ") (" + (r.getType().equals("p") ? "Citizen" : "City") 
                    + ") Initial Reputation: " + r.getInitialRep() + (r.getIntervalRep() > 0 ? " Interval Reputation: " 
                    + r.getIntervalRep() : "") + "\n"
                    + "|   Command: " + r.getCommand() + "\n"
                    + "|   Message: " + r.getMessage() + " Broadcast: " + r.getIsBroadcast());
            }
        } else sender.sendMessage(ChatColor.RED + "No Citizen or City found by the name specified.");
    }
    
    private static void add(CommandSender sender, String[] args) {
    	if (sender.hasPermission("cityzen.rewards.add")) {
            if (args.length >= 7) {
                String rType = args[2];
                if (!rType.equalsIgnoreCase("citizen") && !rType.equalsIgnoreCase("city")) {
                    sender.sendMessage(ChatColor.RED + "Unable to parse reward. type must be either \"citizen\" or \"city\".");
                    return;
                }
                long initialRep;
                long intervalRep;
                boolean isBroadcast = Boolean.valueOf(args[5]);
                try {
                    initialRep = Long.parseLong(args[3]);
                    intervalRep = Long.parseLong(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Unable to parse reward. initialRep and intervalRep must both be whole numbers.");
                    return;
                }
                StringBuilder command = new StringBuilder(args[6]);
                for (int i = 7; i < args.length;i++) {
                    command.append(" " + args[i]);
                }
                
                Reward.createReward(rType,initialRep,intervalRep,isBroadcast,command.toString(),"");
                sender.sendMessage(ChatColor.BLUE + "Reward created successfully.");
            } else sender.sendMessage(Messaging.notEnoughArguments("/cityzen rewards add <citizen | city> <initialRep> <intervalRep> <isBroadcast> <command...>"));
        } else sender.sendMessage(Messaging.noPerms("cityzen.rewards.add"));
    }
    
    private static void remove(CommandSender sender, String[] args) {
    	if (sender.hasPermission("cityzen.rewards.remove")) {
    		if (args.length > 2) {
    			int id = -1;
    			try {
    				id = Integer.parseInt(args[2]);
    			} catch (NumberFormatException e) {
    				sender.sendMessage(ChatColor.RED + "Unable to find a reward. ID must be a number.");
    				return;
    			}
    			
    			try {
    				Reward.deleteReward(id);
    			} catch (Exception e) {
    				sender.sendMessage(ChatColor.RED + "Reward could not be removed. Make sure the ID you specified "
    						+ "corresponds to an existing reward.");
    				return;
    			}
    			sender.sendMessage(ChatColor.BLUE + "Reward removed succesfully.");
    		} else sender.sendMessage(Messaging.notEnoughArguments("/cityzen rewards remove <ID>"));
    	} else sender.sendMessage(Messaging.noPerms("cityzen.rewards.remove"));
    }
    
    private static void message(CommandSender sender, String[] args) {
    	if (sender.hasPermission("cityzen.rewards.message")) {
    		if (args.length > 3) {
    			int id;
    			try {
    				id = Integer.parseInt(args[2]);
    			} catch (NumberFormatException e) {
    				sender.sendMessage(ChatColor.RED + "Reward ID must be a number.");
    				return;
    			}
    			
    			Reward reward = null;
    			try {
    				reward = new Reward(id);
    			} catch (Exception e) {
    				sender.sendMessage(ChatColor.RED + "Could not find a reward with the specified ID.");
    				return;
    			}
    			
    			StringBuilder message = new StringBuilder(args[3]);
    			for (int i = 4;i<args.length;i++) {
    				message.append(args[i]);
    			}
    			
    			reward.setMessage(message.toString());
    		} else sender.sendMessage(Messaging.notEnoughArguments("/cityzen rewards message <ID> <message...>"));
    	} else sender.sendMessage(Messaging.noPerms("cityzen.rewards.message"));
    }
}
