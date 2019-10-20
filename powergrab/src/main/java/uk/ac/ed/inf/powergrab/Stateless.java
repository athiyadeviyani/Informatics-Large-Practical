package uk.ac.ed.inf.powergrab;

class Stateless extends Drone {
	
	
	public Stateless(Position startPos) {
		super(startPos);
	}
	
	// methods are -- move pos.nextdirection, minus power, add movecount
	public void move(Direction direction) {
		Position newPos = startPos.nextPosition(direction);
		//startPos = startPos.nextPosition(direction);
		System.out.println("THIS IS THE POSITION");
		System.out.println(startPos.longitude);
		System.out.println(newPos.longitude);
		power -= 1.25;
		moves++;  // max is 250
	}
	
	
	
	
	// transfer()
	// -- collect coins and power 
		// look for the nearest station	by using euclidean distance with every station

}
