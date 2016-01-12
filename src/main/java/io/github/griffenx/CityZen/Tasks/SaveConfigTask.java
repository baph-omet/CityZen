package io.github.griffenx.CityZen.Tasks;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.griffenx.CityZen.CityZen;

public class SaveConfigTask extends BukkitRunnable {
	private Plugin plugin = CityZen.getPlugin();
	
	public SaveConfigTask() {}
	
	@Override
	public void run() {
		plugin.getLogger().info("Saving CityZen configs...");
		plugin.saveConfig();
		CityZen.citizenConfig.save();
		CityZen.cityConfig.save();
		plugin.getLogger().info("CityZen saved!");
	}

}
