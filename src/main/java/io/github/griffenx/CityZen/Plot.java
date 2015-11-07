package io.github.griffenx.CityZen;

import java.util.List;
import java.util.Vector;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Plot {
	public Citizen creator;
	public Boolean isMega;
	public int baseHeight;
	public int protection;
	
	public List<Citizen> owners;
	
	public Location corner1;
	public Location corner2;
	
	private City affiliation;
	private int identifier;
	
	private Boolean wiped;
	
	public Plot(City city, int id) {
		affiliation = city;
		identifier = id;
		
		
		//TODO: Load values from disk into memory
		
	}
	
	//TODO: Method to create a new plot
	
	public Double getArea() {
		//TODO: Get area from length and width from corners
		// I sure do wish I had access to the Bukkit API >.>
	}
	
	public Boolean isInPlot(Location location) {
		//TODO: return if X and Z values of location are between corner1 and corner2
	}
	
	public void wipe() {
		//TODO: restore the plot to a blank plot, method depends on city's wipe settings
		if (owners.size() == 0) {
			if (affiliation.naturalWipe) {
				//TODO: Restore plot to natural terrain
			} else {
				//TODO: Set plot to flatlands
			}
		}
	}
	
	public void removeOwner(Citizen owner) {
		owners.remove(owner);
		wipe();
	}
	
	public void save() {
		FileConfiguration config = CityZen.cityConfig.getConfig();
		String path = "cities." + affiliation.identifier + ".plots." + identifier;
		if (config.contains(path)) {
			config.createSection(path);
		}
		ConfigurationSection props = config.getConfigurationSection(path);
		props.set(".corner1", (int) corner1.getX() + "," + (int) corner1.getZ());
		props.set(".corner2", (int) corner2.getX() + "," + (int) corner2.getZ());
		props.set(".height",baseHeight);
		props.set(".mega",isMega);
		props.set(".protection", protection);
		props.set(".creator", creator.passport.getUniqueId());
		
		List<String> ownrs = new Vector<String>();
		for (Citizen c : owners) ownrs.add(c.passport.getUniqueId().toString());
		props.set(".owners", ownrs);
	}
	
	private String getProperty(String property) {
		//TODO: Get property of plot from config
	}
	
	private List<Citizen> getOwners() {
		//TODO: Get owners from file
	}
}
