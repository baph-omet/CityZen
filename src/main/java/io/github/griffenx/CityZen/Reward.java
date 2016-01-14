package io.github.griffenx.CityZen;

import java.util.List;
import java.util.Vector;

import org.bukkit.configuration.file.FileConfiguration;

public class Reward {
    
    private int id;
    private String type;
    private long initialRep;
    private long intervalRep;
    private boolean isBroadcast;
    private String command;
    private String message;
    
    public Reward(int id) throws Exception {
        String[] properties = readReward(id);
        if (properties == null) {
            throw new Exception("No reward exists with specified ID");
        } else {
            this.id = id;
            type = properties[1];
            initialRep = Long.parseLong(properties[2]);
            intervalRep = Long.parseLong(properties[3]);
            isBroadcast = Boolean.valueOf(properties[4]);
            command = properties[5];
            message = properties[6];
        }
    }
    
    public static Reward createReward(String type, long initialRep, long intervalRep, boolean isBroadcast, String command, String message) {
        int newID = getNextID();
        if (newID >= 0) {
            FileConfiguration config = CityZen.rewardConfig.getConfig();
            String properties = newID + ";" + type + ";" + initialRep + ";" + intervalRep + ";" + isBroadcast + ";" + command + ";" + message;
            config.set("rewards",config.getStringList("rewards").add(properties));
            try {
            	return new Reward(newID);
            } catch (Exception e) {
            	return null;
            }
        } return null;
    }
    
    public static List<Reward> getRewards() {
        List<Reward> rewards = new Vector<Reward>();
        for (String r : CityZen.rewardConfig.getConfig().getStringList("rewards")) {
            try {
                rewards.add(new Reward(Integer.parseInt(r.split(";")[0])));
            } catch (Exception e) {
                continue;
            }
        } return rewards;
    }
    
    public static void deleteReward(int id) throws Exception {
    	Reward target;
    	try {
    		target = new Reward(id);
    	} catch (Exception e) {
    		throw e;
    	}
        List<String> rewards = CityZen.rewardConfig.getConfig().getStringList("rewards");
        int i = 0;
        while (i<rewards.size()) {
            try {
                if (Integer.parseInt(rewards.get(i).split(";")[0]) == id) {
                    for (Citizen c : Citizen.getCitizens()) {
                        for (Reward r : c.getRewards()) {
                            if (target.equals(r)) {
                                c.cancelReward(r);
                            }
                        }
                    }
                    rewards.remove(i);
                    return;
                } else i++;
            } catch (NumberFormatException e) {
                rewards.remove(i);
            }
        }
    }
    
    public int getID() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        if (type.toLowerCase().charAt(0) == 'c') type = "c";
        else if (type.toLowerCase().charAt(0) == 'p') type = "p";
        saveReward();
    }
    
    public long getInitialRep() {
        return initialRep;
    }
    
    public void setInitialRep(long rep) {
        if (rep < 0) rep = 0;
        initialRep = rep;
        saveReward();
    }
    
    public long getIntervalRep() {
        return intervalRep;
    }
    
    public void setIntervalRep(long rep) {
        if (rep < -1) rep = -1;
        intervalRep = rep;
        saveReward();
    }
    
    public boolean getIsBroadcast() {
        return isBroadcast;
    }
    
    public void setIsBroadcast(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
        saveReward();
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getFormattedString(String input, Citizen target) {
        return input.replace("%p",target.getName()).replace("%r",target.getReputation() + "")
            .replace("%c",(target.getAffiliation() != null ? target.getAffiliation().getName() : "(None)"))
            .replace("%i",(target.getAffiliation() != null ? target.getAffiliation().getIdentifier() : "(None)"));
    }
    public String getFormattedString(String input, City target) {
        return input.replace("%r",target.getReputation() + "").replace("%c",target.getName())
            .replace("%i",target.getName());
    }
    
    public void setCommand(String command) {
        this.command = command;
        saveReward();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
        saveReward();
    }
    
    public String toString() {
        return id + ";" + type + ";" + initialRep + ";" + intervalRep + ";" + isBroadcast + ";" + command + ";" + message;
    }
    
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass().equals("Reward") && ((Reward)obj).getID() == this.getID()) return true;
        return false;
    }
    
    private static  int getNextID() {
        FileConfiguration config = CityZen.rewardConfig.getConfig();
        boolean numberFound = false;
        for (int i=0; i < config.getStringList("rewards").size();i++) {
            numberFound = false;
            for (String r : config.getStringList("rewards")) {
                try {
                    if (Integer.parseInt(r.split(";")[0]) == i) {
                        numberFound = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            if (!numberFound) return i;
        }
        return -1;
    }
    
    private String[] readReward(int id) {
        for (String r : CityZen.rewardConfig.getConfig().getStringList("rewards")) {
            try {
                String[] reward = r.split(";");
                if (id == Integer.parseInt(reward[0])) return reward;
            } catch (NumberFormatException e) {
                return null;
            }
        } return null;
    }
    
    private void saveReward() {
    	List<String> rewards = CityZen.rewardConfig.getConfig().getStringList("rewards");
    	rewards.add(id,toString());
        CityZen.rewardConfig.getConfig().set("rewards",rewards);
    }
}
