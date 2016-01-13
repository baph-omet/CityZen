package io.github.griffenx.CityZen.Commands;

public class CityZenRewardCommand {
    public static boolean delegate(CommandSender sender, String[] args) {
        if (args.length > 1) {
            switch (args[1].toLowerCase().substring(0,1)) {
                case "l":
                    if (sender.hasPermission("cityzen.rewards." + args[1])) {
                        sender.sendMessage(ChatColor.BLUE + "Reputation Rewards:")
                        for (Reward r : Reward.getRewards()) {
                            sender.senMessage(ChatColor.BLUE + "| (" + r.getID() + ") (" + (r.getType().equals("p") ? "Citizen" : "City") 
                                + ") Initial Reputation: " + r.getInitialRep() + (r.getIntervalRep() > 0 ? " Interval Reputation: " 
                                + r.getIntervalRep() : "") + "\n"
                                + "|   "
                        }
                    }
                    break;
                case "a":
                    
                    break;
                case "m":
                    
                    break;
                case "r":
                    
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
    
    private static void list(CommandSender) {
        if (sender.hasPermission)
    }
}
