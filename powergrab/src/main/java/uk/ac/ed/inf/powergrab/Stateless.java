package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

class Stateless extends Drone {
	
	
	public Stateless(Position startPos) {
		super(startPos);
	}
	
	public Station getBestStation(List<Station> stations) {
		Station bestStation = Collections.max(stations, new Comparator<Station>() {
			public int compare(Station s1, Station s2) {
				if (s1.coins < s2.coins) {
					return -1;
				} else if (s1.coins == s2.coins) {
					return 0;
				} else {
					return -1;
				}
			}
		});
		return bestStation;
	}
	
	public Station getRichestStation(List<Station> stations) {
		Station richestStation = stations.get(0);
		for (Station station : stations) {
			if (station.coins > richestStation.coins) {
				richestStation = station;
			}
		}
		return richestStation;
	}
	

	public boolean hasPower() {
		return power >= 1.25;
	}
	
	public boolean hasMoves() {
		return moves <= 250;
	}
	
//	public Station getClosestStation(Direction direction) {
//		Position nextPos = startPos.nextPosition(direction);
//		if (!nextPos.inPlayArea()) {
//			nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
//		}
//		
//		List<Station> closestStations = new ArrayList<Station>();
//		
//		
//		for (Station station : App.stations) {
////			System.out.println(Position.distance(station.position, startPos));
//			if (startPos.distanceFromDrone(station.position) <= 0.00025 && station.position.inPlayArea()) {
//				closestStations.add(station);
//			}
//		}
//		return null;
//	}
//	
	public Direction getBestDirection() {
		Direction bestDirection = Position.getRandomDirection(Direction.values());
//		Station bestStation = App.stations.get(0);
//		Double maxCoins = bestStation.coins;
		HashMap<Direction, Double> DirectionCoins = new HashMap<Direction, Double>();
		
		for (Direction direction : Direction.values()) {
			if (startPos.nextPosition(direction).inPlayArea()) {
				Position nextPos = startPos.nextPosition(direction);
				for (Station station : App.stations) {
					// FOR ALL STATIONS THAT ARE WITHIN RANGE 
					if (nextPos.distanceFromDrone(station.position) <= 0.00025 && station.position.inPlayArea() && station.visited != true) {
						// ADD THE AMOUNT OF COINS THAT YOU'LL GET IF YOU'RE AT THAT DIRECTION
						DirectionCoins.put(direction, station.coins);
					}
				}
			}
		}
		
		double maxCoins = 0.0;
		
		System.out.println(DirectionCoins);
		
		// get the 'richest' direction
		for (Direction direction : DirectionCoins.keySet()) {
			if (DirectionCoins.get(direction) > maxCoins) {
				maxCoins = DirectionCoins.get(direction);
				bestDirection = direction;
			}
		}
		return bestDirection;
	}
	
	
	public HashMap<Integer, Station> getClosestStations() {
		HashMap<Integer, Station> closestStations = new HashMap<Integer, Station>();
		for (int i = 0; i < 50; i++) {
			Station station = App.stations.get(i);
			if (startPos.distanceFromDrone(station.position) <= 0.00025 && station.position.inPlayArea() && station.visited != true) {
				closestStations.put(i, station);
			}
		}
		return closestStations;
	}
	
	
	public List<Position> playStateless() {
		
//		List<Position> flightPath = new ArrayList<Position>();
//		flightPath.add(startPos);
//		
//		while (hasPower() && hasMoves()) {
//			// get best direction
//			Direction bestDirection = getBestDirection();
//			System.out.println(bestDirection);
//			
//			// move to next direction
//			Position nextPos = startPos.nextPosition(bestDirection);
//			startPos = nextPos;
//			flightPath.add(nextPos);
//			
//
//			
//			// check any stations
//
//			
//			HashMap<Integer, Station> closestStations = getClosestStations();
//			
//			if (closestStations.isEmpty()) {
//				System.out.println("NO STATIONS IN RANGE");
//				System.out.println("DRONE DETAILS");
//	    		System.out.println("POWER: " + power);
//	    		System.out.println("COINS: " + coins);
//	    		System.out.println("MOVES: " + moves);
//	    		System.out.println("LATITUDE: " + startPos.latitude);
//	    		System.out.println("LONGITUDE: " + startPos.longitude);
//	    		System.out.println("===========================");
//				//nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
//	    		nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
//				while (!nextPos.inPlayArea()) {
//					nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
//				}
//				flightPath.add(nextPos);
//				startPos = nextPos;
//				power -= 1.25;
//				moves += 1;
//				continue;
//			} else {
//			
//			
//				Set<Integer> indexSets = closestStations.keySet();
//				List<Integer> indexList = new ArrayList<Integer>(indexSets);
//				int closestStationIndex = indexList.get(0);
//				Station closestStation = closestStations.get(closestStationIndex);
//				Double minDist = startPos.distanceFromDrone(closestStation.position);
//				
//				for (int i : closestStations.keySet()) {
//					Station station = closestStations.get(i);
//					Double stationDist = startPos.distanceFromDrone(station.position);
//					if (stationDist < minDist) {
//						minDist = stationDist;
//						closestStation = station;
//						closestStationIndex = i;
//					}				
//				}
//				
//
//				
//				for (Station s : App.stations) {
//					if (closestStation == s) {
//						s.coins = 0.0;
//						s.power = 0.0;
//						s.visited = true;
//						// update moves and power
//						moves += 1;
//						power -= 1.25;
//						// charge from closest station
//						coins += closestStation.coins;
//						power += Math.max(closestStation.power, -power);
//						
//					}
//				}
//			}
//			
//		}
		
		int noStationsInRange = 0;
		int visited = 0;
		
		List<Position> flightPath = new ArrayList<Position>();
		
		flightPath.add(startPos);

		while (hasPower() && hasMoves()) {
			
			//Station bestStation = getBestStation();
			List<Station> closestStations = new ArrayList<Station>();
			
			
			for (Station station : App.stations) {
//				System.out.println(Position.distance(station.position, startPos));
				if (startPos.distanceFromDrone(station.position) <= 0.00055 && station.coins > 0 && station.power > 0 && station.position.inPlayArea()) {
					closestStations.add(station);
				}
			}
			
			if (closestStations.isEmpty()) {
				noStationsInRange++;
				System.out.println("NO STATIONS IN RANGE");
				System.out.println("DRONE DETAILS");
	    		System.out.println("POWER: " + power);
	    		System.out.println("COINS: " + coins);
	    		System.out.println("MOVES: " + moves);
	    		System.out.println("LATITUDE: " + startPos.latitude);
	    		System.out.println("LONGITUDE: " + startPos.longitude);
	    		System.out.println("===========================");
				Position nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
				while (!nextPos.inPlayArea()) {
					nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
				}
				flightPath.add(nextPos);
				startPos = nextPos;
				power -= 1.25;
				moves += 1;
				continue;
			}
			
			System.out.println(closestStations.size());
			Station bestStation = getRichestStation(closestStations);
			if (bestStation.power < 0) {
				System.out.println("BAD BAD BAD STATION");
			}
			//System.out.println(closestStations);
			System.out.println("STATION DETAILS");
    		System.out.println("POWER: " + bestStation.power);
    		System.out.println("COINS: " + bestStation.coins);
    		System.out.println("LONGITUDE: " + bestStation.position.longitude);
    		System.out.println("LATITUDE: " + bestStation.position.latitude);
    		System.out.println("===========================");
			Position nextPos = startPos.nextPosition(Position.getDirection(bestStation.position));
			while (!nextPos.inPlayArea()) {
				nextPos = startPos.nextPosition(Position.getRandomDirection(Direction.values()));
			}
			flightPath.add(nextPos);
			startPos = nextPos;
//			coins += bestStation.coins;
//			power += Math.max(bestStation.power, -power);
//			power -= 1.25;
//			moves += 1;
//			
			for (Station station : App.stations) {
				if (bestStation == station) {
					visited++;
					coins += station.coins;
					power += Math.max(station.power, -power);
					power -= 1.25;
					moves += 1;
					station.coins = 0.0;
					station.power = 0.0;
					System.out.println("VISITED STATION");
					System.out.println("current power: " + station.power);
		    		System.out.println("current coins: " + station.coins);
				}
			}
//			bestStation.coins = 0.0;
//			bestStation.power = 0.0;
			
			System.out.println("DRONE DEETS");
    		System.out.println("POWER: " + power);
    		System.out.println("COINS: " + coins);
    		System.out.println("MOVES: " + moves);
    		System.out.println("===========================");
    		
		}
		
		System.out.println("NUMBER OF STATIONS IN RANGE: " + (250-noStationsInRange));
		System.out.println("VISITED: " + visited);
		for (Station station : App.stations) {
			System.out.println("STATION DETAILS");
    		System.out.println("POWER: " + station.power);
    		System.out.println("COINS: " + station.coins);
    		System.out.println("LONGITUDE: " + station.position.longitude);
    		System.out.println("LATITUDE: " + station.position.latitude);
		}
//		
		return flightPath;
	}
	

}
