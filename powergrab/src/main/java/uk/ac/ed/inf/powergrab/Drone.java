package uk.ac.ed.inf.powergrab;

public class Drone {

	public double coins = 0.0;
	public double power = 250.0;
	public int moves = 0;
	public Position startPos;

	public Drone(Position startPos) {
		this.startPos = startPos;
	}

	/**
	 * Checks if the drone still has sufficient power to make 1 move
	 * 
	 * @return true if the drone still has power, false otherwise
	 */
	public boolean hasPower() {
		return power >= 1.25;
	}

	/**
	 * Checks if the drone still has not exceeded the allowed number of moves (250)
	 * 
	 * @return true if the drone has not exceeded 250 moves, false otherwise
	 */
	public boolean hasMoves() {
		return moves < 250;
	}

	/**
	 * Moves the drone to the next position and update the number of moves and
	 * amount of power accordingly
	 * 
	 * @param nextPos - the position that the drone will move to
	 */
	public void move(Position nextPos) {
		startPos = nextPos;
		moves += 1;
		power -= 1.25;
	}

	/**
	 * Charge (collect coins and power) from the closest station and update the
	 * coins and power of the station to 0.0
	 * 
	 * @param closestStation
	 */
	public void charge(Station closestStation) {
		coins += closestStation.getCoins();
		power += Math.max(closestStation.getCoins(), -power);

		// Update the station's coins and power
		for (Station station : App.stations) {
			if (closestStation == station) {
				station.setCoins(0.0);
				station.setCoins(0.0);
			}
		}
	}

	/**
	 * Print the drone's power, coins, and number of moves for debugging purposes
	 */
	public void printDroneDetails() {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("DRONE DEETS");
		System.out.println("POWER: " + power);
		System.out.println("COINS: " + coins);
		System.out.println("MOVES: " + moves);
		System.out.println(" ");
	}

}
