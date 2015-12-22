package io.github.griffenx.CityZen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class City {
	private String identifier;
	
	private ConfigurationSection properties;
	
	/**
	 * Loads a new City into memory
	 * @param id
	 * The ID of the City to load
	 */
	private City(String id) {
		properties = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + id);
		identifier = id;
	}
	
	/**
	 * Creates a new City in config based on the name given. Populates the City's properties with default values.
	 * @param name
	 * The name of the new City
	 * @return
	 * A brand new City, or {@literal null} if the City already exists
	 */
	public static City createCity(String name) {
		City newCity = null;
		if (Character.isAlphabetic(name.charAt(0))) {
			for (City c : City.getCities()) {
				if (c.getName().equalsIgnoreCase(name)) return newCity;
			}
			
			String id = generateID(name);
			
			FileConfiguration cityConfig = CityZen.cityConfig.getConfig();
			if (!cityConfig.contains("cities." + id)) {
				cityConfig.createSection("cities." + id);
				cityConfig.createSection("cities." + id + ".plots");
			}
			
			newCity = new City(id);
			FileConfiguration cnfg = CityZen.getPlugin().getConfig();
			ConfigurationSection defaults = cnfg.getConfigurationSection("cityDefaults");
			newCity.setName(name);
			newCity.setColor(defaults.getString("color").charAt(1));
			newCity.setSlogan(defaults.getString("slogan"));
			newCity.setFreeJoin(defaults.getBoolean("freeJoin"));
			newCity.setOpenPlotting(defaults.getBoolean("openPlotting"));
			newCity.setNaturalWipe(defaults.getBoolean("naturalWipe"));
			newCity.setBlockExclusion(defaults.getBoolean("blockBlacklist"));
			newCity.setWhitelisted(defaults.getBoolean("useBlacklistAsWhitelist"));
			newCity.setMaxPlotSize(cnfg.getInt("maxPlotSize"));
			newCity.setMinPlotSize(cnfg.getInt("minPlotSize"));
			newCity.setFoundingDate(new Date());
		}
		return newCity;
	}
	public static City createCity(String name, Citizen founder) {
		City newCity = City.createCity(name);
		newCity.setFounder(founder);
		newCity.setMayor(founder);
		newCity.addCitizen(founder);
		return newCity;
	}
	
	/**
	 * Gets all Cities from the config and returns them as a list
	 * @return
	 * A list of all defined Cities in the config
	 */
	public static List<City> getCities() {
		List<City> cities = new Vector<City>();
		ConfigurationSection citydata = CityZen.cityConfig.getConfig().getConfigurationSection("cities");
		for (String c : citydata.getKeys(false)) {
			cities.add(new City(c));
		}
		return cities;
	}
	
	public static City getCity(String name) {
		for (City c : getCities()) {
			if (c.getName() == name) return c;
		}
		return null;
	}
	
	/**
	 * Deletes this city from config
	 */
	public void delete() {
		CityZen.cityConfig.getConfig().set("cities." + identifier, null);
		CityZen.citizenConfig.save();
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
	 * Sets the name of this city, does not change its ID
	 * @param name
	 * The new name to assign to this city
	 */
	public void setName(String name) {
		if (Character.isAlphabetic(name.charAt(0)) && name.length() < 50) {
			setProperty("name",name);
		}
	}
	
	/**
	 * Gets this City's identifier, making it a read-only property
	 * @return
	 * This City's identifier
	 */
	public String getIdentifier() {
		return identifier;
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
	 * Gets the Citizen who created this City
	 * @return
	 * The Citizen who founded this City
	 */
	public Citizen getFounder() {
		return Citizen.getCitizen(UUID.fromString(getProperty("founder")));
	}
	
	private void setFounder(Citizen founder) {
		setProperty("founder",founder.getUUID().toString());
	}
	
	/**
	 * Gets the date that this City's record was created as a Date
	 * @return
	 * Date representing when this record was issued
	 */
	public Date getFoundingDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.US);
		try {
			return sdf.parse(getProperty("foundingDate"));
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	 * Gets the date that this City's record was created as a formatted String
	 * @param dateFormat
	 * A format string used to format the date.
	 * @return
	 * Date representing when this record was issued, as a string
	 */
	public String getFoundingDate(String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,Locale.US);
		return sdf.format(getFoundingDate());
	}
	
	private void setFoundingDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.US);
		setProperty("foundingDate",sdf.format(date));		
	}
	
	/**
	 * Sets the mayor of this city to a new citizen. Overwrites the old mayor.
	 * @param newMayor
	 * The Citizen to set as the City's new Mayor
	 */
	public void setMayor(Citizen newMayor) {
		Citizen ctz = null;
		for (Citizen c : getCitizens()) {
			if (c.getPassport().getUniqueId().equals(newMayor.getPassport().getUniqueId())) {
				ctz = newMayor;
			}
		}
		if (ctz == null) {
			addCitizen(newMayor);
		}
		setProperty("mayor",newMayor.getPassport().getUniqueId().toString());
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
	 * Sets a new slogan for this city
	 * @param slogan
	 * The new slogan for this city
	 */
	public void setSlogan(String slogan) {
		if (Character.isAlphabetic(slogan.charAt(0)) && slogan.length() < 100) {
			setProperty("slogan",slogan);
		}
	}
	
	/**
	 * Gets the world in which this City resides based on its name in file
	 * @return
	 * The World in which this City resides
	 */
	public World getWorld() {
		return CityZen.getPlugin().getServer().getWorld(getProperty("world"));
	}
	
	/**
	 * Sets the World in which this City resides, but only if it does not have any plots. Should basically only be used in city creation.
	 * @param world
	 * The World in which this City resides
	 */
	public void setWorld(World world) {
		if (getPlots().size() > 0) {
			setProperty("world",world.getName());
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
			color = ChatColor.getByChar(getProperty("color").charAt(1));
		} catch(Exception e) {
			color = ChatColor.WHITE;
		}
		return color;
	}
	
	/**
	 * Sets a new display chat color for this city. If a color code can't be gleaned from the character, this command does nothing.
	 * @param colorCharacter
	 * The color code character to assign to this city.
	 */
	public void setColor(char colorCharacter) {
		try {
			ChatColor color = ChatColor.getByChar(colorCharacter);
			if (color != null) setProperty("color","&" + colorCharacter);
		} catch (ClassCastException e) {}
	}
	
	/**
	 * Gets whether or not this City allows any player to join. Default: false. Defaults if property is set incorrectly.
	 * @return
	 * Whether or not this City allows FreeJoin
	 */
	public boolean isFreeJoin() {
		return Boolean.valueOf(getProperty("freeJoin"));
	}
	
	/**
	 * Sets whether or not any player can join this City
	 * @param state
	 * Whether or not freeJoin is allowed
	 */
	public void setFreeJoin(Boolean state) {
		setProperty("freeJoin",state);
	}
	
	/**
	 * Gets whether or not this City allows citizens to place plots. Defaults to false. Defaults if property is set incorrectly.
	 * @return
	 * Whether or not this City allows OpenPlotting
	 */
	public boolean isOpenPlotting() {
		return Boolean.valueOf(getProperty("openPlotting"));
	}
	
	/**
	 * Sets whether or not citizens of this city can create their own plots
	 * @param state
	 * Whether openPlotting is allowed
	 */
	public void setOpenPlotting(Boolean state) {
		setProperty("openPlotting",state);
	}
	
	/**
	 * Gets whether or not this City is set to wipe plots to natural terrain from seed. Defaults to false (wipes to flatlands).
	 * Defaults if property is not set correctly.
	 * @returns
	 * Whether or not this City does NaturalWipe
	 */
	public boolean isNaturalWipe() {
		return Boolean.valueOf(getProperty("naturalWipe"));
	}
	
	/**
	 * Sets whether plots are wiped to natural terrain (from seed) or to flatlands.
	 * @param state
	 * Whether naturalWipe is enabled
	 */
	public void setNaturalWipe(Boolean state) {
		setProperty("naturalWipe",state);
	}
	
	/**
	 * Gets whether or not block restrictions are enabled. Defaults to false. Defaults if property is set incorrectly.
	 * @returns
	 * Whether or not this City uses block restrictions
	 */
	public boolean isBlockExclusion() {
		return Boolean.valueOf(getProperty("blockBlacklist"));
	}
	
	/**
	 * Whether block Blacklisting/Whitelisting is enabled
	 * @param state
	 * Whether or not blockExclusion is allowed
	 */
	public void setBlockExclusion(Boolean state) {
		setProperty("blockBlacklist",state);
	}
	
	/**
	 * Gets whether this City is using Whitelist or Blacklist exclusion mode. True = Whitelist, False = Blacklist.
	 * Defaults to false, and always returns false if block exclusion is disabled.
	 * @returns
	 * Whether this City uses Whitelist or Blacklist exclusion mode
	 */
	public boolean isWhitelisted() {
		return isBlockExclusion() && Boolean.valueOf(getProperty("useBlacklistAsWhitelist"));
	}
	
	/**
	 * Sets whether the exclusion mode of this city is Blacklist or Whitelist
	 * @param state
	 * True for Whitelist, False for Blacklist
	 */
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
	
	/**
	 * Sets the maximum plot size of this city. Must not be greater than the maximum size defined in config.yml
	 * @param size
	 * The new maximum plot size for this city
	 */
	public void setMaxPlotSize(int size) {
		int globalMax = CityZen.getPlugin().getConfig().getInt("maxPlotSize");
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
	
	/**
	 * Sets the minimum plot size of this city. Must not be less than the minimum size defined in config.yml
	 * @param size
	 * The new minimum plot size for this city
	 */
	public void setMinPlotSize(int size) {
		int globalMin = CityZen.getPlugin().getConfig().getInt("minPlotSize");
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
		List<String> ctzs = new Vector<String>();
		for (Citizen c : getCitizens()) {
			ctzs.add(c.getPassport().getUniqueId().toString());
		}
		ctzs.add(ctz.getPassport().getUniqueId().toString());
		setProperty("citizens",ctzs);
		long rep = 0;
		rep = CityZen.getPlugin().getConfig().getLong("reputation.gainedOnJoinCity");
		ctz.addReputation(rep);
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
	 * Costs them more reputation if this is an eviction. Fails if the Citizen is the Mayor.
	 * @param ctz
	 * The citizen to evict
	 * @param evict
	 * Whether not this is an eviction
	 */
	public void removeCitizen(Citizen ctz, Boolean evict) {
		if (!ctz.isMayor()) {
			removeDeputy(ctz);
			List<String> ctzs = new Vector<String>();
			int reduction = 0;
			for (Citizen c : getCitizens()) {
				if (c.equals(ctz)) {
					if (evict) {
						reduction = (int) (ctz.getReputation() * CityZen.getPlugin().getConfig().getInt("reputation.lostOnEvictionPercent") / 100);
						ban(ctz);
					} else {
						reduction = (int) (ctz.getReputation() * CityZen.getPlugin().getConfig().getInt("reputation.lostOnLeaveCityPercent") / 100);
					}
					ctz.subReputation(reduction);
					for (Plot p : getPlots()) {
						if (p.getOwners().contains(ctz)) {
							p.removeOwner(ctz);
						}
					}
				}
				else {
					ctzs.add(c.getPassport().getUniqueId().toString());
				}
			}
			setProperty("citizens",ctzs);
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
	
	/**
	 * Adds a Citizen to the list of Deputies for this City. Ignores players who are not a Citizen of the City or don't exist.
	 * @param deputy
	 * The player to add as a new Deputy
	 */
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
				deps.add(d.getPassport().getUniqueId().toString());
			}
			deps.add(deputy.getPassport().getUniqueId().toString());
			setProperty("deputies",deps);
		}
	}
	
	/**
	 * Removes a player from the list of Deputies for this City
	 * @param deputy
	 * The player to remove from the Deputies list
	 */
	public void removeDeputy(Citizen deputy) {
		List<String> deps = new Vector<String>();
		for (Citizen d : getDeputies()) {
			if (!d.equals(deputy)) {
				deps.add(d.getPassport().getUniqueId().toString());
			}
			setProperty("deputies",deps);
		}
	}
	
	/**
	 * Gets a list of Citizens who have requested to join this City
	 * @return
	 * A List of Citizens who have requested to join this City. Returns null if freeJoin is false.
	 */
	public List<Citizen> getWaitlist() {
		if (isFreeJoin()) return null;
		List<Citizen> citizens = new Vector<Citizen>();
		for (String u : properties.getStringList("waitlist")) {
			citizens.add(Citizen.getCitizen(UUID.fromString(u)));
		}
		return citizens;
	}
	
	/**
	 * Adds a Citizen to this City's waitlist if they are not already on it.
	 * @param citizen
	 * The Citizen to add
	 */
	public void addWaitlist(Citizen citizen) {
		List<String> citizens = new Vector<String>();
		for (Citizen c : getWaitlist()) {
			if (c.equals(citizen)) return;
			citizens.add(c.getUUID().toString());
		}
		setProperty("waitlist", citizens);
	}
	
	/**
	 * Removes a Citizen from this City's waitlist
	 * @param citizen
	 * The Citizen to remove
	 */
	public void removeWaitlist(Citizen citizen) {
		List<String> citizens = new Vector<String>();
		for (Citizen c : getWaitlist()) if (!c.equals(citizen)) citizens.add(c.getUUID().toString());
		setProperty("waitlist", citizens);
	}
	
	/**
	 * Completely empties out this City's waitlist
	 */
	public void clearWaitlist() {
		setProperty("waitlist", new Vector<String>());
	}
	
	/**
	 * Gets a List of all Citizens who are on this City's banlist
	 * @return
	 * A List of all Citizens who are on this City's banlist
	 */
	public List<Citizen> getBanlist() {
		List<Citizen> banned = new Vector<Citizen>();
		for (String key : properties.getConfigurationSection("banlist").getKeys(false)) {
			Citizen c = null;
			try {
				c = Citizen.getCitizen(UUID.fromString(key));
			} catch (IllegalArgumentException e) {
				CityZen.getPlugin().getLogger().log(Level.INFO,"Unable to parse player in banlist for city " + getName() + ": " + key);
			}
			if (c != null) banned.add(c);
		}
		return banned;
	}
	
	/**
	 * Adds a Citizen to this City's banlist. If that Citizen is already banned, this method does nothing.
	 * @param citizen
	 * The Citizen to ban
	 */
	public void ban(Citizen citizen) {
		List<Citizen> banlist = getBanlist();
		if (!banlist.contains(citizen)) banlist.add(citizen);
		List<String> newBanlist = new Vector<String>();
		for (Citizen c : banlist) newBanlist.add(c.getUUID().toString());
		setProperty("banlist", newBanlist);
		
	}
	
	/**
	 * Removes a Citizen to this City's banlist. If that player is not banned, this method does nothing.
	 * @param citizen
	 * The Citizen to pardon.
	 */
	public void pardon(Citizen citizen) {
		if (getBanlist().contains(citizen)) {
			List<String> banlist = new Vector<String>();
			for (Citizen c : getBanlist()) if (!c.equals(citizen)) banlist.add(c.getUUID().toString());
			setProperty("banlist", banlist);
		}
	}
	
	/**
	 * Empties out this City's banlist. Doesn't give a damn whether it exists or not.
	 */
	public void clearBanlist() {
		setProperty("banlist", null);
	}
	
	/**
	 * Determines whether or not this Citizen is banned from this City.
	 * @param citizen
	 * The Citizen to check.
	 * @return
	 * True if this player is banned, else false.
	 */
	public boolean isBanned(Citizen citizen) {
		return getBanlist().contains(citizen);
	}
	
	/**
	 * Checks to see if the Citizen is on this City's waitlist
	 * @param citizen
	 * The Citizen to check
	 * @return
	 * True if the Citizen is on this City's waitlist, else false
	 */
	public boolean isInWaitlist(Citizen citizen) {
		for (Citizen c : getWaitlist()) {
			if (citizen.equals(c)) return true;
		}
		return false;
	}
	
	/**
	 * Gets a list of this City's plots.
	 * @return
	 * A List of this city's plots.
	 */
	public List<Plot> getPlots() {
		List<Plot> plts = new Vector<Plot>();
		for (String key : CityZen.cityConfig.getConfig().getConfigurationSection("cities." + identifier + ".plots").getKeys(false)) {
			plts.add(Plot.getPlot(this,Integer.valueOf(key)));
		} return plts;
	}
	
	/**
	 * Add a new Plot to this city using plot components. Fails if the created plot would overlap an existing plot.
	 * @param corner1
	 * The first corner of the plot
	 * @param corner2
	 * The second corner of the plot
	 * @param creator
	 * The Citizen who created this plot
	 */
	public void addPlot(Location corner1, Location corner2, Citizen creator) {
		Plot.createPlot(this, corner1, corner2, creator);
	}
	
	/**
	 * Remove a plot from this city, wiping it entirely.
	 * @param plot
	 * The plot to wipe and remove from this city.
	 */
	public void removePlot(Plot plot) {
		plot.wipe();
		plot.delete();
	}
	
	/**
	 * Returns whether or not a set of X,Z coordinates are within the bounds of this City.
	 * @param x
	 * The X coordinate to check
	 * @param z
	 * The Z coordinate to check
	 * @return
	 * True if this coordinate is inside a plot or plot buffer for this City
	 */
	public boolean isInCity(double x, double z) {
		for (Plot p : getPlots()) {
			if (p.isInPlot(x, z) || p.isInBuffer(x, z)) return true;
		}
		return false;
	}
	/**
	 * Returns whether or not a Location object is within the bounds of this City.
	 * @param location
	 * The location to check.
	 * @return
	 * True if this location is inside a plot or plot buffer for this City
	 */
	public boolean isInCity(Location location) {
		return isInCity(location.getX(),location.getZ());
	}
	/**
	 * Returns whether or not a player is within the bounds of this City.
	 * @param player
	 * The player whose location should be checked
	 * @return
	 * True if this player is inside a plot or plot buffer for this City.
	 */
	public boolean isInCity(Player player) {
		return isInCity(player.getLocation());
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
	 * Add a block type to this city's exclusion list
	 * @param block
	 * The material type to add to the exclusion list
	 */
	public void addBlock(Material block) {
		List<String> mats = new Vector<String>();
		for (Material m : getBlacklist()) {
			mats.add(m.toString());
		}
		mats.add(block.toString());
		setProperty("blacklistedBlocks",mats);
	}
	
	/**
	 * Removes a block type from this city's exclusion list
	 * @param block
	 * The material type to remove from this city's exclusion list
	 */
	public void removeBlock(Material block) {
		List<String> mats = new Vector<String>();
		for (Material m : getBlacklist()) {
			if (m != block) mats.add(m.toString());
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
				if (p.getCorner1().getX() > maxX) maxX = p.getCorner1().getX();
				if (p.getCorner1().getX() < minX) minX = p.getCorner1().getX();
				if (p.getCorner1().getZ() > maxZ) maxZ = p.getCorner1().getZ();
				if (p.getCorner1().getZ() < minZ) minZ = p.getCorner1().getZ();
				
				if (p.getCorner2().getX() > maxX) maxX = p.getCorner2().getX();
				if (p.getCorner2().getX() < minX) minX = p.getCorner2().getX();
				if (p.getCorner2().getZ() > maxZ) maxZ = p.getCorner2().getZ();
				if (p.getCorner2().getZ() < minZ) minZ = p.getCorner2().getZ();
			}
			center = new Location(getPlots().get(0).getCorner1().getWorld(),(maxX + minX) / 2, 0, (maxZ + minZ) / 2);
		}
		return center;
	}
	
	/**
	 * Puts the city's color and name together to get a chat-friendly representation of its name
	 * @return
	 * Color + Name
	 */
	public String getChatName() {
		return getColor() + getName() + ChatColor.RESET;
	}

	/**
	 * Gets this city's reputation as the sum of its citizens
	 * @return
	 * This city's reputation
	 */
	public long getReputation() {
		long tot = 0;
		for(Citizen c : getCitizens()) tot += c.getReputation();
		return tot;
	}
	
	/**
	 * Send an Alert message to all Citizens of this City
	 * @param alertText
	 * The message of the Alert
	 */
	public void alertCitizens(String alertText) {
		for (Citizen c : getCitizens()) {
			c.addAlert(alertText);
		}
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		City city = (City)obj;
		return identifier.equalsIgnoreCase(city.getIdentifier());
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
		CityZen.cityConfig.getConfig().set("cities." + identifier + "." + property, value);
	}
	
	private static String generateID(String name) {
		String id = "";
		for (int i = 0; i < name.length(); i++) {
			if (Character.isAlphabetic(name.charAt(i))) id += name.charAt(i);
		}
		
		Boolean idChanged = false;
		int modifier = 0;
		Set<String> keys = CityZen.cityConfig.getConfig().getConfigurationSection("cities").getKeys(false);
		do {
			if (keys.contains(id + (modifier != 0 ? modifier : "")) && !(new City(id + (modifier != 0 ? modifier : "")).getName().equalsIgnoreCase(name))) {
				modifier++;
			}
			else {
				if (modifier > 0) id += modifier;
				idChanged = true;
			}
		}
		while (modifier > 0 && !idChanged);
		return id;
	}
}
