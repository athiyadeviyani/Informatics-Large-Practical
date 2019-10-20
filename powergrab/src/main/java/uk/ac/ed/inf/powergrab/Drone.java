package uk.ac.ed.inf.powergrab;

public class Drone {
	
	public double coins = 0.0;
	public double power = 250.0;
	public int moves = 0;
	public Position startPos;
	// power, moveCount, doulbe[][] json
	
	public Drone(Position startPos) {
		this.startPos = startPos;
	}

}
