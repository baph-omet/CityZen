package io.github.griffenx.CityZen;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Citizen {
	
	public int reputation;
	public City affiliation;
	public Player passport;
	
	///<summary>
	///Creates a new Citizen based on the player's name
	///</summary>
	public Citizen(Player plr) {
		passport = plr;
		FileConfiguration cnfg = CityZen.citizenConfig.getConfig();
		if (cnfg.contains("citizens." + passport.getName())) {
			reputation = cnfg.getInt("citizens." + passport.getName() + ".reputation");
			affiliation = new City(cnfg.getString("citizens." + passport.getName() + ".affiliation"));
			cnfg.get
		}
		
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
