package io.github.griffenx.CityZen;

import java.util.UUID;

import org.bukkit.entity.Player;

public class Util {
	/**
	 * Gets a Citizen by name from list of citizens in memory. 
	 * If somehow that player is online, but their citizen record is not in memory, it will be added.
	 * @param name
	 * The name of the player to get
	 * @return
	 * Returns a citizen from memory if online, or a new citizen record if offline.
	 */
	public static Citizen getCitizen(String name) {
		Citizen ctz = null;
		for(Citizen c : CityZen.citizens) {
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
	
	/**
	 * Gets a City by name from list of cities in memory.
	 * If somehow that city is not in memory, it will be added.
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
}
