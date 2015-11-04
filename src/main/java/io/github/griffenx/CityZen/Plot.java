package io.github.griffenx.CityZen;

import org.bukkit.Location;

public class Plot {
	private Location corner1;
	private Location corner2;
	private Citizen owner;
	private City affiliation;
	private int identifier;
	
	public Plot(City city, int id) {
		affiliation = city;
		identifier = id;
		
		//TODO: Load values from disk into memory
		
	}
	
	//TODO: Method to create a new plot
}
