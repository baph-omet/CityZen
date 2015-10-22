package io.github.griffenx.CityZen;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class CityZen extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private FileConfiguration cityConfig = null;
	private File cityConfigFile = null;
	
	private FileConfiguration citizenConfig = null;
	private File citizenConfigFile = null;
	
	public void reloadCitizenConfig() {
		if (citizenConfigFile == null) {
		    citizenConfigFile = new File(getDataFolder(), "citizens.yml");
	    }
	    citizenConfig = YamlConfiguration.loadConfiguration(citizenConfigFile);
	 
	    // Look for defaults in the jar
	    Reader defConfigStream = new InputStreamReader(this.getResource("citizens.yml"), StandardCharsets.UTF_8);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        citizenConfig.setDefaults(defConfig);
	    }
	}
	public void reloadCityConfig() {
		if (cityConfigFile == null) {
		    cityConfigFile = new File(getDataFolder(), "cities.yml");
	    }
	    cityConfig = YamlConfiguration.loadConfiguration(cityConfigFile);
	 
	    // Look for defaults in the jar
	    Reader defConfigStream = new InputStreamReader(this.getResource("cities.yml"), StandardCharsets.UTF_8);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        cityConfig.setDefaults(defConfig);
	    }
	}
	public FileConfiguration getCityConfig() {
	    if (cityConfig == null) {
	        reloadCityConfig();
	    }
	    return cityConfig;
	}
	public FileConfiguration getCitizenConfig() {
	    if (citizenConfig == null) {
	        reloadCityConfig();
	    }
	    return citizenConfig;
	}
	public void saveCityConfig() {
	    if (cityConfig == null || cityConfigFile == null) {
	        return;
	    }
	    try {
	        getCityConfig().save(cityConfigFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + cityConfigFile, ex);
	    }
	}
	public void saveCitizenConfig() {
	    if (citizenConfig == null || citizenConfigFile == null) {
	        return;
	    }
	    try {
	        getCitizenConfig().save(citizenConfigFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + citizenConfigFile, ex);
	    }
	}
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		getConfig();
		this.saveCitizenConfig();
		this.getCitizenConfig();
		this.saveCityConfig();
		this.getCityConfig();
	}
	
	public void onDisable() {
		this.saveConfig();
		this.saveCitizenConfig();
		this.saveCityConfig();
	}
}
