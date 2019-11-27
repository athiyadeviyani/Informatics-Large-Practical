package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class StatelessDrone extends Drone {

	public StatelessDrone(Position startPos) {
		super(startPos);
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
					if (nextPos.distanceFromDrone(station.position) <= 0.00025 && station.position.inPlayArea()
							&& station.coins != 0.0 && station.power != 0.0) {

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
		int dirIndex = bestDirection.ordinal();
		// If there are all red stations or exceed boundary, move clockwise until safe
		while (!nextPos.noRedStations() || !nextPos.inPlayArea()) {
			dirIndex += 1;
			bestDirection = Direction.values()[(dirIndex) % 16];
//			bestDirection = Position.getRandomDirection(Direction.values());
			nextPos = curPos.nextPosition(bestDirection);
		}

		return bestDirection;
	}

	// Make drone move
	public List<Position> playStateless(String filename) throws IOException {

		// Initialise the flightPath
		List<Position> flightPath = new ArrayList<Position>();
		String result = "";

		// Add the starting position to the flightPath
		flightPath.add(startPos);

		while (hasPower() && hasMoves()) {

			// Find the best direction and move there
			Direction bestDirection = getBestDirection(startPos);
			Position nextPos = startPos.nextPosition(bestDirection);

			result += startPos.latitude + ",";
			result += startPos.longitude + ",";
			result += bestDirection + ",";

			// Move
			move(nextPos);

			result += startPos.latitude + ",";
			result += startPos.longitude + ",";

			// Add the flight path
			flightPath.add(startPos);

			// Get the closest station
			Station closestStation = startPos.getClosestStation();

			if (startPos.inRange(closestStation)) {
				collect(closestStation);
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

		return flightPath;
	}

}