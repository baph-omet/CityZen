package io.github.griffenx.CityZen;

public interface Reputable {
    abstract long getReputation();
    
    abstract long getMaxReputation();
    
    abstract void sendReward(Reward r);
    
    abstract List<Reward> getRewards();
}
