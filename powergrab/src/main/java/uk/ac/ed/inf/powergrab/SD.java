
package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

// STRATEGY
// Get a list of positiveStations
// From the positiveStations
// Get a utility metric that has m*coins - n*distance (m and n and weights)
// Highest station is the one that has the highest utility metric (lotsa coins and lesser distance)
class SD extends Drone {

	public SD(Position startPos) {
		super(startPos);
	}

	public List<Station> getPositiveStations() {
		List<Station> positiveStations = new ArrayList<Station>();
		for (Station station : App.stations) {
			if (station.coins > 0.0 && station.power > 0.0) {
				positiveStations.add(station);
			}
		}
		return positiveStations;
	}

	public Station getClosestPositiveStation(Position curPos, List<Station> positiveStations) {
		// Initialise with the first station on the list

		Station closestStation = positiveStations.get(0);

		for (Station station : positiveStations) {
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestStation.position)) {
				closestStation = station;

			}
		}

		System.out.println(positiveStations.size());
		return closestStation;
	}

	public void sortedPositiveStations(List<Station> positiveStations, final Position curPos) {
		Collections.sort(positiveStations, 
				new Comparator<Station>() {
			public int compare(Station s1, Station s2) {
				return Double.compare(curPos.distanceFromDrone(s1.position), (curPos.distanceFromDrone(s2.position)));
			}
		}
				);
	}

	// check if drone still has power
	public boolean hasPower() {
		return power >= 1.25;
	}

	// check if drone still has moves
	public boolean hasMoves() {
		return moves < 250;
	}

	public void printDroneDetails() {
		System.out.println("===========================");
		System.out.println("DRONE DEETS");
		System.out.println("POWER: " + power);
		System.out.println("COINS: " + coins);
		System.out.println("MOVES: " + moves);
		System.out.println("===========================");
	}


	public Double getMaxCoins(List<Station> stations) {
		double maxCoins = 0.0;
		for (Station station : stations) {
			maxCoins += station.coins;
		}
		return maxCoins;
	}

	public List<Station> getClosestStations() {
		// Charge from the closest station
		List<Station> closestStations = new ArrayList<Station>();

		for (Station station : App.stations) {
			if (startPos.distanceFromDrone(station.position) <= 0.00025 &&
					station.coins != 0.0 && station.power != 0.0 && station.position.inPlayArea()) {
				closestStations.add(station);
			}
		}
		return closestStations;
	}

	public boolean noRedStations(Direction direction) {
		boolean flag = true;
		Position nextPos = startPos.nextPosition(direction);
		for (Station station : App.stations) {
			if(nextPos.distanceFromDrone(station.position) <= 0.00025 && station.coins < 0.0) {
				flag = false;
			}
		}
		return flag;
	}
	public Station getClosestStation(Position curPos, List<Station> closestStations) {
		// Initialise with the first station on the list

		if (closestStations.isEmpty()) {
			return null;
		}
		Station closestStation = closestStations.get(0);

		for (Station station : closestStations) {
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestStation.position)) {
				closestStation = station;
			}
		}

		return closestStation;
	}


	public List<Position> playStateful() {

		// Initialise the flight path of the drone 
		List<Position> flightPath = new ArrayList<Position>();

		// Add the starting position to flightPath
		flightPath.add(startPos);

		int redHit = 0;

		List<Station> positiveStations = getPositiveStations();

		double maxCoins = getMaxCoins(positiveStations);

		System.out.println(positiveStations);


		for (Station station : positiveStations) {
			station.printStationDetails();
			Double distance = startPos.distanceFromDrone(station.position);
			System.out.println(distance);
		}


		while (hasPower() && hasMoves() && positiveStations.size() > 0) {

			//Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);

			sortedPositiveStations(positiveStations, startPos);
			System.out.println(positiveStations);

			int st_idx = 0;
			Station closestPositiveStation = positiveStations.get(st_idx);

			int index = 0;
			Position nextPos = startPos.nextPosition(Direction.values()[index]);
			Double minDistance = nextPos.distanceFromDrone(closestPositiveStation.position);



			for (int i = 0; i < 16; i++) {

				Direction d = Direction.values()[i];

				Position newPos = startPos.nextPosition(d);

				if (newPos.distanceFromDrone(closestPositiveStation.position) <= minDistance) {
					
					minDistance = newPos.distanceFromDrone(closestPositiveStation.position);

					System.out.println("THIS IS MOVE NUMBER " + moves);
					System.out.println("THERE ARE NO RED STATIONS HERE: " + noRedStations(d));
					
					

					
					if (!noRedStations(d)) {
						System.out.println("===========================");
						System.out.println("DRONE DEETS");
						System.out.println("POWER: " + power);
						System.out.println("COINS: " + coins);
						System.out.println("MOVES: " + moves);
						System.out.println("===========================");

						continue;
					}

					else if (!newPos.inPlayArea() && !noRedStations(d)) {


						continue;
					}


					
					
//					if (!nextPos.inPlayArea()) {
//						continue;
//					}
					System.out.println("CLOSEST FOUND AFTER " + i + " TRIES");
					
					nextPos = newPos;
					



				} 
				
				if (!noRedStations(d)) {
					continue;
				}

				if (!newPos.inPlayArea()) {
					continue;
				}
				
//				nextPos = newPos;
			
				
				

			}



			startPos = nextPos;

			// Update power and moves
			moves += 1;
			power -= 1.25;


			// Add the flight  path
			flightPath.add(startPos);




			List<Station> closestStations = getClosestStations();

			if (startPos.distanceFromDrone(closestPositiveStation.position) > 0.00025) {

				closestStations = getClosestStations();

				if (!closestStations.isEmpty()) {
					Station closestStation = getClosestStation(startPos, closestStations);
					if (closestStation.coins != 0.0) {
						if (closestStation.coins < 0.0) {
							System.out.println("MAYDAY MAYDAY RED STATION");
							redHit += 1;
						}
						coins += closestStation.coins;
						power += Math.max(closestStation.power, -power);

						System.out.println("===========================");
						System.out.println("DRONE DEETS");
						System.out.println("POWER: " + power);
						System.out.println("COINS: " + coins);
						System.out.println("MOVES: " + moves);
						System.out.println("===========================");

						for (Station station : App.stations) {
							if (closestStation == station) {
								station.coins = 0.0;
								station.power = 0.0;
							}
						}
					}
				}

				System.out.println("===========================");
				System.out.println("DRONE DEETS");
				System.out.println("POWER: " + power);
				System.out.println("COINS: " + coins);
				System.out.println("MOVES: " + moves);
				System.out.println("===========================");

				continue;

			} else {

				closestStations = getClosestStations();

				if (!closestStations.isEmpty()) {
					Station closestStation = getClosestStation(startPos, closestStations);
					if (closestStation.coins != 0.0) {
						if (closestStation.coins < 0.0) {
							System.out.println("MAYDAY MAYDAY RED STATION");
							redHit += 1;

						}
						coins += closestStation.coins;
						power += Math.max(closestStation.power, -power);

						System.out.println("===========================");
						System.out.println("DRONE DEETS");
						System.out.println("POWER: " + power);
						System.out.println("COINS: " + coins);
						System.out.println("MOVES: " + moves);
						System.out.println("===========================");

						for (Station station : App.stations) {
							if (closestStation == station) {
								station.coins = 0.0;
								station.power = 0.0;
							}
						}
					}


					positiveStations.remove(closestPositiveStation);

				}






			}





		}

		System.out.println("MAX COINS IN THIS MAP IS: " + maxCoins);

		System.out.println("I HAVE HIT: " + redHit);


		return flightPath;
	}




}
