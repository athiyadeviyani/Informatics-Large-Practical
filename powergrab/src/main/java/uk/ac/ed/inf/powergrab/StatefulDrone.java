package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class StatefulDrone extends Drone {

	public StatefulDrone(Position startPos) {
		super(startPos);
	}

	/**
	 * Retrieves a list of all the stations with coins greater than zero in the map,
	 * i.e. all the positive stations within a map
	 * 
	 * @return list of positive stations
	 */
	private List<Station> getPositiveStations() {
		List<Station> positiveStations = new ArrayList<Station>();
		for (Station station : App.stations) {
			if (station.getCoins() > 0.0 && station.getPower() > 0.0) {
				positiveStations.add(station);
			}
		}
		return positiveStations;
	}

	/**
	 * Calculates the distances of the positive stations from the drone and returns
	 * the closest one
	 * 
	 * @param curPos           - current position of the drone
	 * @param positiveStations - list of positive stations in the map
	 * @return closest positive station
	 */
	private Station getClosestPositiveStation(Position curPos, List<Station> positiveStations) {
		// Initialise with the first station on the list
		Station closestPositiveStation = positiveStations.get(0);

		for (Station station : positiveStations) {
			if (curPos.distanceFromDrone(station.getPosition()) < curPos
					.distanceFromDrone(closestPositiveStation.getPosition())) {
				closestPositiveStation = station;
			}
		}

		return closestPositiveStation;
	}

	/**
	 * Gets the maximum number of coins in the map.
	 * 
	 * @param stations - list of stations
	 * @return maximum number of coins in the map
	 */
	private Double getMaxCoins(List<Station> stations) {
		double maxCoins = 0.0;
		for (Station station : stations) {
			maxCoins += station.getCoins();
		}
		return maxCoins;
	}

	/**
	 * Sorts the 16 different directions in increasing order of distance to to the
	 * closest positive station i.e. the first item in the list will be the
	 * direction that brings the drone closest to the closest positive station
	 * 
	 * @param directions - list of directions
	 * @param cPS        - closest positive station
	 */
	private void sortedDirections(List<Direction> directions, final Station cPS) {
		Collections.sort(directions, new Comparator<Direction>() {
			public int compare(Direction d1, Direction d2) {
				return Double.compare(startPos.nextPosition(d1).distanceFromDrone(cPS.getPosition()),
						startPos.nextPosition(d2).distanceFromDrone(cPS.getPosition()));
			}
		});
	}

	/**
	 * Gets the best random direction, i.e. a direction that does not bring the
	 * drone outside the play area and that does not make the drone charge from a
	 * negative station
	 * 
	 * @param pos - current position of the drone
	 * @return best random direction
	 */
	private Direction getBestRandomDirection(Position pos) {
		Direction bestRandomDirection = pos.getHighestUtilityDirection();

		Position nextPos = pos.nextPosition(bestRandomDirection);
		Station closestStation = nextPos.getClosestStation();

		List<Direction> values = new ArrayList<Direction>(Arrays.asList(Direction.values()));

		for (int i = 0; i < values.size(); i++) {
			if (nextPos.inPlayArea() && nextPos.noRedStations()) {
				break;
			}
			// 'Run away' from the closestStation as it leads to the boundary
			// or to a red station, i.e. pick the direction that brings you the farthest
			// from it
			sortedDirections(values, closestStation);
			bestRandomDirection = values.get(values.size() - i - 1);
			nextPos = pos.nextPosition(bestRandomDirection);
		}

		return bestRandomDirection;

	}

	/**
	 * Checks whether a position has been visited more than once
	 * 
	 * @param newPos - current position of the drone
	 * @return true if a position has been visited more than once, false otherwise
	 */
	private boolean visited(Position newPos) {

		int count = 0;
		for (Position pos : flightPath) {
			if (newPos.latitude == pos.latitude && newPos.longitude == pos.longitude) {

				count += 1;
			}
		}
		return count > 1;

	}

	/**
	 * Gets the best direction for the drone, i.e the direction that brings the
	 * drone towards the closest positive station and does not bring the drone away
	 * from the play area or towards a station with negative charge
	 * 
	 * @param closestPositiveStation - closest positive station
	 * @return the direction that brings the drone closest to the target station
	 */
	private Direction getBestDirection(Station closestPositiveStation) {

		// Get a list of directions that does not lead the drone outside the play area
		// or towards a negative station
		List<Direction> goodDirections = new ArrayList<Direction>();

		for (Direction direction : Direction.values()) {
			Position nextPos = startPos.nextPosition(direction);
			if (nextPos.noRedStations() && nextPos.inPlayArea()) {
				goodDirections.add(direction);
			}
		}

		// If spawn in a bad region, prevent getting stuck by going to the least worst
		// direction
		if (goodDirections.isEmpty()) {
			return startPos.getHighestUtilityDirection();
		}

		// Sort the directions based on how close they bring the drone to the closest
		// positive station
		sortedDirections(goodDirections, closestPositiveStation);

		Direction bestDirection = goodDirections.get(0);

		Position nextPos = startPos.nextPosition(bestDirection);

		// If the drone is repeating itself (visiting the same position more than once,
		// return null and this will be handled in the playStateful() method)
		// This prevents the drone from being stuck in the same position
		if (visited(nextPos)) {
			return null;
		}

		return bestDirection;
	}

	/**
	 * Move to the next positive station and add the current station to the back of
	 * the list, indicating that it will be 'visited later'
	 * 
	 * @param currentStation   - current positive station
	 * @param positiveStations - list of positive stations within the map
	 * @return a new positive station
	 */
	private Station changeStations(Station currentStation, List<Station> positiveStations) {
		positiveStations.remove(currentStation);
		Station nextPositiveStation = positiveStations.get(0);
		positiveStations.add(currentStation);

		return nextPositiveStation;
	}

	/**
	 * Moves the Stateful drone and update the drone's flight path
	 */
	public void playStateful() {

		// Add the starting position to the flight path
		flightPath.add(startPos);

		// Get positive stations
		List<Station> positiveStations = getPositiveStations();

		// CONTROL VARIABLE
		// Check how many coins are in the map -- goal is to collect all
		double maxCoins = getMaxCoins(positiveStations);

		// Boolean to check whether or not the positive station has been reached
		boolean reached = false;

		// Direction to loop (after all the positive stations are visited)
		Direction loopDir = getBestRandomDirection(startPos);

		// Gets the closest positive station to the drone
		Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);

		// WHILE LOOP TO MOVE DRONE
		while (hasPower() && hasMoves()) {

			// Move drone to the closest positive station
			if (positiveStations.size() > 0) {
				if (reached) {
					closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);
				}

				// Get the best direction
				Direction bestDirection = getBestDirection(closestPositiveStation);

				// If the proposed direction leads to a loop, move to the next positive station
				if (bestDirection == null && positiveStations.size() > 1) {

					closestPositiveStation = changeStations(closestPositiveStation, positiveStations);
					bestDirection = getBestDirection(closestPositiveStation);

					// if the new positive station's direction is also visited, then just get
					// the best random direction
					if (bestDirection == null) {
						bestDirection = getBestRandomDirection(startPos);
					}

				} else if (bestDirection == null && positiveStations.size() <= 1) {
					// if there are no positive stations left, just go to the best random
					// direction, i.e. within play area and not towards a red station
					bestDirection = getBestRandomDirection(startPos);
				}

				// Change the direction of the loop to the bestDirection
				loopDir = bestDirection;
				Position nextPos = startPos.nextPosition(bestDirection);

				txtString += startPos.latitude + ",";
				txtString += startPos.longitude + ",";
				txtString += bestDirection + ",";

				// Move the drone to the best direction
				move(nextPos);

				txtString += startPos.latitude + ",";
				txtString += startPos.longitude + ",";

				// Add position to the flight path
				flightPath.add(startPos);

				// Charge from the closest station
				Station closestStation = startPos.getClosestStation();

				// Check if the closest station is within the charging range of the drone
				if (startPos.inRange(closestStation)) {

					System.out.println("COINS IN STATION: " + closestStation.getCoins());

					charge(closestStation);

					if (closestStation == closestPositiveStation) {
						positiveStations.remove(closestStation);
					}

					reached = true;
					txtString += coins + ",";
					txtString += power + "\n";
					printDroneDetails();
				} else {
					// Continue moving towards the positive station
					reached = false;
					txtString += coins + ",";
					txtString += power + "\n";
					printDroneDetails();
				}

			} else {

				// If all the positive stations have been visited, then go back and forth in a
				// loop for a clean path
				int didx = loopDir.ordinal() + 8;
				loopDir = Direction.values()[didx % 16];
				Position nextPos = startPos.nextPosition(loopDir);
				while (!nextPos.inPlayArea() || !nextPos.noRedStations()) {
					didx += 1;
					loopDir = Direction.values()[didx % 16];
					nextPos = startPos.nextPosition(loopDir);
				}

				txtString += startPos.latitude + ",";
				txtString += startPos.longitude + ",";
				txtString += loopDir + ",";

				// Move the drone to the proposed direction
				move(nextPos);

				txtString += startPos.latitude + ",";
				txtString += startPos.longitude + ",";

				// Add the current position to the flight path
				flightPath.add(startPos);

				// Charge from the closest station
				Station closestStation = startPos.getClosestStation();

				// Check if the closest station is in range
				if (startPos.inRange(closestStation)) {

					System.out.println("COINS IN STATION: " + closestStation.getCoins());

					charge(closestStation);
					txtString += coins + ",";
					txtString += power + "\n";
					printDroneDetails();
				} else {
					txtString += coins + ",";
					txtString += power + "\n";
					printDroneDetails();
				}
			}
		}

		System.out.println("MAX COINS IN THIS MAP IS " + maxCoins);

	}

}
