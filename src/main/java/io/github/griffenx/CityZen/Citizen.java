package io.github.griffenx.CityZen;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Citizen {
	
	public int reputation;
	public City affiliation;
	public Player passport;
	
	public Citizen(UUID uuid) {
		this((Player) CityZen.getPlugin().getServer().getOfflinePlayer(uuid));
	}
	public Citizen(Player player) {
		passport = player;
		FileConfiguration cnfg = CityZen.citizenConfig.getConfig();
		if (cnfg.contains("citizens." + passport.getName())) {
			reputation = cnfg.getInt("citizens." + passport.getName() + ".reputation");
			affiliation = new City(cnfg.getString("citizens." + passport.getName() + ".affiliation"));
		}
	}
	
	/**
	 * Gets a Citizen by name from list of citizens in memory. 
	 * If somehow that player is online, but their citizen record is not in memory, it will be added.
	 * This method should be used exclusively for getting citizens, NOT initializing a new Citizen.
	 * @param name
	 * The name of the player to get
	 * @return
	 * Returns a citizen from memory if online, or a new citizen record if offline.
	 */
	public static Citizen getCitizen(UUID uuid) {
		Citizen ctz = null;
		for(Citizen c : CityZen.citizens) {
			//TODO: Get player's UUID (not quite sure how to do this w/o docs)
			if (c.passport.getName().equalsIgnoreCase(name)) {
				ctz = c;
			}
		}
		
		if (ctz == null) {
			Player newCtz = CityZen.getPlugin().getServer().getPlayer(UUID.fromString(name));
			ctz = new Citizen(newCtz);
			if (newCtz.isOnline()) CityZen.citizens.add(ctz);
		}
		return ctz;
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
	
	
}
