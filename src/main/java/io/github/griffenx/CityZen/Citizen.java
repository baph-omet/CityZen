package io.github.griffenx.CityZen;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Citizen {
	
	public int reputation;
	public City affiliation;
	public Player passport;
	public List<String> alerts;
	
	private ConfigurationSection properties;
	
	public Citizen(UUID uuid) {
		this((Player) CityZen.getPlugin().getServer().getOfflinePlayer(uuid));
	}
	public Citizen(Player player) {
		properties = CityZen.citizenConfig.getConfig().getConfigurationSection("citizens." + player.getUniqueId());
		passport = player;
		reputation = Integer.valueOf(getProperty("reputation"));
		fixRep();
		affiliation = City.getCity(getProperty("affiliation"));
		
		alerts = properties.getStringList("alerts");
	}
	
	/**
	 * Gets a Citizen by name from list of citizens in memory. 
	 * If somehow that player is online, but their citizen record is not in memory, it will be added.
	 * This method should be used exclusively for getting online citizens, NOT initializing a new Citizen.
	 * @param uuid
	 * The uuid of the player to get
	 * @return
	 * Returns a citizen from memory if online, or a new citizen record if offline.
	 */
	public static Citizen getCitizen(UUID uuid) {
		Citizen ctz = null;
		for(Citizen c : CityZen.citizens) {
			if (c.passport.getUniqueId().equals(uuid)) {
				ctz = c;
			}
		}
		
		if (ctz == null) {
			Player newCtz = CityZen.getPlugin().getServer().getPlayer(uuid);
			ctz = new Citizen(newCtz);
			if (newCtz.isOnline()) CityZen.citizens.add(ctz);
		}
		return ctz;
	}
	
	public void alert(String alertText) {
		//TODO: Date of today + alertText, then write back to citizen file
		// SimpleDateFormat?
		alerts.add( + alertText);
	}
	
	public void subRep(int amount) {
		reputation -= amount;
		fixRep();
	}
	
	private void fixRep() {
		if (reputation < 0) reputation = 0;
	}
	
	public void save() {
		FileConfiguration cnfg = CityZen.citizenConfig.getConfig();
		String pName = passport.getName();
		String pPath = "citizens." + pName;
		cnfg.set(pPath + ".name", pName);
		cnfg.set(pPath + ".uuid", passport.getUniqueId());
		cnfg.set(pPath + ".reputation", reputation);
		cnfg.set(pPath + ".affiliation", affiliation.name);
	}
	
	/**
	 * Determines if two Citizen objects are the same by their UUID
	 * @param citizen
	 * The citizen to compare to this one
	 * @return
	 * Whether or not these Citizens are the same
	 */
	public Boolean equals(Citizen citizen) {
		return passport.getUniqueId().equals(citizen.passport.getUniqueId());
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
}
