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

	// calculate distance
	public static double distance(Position currentPos, Position newPos) {
		double currentLatitude = currentPos.latitude;
		double currentLongitude = currentPos.longitude;

		double newLatitude = newPos.latitude;
		double newLongitude = newPos.longitude;

		double result = Math
				.sqrt(Math.pow((newPos.latitude - currentPos.latitude), 2) + Math.pow((newPos.longitude - currentPos.longitude), 2));

		return result;
	}
	
	public static Direction getDirection(Position pos) {
		double angle = Math.toDegrees(Math.atan2(-pos.longitude, pos.latitude));
		//System.out.println(angle);
		double roundedAngle = Math.round((angle/22.5))*22.5;
		//System.out.println(roundedAngle);
		if (roundedAngle < 0.0) {
			roundedAngle += 360.0;
		}
		int index = (int) (roundedAngle / 22.5);
		return Direction.values()[index];
	}
	
	public static int getMinIndex(double[] array){

	    int n = array.length;
	    
	    double min = array[0];
	    int index = 0;
	    
	    for (int i = 0; i < n; i++) {
	    	if (array[i] < min) {
	    		min = array[i];
	    		index = i;
	    	}
	    }
	    return index;
	}
	
	public static Direction getRandomDirection() {
		int index = App.rnd.nextInt(16);
		return Direction.values()[index];
	}
}
