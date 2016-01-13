package io.github.griffenx.CityZen;

public class Reward {
    
    private Config config = CityZen.rewardConfig.getConfig();
    
    private int id;
    private String type;
    private long initialRep;
    private long intervalRep;
    private boolean isBroadcast;
    private String command;
    private String message;
    
    public Reward(int id) {
        String[] properties = readReward(id);
        if (properties == null) {
            return null;
        } else {
            this.id = id;
            type = properties[1];
            initialRep = Long.parseInt(properties[2]);
            intervalRep = Long.parseInt(properties[3]);
            isBroadcast = Boolean.parse(properties[4]);
            command = properties[5];
            message = properties[6];
        }
    }
    
    public static Reward createReward(String type, long initialRep, long intervalRep, boolean isBroadcast, String command, String message) {
        int newID = getNextID();
        if (newID >= 0) {
            Config config = CityZen.rewardConfig.getConfig();
            String properties = newID + ";" + type + ";" + initialRep + ";" + intervalRep + ";" + isBroadcast.toString() + ";" + command + ";" + message;
            config.set("rewards",config.getStringList("rewards").add(properties));
            return new Reward(newID);
        } return null;
    }
    
    public static List<Reward> getRewards() {
        List<Reward> rewards = new Vector<Reward>();
        for (String r : CityZen.rewardConfig.getConfig().getStringList("rewards")) {
            try {
                rewards.add(Integer.parseInt(r.split(";")[0]));
            } catch (NumberFormatException e) {
                continue;
            }
        } return rewards;
    }
    
    public static List<Reward> getRewards(Citizen citizen) {
        List<Reward> rewards = new Vector<Reward>();
        for (String r : getRewards()) {
            if (citizen.getMaxReputation() == r.getInitialRep() || 
                (citizen.getMaxReputation() - r.getInitialRep()) % r.getIntervalRep() == 0) rewards.add(r);
        } return rewards;
    }
    
    public static List<Reward> getRewards(City city) {
        List<Reward> rewards = new Vector<Reward>();
        for (String r : getRewards()) {
            if (city.getMaxReputation() == r.getInitialRep() || 
                (city.getMaxReputation() - r.getInitialRep()) % r.getIntervalRep() == 0) rewards.add(r);
        } return rewards;
    }
    
    public void deleteReward(int id) {
        List<String> rewards = CityZen.rewardConfig.getConfig().getStringList("rewards");
        int i = 0;
        while (i<rewards.size()) {
            try {
                if (Integer.parseInt(rewards.get(i).split(";")[0]) == id) {
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
        return intervalRep();
    }
    
    public void setIntervalRep(long rep) {
        if (rep < -1) rep = -1;
        intervalRep = rep;
        saveReward();
    }
    
    public boolean getIsBroadcast() {
        return isBoolean();
    }
    
    public void setIsBroadcast(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
        saveReward();
    }
    
    public String getCommand() {
        return command;
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
    
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass().equals("Reward") && obj.getID() == this.getID()) return true;
    }
    
    private int getNextID() {
        Config config = CityZen.rewardConfig.getConfig();
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
                String reward = r.split(";");
                if (id == Integer.parseInt(reward[0])) return reward;
            } catch (NumberFormatException e) {
                return null;
            }
        } return null;
    }
    
    private void saveReward() {
        String properties = id + ";" + type + ";" + initialRep + ";" + intervalRep + ";" + isBroadcast.toString() + ";" + command + ";" + message;
        CityZen.rewardConfig.getConfig().set("rewards",CityZen.rewardConfig.getConfig().getStringList("rewards").add(id,properties));
    }
}
