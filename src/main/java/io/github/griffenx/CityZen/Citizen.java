package io.github.griffenx.CityZen;

import org.bukkit.entity.Player;

public class Citizen {
	public int reputation;
	public City affiliation;
	public Player passport;
	
	public Citizen(Player plr) {
		//TODO: Constructor
		this.passport = plr;
	}
	
	public void save() {}
	
	
}
