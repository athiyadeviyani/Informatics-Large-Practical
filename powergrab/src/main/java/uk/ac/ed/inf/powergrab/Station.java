package uk.ac.ed.inf.powergrab;

public class Station {

	private double coins;
	private double power;
	private final Position position;

	public Station(double coins, double power, Position position) {
		this.coins = coins;
		this.power = power;
		this.position = position;
	}

	/**
	 * Gets the amount of coins available in a station
	 * 
	 * @return amount of coins available in a station
	 */
	public double getCoins() {
		return this.coins;
	}

	/**
	 * Sets the amount of coins available in a station to the input parameter
	 * 
	 * @param coins - amount of coins to be set
	 */
	public void setCoins(Double coins) {
		this.coins = coins;
	}

	/**
	 * Gets the amount of power available in a station
	 * 
	 * @return amount of power available in a station
	 */
	public double getPower() {
		return this.power;
	}

	/**
	 * Sets the amount of power available in a station to the input parameter
	 * 
	 * @param power - amount of power to be set
	 */
	public void setPower(Double power) {
		this.power = power;
	}
	
	/**
	 * Gets the position of the station
	 * @return position of the station
	 */
	public Position getPosition() {
		return this.position;
	}

}
