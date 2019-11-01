package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
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
			if (station.coins > 0 && station.power > 0) {
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

		return closestStation;
	}
	
	// check if drone still has power
	public boolean hasPower() {
		return power >= 1.25;
	}

	// check if drone still has moves
	public boolean hasMoves() {
		return moves < 10;
	}
	
	public List<Position> playStateful() {
		
		// Initialise the flight path of the drone 
		List<Position> flightPath = new ArrayList<Position>();
		
		// Add the starting position to flightPath
		flightPath.add(startPos);
		
		List<Station> positiveStations = getPositiveStations();
		
		System.out.println(positiveStations);
		
		while (hasPower() && hasMoves()) {
		
			Station closestPositiveStation = getClosestPositiveStation(startPos, positiveStations);
			
			System.out.println(closestPositiveStation);
			
		
			Direction closestStationDirection = Position.getDirection(closestPositiveStation.position);
			
			System.out.println(closestStationDirection);
			
			Position nextPos = startPos.nextPosition(closestStationDirection);
			
			startPos = nextPos;

			// Update power and moves
			moves += 1;
			power -= 1.25;

			// Add the flight  path
			flightPath.add(startPos);
			
			coins += closestPositiveStation.coins;
			power += Math.max(closestPositiveStation.power, -power);
			
			positiveStations.remove(closestPositiveStation);
			
		}
		return flightPath;
	}
	
	


}
