package io.github.griffenx.CityZen;

public class Selection {
	public Position pos1;
	public Position pos2;
	
	public Selection(Position pos1, Position pos2) {
		if (!pos1.world.equals(pos2.world)) return;
		this.pos1 = pos1;
		this.pos2 = pos2;
	}
	
	public Double getArea() {
		return Math.abs((double)((pos1.x - pos2.x) * (pos1.z - pos2.z)));
	}
	
	public Double getSideX() {
		return Math.abs(pos1.x - pos2.x);
	}
	
	public Double getSideZ() {
		return Math.abs(pos1.z - pos2.z);
	}
	
	public Selection getBuffer(double bufferAdjustment) {
		double buffer = CityZen.getPlugin().getConfig().getDouble("plotBuffer") + bufferAdjustment;
		if (buffer <= 0) return this;
		Position bpos1 = new Position(pos1.world,pos1.x + (pos1.x < pos2.x ? -1 : 1) * buffer,pos1.y,pos1.z + (pos1.z < pos2.z ? 1 : -1) * buffer);
		Position bpos2 = new Position(pos2.world,pos2.x + (pos1.x > pos2.x ? -1 : 1) * buffer,pos2.y,pos2.z + (pos1.z > pos2.z ? 1 : -1) * buffer);
		return new Selection(bpos1,bpos2);
	}
	public Selection getBuffer() {
		return getBuffer(0);
	}
	
	public boolean isSpaced(City affiliation) {
		int xDirection = 1;
		int zDirection = 1;
		if (pos1.x > pos2.x) xDirection = -1;
		if (pos1.z > pos2.z) zDirection = -1;
		for (int x = (int) pos1.x; x < ((int) pos2.x * xDirection) + (1 * xDirection); x = x + (1 * xDirection)) {
			for (int z = (int) pos1.z; z < ((int) pos2.z * zDirection) + (1 * zDirection); z = z + (1 * zDirection)) {
				for (City c : City.getCities()) {
					if (!c.equals(affiliation)) {
						if (c.isInCity(x,z) || (c.getWorld().equals(pos1.world) 
								&& Util.getDistace(new Position(pos1.world, x, 0, z), c.getCenter()) < CityZen.getPlugin().getConfig().getInt("minCitySeparation"))) return false;
					}
				}
			}
		} return true;
	}
	
	public boolean worldGuardConflicts(Citizen citizen) {
		if (CityZen.WorldGuard != null) {
			int xDirection = 1;
			int zDirection = 1;
			if (pos1.x > pos2.x) xDirection = -1;
			if (pos1.z > pos2.z) zDirection = -1;
			for (int x = (int) pos1.x; x < ((int) pos2.x * xDirection) + (1 * xDirection); x = x + (1 * xDirection)) {
				for (int z = (int) pos1.z; z < ((int) pos2.z * zDirection) + (1 * zDirection); z = z + (1 * zDirection)) {
					for (int y = 0; y < pos1.world.getMaxHeight(); y++) {
						if (!CityZen.WorldGuard.canBuild(citizen.getPlayer(), new Position(pos1.world,x,y,z).asLocation())) {
							return true;
						}
					}
				}
			}
		} return false;
	}
}
