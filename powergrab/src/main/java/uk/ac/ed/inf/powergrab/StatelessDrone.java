package uk.ac.ed.inf.powergrab;

class StatelessDrone extends Drone {

	public StatelessDrone(Position startPos) {
		super(startPos);
	}

	/**
	 * Gets the best direction for the drone that does not lead to a play area
	 * boundary or a negatively charged station. If the proposed direction leads the
	 * drone to a play area boundary or a negatively charged station, then avoid
	 * that position by turning in a clockwise direction until the drone is in a
	 * safe position.
	 * 
	 * @param curPos - current position of the drone
	 * @return best direction for the drone to go to 
	 */
	private Direction getBestDirection(Position curPos) {

		Direction bestDirection = curPos.getHighestUtilityDirection();

		Position nextPos = curPos.nextPosition(bestDirection);

		int dirIndex = bestDirection.ordinal();
		// If the next position leads to a negatively charged station or a play area
		// boundary, move in a clockwise direction until safe
		for (int i = 0; i < 16; i++) {
			if (nextPos.noRedStations() && nextPos.inPlayArea()) {
				break;
			}
			dirIndex += 1;
			bestDirection = Direction.values()[(dirIndex) % 16];
			nextPos = curPos.nextPosition(bestDirection);
		}

		return bestDirection;
	}

	/**
	 * Moves the Stateless drone and update the drone's flight path
	 */
	public void playStateless() {

		// Add the starting position to the flightPath
		flightPath.add(startPos);

		// Move the drone as long as it has sufficient power and has not exceeded the
		// allowed number of moves
		while (hasPower() && hasMoves()) {

			// Find the best direction and move there
			Direction bestDirection = getBestDirection(startPos);
			Position nextPos = startPos.nextPosition(bestDirection);

			txtString += startPos.latitude + ",";
			txtString += startPos.longitude + ",";
			txtString += bestDirection + ",";

			// Move the drone towards the best direction
			move(nextPos);

			txtString += startPos.latitude + ",";
			txtString += startPos.longitude + ",";

			// Add the current position to the flight path
			flightPath.add(startPos);

			// Get the closest station
			Station closestStation = startPos.getClosestStation();

			// Charge from the closest station if it is within charging range
			if (startPos.inRange(closestStation)) {
				charge(closestStation);
				txtString += coins + ",";
				txtString += power + "\n";
				printDroneDetails();
			} else {
				// Continue moving
				txtString += coins + ",";
				txtString += power + "\n";
				printDroneDetails();
			}

		}

	}

}