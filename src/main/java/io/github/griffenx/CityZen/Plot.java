package io.github.griffenx.CityZen;

import org.bukkit.Location;

public class Plot {
	public Citizen creator;
	public Boolean isMega;
	public int baseHeight;
	public int protection;
	
	public List<Citizen> owners;
	
	private Location corner1;
	private Location corner2;
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
		if (owners.Length() == 0) {
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
	
	private String getProperty(String property) {
		//TODO: Get property of plot from config
	}
	
	private List<Citizen> getOwners() {
		//TODO: Get owners from file
	}
}
