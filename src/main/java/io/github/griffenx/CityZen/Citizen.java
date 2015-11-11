package io.github.griffenx.CityZen;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Citizen {
	private ConfigurationSection properties;
	
	public Citizen(UUID uuid) {
		this((Player) CityZen.getPlugin().getServer().getOfflinePlayer(uuid));
	}
	public Citizen(Player player) {
		FileConfiguration citizenConfig = CityZen.citizenConfig.getConfig();
		if (!citizenConfig.contains("citizens." + player.getUniqueId().toString())) {
			citizenConfig.createSection("citizens." + player.getUniqueId().toString()));
		}
		properties = citizenConfig.getConfigurationSection("citizens." + player.getUniqueId().toString());
		
		//alerts = properties.getStringList("alerts");
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
		ctz.setReputation(CityZen.getPlugin().getConfig().getInt("reputation.default");
		return ctz;
	}
	
	public static void deleteCitizen(Citizen citizen) {
		//TODO: Remove their configuration section
		//All city/plot related stuff should be handled by the city
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
	public int getReputation() {
		int rep;
		try {
			rep = Integer.valueOf(getProperty("reputation"));
		} catch (NumberFormatException e) {
			rep = -1;
		}
		return rep;
	}
	
	/**
	 * Increases the reptuation of this player. Negative amounts are allowed.
	 * Does not let the player's reputation drop below 0 or rise above Integer.MAX_VALUE
	 * @param amount
	 * The amount by which to increase this player's reputation
	 */
	public void addReputation(int amount) {
		if (amount > (Integer.MAX_VALUE - getReputation()) setReputation(Integer.MAX_VALUE - 1);
		else {
			int rep = getReputation() + amount;
			fixRep();
			setReputation(rep);
		}
	}
	
	/**
	 * Subtract reputation from this player. Negative amounts are allowed.
	 * Will not let the player's reputation drop below 0 or rise above Integer.MAX_VALUE
	 * @param amount
	 * The amount by which to decrease this player's reputation.
	 */
	public void subReputation(int amount) {
		int rep;
		if (amount < 0 && (Math.Abs(amount) > (Integer.MAX_VALUE - getReputation()))) rep = Integer.MAX_VALUE - 1;
		else rep = getReputation() - amount;
		fixRep();
		setProperty("reputation",rep);
	}
	
	/**
	 * Sets the reputation of the player to a certain amount. 
	 * Must be between 0 and the maximum value of a 32-bit signed Integer (a little over 2 billion)
	 * @param amount
	 * The amount of reputation to set to this player
	 */
	public void setReputation(int amount) {
		if (amount >= 0 && amount < Integer.MAX_VALUE) setProperty("reputation",amount);
		else setProperty("reputation",0);
	}
	
	/**
	 * Gets a City object of the city with which this player is affiliated
	 * @returns
	 * This player's city
	 */
	public City getAffiliation() {
		City aff = null;
		String affname = getProperty("affiliation");
		if (affname.length() > 0) {
			aff = new City(affname);
		}
		return aff;
	}
	
	/**
	 * Used to set which city this Citizen is affiliated with. Mostly for internal use.
	 * DO NOT USE to add a player to a city.
	 * You should instead call addCitizen() on the city.
	 * @param city
	 * The City to set as this player's affiliation
	 */
	public void setAffiliation(City city) {
		if (city != null) setProperty("affiliation",city.identifier);
		else setProperty("affiliation",null);
	}
	
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
	
	public List<String> getAlerts() {
		return CityZen.citizenConfig.getConfig().getStringList("citizens." + getPassport().getUniqueId().toString() + ".alerts");
	}
	
	public void addAlert(String alertText) {
		//TODO: Date of today + alertText, then write back to citizen file
		// SimpleDateFormat?
		//alerts.add( + alertText);
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
				//TODO: I'm not sure this is correct
				// Check this in the city class as well
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
			}
		}
	}
}
