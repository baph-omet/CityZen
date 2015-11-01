package io.github.griffenx.CityZen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class City {
	public Citizen mayor;
	public String name;
	public ArrayList<Citizen> citizens;
	
	private Location center;
	
	public City(String name) {
		//TODO: Load city from config, set handling for if the city doesn't exist
	}
	public City(String name, Player founder) {
		Citizen mayor = new Citizen(founder);
		
		name = name;
		mayor = mayor;
	}
	public City(String cityName, Player founder, int x, int z) {
		name = cityName;
		mayor = new Citizen(founder);
		initialX = x;
		initialZ = z;
	}
	
	public List getCitizens() {
		return CityZen.cityConfig.getConfig().getShortList("cities." + name + ".citizens");
	}
	
	public void addCitizen(Citizen ctz) {
//		reputation += ctz.reputation;
		citizens.add(ctz);
	}
	
	public void removeCitizen(Citizen ctz) {
		// Player loses rep for leaving a city
		ctz.reputation /= 2;
		citizens.remove(ctz);
	}
	
	public int getReputation() {
		int tot = 0;
		for(Citizen c : citizens) tot += c.reputation;
		return tot;
	}
	
	public void save() {
		
	}
}
