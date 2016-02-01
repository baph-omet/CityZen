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
		double xLow = pos1.x;
		double xHigh = pos2.x;
		double zLow = pos1.z;
		double zHigh = pos2.z;
		if (pos2.x < xLow) {
			xLow = pos2.x;
			xHigh = pos1.x;
		}
		if (pos2.z < zLow) {
			zLow = pos2.z;
			zHigh = pos1.z;
		}
		
		for (double x = xLow; x <= xHigh; x++) {
			for (double z = zLow; z <= zHigh; z++) {
				for (City c : City.getCities()) {
					if (!c.equals(affiliation) && c.getCenter() != null) {
						if (c.isInCity(x,z) || (c.getWorld().equals(pos1.world) 
								&& Util.getDistace(new Position(pos1.world, x, 0, z), c.getCenter()) < CityZen.getPlugin().getConfig().getInt("minCitySeparation"))) return false;
					}
				}
			}
		} return true;
	}
	
	public boolean worldGuardConflicts(Citizen citizen) {
		if (CityZen.WorldGuard != null) {
			double xLow = pos1.x;
			double xHigh = pos2.x;
			double zLow = pos1.z;
			double zHigh = pos2.z;
			if (pos2.x < xLow) {
				xLow = pos2.x;
				xHigh = pos1.x;
			}
			if (pos2.z < zLow) {
				zLow = pos2.z;
				zHigh = pos1.z;
			}
			
			for (double y = 1; y <= pos1.world.getMaxHeight(); y++) {
				for (double x = xLow; x <= xHigh; x++) {
					for (double z = zLow; z <= zHigh; z++) {
						if (!CityZen.WorldGuard.canBuild(citizen.getPlayer(), new Position(pos1.world,x,y,z).asLocation())) {
							return true;
						}
					}
				}
			}
		} return false;
	}
}
