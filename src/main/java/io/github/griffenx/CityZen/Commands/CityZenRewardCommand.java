package io.github.griffenx.CityZen.Commands;

public class CityZenRewardCommand {
    public static boolean delegate(CommandSender sender, String[] args) {
        if (args.length > 1) {
            switch (args[1].toLowerCase().substring(0,1)) {
                case "l":
                    List<Reward> rewards = null;
                    if (args.length == 2) {
                        if (sender.hasPermission("cityzen.rewards.list")) {
                            sender.sendMessage(ChatColor.BLUE + "Reputation Rewards:")
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
                                rewards = Reward.getRewards(target);
                            }
                        } else sender.sendMessage(Messaging.noPerms("cityzen.rewards.list.others"));
                    }
                    if (rewards != null) {
                        for (Reward r : Reward.getRewards()) {
                            sender.sendMessage(ChatColor.BLUE + "| (ID:" + r.getID() + ") (" + (r.getType().equals("p") ? "Citizen" : "City") 
                                + ") Initial Reputation: " + r.getInitialRep() + (r.getIntervalRep() > 0 ? " Interval Reputation: " 
                                + r.getIntervalRep() : "") + "\n"
                                + "|   Command: " + r.getCommand() + "\n"
                                + "|   Message: " + r.getMessage() + " Broadcast: " + r.getIsBroadcast().toString());
                        }
                    } else sender.sendMessage(ChatColor.RED + "No Citizen or City found by the name specified.");
                    break;
                case "a":
                    //TODO: Reward add command
                    if (sender.hasPermission("cityzen.rewards.add") {
                        if (args.length >= 7) {
                            String rType = args[2];
                            if (!rType.equalsIgnoreCase("citizen") && !rType.equalsIgnoreCase("city")) {
                                sender.sendMessage(ChatColor.RED + "Unable to parse reward. type must be either \"citizen\" or \"city\".");
                                return;
                            }
                            long initialRep;
                            long intervalRep;
                            boolean isBroadcast = Boolean.parse(args[5]);
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
                            
                            Reward.createReward(rType,initialRep,intervalRep,isBroadcast,command,"");
                            
                        }
                    }
                    break;
                case "m":
                    //TODO: reward message command
                    break;
                case "r":
                    //TODO: reward remove command
                    break;
                default:
                    return false;
            }
            return true;
        } return false;
    }
}
