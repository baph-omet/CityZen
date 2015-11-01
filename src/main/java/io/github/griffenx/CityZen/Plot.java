package io.github.griffenx.CityZen;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Plot {
	private Location center;
	private Citizen owner;
	private City affiliation;
	
	public Plot(Citizen creator) {
		center = creator.passport.getLocation();
		owner = creator;
		affiliation = creator.affiliation;
	}
	public Plot(Location location, Citizen creator, City city) {
		center = location;
		owner = creator;
		affiliation = city;
	}
	public Plot(double x, double y, double z, Citizen creator, City city) {
		center = new Location(creator.passport.getWorld(),x,y,z);
		owner = creator;
		affiliation = city;
	}
}
