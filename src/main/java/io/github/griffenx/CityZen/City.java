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
	public String identifier;
	
	public int maxPlotSize;
	public int minPlotSize;
	
	public Boolean freeJoin;
	public Boolean openPlotting;
	public Boolean naturalWipe;
	public Boolean blockBlacklist;
	public Boolean useBlacklistAsWhitelist;
	
	public List<Material> blacklistedBlocks;
	
	private ConfigurationSection properties;
	
	public City(String id) {
		properties = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + id);
		identifier = id;
		name = getProperty("name");
		slogan = getProperty("slogan");
		color = ChatColor.getByChar(getProperty("color"));
		
		maxPlotSize = Integer.parseInt(getProperty("maxPlotSize"));
		minPlotSize = Integer.parseInt(getProperty("minPlotSize"));
		
		freeJoin = Boolean.valueOf(getProperty("freeJoin"));
		openPlotting = Boolean.valueOf(getProperty("openPlotting"));
		naturalWipe = Boolean.valueOf(getProperty("naturalWipe"));
		blockBlacklist = Boolean.valueOf(getProperty("blockBlacklist"));
		if (blockBlacklist) useBlacklistAsWhitelist = Boolean.valueOf(getProperty("useBlacklistAsWhitelist"));
		
		blacklistedBlocks = getBlacklist();
		
		mayor = Citizen.getCitizen(UUID.fromString(getProperty("mayor")));
		citizens = getCitizens();
		deputies = getDeputies();
		
	}
	
	/**
	 * Gets a City by name from list of cities in memory.
	 * If somehow that city is not in memory, it will be added.
	 * This method should be used exclusively for getting cities, NOT initializing a new City.
	 * @param name
	 * The name of the city to get.
	 * @return
	 * A city from list of cities in memory
	 */
	public static City getCity(String name) {
		City cty = null;
		for(City c : CityZen.cities) {
			if (c.name.equalsIgnoreCase(name)) {
				cty = c;
			}
		}
		
		if (cty == null) {
			cty = new City(name);
			CityZen.cities.add(cty);
		}
		
		return cty;
	}
	
	public Location getCenter() {
		Location center = null;
		if (plots.size() > 0) {
			double maxX = 0,
					minX = 0,
					maxZ = 0,
					minZ = 0;
			for (Plot p : plots) {
				if (p.corner1.getX() > maxX) maxX = p.corner1.getX();
				if (p.corner1.getX() < minX) minX = p.corner1.getX();
				if (p.corner1.getZ() > maxZ) maxZ = p.corner1.getZ();
				if (p.corner1.getZ() < minZ) minZ = p.corner1.getZ();
				
				if (p.corner2.getX() > maxX) maxX = p.corner2.getX();
				if (p.corner2.getX() < minX) minX = p.corner2.getX();
				if (p.corner2.getZ() > maxZ) maxZ = p.corner2.getZ();
				if (p.corner2.getZ() < minZ) minZ = p.corner2.getZ();
			}
			center = new Location(plots.get(0).corner1.getWorld(),(maxX + minX) / 2, 0, (maxZ + minZ) / 2);
		}
		return center;
	}
	
	public String getChatName() {
		//TODO: Verify that this works
		return color + name;
	}

	
	public int getReputation() {
		int tot = 0;
		for(Citizen c : citizens) tot += c.reputation;
		return tot;
	}
	
	public void addCitizen(Citizen ctz) {
		citizens.add(ctz);
		//TODO: Handling for when a citizen is added to a city, perhaps
		save();
	}
	
	public void removeCitizen(Citizen ctz) {
		removeCitizen(ctz, false);
	}
	public void removeCitizen(Citizen ctz, Boolean evict) {
		if (evict) {
			ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnEvictionPercent") / 100);
		} else {
			ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnLeaveCityPercent") / 100);
		}
		citizens.remove(ctz);
		//TODO: Remove plots
		for (Plot p : plots) {
			// I sure hope this is the right syntax
			if (p.owners.contains(ctz)) {
				p.removeOwner(ctz);
			}
		}
	}
	
	public void alertCitizens(String alertText) {
		for (Citizen c : citizens) {
			c.alert(alertText);
		}
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
		
		for (Plot p : plots) p.save();
	}
	
	public void reload() {
		CityZen.cityConfig.save();
		CityZen.cityConfig.reload();
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
	
	private void setProperty(String property, Object value) {
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				CityZen.cityConfig.getConfig().set("cities." + identifier + "." + property, value);
				//TODO: Reload properties into memory
				return;
			}
		}
	}
	
	private List<Citizen> getCitizens() {
		List<Citizen> cits = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".citizens")) {
			cits.add(Citizen.getCitizen(UUID.fromString(u)));
		}
		return cits;
	}
	
	private List<Citizen> getDeputies() {
		List<Citizen> deps = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".deputies")) {
			deps.add(Citizen.getCitizen(UUID.fromString(u)));
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
	
	private void wipePlots() {
		for (Plot p : plots) {
			if (p.owners.size() == 0) {
				p.wipe();
			}
		}
	}
	
	private String generateID() {
		String id = "";
		for (int i=0;i<name.length();i++) {
			if (Character.isAlphabetic(name.charAt(i))) id += name.charAt(i);
		}
		
		Boolean idChanged = false;
		while (!idChanged) {
			idChanged = true;
			for (String c : CityZen.cityConfig.getConfig().getConfigurationSection("cities").getKeys(false)) {
				if (id.equalsIgnoreCase(c)) {
					idhj
				}
			}
		}
			
	}
}
