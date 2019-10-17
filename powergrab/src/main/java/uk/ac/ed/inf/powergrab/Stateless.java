package uk.ac.ed.inf.powergrab;

class Stateless extends Drone {
	
	public static Position pos;
	
	public Stateless(double coins, double power, int moves, Position pos) {
		super(coins, power, moves);
		this.pos = pos;
	}
	
	// methods are -- move pos.nextdirection, minus power, add movecount
	public static void move(Direction direction) {
		pos.nextPosition(direction);
		power-= 1.25;
		moves++;  // max is 250
	}
	
	// transfer()
	// -- collect coins and power 
		// look for the nearest station	by using euclidean distance with every station

}
