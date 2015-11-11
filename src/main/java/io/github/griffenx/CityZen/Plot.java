package io.github.griffenx.CityZen;

import java.util.List;
import java.util.Vector;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Plot {
	private int identifier;
	
	private Boolean wiped;
	
	public Plot(City city, int id) {
		identifier = id;
	}
	
	public static createPlot(City city, Location corner1, Location corner2, Citizen creator) {
		Plot newPlot = null;
		for (Plot p : city.getPlots()) {
			if (p.getCorner1().equals(corner1) || p.getCorner2.equals(corner2) || p.overlaps(corner1,corner2)) {
				return newPlot;
			}
		}
		int id = generateID();
		newPlot = new Plot(city,id);
		newPlot.setCorner1(corner1);
		newPlot.setCorner2(corner2);
		newPlot.setCreator(creator);
		newPlot.setMega(false);
		newPlot.setProtectionLevel(2);
		newPlot.addOwner(creator);
		newPlot.setBaseHeight((int) (corner1.getY() + corner2.getY()) / 2);
		return newPlot;
	}
	
	public void delete() {
		//TODO: Remove configuration section
	}
	
	public Location getCorner1() {
		String coords = getProperty("corner1");
		return new Location(getAffiliation().getWorld(), coords.split(",")[0], 0, coords.split(",")[1]);
	}
	
	public void setCorner1(Location location) {
		String coords = ((int) location.getX()) + "," + ((int) location.getZ());
		setProperty("corner1",coords);
	}
	
	public Location getCorner2() {
		String coords = getProperty("corner2");
		return new Location(getAffiliation().getWorld(), coords.split(",")[0], 0, coords.split(",")[1]);
	}
	
	public void setCorner2(Location location) {
		String coords = ((int) location.getX()) + "," + ((int) location.getZ());
		setProperty("corner2",coords);
	}
	
	public Boolean isMega() {
		return Boolean.valueOf(getProperty("mega"));
	}
	
	public void setMega(Boolean state) {
		setProperty("mega",state);
	}
	
	public int getBaseHeight() {
		try {
			return Integer.valueOf(getProperty("height"));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public void setBaseHeight(int height) {
		setProperty("height",height);
	}
	
	public int getProtectionLevel() {
		if (!isMega()) {
			return 2;
		}
		try {
			return Integer.valueOf(getProperty("protection"));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public void setProtectionLevel(int level) {
		if (level >=0 && level < 3) setProperty("protection",level);
		else setProperty("protection",2);
	}
	
	public City getAffiliation() {
		for (City c : City.getCities()) {
			for (Plot p : c.getPlots()) {
				if (p.equals(this)) {
					return c;
				}
			}
		}
		return null;
	}
	
	public List<Citizen> getOwners() {
		List<Citizen> owners = new Vector<Citizen>();
		ConfigurationSection section = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + getAffiliation().identifier + ".plots." + identifier);
		for (String u : section.getStringList("owners") {
			owners.add(new Citizen(UUID.fromString(u)));
		}
		return owners;
	}
	
	public void addOwner(Citizen owner) {
		List<String> owners = new Vector<String>();
		if (getAffiliation().equals(owner.getAffiliation())) {
			for (String u : getOwners()) {
				if (!u.equals(owner)) {
					owners.add(u.getPassport().getUniqueId().toString());
				}
			}
			owners.add(owner.getPassport().getUniqueId().toString());
			setProperty("owners",owners);
		}
	}
	
	public void removeOwner(Citizen owner) {
		List<String> owners = new Vector<String>();
		for (String u : getOwners()) {
			if (!u.equals(owner)) {
				owners.add(u.getPassport().getUniqueId().toString());
			}
		}
		setProperty("owners",owners);
		wipe();
	}
	
	public Citizen getCreator() {
		//TODO: Check the error thown
		try {
			return new Citizen(UUID.fromString(getProperty("creator")));
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public void setCreator(Citizen creator) {
		setProperty("creator",creator.getPassport().getUniqueId().toString());
	}
	
	public Double getArea() {
		return Math.Abs((getCorner1().getX() - getCorner2().getX()) * (getCorner1.getZ() - getCorner2().getZ()))
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
		if (getCorner1().getX() > getCorner2().getX()) xDirection = -1;
		if (getCorner1().getZ() > getCorner2().getZ()) zDirection = -1;
		for (int x = (int) getCorner1().getX(); x < ((int) getCorner2().getX() * xDirection); x = x + (1 * xDirection)) {
			for (int z = (int) getCorner1().getZ(); x < ((int) getCorner2().getZ() * zDirection); x = x + (1 * zDirection)) {
				if (plot.isInPlot(x, z)) return true;
			}
		}
		return false;
	}
	public Boolean overlaps(Location corner1, Location corner2) {
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
		if (owners.size() == 0) {
			int xDirection = 1;
			int zDirection = 1;
			if (getCorner1().getX() > getCorner2().getX()) xDirection = -1;
			if (getCorner1().getZ() > getCorner2().getZ()) zDirection = -1;
			if (affiliation.isNaturalWipe()) {
				//TODO: Restore plot to natural terrain
			} else {
				//TODO: Set plot to flatlands
				for (int y = 1; y <= getBaseHeight; y++) {
					for (int x = (int) getCorner1().getX(); x < ((int) getCorner2().getX() * xDirection); x = x + (1 * xDirection)) {
						for (int z = (int) getCorner1().getZ(); x < ((int) getCorner2().getZ() * zDirection); x = x + (1 * zDirection)) {
							if (/* world type is nether */) {
								//lava
								//netherrack
							} else if (/* world type is end */) {
								//air
								//endstone
							} else /* world is overworld, or something else */ {
								if (y < 5) {
									//TODO: Set block to bedrock
								} else if (y < getBaseHeight() - 5) {
									//TODO: Set block to stone
								} else if (y < getBaseHeight()) {
									//TODO: Set block to dirt
								} else {
									//TODO: Set block to grass
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Compares plot corners to see if they're the same plot.
	 * It's impossible for plots to overlap, so if a plot's corners match, they must be the same.
	 * @param plot The plot to compare to this one
	 * @returns True if the plots have the same corners
	 */
	public Boolean equals(Plot plot) {
		return getCorner1().getX() == plot.getCorner1().getX() && getCorner1().getZ() == plot.getCorner1().getZ()
			&& getCorner2().getX() == plot.getCorner2(d).getX() && getCorner2().getZ() == plot.getCorner2().getZ();
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
		CityZen.cityConfig.save();
	}
	
	private static int generateID(City city) {
		int id = 0;
		Boolean idChanged = false;
		Set<String> keys = CityZen.cityConfig.getConfig().getConfigurationSection("cities." + city.identifier + ".plots").getKeys(false);
		do {
			if (keys.contains(id)) {
				id++;
			} else {
				idChanged = true;
			}
		} while (id > 0 && !idChanged);
		return id;
	}
}
