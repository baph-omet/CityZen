package io.github.griffenx.CityZen.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

import io.github.griffenx.CityZen.Citizen;
import io.github.griffenx.CityZen.Reward;

public class RewardDisbursementTask extends BukkitRunnable  {

	private Citizen citizen;
	private Reward reward;
	
	public RewardDisbursementTask(Citizen citizen, Reward reward) {
		this.citizen = citizen;
		this.reward = reward;
	}
	
	@Override
	public void run() {
		citizen.sendReward(reward);
	}
	
}
