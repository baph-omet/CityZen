package io.github.griffenx.CityZen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class Config {
	/*private final Plugin plugin;
	
	private FileConfiguration cnfg = null;
	private File cnfgFile = null;
	private String cnfgName = null;
	
	public Config(String name) {
		plugin = CityZen.getPlugin();
		cnfgName = name;
		saveDefaultConfig();
		cnfgFile = new File(plugin.getDataFolder(), cnfgName);
		cnfg = YamlConfiguration.loadConfiguration(cnfgFile);
	}
	
	public void reload() {
		if (cnfgFile == null) {
			cnfgFile = new File(plugin.getDataFolder(), cnfgName);
	    }
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
	
	public void saveDefaultConfig() {
		if (!cnfgFile.exists()) {
            plugin.saveResource(cnfgName, false);
        }
	}*/
    private final String fileName;
    private final JavaPlugin plugin;
    
    private File configFile;
    private FileConfiguration fileConfiguration;

    public Config(String fileName) {
        this.plugin = (JavaPlugin) CityZen.getPlugin();
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null)
            throw new IllegalStateException();
        this.configFile = new File(plugin.getDataFolder(), fileName);
        saveDefaultConfig();
    }

    public void reload() {  
    	this.configFile = new File(plugin.getDataFolder(), fileName);
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

        // Look for defaults in the jar
        Reader defConfigStream = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reload();
        }
        return fileConfiguration;
    }

    public void save() {
        if (fileConfiguration == null || configFile == null) {
            return;
        } else {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }
    
    public void saveDefaultConfig() {
        if (!configFile.exists()) {            
            this.plugin.saveResource(fileName, false);
        }
    }
}
