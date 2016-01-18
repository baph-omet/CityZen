package io.github.griffenx.CityZen;

import org.bukkit.Location;
import org.bukkit.World;

public class Position {
	public World world;
	public double x;
	public double y;
	public double z;
	
	
	public Position(World world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Position(Location location) {
		world = location.getWorld();
		x = location.getX();
		y = location.getY();
		z = location.getZ();
	}
	public Position(String metadata) {
		String[] meta = metadata.split(";");
		world = CityZen.getPlugin().getServer().getWorld(meta[0]);
		x = Double.valueOf(meta[1]);
		y = Double.valueOf(meta[2]);
		z = Double.valueOf(meta[3]);
	}
	
	public Location asLocation() {
		return new Location(world,(double)x,(double)y,(double)z);
	}
}
