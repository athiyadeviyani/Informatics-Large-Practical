package uk.ac.ed.inf.powergrab;

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

	// =============================== DRONE STUFF ===============================

	// Checks if drone still has power
	public boolean hasPower() {
		return power >= 1.25;
	}

	// Check if drone still has moves
	public boolean hasMoves() {
		return moves < 250;
	}

	// REMOVE 
	public void printDroneDetails() {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("DRONE DEETS");
		System.out.println("POWER: " + power);
		System.out.println("COINS: " + coins);
		System.out.println("MOVES: " + moves);
		System.out.println(" ");
	}

	// =============================== POSITIVE STATIONS ===============================

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
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestPositiveStation.position)) {
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




	// =============================== CLOSEST STATIONS ===============================

	// Get closest stations
	public List<Station> getClosestStations(Position pos) {
		// Charge from the closest station
		List<Station> closestStations = new ArrayList<Station>();

		for (Station station : App.stations) {
			if (pos.distanceFromDrone(station.position) <= 0.00025 &&
					station.coins != 0.0 && station.power != 0.0 && station.position.inPlayArea()) {
				closestStations.add(station);
			}
		}
		return closestStations;
	}


	// Get closest station from a list of closest stations
	public Station getClosestStation(Position curPos, List<Station> closestStations) {
		// Initialise with the first station on the list

		if (closestStations.isEmpty()) {
			return null;
		}
		Station closestStation = closestStations.get(0);

		for (Station station : closestStations) {
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestStation.position)) {
				closestStation = station;
			}
		}

		return closestStation;
	}

	// =============================== GET DIRECTION ===============================
	// Sort Directions
	public void sortedDirections(List<Direction> directions, final Station cPS) {
		Collections.sort(directions, 
				new Comparator<Direction>() {
			public int compare(Direction d1, Direction d2) {
				return Double.compare(startPos.nextPosition(d1).distanceFromDrone(cPS.position), 
						startPos.nextPosition(d2).distanceFromDrone(cPS.position));
			}
		}
				);
	}


	public Direction getBestDirection(List<Position> flightPath, Station closestPositiveStation) {
		
		
		List<Direction> values = Arrays.asList(Direction.values());
		
		sortedDirections(values, closestPositiveStation);
		
		
		int idx = 0;
		Direction bestDirection = values.get(idx);
		Position nextPos = startPos.nextPosition(bestDirection);
		
		while (!nextPos.inPlayArea() || !noRedStations(bestDirection)) {

			idx += 1;
			
			bestDirection = values.get(idx % 16);
			nextPos = startPos.nextPosition(bestDirection);
			
		}
		
		boolean flag = true;
		
		for (Position pos : flightPath) {
			while ((nextPos.latitude == pos.latitude && nextPos.longitude == pos.longitude) || flag) { 
				idx += 1;
				
				bestDirection = values.get(idx % 16);
				nextPos = startPos.nextPosition(bestDirection);
				
				if (nextPos.inPlayArea() && noRedStations(bestDirection)) {
					flag = false;
				} else {
					flag = true;
				}

			}
		}
		
		

		System.out.println("BEST DIRECTION IS " + bestDirection);
		return bestDirection;
	}
	
	


	// =============================== RED STATION HANDLER ===============================

	// Checks if a direction has red stations
	public boolean noRedStations(Direction direction) {
		boolean flag = true;
		Position nextPos = startPos.nextPosition(direction);
		List<Station> closestStationsNextPos = getClosestStations(nextPos);
		if (!closestStationsNextPos.isEmpty()) {
			Station closestStation = getClosestStation(nextPos, closestStationsNextPos);
			if (closestStation.coins < 0.0) {
				flag = false;
			}
		}
		return flag;
	}



	// =============================== MAKE DRONE MOVE ===============================

	public List<Position> playStateful() {

		// Initialise the flight path for the drone
		List<Position> flightPath = new ArrayList<Position>();

		// Add the starting position to the flight path 
		flightPath.add(startPos);

		// Get positive stations
		List<Station> positiveStations = getPositiveStations();

		// CONTROL VARIABLES

		// Check how many red stations are hit -- goal is 0 
		int redHit = 0;

		// Check how many coins are in the map -- goal is to collect all
		double maxCoins = getMaxCoins(positiveStations);


		// WHILE LOOP TO MOVE DRONE

		while (hasPower() && hasMoves() && positiveStations.size() > 0) {

			// Move drone to the closest positive station
			Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);

			// Find the direction that brings you closer to the closestPositiveStation

			List<Direction> curDirs = new ArrayList<Direction>();
			curDirs.addAll(Arrays.asList(Direction.values()));

			System.out.println("================================== THIS IS MOVE NUMBER: " + moves + " ==================================");


			Direction bestDirection = getBestDirection(flightPath, closestPositiveStation);
			Position nextPos = startPos.nextPosition(bestDirection);
			

			// MOVE
			startPos = nextPos;	
			power -= 1.25;
			moves += 1;
			flightPath.add(startPos);
			

			// Check if the closestPositiveStation is in range
			if (startPos.distanceFromDrone(closestPositiveStation.position) <= 0.00025) {

				// Collect from closest Station
				List<Station> closestStations = getClosestStations(startPos);

				if (!closestStations.isEmpty()) {
					// Absorb from the closest station
					Station closestStation = getClosestStation(startPos, closestStations);
					System.out.println("COINS IN STATION: " + closestStation.coins);
					coins += closestStation.coins;
					power += Math.max(closestStation.power, -power);

					// Update the station's coins and power
					for (Station station : App.stations) {
						if (closestStation == station) {
							station.coins = 0.0;
							station.power = 0.0;
						}
					}

					positiveStations.remove(closestStation);

				}
				
				printDroneDetails();
			} else {
				printDroneDetails();
			}

		}




		System.out.println("MAX COINS IN THIS MAP IS " + maxCoins);

		// Return the final flight path
		return flightPath;
	}

}

