package io.github.griffenx.CityZen;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
	private final Plugin plugin;
	
	private FileConfiguration cnfg;
	private File cnfgFile;
	private String cnfgName;
	
	public Config(String name) {
		plugin = CityZen.getPlugin();
		cnfgName = name;
		cnfgFile = new File(plugin.getDataFolder(), cnfgName);
		cnfg = YamlConfiguration.loadConfiguration(cnfgFile);
	}
	
	public void reload() {
	    cnfg = YamlConfiguration.loadConfiguration(cnfgFile);
	 
	    // Look for defaults in the jar
	    Reader defConfigStream = new InputStreamReader(plugin.getResource(cnfgName), StandardCharsets.UTF_8);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        cnfg.setDefaults(defConfig);
	    }
	}
	
	public void save() {
		if (cnfg == null || cnfgFile == null) {
	        return;
	    }
	    try {
	        getConfig().save(cnfgFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + cnfgFile, ex);
	    }
	}
	
	public FileConfiguration getConfig() {
		if (cnfg == null) {
	        reload();
	    }
	    return cnfg;
	}
	
	
}
