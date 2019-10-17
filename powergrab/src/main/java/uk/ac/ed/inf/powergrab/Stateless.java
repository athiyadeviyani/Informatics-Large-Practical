package uk.ac.ed.inf.powergrab;

class Stateless extends Drone {
	
	public Position pos;
	
	public Stateless(double coins, Position pos) {
		super(coins);
		this.pos = pos;
	}
	
	// methods are -- move pos.nextdirection, minus power, add movecount
	
	// transfer()
	// -- collect coins and power 
		// look for the nearest station	by using euclidean distance with every station

}
