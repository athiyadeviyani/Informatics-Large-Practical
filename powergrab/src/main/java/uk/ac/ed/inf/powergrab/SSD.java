package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class SSD extends Drone {

	public SSD(Position startPos) {
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

	public Direction getBestDirection(Position currentPosition) {

		HashMap<Direction, Double> DirectionStation = new HashMap<Direction, Double>();

		for (Direction direction : Direction.values()) {
			Position nextPos = currentPosition.nextPosition(direction);
			if (nextPos.inPlayArea()) {
				for (Station station : App.stations) {
					// Look for all the stations that are within 0.00025 from nextPos
					if (nextPos.distanceFromDrone(station.position) <= 0.00025 
							&& station.position.inPlayArea() 
							&& station.coins != 0.0
							&& station.power != 0.0) {

						if (DirectionStation.get(direction) != null) {
							Double current = DirectionStation.get(direction);
							DirectionStation.put(direction, current + station.coins);
						} 
						DirectionStation.put(direction, station.coins);

					}
				}
			}
		}

		double maxCoins = 0.0;

		Direction bestDirection = Position.getRandomDirection(Direction.values());

		


		// get the richest direction
		for (Direction direction : DirectionStation.keySet()) {
			if (DirectionStation.get(direction) > maxCoins) {
				maxCoins = DirectionStation.get(direction);
				bestDirection = direction;
			}
		}
		
		while (DirectionStation.get(bestDirection) != null && 
				!noRedStations(bestDirection)) {
			bestDirection = Position.getRandomDirection(Direction.values());
		}
		
		
		if (DirectionStation.isEmpty()) {
			System.out.println("DIRECTION STATION IS EMPTY");
		}

		System.out.println("REACHABLE STATIONS: " + DirectionStation);
		System.out.println("BEST DIRECTION: " + bestDirection);
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
				nextPos = startPos.nextPosition(getBestDirection(nextPos));
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
			List<Station> closestStations = getClosestStations(startPos);

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
