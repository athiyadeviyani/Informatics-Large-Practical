package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import com.mapbox.geojson.*;

public class App {

	// Random seed
	public static java.util.Random rnd;

	// List of stations within a map
	public static List<Station> stations = new ArrayList<Station>();

	// Result string to be written to the output .txt file
	public static String result = "";

	/**
	 * Main function that reads input arguments, calls the methods to move the
	 * Stateless and Stateful drones with respect to the input, and generates the
	 * output .geojson and .txt files
	 * 
	 * @param args
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JSONException, IOException {

		// Read the input arguments
		String day = args[0];
		String month = args[1];
		String year = args[2];

		double latitude = Double.parseDouble(args[3]);
		double longitude = Double.parseDouble(args[4]);

		int seed = Integer.parseInt(args[5]);

		String state = args[6];

		// Generate the random seed
		rnd = new Random(seed);

		// Generate the map GeoJSON URL
		String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + year + "/" + month + "/" + day
				+ "/powergrabmap.geojson";

		// Generate the map GeoJSON
		String mapJSON = JsonParser.readJsonFromUrl(mapString);

		// Create the output filenames
		String fileNameGeoJson = state + "-" + day + "-" + month + "-" + year + ".geojson";
		String fileNameTxt = state + "-" + day + "-" + month + "-" + year + ".txt";

		// Retrieve a list of all the stations within the map 
		FeatureCollection collection = FeatureCollection.fromJson(mapJSON);
		List<Feature> features = collection.features();

		for (Feature feature : features) {
			List<Double> coordinates = ((Point) feature.geometry()).coordinates();
			Position position = new Position(coordinates.get(1), coordinates.get(0));
			double coins = Double.parseDouble(feature.getStringProperty("coins"));
			double power = Double.parseDouble(feature.getStringProperty("power"));
			Station station = new Station(coins, power, position);
			stations.add(station);
		}

		// Creates the starting position object based on the input arguments
		Position startPos = new Position(latitude, longitude);

		// Start the drones
		if (state.equals("stateless")) {

			StatelessDrone stateless = new StatelessDrone(startPos);
			List<Position> flightPath = stateless.playStateless();
			
			// Output the final flight path and details of each move 
			FeatureCollection finalFeatureCollection = Output.displayPath(flightPath, collection);
			System.out.println(finalFeatureCollection.toJson());
			Output.writeToFile(fileNameGeoJson, finalFeatureCollection.toJson());
			Output.writeToFile(fileNameTxt, result);

		} else if (state.equals("stateful")) {

			StatefulDrone stateful = new StatefulDrone(startPos);
			List<Position> flightPath = stateful.playStateful();
			
			// Output the final flight path and details of each move 
			FeatureCollection finalFeatureCollection = Output.displayPath(flightPath, collection);
			System.out.println(finalFeatureCollection.toJson());
			Output.writeToFile(fileNameGeoJson, finalFeatureCollection.toJson());
			Output.writeToFile(fileNameTxt, result);
		}

	}
}
