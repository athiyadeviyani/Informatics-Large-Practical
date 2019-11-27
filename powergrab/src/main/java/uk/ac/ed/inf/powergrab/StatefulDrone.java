package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

class StatefulDrone extends Drone {

	public StatefulDrone(Position startPos) {
		super(startPos);
	}

	// =============================== POSITIVE STATIONS
	// ===============================

	// Retrieve a list of Positive Stations
	public List<Station> getPositiveStations() {
		List<Station> positiveStations = new ArrayList<Station>();
		for (Station station : App.stations) {
			if (station.coins > 0.0 && station.power > 0.0) {
				positiveStations.add(station);
			}
		}
		return positiveStations;
	}

	// Gets the closest positive station from a list of positive stations
	public Station getClosestPositiveStation(Position curPos, List<Station> positiveStations) {
		// Initialise with the first station on the list
		Station closestPositiveStation = positiveStations.get(0);

		for (Station station : positiveStations) {
			if (curPos.distanceFromDrone(station.position) < curPos
					.distanceFromDrone(closestPositiveStation.position)) {
				closestPositiveStation = station;
			}
		}

		return closestPositiveStation;
	}

	public Double getMaxCoins(List<Station> stations) {
		double maxCoins = 0.0;
		for (Station station : stations) {
			maxCoins += station.coins;
		}
		return maxCoins;
	}

	// =============================== GET DIRECTION ===============================
	// Sort Directions
	public void sortedDirections(List<Direction> directions, final Station cPS) {
		Collections.sort(directions, new Comparator<Direction>() {
			public int compare(Direction d1, Direction d2) {
				return Double.compare(startPos.nextPosition(d1).distanceFromDrone(cPS.position),
						startPos.nextPosition(d2).distanceFromDrone(cPS.position));
			}
		});
	}

	public Direction getBestDirection(List<Position> flightPath, Station closestPositiveStation) {

		List<Direction> values = Arrays.asList(Direction.values());

		sortedDirections(values, closestPositiveStation);

		int idx = 0;
		Direction bestDirection = values.get(idx);

		Position nextPos = startPos.nextPosition(bestDirection);

		boolean flag = true;

		while (!nextPos.inPlayArea() || !nextPos.noRedStations()) {
			idx += 1;
			bestDirection = values.get(idx % 16);
			nextPos = startPos.nextPosition(bestDirection);
		}

		while (visited(nextPos, flightPath) || flag) {
			idx += 1;
			bestDirection = values.get(idx % 16);

			nextPos = startPos.nextPosition(bestDirection);
			if (nextPos.inPlayArea() && nextPos.noRedStations()) {
				flag = false;
			} else {
				flag = true;
			}
		}

		System.out.println("BEST DIRECTION IS " + bestDirection);
		return bestDirection;
	}

	public boolean visited(Position curPos, List<Position> path) {
		boolean visited = false;
		for (Position pos : path) {
			if (curPos.latitude == pos.latitude && curPos.longitude == pos.longitude) {
				visited = true;
			}
		}
		return visited;

	}

	// =============================== MAKE DRONE MOVE
	// ===============================

	public List<Position> playStateful(String filename) throws IOException {

		// Initialise the flight path for the drone
		List<Position> flightPath = new ArrayList<Position>();
		String result = "";

		// Add the starting position to the flight path
		flightPath.add(startPos);

		// Get positive stations
		List<Station> positiveStations = getPositiveStations();

		// CONTROL VARIABLES
		// Check how many coins are in the map -- goal is to collect all
		double maxCoins = getMaxCoins(positiveStations);

		// WHILE LOOP TO MOVE DRONE

		while (hasPower() && hasMoves() && positiveStations.size() > 0) {

			// Move drone to the closest positive station
			Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);

			Direction bestDirection = getBestDirection(flightPath, closestPositiveStation);
			Position nextPos = startPos.nextPosition(bestDirection);

			result += startPos.latitude + ",";
			result += startPos.longitude + ",";
			result += bestDirection + ",";

			// MOVE
			move(nextPos);

			result += startPos.latitude + ",";
			result += startPos.longitude + ",";

			flightPath.add(startPos);

			Station closestStation = startPos.getClosestStation();

			// Check if the closestPositiveStation is in range
			if (startPos.inRange(closestStation) && closestStation == closestPositiveStation) {

				System.out.println("COINS IN STATION: " + closestStation.coins);

				collect(closestStation);

				positiveStations.remove(closestPositiveStation);
				result += coins + ",";
				result += power + "\n";
				printDroneDetails();
			} else {
				result += coins + ",";
				result += power + "\n";
				printDroneDetails();
			}

		}

		App.writeToFile(filename, result);

		System.out.println("MAX COINS IN THIS MAP IS " + maxCoins);

		// Return the final flight path
		return flightPath;
	}

}
