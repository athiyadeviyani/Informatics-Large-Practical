package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	// Method to calculate the next position of the drone
	public Position nextPosition(Direction direction) {

		// Gets the respective angle based on the direction passed in
		// Starts at N = 0 degrees
		int index = direction.ordinal();
		double angle = Math.toRadians(index * 22.5);
		
		// Calculate the change in latitude and longitude
		double delta_lat = 0.0003 * Math.cos(angle);
		double delta_long = 0.0003 * Math.sin(angle);
		
		// Calculate the nextPos by adding the change in latitude and longitude
		// to the current position
		Position nextPos = new Position(latitude + delta_lat, longitude + delta_long);
		
		return nextPos;
	}
	
	// Method to check whether the drone is within the playing area
	public boolean inPlayArea() {
		boolean latitude_check = latitude > 55.942617 && latitude < 55.946233;
		boolean longitude_check = longitude > -3.192473 && longitude < -3.184319;
		return latitude_check && longitude_check; 
	}
}
