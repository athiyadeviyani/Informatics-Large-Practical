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

	// ===== POSITIVE STATIONS

	// Retrieve a list of Positive Stations
	private List<Station> getPositiveStations() {
		List<Station> positiveStations = new ArrayList<Station>();
		for (Station station : App.stations) {
			if (station.coins > 0.0 && station.power > 0.0) {
				positiveStations.add(station);
			}
		}
		return positiveStations;
	}

	// Gets the closest positive station from a list of positive stations
	private Station getClosestPositiveStation(Position curPos, List<Station> positiveStations) {
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

	private Double getMaxCoins(List<Station> stations) {
		double maxCoins = 0.0;
		for (Station station : stations) {
			maxCoins += station.coins;
		}
		return maxCoins;
	}

	// ===== DIRECTIONS
	
	// Sort Directions
	private void sortedDirections(List<Direction> directions, final Station cPS) {
		Collections.sort(directions, new Comparator<Direction>() {
			public int compare(Direction d1, Direction d2) {
				return Double.compare(startPos.nextPosition(d1).distanceFromDrone(cPS.position),
						startPos.nextPosition(d2).distanceFromDrone(cPS.position));
			}
		});
	}
	
	
	
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
			// or to a red station, i.e. pick the direction that brings you the furthest from it
			sortedDirections(values, closestStation);
			bestRandomDirection = values.get(values.size() - i - 1);
			nextPos = pos.nextPosition(bestRandomDirection);
		}
		
		return bestRandomDirection;
		
	}

	private Direction getBestDirection(List<Position> flightPath, Station closestPositiveStation) {

		List<Direction> goodDirections = new ArrayList<Direction>();
		
		for (Direction direction : Direction.values()) {
			Position nextPos = startPos.nextPosition(direction);
			if (nextPos.noRedStations() && nextPos.inPlayArea()) {
				goodDirections.add(direction);
			}
		}
		
		
		// If spawn in a bad region, prevent getting stuck
		if (goodDirections.isEmpty()) {
			return startPos.getHighestUtilityDirection();
		}
		
		sortedDirections(goodDirections, closestPositiveStation);
		
		int idx = 0;
		Direction bestDirection = goodDirections.get(idx);

		System.out.println(goodDirections);
		Position nextPos = startPos.nextPosition(bestDirection);
		

		
		Direction[] goodDirArray = new Direction[goodDirections.size()];
        goodDirArray = goodDirections.toArray(goodDirArray);


	
		if (visited(nextPos, flightPath)) {
			return null;
		}
		
		System.out.println("COINS IN NEXT STATION: " + nextPos.getClosestStation().coins);
		System.out.println(goodDirArray);
		System.out.println("BEST DIRECTION IS " + bestDirection);
		return bestDirection;
	}
	
	private Station changeStations(Station currentStation, List<Station> positiveStations) {
		positiveStations.remove(currentStation);
		Station nextPositiveStation = positiveStations.get(0);
		positiveStations.add(currentStation);
		
		return nextPositiveStation;
	}

	private boolean visited(Position curPos, List<Position> path) {

		int count = 0;
		for (Position pos : path) {
			if (curPos.latitude == pos.latitude && curPos.longitude == pos.longitude) {

				count += 1;
			}
		}
		return count > 1;

	}

	// ====== MAKE DRONE MOVE

	public List<Position> playStateful(String filename) throws IOException {

		// Initialise the flight path for the drone
		List<Position> flightPath = new ArrayList<Position>();
		String result = "";

		// Add the starting position to the flight path
		flightPath.add(startPos);

		// Get positive stations
		List<Station> positiveStations = getPositiveStations();
		List<Station> visitedStations = new ArrayList<Station>();

		// CONTROL VARIABLES
		// Check how many coins are in the map -- goal is to collect all
		double maxCoins = getMaxCoins(positiveStations);

		// WHILE LOOP TO MOVE DRONE
		
		boolean reached = false;
		Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);

		while (hasPower() && hasMoves() && positiveStations.size() > 0) {

			// Move drone to the closest positive station
			
			if (reached) {
				closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);
			}
			
			
			Direction bestDirection = getBestDirection(flightPath, closestPositiveStation);
			if (bestDirection == null && positiveStations.size() > 1) {

				closestPositiveStation = changeStations(closestPositiveStation, positiveStations);
				bestDirection = getBestDirection(flightPath, closestPositiveStation);
				
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
			
			System.out.println(positiveStations.size());
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
			if (startPos.inRange(closestStation)) {

				System.out.println("COINS IN STATION: " + closestStation.coins);

				collect(closestStation);
				visitedStations.add(closestStation);
				
				if (closestStation == closestPositiveStation) {
					positiveStations.remove(closestStation);
				}
				reached = true;
				result += coins + ",";
				result += power + "\n";
				printDroneDetails();  
			} else {
				reached = false;
				result += coins + ",";
				result += power + "\n";
				printDroneDetails();
			}

		}

		Output.writeToFile(filename, result);

		System.out.println("MAX COINS IN THIS MAP IS " + maxCoins);

		// Return the final flight path
		return flightPath;
	}

}
