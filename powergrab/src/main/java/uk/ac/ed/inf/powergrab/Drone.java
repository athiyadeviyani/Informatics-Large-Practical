package uk.ac.ed.inf.powergrab;

import java.util.HashMap;

public class Drone {

	public double coins = 0.0;
	public double power = 250.0;
	public int moves = 0;
	public Position startPos;

	public Drone(Position startPos) {
		this.startPos = startPos;
	}

	// Checks if drone still has power
	public boolean hasPower() {
		return power >= 1.25;
	}

	// Check if drone still has moves
	public boolean hasMoves() {
		return moves < 250;
	}

	// Move the drone to a new position
	public void move(Position nextPos) {
		startPos = nextPos;
		moves += 1;
		power -= 1.25;
	}

	// Collect Coins and Power from nearest station
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
	
	

	// Print Drone details (for Debugging)
	public void printDroneDetails() {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("DRONE DEETS");
		System.out.println("POWER: " + power);
		System.out.println("COINS: " + coins);
		System.out.println("MOVES: " + moves);
		System.out.println(" ");
	}

}
