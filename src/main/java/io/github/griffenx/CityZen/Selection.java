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
		if (buffer <= 0) return null;
		Position bpos1 = new Position(pos1.world,pos1.x + (Math.abs(pos1.x) / pos1.x) * buffer,pos1.y,pos1.z + (Math.abs(pos1.z) / pos1.z) * buffer);
		Position bpos2 = new Position(pos2.world,pos2.x + (Math.abs(pos2.x) / pos2.x) * buffer,pos2.y,pos2.z + (Math.abs(pos2.z) / pos2.z) * buffer);
		return new Selection(bpos1,bpos2);
	}
	public Selection getBuffer() {
		return getBuffer(0);
	}
}
