package io.github.griffenx.CityZen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Citizen implements Reputable {
	private static final Plugin plugin = CityZen.getPlugin();
	private static FileConfiguration citizenConfig = CityZen.citizenConfig.getConfig();
	private ConfigurationSection properties;
	
	@SuppressWarnings(value = { "unused" })
	private static final Logger log = plugin.getLogger();
	
	/**
	 * Initializes a new Citizen object based on their UUID
	 * @param uuid
	 * The UUID to use to identify this Citizen
	 */
	private Citizen(UUID uuid, String name) {
		if (CityZen.citizenConfig.getConfig().getConfigurationSection("citizens." + uuid.toString()) != null) {
			properties = citizenConfig.getConfigurationSection("citizens." + uuid.toString());
		} else {
			citizenConfig.createSection("citizens." + uuid.toString());
			properties = citizenConfig.getConfigurationSection("citizens." + uuid.toString());
			properties.set("name", name);
			properties.set("reputation", plugin.getConfig().getLong("reputation.default"));
			properties.set("maxReputation", plugin.getConfig().getLong("reputation.default"));
			properties.set("maxPlots", 1);
			properties.set("issueDate", new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date()));
			List<String> alerts = new ArrayList<String>();
			alerts.add("Welcome to CityZen! These alerts will inform you about various goings-on with Cities on this server. Type \"/cityzen help\" for more info.");
			properties.set("alerts", alerts);
			properties.set("queuedRewards", new ArrayList<String>());
			CityZen.citizenConfig.save();
		}
	}
	/**
	 * Initializes a new Citizen object based on the corresponding player
	 * @param player
	 * The player whose Citizen record to initialize
	 */
	private Citizen(Player player) {
		this(player.getUniqueId(), player.getName());
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
		ctz = new Citizen(player);
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
				return new Citizen(UUID.fromString(c),name);
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
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
		if (CityZen.citizenConfig.getConfig().getConfigurationSection("citizens." + uuid.toString()) != null) {
			return new Citizen(uuid,player.getName());
		}
		else return null;
	}
	/**
	 * Gets a Citizen from the config based on an OfflinePlayer reference
	 * @param player
	 * The player whose Citizen record should be returned
	 * @return
	 * A Citizen that corresponds to this OfflinePlayer if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(OfflinePlayer player) {
		return getCitizen(player.getUniqueId());
	}
	/**
	 * Gets a Citizen from the config based on a Player reference
	 * @param player
	 * The player whose Citizen record should be returned
	 * @return
	 * A Citizen that corresponds to this Player if one exists, else {@literal null}.
	 */
	public static Citizen getCitizen(Player player) {
		return getCitizen(player.getUniqueId());
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
			return Citizen.getCitizen(((Player) sender).getUniqueId());
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
			citizens.add(new Citizen(UUID.fromString(c),configSection.getString(c + ".name")));
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
			rep = properties.getLong("reputation");
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
			if (rep > getMaxReputation()) setMaxReputation(rep);
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
		setProperty("reputation",rep);
		fixRep();
		if (rep > getMaxReputation()) setMaxReputation(rep);
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
		if (amount > getMaxReputation()) setMaxReputation(amount);
	}
	
	/**
	 * Gets the highest amount of reputation that this Citizen has ever achieved
	 * @return
	 * This Citizen's record highest reputation
	 */
	public long getMaxReputation() {
		long rep;
		try {
			rep = properties.getLong("maxReputation");
		} catch (NumberFormatException e) {
			rep = -1;
		}
		return rep;
	}
	
	private void setMaxReputation(long amount) {
		if (amount < 0) amount = 0;
		for (long i = getMaxReputation() + 1; i <= amount; i++) {
			setProperty("maxReputation",i);
			City city = getAffiliation();
			if (city != null && city.getReputation() > city.getMaxReputation()) city.setMaxReputation(city.getReputation());
			for (Reward r : getRewards()) {
				sendReward(r);
			}
		}
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
	 * Gets the maximum number of plots this Citizen is allowed to own concurrently.
	 * @return
	 * This Citizen's maximum number of plots. If the property cannot be read or is less than zero, returns zero.
	 */
	public int getMaxPlots() {
		try {
			int maxPlots = Integer.parseInt(getProperty("maxPlots"));
			if (maxPlots > 0) return maxPlots;
			else return 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	/**
	 * Sets the maximum number of plots this Citizen can have.
	 * @param amount
	 * The maximum number of plots this Citizen can have. Must be at least 0.
	 */
	public void setMaxPlots(int amount) {
		if (amount < 0) amount = 0;
		setProperty("maxPlots", amount);
	}
	
	/**
	 * Gets the Player object associated with this Citizen
	 * @return
	 * This Citizen's Player
	 */
	public OfflinePlayer getPassport() {
		OfflinePlayer ofp = null;
		
		UUID uuid = UUID.fromString(properties.getName());
		if (uuid != null) ofp = plugin.getServer().getOfflinePlayer(uuid);
		return ofp;
	}
	
	public Player getPlayer() {
		return getPassport().getPlayer();
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
	
	/**
	 * Gets a list of alerts for this Citizen
	 * @return
	 * A List of alert messages
	 */
	public List<String> getAlerts() {
		return CityZen.citizenConfig.getConfig().getStringList(properties.getCurrentPath() + ".alerts");
	}
	
	/**
	 * Add an alert to this Citizen's list of alerts
	 * @param alertText
	 * The text of the alert to add
	 */
	public void addAlert(String alertText) {
		List<String> alerts = getAlerts();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
		alerts.add("[" + sdf.format(new Date()) + "] " + alertText);
		setProperty("alerts",alerts);
	}
	
	/**
	 * Removes all alerts from this Citizen.
	 */
	public void clearAlerts() {
		setProperty("alerts", new Vector<String>());
	}
	
	/**
	 * If the player is online, send them a chat message. Else, send them an alert.
	 * @param message
	 * The message to send to this Citizen
	 */
	public void sendMessage(String message) {
		if (getPassport().isOnline()) {
			getPlayer().sendMessage(message);
		} else {
			addAlert(ChatColor.stripColor(message));
		}
	}
	
	/**
	 * Determines if this Citizen is the Mayor of their City.
	 * @return
	 * True if this Citizen is the Mayor of their City, else false.
	 */
	public Boolean isMayor() {
		if (getAffiliation() != null) return equals(getAffiliation().getMayor());
		else return false;
	}
	
	/**
	 * Determines if this Citizen is a Deputy of their City.
	 * @return
	 * True if this Citizen is a Deputy of their City, else false.
	 */
	public Boolean isDeputy() {
		if (getAffiliation() == null) return false;
		for (Citizen c : getAffiliation().getDeputies()) {
			if (equals(c)) return true;
		}
		return false;
	}
	
	/**
	 * Shortcut for isMayor() || isDeputy()
	 * @return
	 * isMayor() || isDeputy()
	 */
	public boolean isCityOfficial() {
		return isMayor() || isDeputy();
	}
	
	public boolean isCityOfficial(City city) {
		return (isMayor() || isDeputy()) && getAffiliation().equals(city);
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
	
	@Override
	public List<Reward> getRewards() {
		List<Reward> rewards = new Vector<Reward>();
        for (Reward r : Reward.getRewards()) {
            if (r.getType().equals("p")) {
            	if (r.getIntervalRep() > 0 && getMaxReputation() >= r.getInitialRep() && 
            			(getMaxReputation() - r.getInitialRep()) % r.getIntervalRep() == 0) rewards.add(r);
            	else if (getMaxReputation() == r.getInitialRep()) rewards.add(r);
            }
        } return rewards;
	}
	
	public void sendReward(Reward r) {
		if (getPassport().isOnline() && Reward.getEnabledWorlds().contains(getPlayer().getWorld())) {
			CityZen.getPlugin().getServer().dispatchCommand(plugin.getServer().getConsoleSender(), r.getFormattedString(r.getCommand(),this));
			if (r.getIsBroadcast()) {
				CityZen.getPlugin().getServer().broadcastMessage(r.getFormattedString(r.getMessage(), this));
			} else {
				sendMessage(r.getFormattedString(r.getMessage(),this));
			}
		} else queueReward(r);
	}
	
	public void queueReward(Reward r) {
		List<String> rewards = properties.getStringList("queuedRewards");
		rewards.add(Integer.toString(r.getID()));
		setProperty("queuedRewards",rewards);
	}
	
	public void cancelReward(Reward r) {
		setProperty("queuedRewards",properties.getStringList("queuedRewards").remove(r.getID() + ""));
	}
	
	public List<Reward> getQueuedRewards() {
		List<Reward> rewards = new Vector<Reward>();
		for (String s : properties.getStringList("queuedRewards")) {
			try {
				rewards.add(new Reward(Integer.parseInt(s)));
			} catch (Exception e) {
				continue;
			}
		} return rewards;
	}
	
	public void clearQueuedRewards() {
		setProperty("queuedRewards", new ArrayList<String>());
	}

	/**
	 * Determines if two Citizen objects are the same by their UUID
	 * @param citizen
	 * The citizen to compare to this one
	 * @return
	 * Whether or not these Citizens are the same
	 */
	public boolean equals(Citizen ctz) {
		if (this == ctz) return true;
		if (ctz == null) return false;
		return getUUID().equals(ctz.getUUID());
	}
	public boolean equals(Object obj) {
		if (obj.getClass().equals(this.getClass())) return equals((Citizen)obj);
		return false;
	}
	
	private void fixRep() {
		if (getReputation() < 0) setReputation(0);
	}
	
	private String getProperty(String property) {
		return properties.getString(property);
	}
	
	private void setProperty(String property, Object value) {
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				CityZen.citizenConfig.getConfig().set(properties.getCurrentPath() + "." + property,value);
				citizenConfig = CityZen.citizenConfig.getConfig();
			}
		}
	}
}
