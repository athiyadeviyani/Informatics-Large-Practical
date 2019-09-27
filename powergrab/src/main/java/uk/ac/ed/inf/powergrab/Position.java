package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {

		int index = direction.ordinal();
		double angle = Math.toRadians(index * 22.5);
		
		double delta_lat = 0.0003 * Math.cos(angle);
		double delta_long = 0.0003 * Math.sin(angle);
		
		Position nextPos = new Position(latitude + delta_lat, longitude + delta_long);
		
		return nextPos;
	}
	
	public boolean inPlayArea() {
		return false;
	}
}
