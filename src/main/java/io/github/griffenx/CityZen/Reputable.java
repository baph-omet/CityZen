package io.github.griffenx.CityZen;

import java.util.List;

public interface Reputable {
    abstract long getReputation();
    
    abstract long getMaxReputation();
    
    abstract void sendReward(Reward r);
    
    abstract List<Reward> getRewards();
}
