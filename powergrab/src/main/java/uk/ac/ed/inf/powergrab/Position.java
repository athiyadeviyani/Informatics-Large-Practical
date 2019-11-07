package uk.ac.ed.inf.powergrab;

import java.util.List;

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

	
	public double distanceFromDrone(Position newPos) {

		double result = Math
				.sqrt(Math.pow((newPos.latitude - this.latitude), 2) 
						+ Math.pow((newPos.longitude - this.longitude), 2));

		return result;
	}
	
	public boolean inRange(Position destination) {
		return distanceFromDrone(destination) <= 0.00025;
	}
	
	public static Direction getDirection(Position pos) {
		double angle = Math.toDegrees(Math.atan2(pos.latitude, pos.longitude));
		//System.out.println(angle);
		double roundedAngle = Math.round((angle/22.5))*22.5;
		//System.out.println(roundedAngle);
		if (roundedAngle < 0.0) {
			roundedAngle += 360.0;
		}
		int index = (int) (roundedAngle / 22.5);
		System.out.println(angle);
		return Direction.values()[index];
	}
	
	public static Direction getDirectionFromPosition(Position dronePosition, Position stationPosition) {
		Double deltaY = stationPosition.latitude - dronePosition.latitude;
		Double deltaX = stationPosition.longitude - dronePosition.longitude;
		
		Double theta = Math.atan2(deltaY, deltaX);
		
		// If station is on the left of the drone (stationPosition.longitude < dronePosition.longitude)
		// Then add pi to theta
		if (stationPosition.longitude < dronePosition.longitude) {
			theta = theta + Math.PI;
		}
		
		// Counter the negative values
		theta = (theta + 2*Math.PI) % Math.PI;
		theta = theta * 180/Math.PI;
		
		Double angle = Math.round((theta)*16/360.0) * 22.5;
		
		angle = ((angle-90.0)*(-1)+360.0) % 360.0;
		
		int index = (int) (angle / 22.5);
		
		System.out.println(angle);
		
		// Direction nextDirection = Direction.values()[index];
		
//		while (!dronePosition.nextPosition(nextDirection).inPlayArea()) {
//			nextDirection = Direction.values()[(index + 1) % 16];
//		}

		return Direction.values()[index];
	}
	
	
	
	public static int getDirectionIndexFromPosition(Position dronePosition, Position stationPosition) {
		Double deltaY = stationPosition.latitude - dronePosition.latitude;
		Double deltaX = stationPosition.longitude - dronePosition.longitude;
		
		Double theta = Math.atan2(deltaY, deltaX);
		
		// If station is on the left of the drone (stationPosition.longitude < dronePosition.longitude)
		// Then add pi to theta
		if (stationPosition.longitude < dronePosition.longitude) {
			theta = theta + Math.PI;
		}
		
		// Counter the negative values
		theta = (theta + 2*Math.PI) % Math.PI;
		theta = theta * 180/Math.PI;
		
		Double angle = Math.round((theta)*16/360.0) * 22.5;
		
		angle = ((angle-90.0)*(-1)+360.0) % 360.0;
		
		int index = (int) (angle / 22.5);
		
		System.out.println(angle);
		
//		Direction nextDirection = Direction.values()[index];
		
//		while (!dronePosition.nextPosition(nextDirection).inPlayArea()) {
//			index = (index + 15) % 16;
//		}

		return index;
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
	
	public static Direction getRandomDirection(Direction[] directions) {
		int index = App.rnd.nextInt(directions.length);
		return Direction.values()[index];
	}
}
