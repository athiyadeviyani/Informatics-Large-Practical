package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// STRATEGY
// Get a list of positiveStations
// From the positiveStations
	// Get a utility metric that has m*coins - n*distance (m and n and weights)
	// Highest station is the one that has the highest utility metric (lotsa coins and lesser distance)
class StatefulDrone extends Drone {
	
	public StatefulDrone(Position startPos) {
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
	
	public Station getClosestStation(Position curPos, List<Station> closestStations) {
		// Initialise with the first station on the list
		Station closestStation = closestStations.get(0);

		for (Station station : closestStations) {
			if (curPos.distanceFromDrone(station.position) < curPos.distanceFromDrone(closestStation.position)) {
				closestStation = station;
			}
		}

		return closestStation;
	}
	
	public Direction getBestDirection(Position currentPosition) {

		HashMap<Direction, Double> DirectionStation = new HashMap<Direction, Double>();

		for (Direction direction : Direction.values()) {
			Position nextPos = currentPosition.nextPosition(direction);
			if (nextPos.inPlayArea()) {
				for (Station station : App.stations) {
					// Look for all the stations that are within 0.00025 from nextPos
					if (nextPos.distanceFromDrone(station.position) <= 0.00025 
							&& station.position.inPlayArea() 
							&& station.coins != 0.0
							&& station.power != 0.0) {
//						if (DirectionStation.get(direction) == null) {
//							DirectionStation.put(direction, station);
//						}
//						else if (nextPos.distanceFromDrone(station.position) < nextPos.distanceFromDrone(DirectionStation.get(direction).position)) {
//							DirectionStation.put(direction, station);
//						}
						if (DirectionStation.get(direction) != null) {
							Double current = DirectionStation.get(direction);
							DirectionStation.put(direction, current + station.coins);
						} 
						DirectionStation.put(direction, station.coins);
						
					}
				}
			}
		}

		double maxCoins = 0.0;

		Direction bestDirection = Position.getRandomDirection(Direction.values());
		

		while (DirectionStation.get(bestDirection) != null && DirectionStation.get(bestDirection) < 0.0) {
			bestDirection = Position.getRandomDirection(Direction.values());
		}


		// get the richest direction
		for (Direction direction : DirectionStation.keySet()) {
			if (DirectionStation.get(direction) > maxCoins) {
				maxCoins = DirectionStation.get(direction);
				bestDirection = direction;
			}
		}
		
		if (DirectionStation.isEmpty()) {
			System.out.println("DIRECTION STATION IS EMPTY");
		}
		
		System.out.println("REACHABLE STATIONS: " + DirectionStation);
		System.out.println("BEST DIRECTION: " + bestDirection);
		return bestDirection;
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
		
			Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);
			
			
			closestPositiveStation.printStationDetails();
			
			////// MAYBE
			// loop through all directions
			// to find the direction that leads you closest to the closest Positive Station
			int index = 0;
			Position nextPos = startPos.nextPosition(Direction.values()[index]);
			Double minDistance = nextPos.distanceFromDrone(closestPositiveStation.position);
			
//			for (Direction direction : Direction.values()) {
//				Position newPos = startPos.nextPosition(direction);
//				if (newPos.distanceFromDrone(closestPositiveStation.position) < minDistance) {
//					minDistance = newPos.distanceFromDrone(closestPositiveStation.position);
//					nextPos = newPos;
//				}
//			}
			
			for (int i = 0; i < 16; i++) {
				Position newPos = startPos.nextPosition(Direction.values()[i]);
				if (newPos.distanceFromDrone(closestPositiveStation.position) < minDistance) {
					minDistance = newPos.distanceFromDrone(closestPositiveStation.position);
					nextPos = newPos;
				}
				
			}
			
//			for (Station station : App.stations) {
//				if (nextPos.distanceFromDrone(station.position) <= 0.00025 && station.coins < 0) {
//					nextPos = nextPos.nextPosition(Direction.S);
//				}
//			}
			
		
			// Direction closestStationDirection = Position.getDirectionFromPosition(startPos, closestPositiveStation.position);
			
//			int closestStationDirectionIndex = Position.getDirectionIndexFromPosition(startPos, closestPositiveStation.position);
//			
//			Direction closestStationDirection = Direction.values()[closestStationDirectionIndex];
//			
//			System.out.println(closestStationDirection);
//			
//			Position nextPos = startPos.nextPosition(closestStationDirection);
			
//			while (!nextPos.inPlayArea()) {
//				closestStationDirection = Direction.values()[(closestStationDirectionIndex + 1) % 16];
//			}
			
//			while (!nextPos.inPlayArea()) {
//				nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
//				// nextPos = startPos.nextPosition(getBestDirection(startPos));
//			}
			

			List<Station> closestStations = getClosestStations();
			
			if (!closestStations.isEmpty()) {
				Station closestStation = getClosestStation(nextPos, closestStations);
				if (closestStation.coins < 0) {
					System.out.println("MAY DAY MAY DAY RED STATION NOOOOOOO");
					redHit += 1;
//					Station cp = closestPositiveStation;
//					positiveStations.remove(closestPositiveStation);
//					Station newPositiveStation =  getClosestPositiveStation(startPos, positiveStations);
//					nextPos = startPos.nextPosition(getBestDirection(nextPos));
//					index = 0;
//					nextPos = startPos.nextPosition(Direction.values()[index]);
//					minDistance = nextPos.distanceFromDrone(closestPositiveStation.position);
//					
////					for (Direction direction : Direction.values()) {
////						Position newPos = startPos.nextPosition(direction);
////						if (newPos.distanceFromDrone(closestPositiveStation.position) < minDistance) {
////							minDistance = newPos.distanceFromDrone(closestPositiveStation.position);
////							nextPos = newPos;
////						}
////					}
//					
//					for (int i = 0; i < 16; i++) {
//						Position newPos = startPos.nextPosition(Direction.values()[i]);
//						if (newPos.distanceFromDrone(newPositiveStation.position) < minDistance) {
//							minDistance = newPos.distanceFromDrone(newPositiveStation.position);
//							nextPos = newPos;
//						}
//						
//					}
//					
//					positiveStations.add(cp);
				}
			}
			
			
			
			
			
			
			
			startPos = nextPos;

			// Update power and moves
			moves += 1;
			power -= 1.25;
		

			// Add the flight  path
			flightPath.add(startPos);
			
//			System.out.println("===========================");
//			System.out.println("DRONE DEETS");
//			System.out.println("POWER: " + power);
//			System.out.println("COINS: " + coins);
//			System.out.println("MOVES: " + moves);
//			System.out.println("===========================");
			
			
			
			if (startPos.distanceFromDrone(closestPositiveStation.position) > 0.00025) {
				continue;
			} else {
				coins += closestPositiveStation.coins;
				power += Math.max(closestPositiveStation.power, -power);
				
				positiveStations.remove(closestPositiveStation);
				
				System.out.println("===========================");
				System.out.println("DRONE DEETS");
				System.out.println("POWER: " + power);
				System.out.println("COINS: " + coins);
				System.out.println("MOVES: " + moves);
				System.out.println("===========================");
			}
			
			
			
			
			
		}
		
		System.out.println("MAX COINS IN THIS MAP IS: " + maxCoins);
		
		System.out.println("I HAVE HIT: " + redHit);
		
		
		return flightPath;
	}
	
	


}
