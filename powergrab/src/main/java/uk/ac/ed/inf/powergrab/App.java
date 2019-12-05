package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

	/**
	 * Writes the input string parameter to the specified input file.
	 * 
	 * @param fileName - file to be written to
	 * @param str      - String object to be written to the file
	 * @throws IOException
	 */
	private static void writeToFile(String fileName, String str) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(str);

		writer.close();
	}

	/**
	 * Outputs a list of positions (flight path of the drone) as a FeatureCollection
	 * object
	 * 
	 * @param path              - list of positions (flight path of the drone)
	 * @param featurecollection - FeatureCollection object to be added with the list
	 *                          of positions
	 * @return FeatureColletion object
	 */
	private static FeatureCollection displayPath(List<Position> path, FeatureCollection featurecollection) {

		List<Point> points = new ArrayList<Point>();
		for (Position position : path) {
			points.add(Point.fromLngLat(position.longitude, position.latitude));
		}
		LineString myLineString = LineString.fromLngLats(points);
		Feature myFeature = Feature.fromGeometry(myLineString);
		List<Feature> myFeatures = featurecollection.features();
		myFeatures.add(myFeature);
		FeatureCollection finalPath = FeatureCollection.fromFeatures(myFeatures);
		return finalPath;
	}

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

		// Check if the input arguments are enough
		if (args.length != 7) {
			System.out.println("Incorrect number of input arguments!");
			System.exit(0);
		}

		// Read the input arguments
		String day = args[0];
		Integer day_int = Integer.parseInt(day);
		String month = args[1];
		Integer month_int = Integer.parseInt(month);
		String year = args[2];

		// Check if the input month is valid
		if (month_int > 12 || month_int < 1) {
			System.out.println("Invalid month input!");
			System.exit(0);
		}
		
		// Check if the input day is valid
		if (day_int > 31 || day_int < 1) {
			System.out.println("Invalid day input!");
			System.exit(0);
		}

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

		// Check if the input position is within the play area
		if (!startPos.inPlayArea()) {
			System.out.println("Input longitude and latitude are outside of the play area!");
			System.exit(0);
		}

		// Check if the input state is either stateless or stateful only
		if (!(state.equals("stateless") || state.equals("stateful"))) {
			System.out.println("Input state should only be 'stateless' or 'stateful'!");
		}

		// Start the drones
		if (state.equals("stateless")) {

			StatelessDrone stateless = new StatelessDrone(startPos);
			stateless.playStateless();
			List<Position> flightPath = stateless.flightPath;

			// Output the final flight path and details of each move
			FeatureCollection finalFeatureCollection = displayPath(flightPath, collection);
			System.out.println(finalFeatureCollection.toJson());
			writeToFile(fileNameGeoJson, finalFeatureCollection.toJson());
			writeToFile(fileNameTxt, stateless.txtString);

		} else if (state.equals("stateful")) {

			StatefulDrone stateful = new StatefulDrone(startPos);
			stateful.playStateful();
			List<Position> flightPath = stateful.flightPath;

			// Output the final flight path and details of each move
			FeatureCollection finalFeatureCollection = displayPath(flightPath, collection);
			System.out.println(finalFeatureCollection.toJson());
			writeToFile(fileNameGeoJson, finalFeatureCollection.toJson());
			writeToFile(fileNameTxt, stateful.txtString);
		}

	}
}
