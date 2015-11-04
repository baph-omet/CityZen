package io.github.griffenx.CityZen;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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
	public Boolean naturalWipe;
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
		naturalWipe = Boolean.valueOf(getProperty("naturalWipe"));
		blockBlacklist = Boolean.valueOf(getProperty("blockBlacklist"));
		if (blockBlacklist) useBlacklistAsWhitelist = Boolean.valueOf(getProperty("useBlacklistAsWhitelist"));
		
		blacklistedBlocks = getBlacklist();
		
		mayor = Util.getCitizen(UUID.fromString(getProperty("mayor")));
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
		String path = "cities." + identifier + ".";
		FileConfiguration config = CityZen.cityConfig.getConfig();
		
		config.set(path + "name", name);
		config.set(path + "slogan", slogan);
		config.set(path + "color", color);
		config.set(path + "mayor", mayor);
		config.set(path + "maxPlotSize",maxPlotSize);
		config.set(path + "minPlotSize", minPlotSize);
		config.set(path + "center", center);
		config.set(path + "freeJoin", freeJoin);
		config.set(path + "openPlotting", openPlotting);
		config.set(path + "naturalWipe", naturalWipe);
		config.set(path + "blockBlacklist", blockBlacklist);
		config.set(path + "useBlacklistAsWhitelist", useBlacklistAsWhitelist);
		
		List<String> cits = new Vector<String>();
		for (Citizen c : citizens) cits.add(c.passport.getUniqueId().toString());
		config.set(path + "citizens", cits);
		
		List<String> deps = new Vector<String>();
		for (Citizen d : deputies) deps.add(d.passport.getUniqueId().toString());
		config.set(path + "deputies", deps);
		
		//TODO: Save plots
		
		
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
				String val = defaultConfig.getString("cityDefaults." + property);
				CityZen.cityConfig.getConfig().set("cities." + identifier + "." + property, val);
				//NOTE: This assumes that the default config is intact. Should probably do some sort of error handling on loading configs to make sure that each value is valid.
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
		List<Material> mats = new Vector<Material>();
		for (String block : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".blacklistedBlocks")) {
			Material mat = Material.getMaterial(block);
			if (mat != null) {
				mats.add(mat);
			}
		} return mats;
	}
	
	private List<Plot> getPlots() {
		List<Plot> plts = new Vector<Plot>();
		for (String key : CityZen.cityConfig.getConfig().getConfigurationSection("cities." + identifier + ".plots").getKeys(false)) {
			//TODO: Get a plot
			plts.add(new Plot(this, Integer.valueOf(key)));
		} return plts;
	}
}
