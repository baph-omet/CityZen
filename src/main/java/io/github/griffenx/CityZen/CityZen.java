package io.github.griffenx.CityZen;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import io.github.griffenx.CityZen.Tasks.SaveConfigTask;
import net.milkbowl.vault.economy.Economy;

public final class CityZen extends JavaPlugin {
	private static final Logger serverLog = Logger.getLogger("Minecraft");
	private static Plugin plugin;
	
	public static Config citizenConfig;
	public static Config cityConfig;
	public static Config rewardConfig;
	
	public static Economy econ = null;
	public static WorldGuardPlugin WorldGuard = null;
	
	public static CityLog cityLog;
	
	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		getConfig();
		citizenConfig = new Config("citizens.yml");
		cityConfig = new Config("cities.yml");
		rewardConfig = new Config("rewards.yml");
		citizenConfig.save();
		citizenConfig.reload();
		cityConfig.save();
		cityConfig.reload();
		rewardConfig.save();
		rewardConfig.reload();
		
		if (CityZen.getPlugin().getConfig().getBoolean("logEnabled")) serverLog.info("Logging enabled. Check for logs in the Logs folder of your CityZen folder.");
		if (CityZen.getPlugin().getConfig().getBoolean("logDebug")) serverLog.info("Debug mode enabled for logs.");
		cityLog = new CityLog(plugin.getDataFolder().getPath() + "/" + CityLog.generateLogName());
		cityLog.write("Enabling plugin...");
		cityLog.debug("Started logging in Debug mode.");
		
		if (getConfig().getBoolean("useEconomy")) {
			if (!setupEconomy() ) {
	            serverLog.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
			cityLog.write("Economy hooked. Econ features should be enabled.");
			cityLog.debug("Using economy: " + econ.getName());
		} else {
			serverLog.info("Economy disabled in config. Set \"useEconomy\" to true in config.yml to use Economy features.");
			cityLog.write("Economy disabled in config. Set \"useEconomy\" to true in config.yml to use Economy features.");
		}
		
		WorldGuard = getWorldGuard();
		if (WorldGuard != null) {
			serverLog.info("WorldGuard successfully hooked! Regions will be protected from CityZen activity.");
			cityLog.write("WorldGuard successfully hooked! Regions will be protected from CityZen activity.");
		}
		else {
			serverLog.info("WorldGuard not found. WorldGuard-dependent functions will be ignored.");
			cityLog.write("WorldGuard not found. WorldGuard-dependent functions will be ignored.");
		}
		
		
		Commander commander = new Commander();
		String[] commands = {
				"psp",
				"passport",
				"rep",
				"reputation",
				"alert",
				"alerts",
				"ctz",
				"citizen",
				"cty",
				"city",
				"plt",
				"plot",
				"cityzen"
		};
		for (String cmd : commands) getCommand(cmd).setExecutor(commander);
		getServer().getPluginManager().registerEvents(new CityZenEventListener(), plugin);
		int saveInterval = getConfig().getInt("saveInterval");
		if (saveInterval > 0) {
			new SaveConfigTask().runTaskTimer(plugin, 20 * 60 * saveInterval, 20 * 60 * saveInterval);
			serverLog.info("CityZen AutoSave started. Plugin will automatically save data every " + saveInterval + " minutes.");
			cityLog.write("CityZen AutoSave started. Plugin will automatically save data every " + saveInterval + " minutes.");
		} else serverLog.warning("Autosaving is disabled. This is not recommended. To enable AutoSave, set \"saveInterval\" in config.yml to a value greater than 0.");
	}
	
	public void onDisable() {
		cityLog.write("Disabling plugin...");
		saveConfig();
		citizenConfig.save();
		cityConfig.save();
		rewardConfig.save();
		plugin = null;
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
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
}
