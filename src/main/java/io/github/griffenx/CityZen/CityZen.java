package io.github.griffenx.CityZen;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CityZen extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Plugin plugin;
	
	public static Config citizenConfig = new Config("citizens.yml");
	public static Config cityConfig = new Config("cities.yml");
	
	public static List<City> cities;
	public static List<Citizen> citizens;
	
	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		getConfig();
		citizenConfig.save();
		citizenConfig.getConfig();
		cityConfig.save();
		cityConfig.getConfig();
		
		log.info("Loading cities into memory...");
		for (String identifier : cityConfig.getConfig().getConfigurationSection("cities").getKeys(false)) {
			cities.add(new City(identifier));
		}
		log.info("Finished loading cities.");
	}
	
	public void onDisable() {
		plugin = null;
		saveConfig();
		citizenConfig.save();
		
		for (City c : cities) c.save();
		cityConfig.save();
	}
	
	public static Plugin getPlugin(){
		return plugin;
	}
}
