package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

public class Drone {

	protected double coins = 0.0;
	protected double power = 250.0;
	protected int moves = 0;
	protected Position startPos;

	// Result string to be written to the output .txt file
	public String txtString = "";

	// Final flight path of the drone
	public List<Position> flightPath = new ArrayList<Position>();

	public Drone(Position startPos) {
		this.startPos = startPos;
	}

	/**
	 * Checks if the drone still has sufficient power to make 1 move
	 * 
	 * @return true if the drone still has power, false otherwise
	 */
	protected boolean hasPower() {
		return power >= 1.25;
	}

	/**
	 * Checks if the drone still has not exceeded the allowed number of moves (250)
	 * 
	 * @return true if the drone has not exceeded 250 moves, false otherwise
	 */
	protected boolean hasMoves() {
		return moves < 250;
	}

	/**
	 * Moves the drone to the next position and update the number of moves and
	 * amount of power accordingly
	 * 
	 * @param nextPos - the position that the drone will move to
	 */
	protected void move(Position nextPos) {
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
	protected void charge(Station closestStation) {
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
	protected void printDroneDetails() {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("DRONE DEETS");
		System.out.println("POWER: " + power);
		System.out.println("COINS: " + coins);
		System.out.println("MOVES: " + moves);
		System.out.println(" ");
	}

}
