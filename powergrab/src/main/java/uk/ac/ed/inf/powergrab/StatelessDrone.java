package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class StatelessDrone extends Drone {

	public StatelessDrone(Position startPos) {
		super(startPos);
	}

	// Check if the drone has power
	public boolean hasPower() {
		return power >= 1.25;
	}

	// Check if the drone still has moves
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


	// Get the closest station
	public Station getClosestStation(Position curPos) {
		Station closestStation = App.stations.get(0);

		for (Station station : App.stations) {
			if (curPos.distanceFromDrone(closestStation.position) >
			curPos.distanceFromDrone(station.position)) {
				closestStation = station;
			}
		}

		return closestStation;
	}


	// Check if a station is within range
	public boolean inRange(Position curPos, Station station) {
		if (curPos.distanceFromDrone(station.position) <= 0.00025) {
			return true;
		}
		return false;
	}


	// Check if a direction leads you to a red station
	public boolean noRedStations(Direction direction) {
		Position nextPos = startPos.nextPosition(direction);
		Station closestStation = getClosestStation(nextPos);

		if (inRange(nextPos, closestStation) && closestStation.coins < 0.0) {
			return false;
		}

		return true;
	}


	// Get the best direction
	public Direction getBestDirection(Position curPos) {
		Direction bestDirection = Position.getRandomDirection(Direction.values());

		HashMap<Direction, Double> directionStation = new HashMap<Direction, Double>();
		Position nextPos = curPos.nextPosition(bestDirection);

		for (Direction direction : Direction.values()) {
			nextPos = curPos.nextPosition(direction);
			if (nextPos.inPlayArea()) {
				for (Station station : App.stations) {
					// Look for all the stations that are within 0.00025 from nextPos
					if (nextPos.distanceFromDrone(station.position) <= 0.00025 
							&& station.position.inPlayArea() 
							&& station.coins != 0.0
							&& station.power != 0.0) {

						if (directionStation.get(direction) != null) {
							Double current = directionStation.get(direction);
							directionStation.put(direction, current + station.coins);
						} 
						directionStation.put(direction, station.coins);

					}
				}
			}
		}


		double maxCoins = 0.0;

		for (Direction direction : directionStation.keySet()) {
			if (directionStation.get(direction) > maxCoins) {
				maxCoins = directionStation.get(direction);
				bestDirection = direction;
			}
		}
		
		nextPos = curPos.nextPosition(bestDirection);
//		int dirIndex = bestDirection.ordinal();
		// If there are all red stations or exceed boundary, move clockwise until safe
		while(!noRedStations(bestDirection) || !nextPos.inPlayArea()) {
//			dirIndex += 1;
//			bestDirection = Direction.values()[(dirIndex) % 16];
			bestDirection = Position.getRandomDirection(Direction.values());
			nextPos = curPos.nextPosition(bestDirection);
		}


		return bestDirection;
	}

	// Collect coins
	public void collect(Station closestStation) {
		coins += closestStation.coins;
		power += Math.max(closestStation.power, -power);

		// Update the station's coins and power
		for (Station station : App.stations) {
			if (closestStation == station) {
				station.coins = 0.0;
				station.power = 0.0;
			}
		}
	}


	// Make drone move
	public List<Position> playStateless() {

		// Initialise the flightPath 
		List<Position> flightPath = new ArrayList<Position>();

		// Add the starting position to the flightPath
		flightPath.add(startPos);

		while (hasPower() && hasMoves()) {

			// Find the best direction and move there
			Direction bestDirection = getBestDirection(startPos);
			Position nextPos = startPos.nextPosition(bestDirection);


			// Move
			startPos = nextPos;
			moves += 1;
			power -= 1.25;

			// Add the flight path
			flightPath.add(startPos);

			// Get the closest station
			Station closestStation = getClosestStation(startPos);

			if (inRange(startPos, closestStation)) {
				collect(closestStation);
				printDroneDetails();
			} else {
				printDroneDetails();
			}

		}


		return flightPath;
	}



}