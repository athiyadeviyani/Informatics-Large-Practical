package uk.ac.ed.inf.powergrab;

class Stateless extends Drone {
	
	
	public Stateless(Position startPos) {
		super(startPos);
	}
	
	// methods are -- move pos.nextdirection, minus power, add movecount
	public void move(Direction direction) {
		startPos = startPos.nextPosition(direction);
		power -= 1.25;
		moves++;  // max is 250
	}
	
	
	// calculate distance
	public double distance(Position currentPos, Position newPos) {
		double currentLatitude = currentPos.latitude;
		double currentLongitude = currentPos.longitude;
		
		double newLatitude = currentPos.latitude;
		double newLongitude = currentPos.longitude;
		
		double result = Math.sqrt(Math.pow((newLatitude-currentLatitude), 2) + Math.pow((newLongitude-currentLongitude), 2));
		
		return result;
	}
	
	// transfer()
	// -- collect coins and power 
		// look for the nearest station	by using euclidean distance with every station

}
