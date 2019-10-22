package uk.ac.ed.inf.powergrab;

public class Station {
	
	public double coins;
	public double power;
	public Position position;
	
	public Station(double coins, double power, Position position) {
		this.coins = coins;
		this.power = power;
		this.position = position;
	}
	
	public boolean negativeStation() {
		return coins < 0.0 || power < 0.0;
	}
	

}
