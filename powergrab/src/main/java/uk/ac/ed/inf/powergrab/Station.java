package uk.ac.ed.inf.powergrab;

public class Station {
	
	public double coins;
	public double power;
	public Position position;
	public boolean visited;
	
	public Station(double coins, double power, Position position) {
		this.coins = coins;
		this.power = power;
		this.position = position;
	}
	
	public boolean negativeStation() {
		return coins < 0.0 || power < 0.0;
	}
	
	public void printStationDetails() {
		System.out.println("===========================");
		System.out.println("STATION DETAILS");
		System.out.println("COINS: " + coins);
		System.out.println("POWER: " + power);
		System.out.println("LONGITUDE: " + position.longitude);
		System.out.println("LATITUDE: " + position.latitude);
		System.out.println("===========================");
	}
	

}
