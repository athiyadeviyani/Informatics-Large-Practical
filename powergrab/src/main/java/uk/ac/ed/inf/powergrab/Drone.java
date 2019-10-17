package uk.ac.ed.inf.powergrab;

public class Drone {
	
	public double coins = 0.0;
	public double power = 250.0;
	public int moves = 0;
	// power, moveCount, doulbe[][] json
	
	public Drone(double coins, double power, int moves) {
		this.coins = coins;
		this.power = power;
		this.moves = moves;
	}

}
