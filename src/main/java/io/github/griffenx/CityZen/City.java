package io.github.griffenx.CityZen;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class City {
	public Citizen mayor;
	public String name;
	//public int reputation;
	public ArrayList<Citizen> citizens;
	
	private int initialX;
	private int initialZ;
	
	public City(String name, Player founder) {
		Citizen mayor = new Citizen(founder);
		
		this.name = name;
		this.mayor = mayor;
	}
	public City(String name, Player founder, int x, int z) {
		Citizen mayor = new Citizen(founder);
		
		this.name = name;
		this.mayor = mayor;
		this.initialX = x;
		this.initialZ = z;
	}
//	public City(String name, Player founder, int x, int z,int rep) {
//		Citizen mayor = new Citizen(founder);
//		
//		this.name = name;
//		this.mayor = mayor;
//		this.initialX = x;
//		this.initialZ = z;
//		this.reputation = rep;
//	}
	
	public void addCitizen(Citizen ctz) {
//		this.reputation += ctz.reputation;
		this.citizens.add(ctz);
	}
	
	public void removeCitizen(Citizen ctz) {
		// Player loses rep for leaving a city
		ctz.reputation /= 2;
		this.citizens.remove(ctz);
	}
	
	public int getReputation() {
		int tot = 0;
		for(Citizen c : this.citizens) tot += c.reputation;
		return tot;
	}
	
	public void save() {
		
	}
}
