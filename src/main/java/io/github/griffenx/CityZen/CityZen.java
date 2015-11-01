package io.github.griffenx.CityZen;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CityZen extends JavaPlugin {
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
		cityConfig.getConfig();
	}
	
	public void onDisable() {
		plugin = null;
		this.saveConfig();
		citizenConfig.save();
		cityConfig.save();
	}
	
	public static Plugin getPlugin(){
		return plugin;
	}
}
