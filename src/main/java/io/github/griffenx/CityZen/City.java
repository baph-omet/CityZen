package io.github.griffenx.CityZen;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class City {
	public Citizen mayor;
	public List<Citizen> citizens;
	public List<Citizen> deputies;
	
	public String name;
	public String slogan;
	public ChatColor color;
	
	public int maxPlotSize;
	public int minPlotSize;
	public Location center;
	
	public Boolean freeJoin;
	public Boolean openPlotting;
	public Boolean blockBlacklist;
	public Boolean useBlacklistAsWhitelist;
	
	public List<Material> blacklistedBlocks;
	
	private String identifier;
	private ConfigurationSection properties;
	
	public City(String id) {
		properties = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + id);
		identifier = id;
		name = getProperty("name");
		slogan = getProperty("slogan");
		color = ChatColor.getByChar(getProperty("color"));
		
		//mayor = new Citizen()
		
		citizens = getCitizens();
		
	}

	public List<Citizen> getCitizens() {
		List<Citizen> cits = new Vector<Citizen>();
		for (String u : CityZen.cityConfig.getConfig().getStringList("cities." + identifier + ".citizens")) {
			cits.add(new Citizen(CityZen.getPlugin().getServer().getPlayer(UUID.fromString(u))));
		}
		return cits;
	}
	
	public void addCitizen(Citizen ctz) {
		citizens.add(ctz);
		//TODO: Handling for when a citizen is added to a city, perhaps
	}
	
	public void removeCitizen(Citizen ctz) {
		ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOnLeaveCityPercent") / 100);
		citizens.remove(ctz);
		//TODO: Remove plots
	}
	
	public void evictCitizen(Citizen ctz) {
		ctz.reputation -= (ctz.reputation * CityZen.getPlugin().getConfig().getInt("reputation.lostOneEvictionPercent") / 100);
		citizens.remove(ctz);
		//TODO: Remove plots
	}
	
	public int getReputation() {
		int tot = 0;
		for(Citizen c : citizens) tot += c.reputation;
		return tot;
	}
	
	public void save() {
		// This operation should be done regularly to avoid data loss if the server crashes or whatever
		//TODO: Save all properties of city to config, then reload config
	}
	
	private String getProperty(String property) {
		for (String prop : properties.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				String val = CityZen.cityConfig.getConfig().getString(properties.getString(property));
				if (val.length() == 0) {
					val = CityZen.getPlugin().getConfig().getString("cityDefaults." + property);
					CityZen.cityConfig.getConfig().set("cities." + identifier + "." + property, val);
				} return val;
			}
		} return "";
	}
}
