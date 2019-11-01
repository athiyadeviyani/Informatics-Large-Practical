package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class StatelessDrone extends Drone {

	public StatelessDrone(Position startPos) {
		super(startPos);
	}

	// check if drone still has power
	public boolean hasPower() {
		return power >= 1.25;
	}

	// check if drone still has moves
	public boolean hasMoves() {
		return moves < 250;
	}

	public Direction getBestDirection(Position currentPosition) {

		HashMap<Direction, Double> DirectionCoins = new HashMap<Direction, Double>();

		for (Direction direction : Direction.values()) {
			Position nextPos = currentPosition.nextPosition(direction);
			if (nextPos.inPlayArea()) {
				for (Station station : App.stations) {
					// Look for all the stations that are within 0.00025 from nextPos
					if (nextPos.distanceFromDrone(station.position) <= 0.00025 
							&& station.position.inPlayArea() 
							&& !station.visited ) {
						DirectionCoins.put(direction, station.coins);
					}
				}
			}
		}

		double maxCoins = 0.0;

		Direction bestDirection = Position.getRandomDirection(Direction.values());

		// get the richest direction
		for (Direction direction : DirectionCoins.keySet()) {
			if (DirectionCoins.get(direction) > maxCoins) {
				maxCoins = DirectionCoins.get(direction);
				bestDirection = direction;
			}
		}
		return bestDirection;
	}

	public Station getClosestStation(Position curPos, List<Station> closestStations) {
		// Initialise with the first station on the list
		Station closestStation = closestStations.get(0);

		for (Station station : closestStations) {
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestStation.position)) {
				closestStation = station;
			}
		}

		return closestStation;
	}
	
	public List<Station> getClosestStations() {
		// Charge from the closest station
		List<Station> closestStations = new ArrayList<Station>();

		for (Station station : App.stations) {
			if (startPos.distanceFromDrone(station.position) <= 0.00025 &&
					!station.visited && station.position.inPlayArea()) {
				closestStations.add(station);
			}
		}
		return closestStations;
	}

	public List<Position> playStateless() {

		// Initialise the flightPath 
		List<Position> flightPath = new ArrayList<Position>();

		// Add the starting position to flightPath
		flightPath.add(startPos);

		while (hasPower() && hasMoves()) {

			// Find the best direction and move there
			Direction bestDirection = getBestDirection(startPos);
			Position nextPos = startPos.nextPosition(bestDirection);
			
			// when nextPos is not inPlayArea
			while (!nextPos.inPlayArea()) {
				nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
				// nextPos = startPos.nextPosition(getBestDirection(startPos));
			}
			
			// update current position
			startPos = nextPos;

			// Update power and moves
			moves += 1;
			power -= 1.25;

			// Add the flight  path
			flightPath.add(startPos);

			// Get the closest stations --> stations in range
			List<Station> closestStations = getClosestStations();
			
			// No stations in range
			if (closestStations.isEmpty()) {
				System.out.println("NO STATIONS IN RANGE");
				System.out.println("DRONE DEETS");
				System.out.println("POWER: " + power);
				System.out.println("COINS: " + coins);
				System.out.println("MOVES: " + moves);
				System.out.println("===========================");
				continue;  // goes back to the top of the while loop				
			} 

			System.out.println("STATIONS IN RANGE");
			// Charge from the closest station
			Station closestStation = getClosestStation(startPos, closestStations);
			coins += closestStation.coins;
			power += Math.max(closestStation.power, -power);

			// Update the station details from the global list
			for (Station station : App.stations) {
				if (closestStation == station) {
					station.coins = 0.0;
					station.power = 0.0;
					station.visited = true;
				}
			}


			System.out.println("DRONE DEETS");
			System.out.println("POWER: " + power);
			System.out.println("COINS: " + coins);
			System.out.println("MOVES: " + moves);
			System.out.println("===========================");


		}

		return flightPath;
	}

}
