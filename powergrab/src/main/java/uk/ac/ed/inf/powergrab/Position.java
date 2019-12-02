package uk.ac.ed.inf.powergrab;

import java.util.HashMap;

public class Position {
	public double latitude;
	public double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Calculates the next position of the drone with respect to the input direction
	 * 
	 * @param direction - direction of movement
	 * @return next position of the drone after moving towards the input direction
	 */
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

	/**
	 * Checks whether the drone is within the specified play area (game boundaries)
	 * 
	 * @return true if the done is within the play area, false otherwise
	 */
	public boolean inPlayArea() {
		boolean latitude_check = latitude > 55.942617 && latitude < 55.946233;
		boolean longitude_check = longitude > -3.192473 && longitude < -3.184319;
		return latitude_check && longitude_check;
	}

	/**
	 * Calculates the distance of a position object (usually the position of a
	 * station) from the drone
	 * 
	 * @param newPos - position object (e.g. a station's position)
	 * @return distance of a position object from the drone
	 */
	public double distanceFromDrone(Position newPos) {
		double result = Math.sqrt(
				Math.pow((newPos.latitude - this.latitude), 2) + Math.pow((newPos.longitude - this.longitude), 2));

		return result;
	}

	/**
	 * Gets the closest station from the drone's current position
	 * 
	 * @return closest station from the drone
	 */
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

	/**
	 * Checks whether a station is within charging range of the drone, i.e. within a
	 * distance of 0.00025 from the drone's current position
	 * 
	 * @param station - station to check
	 * @return true of the station is within charging range, false otherwise
	 */
	public boolean inRange(Station station) {
		Position pos = new Position(this.latitude, this.longitude);
		if (pos.distanceFromDrone(station.position) <= 0.00025) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether moving the drone to a certain position makes the drone charge
	 * from a negative station
	 * 
	 * @return true if the position does not make a drone charge from a negative
	 *         station, false otherwise
	 */
	public boolean noRedStations() {
		Position pos = new Position(this.latitude, this.longitude);

		boolean noRedStations = true;

		Station closestStation = pos.getClosestStation();

		// Check if the closest station is within charging range and is a negative
		// station
		if (pos.inRange(closestStation) && (closestStation.getCoins() < 0.0)) {
			noRedStations = false;
		}

		return noRedStations;
	}

	/**
	 * Gets the direction with highest utility, i.e. returns the maximum number of
	 * coins. This method is used when a drone has nowhere else to go (i.e.
	 * surrounded by negative stations and game boundaries) and would rather charge
	 * from any random direction rather than getting stuck.
	 * 
	 * @return
	 */
	public Direction getHighestUtilityDirection() {
		Position pos = new Position(this.latitude, this.longitude);
		Direction highestUtilityDirection = Position.getRandomDirection(Direction.values());

		HashMap<Direction, Double> directionCoins = new HashMap<Direction, Double>();

		// Adds the total amount of coins a direction yields
		for (Direction direction : Direction.values()) {
			Position newPos = pos.nextPosition(direction);
			Station newClosestStation = newPos.getClosestStation();
			if (newPos.inRange(newClosestStation) && newPos.inPlayArea()) {
				if (directionCoins.get(direction) != null) {
					Double current = directionCoins.get(direction);
					directionCoins.put(direction, current + newClosestStation.getCoins());
				}
				directionCoins.put(direction, newClosestStation.getCoins());
			}
		}

		// If there are no stations in range, this method will return a random direction
		if (directionCoins.isEmpty()) {
			System.out.println("NO STATIONS IN RANGE");
		}

		// Get the best direction, i.e. the direction that yields the most coins
		Double maxCoins = 0.0;
		for (Direction direction : directionCoins.keySet()) {
			if (directionCoins.get(direction) > maxCoins) {
				highestUtilityDirection = direction;
				maxCoins = directionCoins.get(direction);
			}
		}

		return highestUtilityDirection;

	}

	/**
	 * Gets a random direction based on the seed input
	 * 
	 * @param directions - list of directions
	 * @return random direction
	 */
	public static Direction getRandomDirection(Direction[] directions) {
		int index = App.rnd.nextInt(directions.length);
		return directions[index];
	}

}
