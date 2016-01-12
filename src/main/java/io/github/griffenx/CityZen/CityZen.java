package io.github.griffenx.CityZen;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.griffenx.CityZen.Tasks.SaveConfigTask;
import net.milkbowl.vault.economy.Economy;

public final class CityZen extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Plugin plugin;
	
	public static Config citizenConfig = new Config("citizens.yml");
	public static Config cityConfig = new Config("cities.yml");
	public static Config rewardConfig = new Config("rewards.yml");
	
	public static Economy econ = null;
	
	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		getConfig();
		citizenConfig.save();
		citizenConfig.getConfig();
		cityConfig.save();
		
		if (getConfig().getBoolean("useEconomy")) {
			if (!setupEconomy() ) {
	            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
		} else log.info("Economy disabled in config. Set \"useEconomy\" to true in config.yml to use Economy features.");
		
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
	
	private boolean setupEconomy() {
	        if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            return false;
	        }
	        econ = rsp.getProvider();
	        return econ != null;
	}
}
