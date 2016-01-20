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
	
	public static Config citizenConfig;
	public static Config cityConfig;
	public static Config rewardConfig;
	
	public static Economy econ = null;
	
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
		
		if (getConfig().getBoolean("useEconomy")) {
			if (!setupEconomy() ) {
	            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
		} else log.info("Economy disabled in config. Set \"useEconomy\" to true in config.yml to use Economy features.");
		
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
			log.info("CityZen AutoSave started. Plugin will automatically save data every " + saveInterval + " minutes.");
		} else log.warning("Autosaving is disabled. This is not recommended. To enable AutoSave, set \"saveInterval\" in config.yml to a value greater than 0.");
	}
	
	public void onDisable() {
		plugin = null;
		saveConfig();
		citizenConfig.save();
		cityConfig.save();
		rewardConfig.save();
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
