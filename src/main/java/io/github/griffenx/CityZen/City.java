package io.github.griffenx.CityZen;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class City {
	public Citizen mayor;
	public List<Citizen> citizens;
	public List<Citizen> deputies;
	
	public List<Plot> plots;
	
	public String name;
	public String slogan;
	public ChatColor color;
	
	public int maxPlotSize;
	public int minPlotSize;
	public Location center;
	
	public Boolean freeJoin;
	public Boolean openPlotting;
	public Boolean blockBlacklist;
	public Boolean useBlacklistAsWhitelist;
	
	public List<Material> blacklistedBlocks;
	
	private String identifier;
	private ConfigurationSection properties;
	
	public City(String id) {
		properties = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + id);
		identifier = id;
		name = getProperty("name");
		slogan = getProperty("slogan");
		color = ChatColor.getByChar(getProperty("color"));
		
		maxPlotSize = Integer.parseInt(getProperty("maxPlotSize"));
		minPlotSize = Integer.parseInt(getProperty("minPlotSize"));
		//TODO: get city center location
		
		freeJoin = Boolean.valueOf(getProperty("freeJoin"));
		openPlotting = Boolean.valueOf(getProperty("openPlotting"));
		blockBlacklist = Boolean.valueOf(getProperty("blockBlacklist"));
		if (blockBlacklist) useBlacklistAsWhitelist = Boolean.valueOf(getProperty("useBlacklistAsWhitelist"));
		
		//TODO: Create a method to get block blacklist and convert to type Material
		blacklistedBlocks = getBlacklist();
		
		mayor = Util.getCitizen(UUID.fromString(getProperty("mayor"));
		citizens = getCitizens();
		deputies = getDeputies();
		
	}
	
	public void addCitizen(Citizen ctz) {
		citizens.add(ctz);
		//TODO: Handling for when a citizen is added to a city, perhaps
	}
	
	public void removeCitizen(Citizen ctz) {
		ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnLeaveCityPercent") / 100);
		citizens.remove(ctz);
		//TODO: Remove plots
	}
	
	public void evictCitizen(Citizen ctz) {
		ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnEvictionPercent") / 100);
		citizens.remove(ctz);
		//TODO: Remove plots
	}
	
	public int getReputation() {
		int tot = 0;
		for(Citizen c : citizens) tot += c.reputation;
		return tot;
	}
	
	public void save() {
		// This operation should be done regularly to avoid data loss if the server crashes or whatever
		//TODO: Save all properties of city to config, then reload config
	}
	
	private String getProperty(String property) {
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				String val = CityZen.cityConfig.getConfig().getString(properties.getString(property));
				if (val.length() == 0) {
					val = CityZen.getPlugin().getConfig().getString("cityDefaults." + property);
					CityZen.cityConfig.getConfig().set("cities." + identifier + "." + property, val);
				} return val;
			}
		}
		FileConfiguration defaultConfig = CityZen.getPlugin().getConfig();
		for (String prop : defaultConfig.getConfigurationSection("cityDefaults").getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				val = defaultConfig.getString("cityDefaults." + property);
				//TODO: Change value of property to default
				//TODO: Change value of property in config to default
				//NOTE: This assuemes that the default config is intact. Should probably do some sort of error handling on loading configs to make sure that each value is valid.
				return val;
			}
		}
		return "";
	}
	
	private List<Citizen> getCitizens() {
		List<Citizen> cits = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".citizens")) {
			cits.add(Util.getCitizen(UUID.fromString(u)));
		}
		return cits;
	}
	
	private List<Citizen> getDeputies() {
		List<Citizen> deps = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".deputies")) {
			deps.add(Util.getCitizen(UUID.fromString(u)));
		}
		return deps;
	}
	
	private List<Material> getBlacklist() {
		//TODO: Get blacklisted materials from config, then convert them to type Material
	}
	
	private List<Plot> getPlots() {
		List<Plot> plts = new Vector<Plot>();
		for (String key : CityZen.cityConfig.getConfig().getConfigurationSection("cities." + identifier + ".plots").getKeys(false)) {
			//TODO: Get a plot
			plts.add()
		}
	}
}
