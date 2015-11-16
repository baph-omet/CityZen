package io.github.griffenx.CityZen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Citizen {
	private static final Plugin plugin = CityZen.getPlugin();
	private static FileConfiguration citizenConfig = CityZen.citizenConfig.getConfig();
	private ConfigurationSection properties;
	
	/**
	 * Initializes a new Citizen object based on their UUID
	 * @param uuid
	 * The UUID to use to identify this Citizen
	 */
	private Citizen(UUID uuid) {
		this((Player) CityZen.getPlugin().getServer().getOfflinePlayer(uuid));
	}
	/**
	 * Initializes a new Citizen object based on the corresponding player
	 * @param player
	 * The player whose Citizen record to initialize
	 */
	private Citizen(Player player) {
		properties = CityZen.citizenConfig.getConfig().getConfigurationSection("citizens." + player.getUniqueId().toString());
	}
	
	/**
	 * Creates and initializes a new Citizen.
	 * @param player The player for whom to create a Citizen record
	 * @returns
	 * A new Citizen record for this player. Returns null if the specified player already has a Citizen record.
	 */
	public static Citizen createCitizen(Player player) {
		Citizen ctz = null;
		for (Citizen c : getCitizens()) {
			if (player.getUniqueId().equals(c.getPassport().getUniqueId())) return ctz;
		}
		CityZen.citizenConfig.getConfig().createSection("citizens." + player.getUniqueId().toString());
		ctz = new Citizen(player);
		long defaultRep = CityZen.getPlugin().getConfig().getLong("reputation.default");
		ctz.setReputation(defaultRep);
		ctz.setMaxReputation(defaultRep);
		ctz.setIssueDate(new Date());
		return ctz;
	}
	
	/**
	 * Deletes the record for this Citizen. Should not be used lightly.
	 * @param citizen
	 * The Citizen to delete
	 */
	public static void deleteCitizen(Citizen citizen) {
		if (!citizen.isMayor()) {
			citizen.getAffiliation().removeCitizen(citizen);
			CityZen.citizenConfig.getConfig().set("citizens." + citizen.getUUID().toString(), null);
		}
	}
	
	/**
	 * Gets a Citizen from the config based on their name
	 * @param name
	 * The name of the Citizen to get based on their last known username.
	 * @return
	 * A Citizen that corresponds to this name, if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(String name) {
		ConfigurationSection config = citizenConfig.getConfigurationSection("citizens");
		for (String c : config.getKeys(false)) {
			if (citizenConfig.getString("citizens." + c + ".name").equalsIgnoreCase(name)) {
				return new Citizen(UUID.fromString(c));
			}
		}
		return null;
	}
	/**
	 * Gets a Citizen from the config based on their UUID
	 * @param uuid
	 * The UUID of the Citizen to get
	 * @return
	 * A Citizen that corresponds to this UUID if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(UUID uuid) {
		return getCitizen(plugin.getServer().getOfflinePlayer(uuid));
	}
	/**
	 * Gets a Citizen from the config based on an OfflinePlayer reference
	 * @param player
	 * The player whose Citizen record should be returned
	 * @return
	 * A Citizen that corresponds to this OfflinePlayer if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(OfflinePlayer player) {
		return getCitizen((Player) player);
	}
	/**
	 * Gets a Citizen from the config based on a Player reference
	 * @param player
	 * The player whose Citizen record should be returned
	 * @return
	 * A Citizen that corresponds to this Player if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(Player player) {
		for (Citizen c : getCitizens()) {
			if (c.getPassport().equals(player)) return c;
		}
		return null;
	}
	/**
	 * Gets a Citizen from the config based on a CommandSender reference
	 * @param player
	 * The player whose Citizen record should be returned
	 * @return
	 * A Citizen that corresponds to this CommandSender if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(CommandSender sender) {
		if (sender instanceof Player) {
			return Citizen.getCitizen((Player) sender);
		}
		return null;
	}
	
	/**
	 * Gets a list of all Citizens on file.
	 * @returns
	 * A List containing all known Citizens
	 */
	public static List<Citizen> getCitizens() {
		List<Citizen> citizens = new Vector<Citizen>();
		ConfigurationSection configSection = CityZen.citizenConfig.getConfig().getConfigurationSection("citizens");
		for (String c : configSection.getKeys(false)) {
			citizens.add(new Citizen(UUID.fromString(c)));
		}
		return citizens;
	}
	
	/**
	 * Shortcut method to get this Citizen's UUID
	 * @return
	 * This Citizen's UUID
	 */
	public UUID getUUID() {
		return getPassport().getUniqueId();
	}
	
	/**
	 * Returns the username of this player from file. Used for display purposes.
	 * @returns
	 * The username of this Citizen
	 */
	public String getName() {
		return getProperty("name");
	}
	
	/**
	 * Set the name of this player on file. Really only used for display purposes, and should be auto-updated when the player logs in.
	 * @param newName The new name to assign to this Citizen
	 */
	public void setName(String newName) {
		setProperty("name",newName);
	}

	/**
	 * Gets the reputation of this player.
	 * If the reputation field in the config is not set properly, it sets it to -1.
	 * This is essentially a check for improper config.
	 * @returns
	 * The reputation of this player
	 */
	public long getReputation() {
		long rep;
		try {
			rep = Long.valueOf(getProperty("reputation"));
		} catch (NumberFormatException e) {
			rep = -1;
		}
		return rep;
	}
	
	/**
	 * Increases the reputation of this player. Negative amounts are allowed.
	 * Does not let the player's reputation drop below 0 or rise above the maximum value in config
	 * @param amount
	 * The amount by which to increase this player's reputation
	 */
	public void addReputation(long amount) {
		long globalMax = CityZen.getPlugin().getConfig().getLong("reputation.maximum");
		if (amount > (globalMax - getReputation())) setReputation(Long.MAX_VALUE - 1);
		else {
			long rep = getReputation() + amount;
			fixRep();
			setReputation(rep);
		}
	}
	
	/**
	 * Subtract reputation from this player. Negative amounts are allowed.
	 * Will not let the player's reputation drop below 0 or rise above the maximum value in config
	 * @param amount
	 * The amount by which to decrease this player's reputation.
	 */
	public void subReputation(long amount) {
		long globalMax = CityZen.getPlugin().getConfig().getLong("reputation.maximum");
		long rep;
		if (amount < 0 && (Math.abs(amount) > (globalMax - getReputation()))) rep = globalMax;
		else rep = getReputation() - amount;
		fixRep();
		setProperty("reputation",rep);
	}
	
	/**
	 * Sets the reputation of the player to a certain amount. 
	 * Must be between 0 and the maximum value in config
	 * @param amount
	 * The amount of reputation to set to this player. If outside of the allowed bounds, rep is set to 0
	 */
	public void setReputation(long amount) {
		long globalMax = CityZen.getPlugin().getConfig().getLong("reputation.maximum");
		if (amount >= 0 && amount < globalMax) setProperty("reputation",amount);
		else setProperty("reputation",0);
	}
	
	/**
	 * Gets the highest amount of reputation that this Citizen has ever achieved
	 * @return
	 * This Citizen's record highest reputation
	 */
	public long getMaxReputation() {
		long rep;
		try {
			rep = Long.valueOf(getProperty("maxReputation"));
		} catch (NumberFormatException e) {
			rep = -1;
		}
		return rep;
	}
	
	private void setMaxReputation(long amount) {
	}
	
	/**
	 * Gets a City object of the city with which this player is affiliated
	 * @returns
	 * This Citizen's city. If this Citizen does not belong to a City, returns {@literal null}.
	 */
	public City getAffiliation() {
		for (City c : City.getCities()) {
			for (Citizen z : c.getCitizens()) {
				if (equals(z)) return c;
			}
		}
		return null;
	}
	
	/** 
	 * Gets a List of Plots owned by this Citizen
	 * @return
	 * List of Plots owned by this Citizen
	 */
	public List<Plot> getPlots() {
		List<Plot> plots = new Vector<Plot>();
		for (Plot p : getAffiliation().getPlots()) {
			for (Citizen c : p.getOwners()) {
				if (equals(c)) {
					plots.add(p);
					break;
				}
			}
		}
		return plots;
	}
	
	/**
	 * Gets the Player object associated with this Citizen
	 * @return
	 * This Citizen's Player
	 */
	public Player getPassport() {
		String foundUUID = null;
		Player passport = null;
		ConfigurationSection cits = CityZen.citizenConfig.getConfig().getConfigurationSection("citizens");
		for (String key : cits.getKeys(false)) {
			if (cits.getString(key + ".name").equalsIgnoreCase(CityZen.getPlugin().getServer().getOfflinePlayer(UUID.fromString(key)).getName())) {
				foundUUID = key;
				break;
			}
		}
		if (foundUUID != null) {
			passport = (Player) CityZen.getPlugin().getServer().getOfflinePlayer(UUID.fromString(foundUUID));
		}
		return passport;
	}
	
	/**
	 * Gets the date that this Citizen's record was created as a Date
	 * @return
	 * Date representing when this record was issued
	 */
	public Date getIssueDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.US);
		try {
			return sdf.parse(getProperty("issueDate"));
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	 * Gets the date that this Citizen's record was created as a formatted String
	 * @param dateFormat
	 * A format string used to format the date.
	 * @return
	 * Date representing when this record was issued, as a string
	 */
	public String getIssueDate(String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,Locale.US);
		return sdf.format(getIssueDate());
	}
	
	private void setIssueDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.US);
		setProperty("issueDate",sdf.format(date));		
	}
	
	/**
	 * Gets a list of alerts for this Citizen
	 * @return
	 * A List of alert messages
	 */
	public List<String> getAlerts() {
		return CityZen.citizenConfig.getConfig().getStringList("citizens." + getPassport().getUniqueId().toString() + ".alerts");
	}
	
	/**
	 * Add an alert to this Citizen's list of alerts
	 * @param alertText
	 * The text of the alert to add
	 */
	public void addAlert(String alertText) {
		List<String> alerts = new Vector<String>();
		for (String a : getAlerts()) {
			alerts.add(a);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.US);
		alerts.add(sdf.format(new Date()) + alertText);
		setProperty("alerts",alerts);
	}
	
	/**
	 * Determines if this Citizen is the Mayor of their City.
	 * @return
	 * True if this Citizen is the Mayor of their City, else false.
	 */
	public Boolean isMayor() {
		return equals(getAffiliation().getMayor());
	}
	
	/**
	 * Determines if this Citizen is a Deputy of their City.
	 * @return
	 * True if this Citizen is a Deputy of their City, else false.
	 */
	public Boolean isDeputy() {
		for (Citizen c : getAffiliation().getDeputies()) {
			if (equals(c)) return true;
		}
		return false;
	}
	
	/**
	 * Determines whether or not this Citizen is a member of any City's waitlist
	 * @return
	 * True if this Citizen is waitlisted anywhere, else false
	 */
	public Boolean isWaitlisted() {
		return getWaitlistedCity() != null;
	}
	
	/**
	 * Finds a City in which this Citizen is waitlisted
	 * @return
 * The City in which this Citizen is waitlisted, or null if this Citizen is not waitlisted anywhere.
	 */
	public City getWaitlistedCity() {
		for (City c : City.getCities()) {
			if (c.isInWaitlist(this)) return c;
		} return null;
	}
	
	/**
	 * Determines if two Citizen objects are the same by their UUID
	 * @param citizen
	 * The citizen to compare to this one
	 * @return
	 * Whether or not these Citizens are the same
	 */
	public Boolean equals(Citizen citizen) {
		return getPassport().getUniqueId().equals(citizen.getPassport().getUniqueId());
	}
	
	private void fixRep() {
		if (getReputation() < 0) setReputation(0);
	}
	
	private String getProperty(String property) {
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				return CityZen.citizenConfig.getConfig().getString(properties.getString(property));
				// No need to set defaults, as there are no defaults for citizens
			}
		}
		return "";
	}
	
	private void setProperty(String property, Object value) {
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				CityZen.citizenConfig.getConfig().set("citizens." + getPassport().getUniqueId().toString() + property,value);
				CityZen.citizenConfig.save();
				citizenConfig = CityZen.citizenConfig.getConfig();
			}
		}
	}
}
