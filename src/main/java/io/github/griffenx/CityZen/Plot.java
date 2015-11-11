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
	
	public Citizen getCreator() {
		
	}
	
	public Double getArea() {
		//TODO: Get area from length and width from corners
		// I sure do wish I had access to the Bukkit API >.>
	}
	
	public Boolean isInPlot(Location location) {
		return isInPlot((int) location.getX(), (int) location.getY());
	}
	public Boolean isInPlot(int x, int z) {
		if ((x < corner2.getX() && x > corner1.getX()) || (x > corner2.getX() && x < corner1.getX())) {
			if ((z < corner2.getZ() && z > corner1.getZ()) || (z > corner2.getZ() && z < corner1.getZ())) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean overlaps(Plot plot) {
		int xDirection = 1;
		int zDirection = 1;
		if (corner1.getX() > corner2.getX()) xDirection = -1;
		if (corner1.getZ() > corner2.getZ()) zDirection = -1;
		for (int x = (int) corner1.getX(); x < ((int) corner2.getX() * xDirection); x = x + (1 * xDirection)) {
			for (int z = (int) corner1.getZ(); x < ((int) corner2.getZ() * zDirection); x = x + (1 * zDirection)) {
				if (plot.isInPlot(x, z)) return true;
			}
		}
		return false;
	}
	
	public void wipe() {
		//TODO: restore the plot to a blank plot, method depends on city's wipe settings
		if (owners.size() == 0) {
			if (affiliation.isNaturalWipe()) {
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
		String val = "";
		ConfigurationSection props = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + getAffiliation().identifier + ".plots." + identifier);
		for (String prop : props.getKeys(false)) {
			if (prop.equalsIgnoreCase(property)) {
				val = props.getString(property);
				break;
			}
		}
		return val;
	}
	
	private void setProperty(String property, Object value) {
		CityZen.cityConfig.getConfig().set("cities." + getAffiliation().identifier + ".plots." + identifier + "." + property,value);
	}
}
