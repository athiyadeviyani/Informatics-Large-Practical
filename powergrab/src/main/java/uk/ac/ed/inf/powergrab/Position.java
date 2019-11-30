package uk.ac.ed.inf.powergrab;

import java.util.HashMap;

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

	
	// Calculates the distance of a station from the drone
	public double distanceFromDrone(Position newPos) {
		double result = Math.sqrt(
				Math.pow((newPos.latitude - this.latitude), 2) + Math.pow((newPos.longitude - this.longitude), 2));

		return result;
	}

	
	// Gets the closest station from current position
	public Station getClosestStation() {
		Position curPos = new Position(this.latitude, this.longitude);
		Station closestStation = App.stations.get(0);
		for (Station station : App.stations) {
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestStation.position)) {
				closestStation = station;
			}
		}

		return closestStation;
	}

	
	// Checks whether or not a station is within charging range of the drone
	public boolean inRange(Station station) {
		Position pos = new Position(this.latitude, this.longitude);
		if (pos.distanceFromDrone(station.position) <= 0.00025) {
			return true;
		}
		return false;
	}

	
	// Checks whether moving to a certain position makes you charge from a negative station
	public boolean noRedStations() {
		Position pos = new Position(this.latitude, this.longitude);

		boolean noRedStations = true;

		Station closestStation = pos.getClosestStation();

		if (pos.inRange(closestStation) && (closestStation.coins < 0.0)) {
			noRedStations = false;
		}

		return noRedStations;
	}

	public Direction getHighestUtilityDirection() {
		Position pos = new Position(this.latitude, this.longitude);
		Direction highestUtilityDirection = Position.getRandomDirection(Direction.values());
		Position nextPos = pos.nextPosition(highestUtilityDirection);
		Station closestStation = nextPos.getClosestStation();
		
		HashMap<Direction, Double> directionCoins = new HashMap<Direction, Double>();
		
		
		for (Direction direction : Direction.values()) {
			Position newPos = pos.nextPosition(direction);
			Station newClosestStation = newPos.getClosestStation();
			if (newPos.inRange(newClosestStation) && newPos.inPlayArea()) {
				if (directionCoins.get(direction) != null) {
					Double current = directionCoins.get(direction);
					directionCoins.put(direction, current + newClosestStation.coins);
				}
				directionCoins.put(direction, newClosestStation.coins);
			}
		}
		
		for (Direction direction : directionCoins.keySet()) {
			if (directionCoins.get(direction) > closestStation.coins) {
				highestUtilityDirection = direction;
				nextPos = pos.nextPosition(highestUtilityDirection);
				closestStation = nextPos.getClosestStation();
			}
		}
		
		return highestUtilityDirection;		
		
	}
	
	// Gets a random direction (based on the seed input)
	public static Direction getRandomDirection(Direction[] directions) {
		int index = App.rnd.nextInt(directions.length);
		return directions[index];
	}
	
}
