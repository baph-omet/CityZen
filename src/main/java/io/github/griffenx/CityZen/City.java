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
	public String identifier;
	
	private ConfigurationSection properties;
	private FileConfiguration cityConfig = CityZen.cityConfig.getConfig();
	
	public City(String id) {
		properties = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + id);
		identifier = id;
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
	public static City getCity(String id) {
		City cty = null;
		for(City c : City.getCities()) {
			if (c.identifier.equalsIgnoreCase(id)) {
				cty = c;
			}
		}
		return cty;
	}
	
	public static List<City> getCities() {
		List<City> cities = new Vector<City>();
		ConfigurationSection citydata = CityZen.cityconfig.getConfig().getConfigurationSection("cities");
		for (String c : citydata.getKeys(false)) {
			cities.add(new City(c));
		}
		return cities;
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
	
	public void setName(String name) {
		if (name.charAt(0).isAlphabetic() && name.length() < 50) {
			setProperty("name",name);
		}
	}
	
	/**
	 * Simply gets this city's slogan
	 * @return
	 * This city's slogan
	 */
	public String getSlogan() {
		return getProperty("slogan");
	}
	
	public void setSlogan(String slogan) {
		if (slogan.charAt(0).isAlphabetic() && slogan.length() < 100) {
			setProperty("slogan",slogan);
		}
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
	
	public void setColor(char colorCharacter) {
		//TODO: See what type of exception this throws.
		try {
			ChatColor.getByChar(colorCharacter);
			setProperty("color","&" + colorCharacter);
		} catch (Exception e) {}
	}
	
	/**
	 * Gets whether or not this City allows any player to join. Default: false. Defaults if property is set incorrectly.
	 * @return
	 * Whether or not this City allows FreeJoin
	 */
	public Boolean isFreeJoin() {
		return Boolean.valueOf(getProperty("freeJoin"));
	}
	
	public void setFreeJoin(Boolean state) {
		setProperty("freeJoin",state);
	}
	
	/**
	 * Gets whether or not this City allows citizens to place plots. Defaults to false. Defaults if property is set incorrectly.
	 * @return
	 * Whether or not this City allows OpenPlotting
	 */
	public Boolean isOpenPlotting() {
		return Boolean.valueOf(getProperty("openPlotting"));
	}
	
	public void setOpenPlotting(Boolean state) {
		setProperty("openPlotting",state);
	}
	
	/**
	 * Gets whether or not this City is set to wipe plots to natural terrain from seed. Defaults to false (wipes to flatlands).
	 * Defaults if property is not set correctly.
	 * @returns
	 * Whether or not this City does NaturalWipe
	 */
	public Boolean isNaturalWipe() {
		return Boolean.valueOf(getProperty("naturalWipe"));
	}
	
	public void setNaturalWipe(Boolean state) {
		setProperty("naturalWipe",state);
	}
	
	/**
	 * Gets whether or not block restrictions are enabled. Defaults to false. Defaults if property is set incorrectly.
	 * @returns
	 * Whether or not this City uses block restrictions
	 */
	public Boolean isBlockExclusion() {
		return Boolean.valueOf(getProperty("blockBlacklist"));
	}
	
	public void setBlockExclusion(Boolean state) {
		setProperty("blockBlacklist",state);
	}
	
	/**
	 * Gets whether this City is using Whitelist or Blacklist exclusion mode. True = Whitelist, False = Blacklist.
	 * Defaults to false, and always returns false if block exclusion is disabled.
	 * @returns
	 * Whether this City uses Whitelist or Blacklist exclusion mode
	 */
	public Boolean isWhitelisted() {
		return isBlockExclusion() && Boolean.valueOf(getProperty("useBlacklistAsWhitelist"));
	}
	
	public void setWhitelisted(Boolean state) {
		setProperty("useBlacklistAsWhitelist",state);
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
	
	public void setMaxPlotSize(int size) {
		int globalMax = CityZen.getConfig().getInt("maxPlotSize");
		if (size > globalMax) size = globalMax;
		setProperty("maxPlotSize",size);
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
	
	public void setMinPlotSize(int size) {
		int globalMin = CityZen.getConfig().getInt("minPlotSize");
		if (size < globalMin) size = globalMin;
		setProperty("minPlotSize",size);
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
	 * Adds a citizen to this city's list of citizens, then adds it back to the config
	 * @param ctz
	 * The citizen to add to this city
	 */
	public void addCitizen(Citizen ctz) {
		//TODO: Needs to set it as a string list of their UUID's
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
				//TODO: Need to properly set citizens here as well
				setProperty("citizens",ctzs);
				return;
			}
		}
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
	
	public void addDeputy(Citizen deputy) {
		Citizen dep = null;
		for (Citizen c : getCitizens()) {
			if (c.equals(deputy)) {
				dep = deputy;
				break;
			}
		}
		if (dep != null) {
			List<String> deps = new Vector<String>();
			for (Citizen d : getDeputies()) {
				deps.add(d.passport.getUniqueId().toString());
			}
			deps.add(deputy.passport.getUniqueId().toString());
			setProperty("deputies",deps);
		}
	}
	
	public void removeDeputy(Citizen deputy) {
		List<String> deps = new Vector<String>();
		for (Citizen d : getDeputies()) {
			if (!d.equals(deputy)) {
				deps.add(d.passport.getUniqueId().toString());
			}
			setProperty("deputies",deps);
		}
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
	
	public void addPlot(Plot plot) {
		//TODO: check to see if the city does not already contain this plot.
		// It's a weird oblique case, but I think it could still help prevent weird multiples
		List<Plot> plots = getPlots();
		plots.add(plot);
		for (Plot p : plots) p.save();
	}
	
	public void removePlot(Plot plot) {
		List<Plots> plots = getPlots();
		plots.remove(plot);
		for (Plot p : plots) p.save();
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
	
	public void addBlock(Material block) {
		//TODO: Verify this
		List<String> mats = new Vector<String>
		for (Material m : getBlacklist()) {
			mats.add(m.getName());
		}
		mats.add(block.getName());
		setProperty("blacklistedBlocks",mats);
	}
	
	public void removeBlock(Material block) {
		List<String> mats = new Vector<String>
		for (Material m : getBlacklist()) {
			if (m != block) mats.add(m.getName());
		}
		setProperty("blacklistedBlocks",mats);
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
