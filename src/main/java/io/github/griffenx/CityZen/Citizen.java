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
