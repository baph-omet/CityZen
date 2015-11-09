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
	//public Citizen mayor;
	//public List<Citizen> citizens;
	//public List<Citizen> deputies;
	
	//public List<Plot> plots;
	
	//public String name;
	//public String slogan;
	//public ChatColor color;
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
	private FileConfiguration cityConfig = CityZen.cityConfig.getConfig();
	
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
		
		//mayor = Citizen.getCitizen(UUID.fromString(getProperty("mayor")));
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
			if (c.getName().equalsIgnoreCase(name)) {
				cty = c;
			}
		}
		if (cty == null) {
			cty = new City(name);
			CityZen.cities.add(cty);
		}
		return cty;
	}
	
	/**
	 * Gets the Mayor of this City
	 * @return
	 * Citizen who is the Mayor of this city
	 */
	public Citizen getMayor() {
		return Citizen.getCitizen(UUID.fromString(getProperty("mayor")));
	}
	
	/**
	 * Sets the mayor of this city to a new citizen. Overwrites the old mayor.
	 * @param newMayor
	 * The Citizen to set as the City's new Mayor
	 */
	public void setMayor(Citizen newMayor) {
		Citizen ctz = null;
		for (Citizen c : getCitizens()) {
			if (c.passport.getUniqueId().equals(newMayor.passport.getUniqueId())) {
				ctz = newMayor;
			}
		}
		if (ctz == null) {
			addCitizen(newMayor);
		}
		setProperty("mayor",newMayor.passport.getUniqueId().toString());
	}
	
	/**
	 * Simply gets this City's name
	 * @return
	 * This city's name
	 */
	public String getName() {
		return getProperty("name");
	}
	
	/**
	 * Simply gets this city's slogan
	 * @return
	 * This city's slogan
	 */
	public String getSlogan() {
		return getProperty("slogan");
	}
	
	/**
	 * Gets the chat color for this City's name. If the value in config can't be converted to a color, it just uses WHITE instead.
	 * @return
	 * A ChatColor from this City's config
	 */
	public ChatColor getColor() {
		ChatColor color;
		try {
			color = ChatColor.getByChar(getProperty("color"));
		} catch(Exception e) {
			color = ChatColor.WHITE;
		}
		return color;
	}
	
	/**
	 * Gets the maximum size of plots for this City. If the value in config is set to ignore (is less than 0),
	 * or otherwise can't be converted, the server default is used instead.
	 * @return
	 * The maximum plot size for this City
	 */
	public int getMaxPlotSize() {
		int maxPlotSize;
		try {
			maxPlotSize = Integer.valueOf(getProperty("maxPlotSize"));
		} catch (NumberFormatException e) {
			maxPlotSize = CityZen.getPlugin().getConfig().getInt("maxPlotSize");
		}
		if (maxPlotSize > -1) return maxPlotSize;
		else return CityZen.getPlugin().getConfig().getInt("maxPlotSize");
	}
	
	/**
	 * Gets the minimum size of plots for this City. If the value in config is set to ignore (is less than 0),
	 * or otherwise can't be converted, the server default is used instead.
	 * @return
	 * The minimum plot size for this City
	 */
	public int getMinPlotSize() {
		int minPlotSize;
		try {
			minPlotSize = Integer.valueOf(getProperty("minPlotSize"));
		} catch (NumberFormatException e) {
			minPlotSize = CityZen.getPlugin().getConfig().getInt("minPlotSize");
		}
		if (minPlotSize > -1) return minPlotSize;
		else return CityZen.getPlugin().getConfig().getInt("minPlotSize");
	}
	
	/**
	 * Gets a list of citizens from their UUID's in file. If a line cannot be converted, it will be ignored.
	 * @return
	 * A list of Citizens who are members of this City
	 */
	public List<Citizen> getCitizens() {
		List<Citizen> cits = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".citizens")) {
			try {
				cits.add(Citizen.getCitizen(UUID.fromString(u)));
			} catch (IllegalArgumentException e) {}
		}
		return cits;
	}
	
	/**
	 * Gets a list of deputies from their UUID's in file. If a line cannot be converted, it will be ignored.
	 * @return
	 * A list of Citizens who are deputies of this City
	 */
	public List<Citizen> getDeputies() {
		List<Citizen> deps = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".deputies")) {
			try {
				deps.add(Citizen.getCitizen(UUID.fromString(u)));
			} catch (IllegalArgumentException e) {}
		}
		return deps;
	}
	
	/**
	 * Gets a list of this City's plots.
	 * @return
	 * A List of this city's plots.
	 */
	public List<Plot> getPlots() {
		List<Plot> plts = new Vector<Plot>();
		for (String key : CityZen.cityConfig.getConfig().getConfigurationSection("cities." + identifier + ".plots").getKeys(false)) {
			//TODO: Get a plot
			plts.add(new Plot(this, Integer.valueOf(key)));
		} return plts;
	}
	
	/**
	 * Returns a list of materials that are blacklisted in this city. Illegible lines are ignored.
	 * @return
	 * A list of Materials that are blacklisted in this City.
	 */
	public List<Material> getBlacklist() {
		List<Material> mats = new Vector<Material>();
		for (String block : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".blacklistedBlocks")) {
			Material mat = Material.getMaterial(block);
			if (mat != null) {
				mats.add(mat);
			}
		} return mats;
	}
	
	/**
	 * Returns the center point of this city, based on its plots.
	 * @return
	 * A Location that signifies the center of this city
	 */
	public Location getCenter() {
		Location center = null;
		if (getPlots().size() > 0) {
			double maxX = 0,
					minX = 0,
					maxZ = 0,
					minZ = 0;
			for (Plot p : getPlots()) {
				if (p.corner1.getX() > maxX) maxX = p.corner1.getX();
				if (p.corner1.getX() < minX) minX = p.corner1.getX();
				if (p.corner1.getZ() > maxZ) maxZ = p.corner1.getZ();
				if (p.corner1.getZ() < minZ) minZ = p.corner1.getZ();
				
				if (p.corner2.getX() > maxX) maxX = p.corner2.getX();
				if (p.corner2.getX() < minX) minX = p.corner2.getX();
				if (p.corner2.getZ() > maxZ) maxZ = p.corner2.getZ();
				if (p.corner2.getZ() < minZ) minZ = p.corner2.getZ();
			}
			center = new Location(getPlots().get(0).corner1.getWorld(),(maxX + minX) / 2, 0, (maxZ + minZ) / 2);
		}
		return center;
	}
	
	/**
	 * Puts the city's color and name together to get a chat-friendly representation of its name
	 * @return
	 * Color + Name
	 */
	public String getChatName() {
		//TODO: Verify that this works
		return getColor() + getName();
	}

	/**
	 * Gets this city's reputation as the sum of its citizens
	 * @return
	 * This city's reputation
	 */
	public int getReputation() {
		int tot = 0;
		for(Citizen c : getCitizens()) tot += c.reputation;
		return tot;
	}
	
	/**
	 * Adds a citizen to this city's list of citizens, then adds it back to the config
	 * @param ctz
	 * The citizen to add to this city
	 */
	public void addCitizen(Citizen ctz) {
		List<Citizen> ctzs = getCitizens();
		ctzs.add(ctz);
		setProperty("citizens",ctzs);
	}
	
	/**
	 * Remove a citizen from this city, and remove them from ownership of all plots
	 * @param ctz
	 * The citizen to remove
	 */
	public void removeCitizen(Citizen ctz) {
		removeCitizen(ctz, false);
	}
	/**
	 * Evict a citizen from this city, and remove them from ownership of all plots.
	 * Costs them more reputation if this is an eviction.
	 * @param ctz
	 * The citizen to evict
	 * @param evict
	 * Whether not this is an eviction
	 */
	public void removeCitizen(Citizen ctz, Boolean evict) {
		List<Citizen> ctzs = getCitizens();
		for (Citizen c : ctzs) {
			if (c.equals(ctz)) {
				if (evict) {
					ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnEvictionPercent") / 100);
				} else {
					ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnLeaveCityPercent") / 100);
				}
				ctzs.remove(ctz);
				//TODO: Remove plots
				for (Plot p : getPlots()) {
					// I sure hope this is the right syntax
					if (p.owners.contains(ctz)) {
						p.removeOwner(ctz);
					}
				}
				setProperty("citizens",ctzs);
				return;
			}
		}
	}
	
	public void alertCitizens(String alertText) {
		for (Citizen c : getCitizens()) {
			c.alert(alertText);
		}
	}
	
	/*TODO: Determine whether or not this method is necessary
	 I believe that I could probably nix it and just go with saving the city config outright.*/
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
		// Get the property from the city's config
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				String val = CityZen.cityConfig.getConfig().getString(properties.getString(property));
				// If the value is not set for some reason
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
				CityZen.cityConfig.save();
				return;
			}
		}
	}
	
	private void wipePlots() {
		for (Plot p : getPlots()) {
			if (p.owners.size() == 0) {
				p.wipe();
			}
		}
	}
	
	private String generateID() {
		String id = "";
		for (int i = 0; i < getName().length(); i++) {
			if (Character.isAlphabetic(getName().charAt(i))) id += getName().charAt(i);
		}
		
		Boolean idChanged = false;
		while (!idChanged) {
			idChanged = true;
			for (String c : CityZen.cityConfig.getConfig().getConfigurationSection("cities").getKeys(false)) {
				if (id.equalsIgnoreCase(c)) {
					//TODO: If ID already exists, create a new one
				}
			}
		}
			
	}
}
