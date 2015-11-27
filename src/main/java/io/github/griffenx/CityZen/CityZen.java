package io.github.griffenx.CityZen;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.griffenx.CityZen.Tasks.SaveConfigTask;

public final class CityZen extends JavaPlugin {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Plugin plugin;
	
	public static Config citizenConfig = new Config("citizens.yml");
	public static Config cityConfig = new Config("cities.yml");
	
	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		getConfig();
		citizenConfig.save();
		citizenConfig.getConfig();
		cityConfig.save();
		
		//TODO: Register command handlers
		//TODO: Register event handlers
		int saveInterval = getConfig().getInt("saveInterval");
		if (saveInterval > 0) new SaveConfigTask().runTaskTimer(plugin, 20 * 60 * saveInterval, 20 * 60 * saveInterval);
	}
	
	public void onDisable() {
		plugin = null;
		saveConfig();
		citizenConfig.save();
		cityConfig.save();
	}
	
	public static Plugin getPlugin(){
		return plugin;
	}
}
