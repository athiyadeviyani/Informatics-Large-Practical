package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import java.net.URL;
//import com.mapbox.geojson.*;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import com.mapbox.geojson.*;

/**
 * Hello world!
 *
 */
public class App 
{
	public static java.util.Random rnd;
	public static java.util.Random statefulRandom = new Random(5932);
	public static List<Station> stations = new ArrayList<Station>();

	public static void writeToFile(String fileName, String str) 
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(str);

		writer.close();
	}

	
	// Outputs a list of positions (flight path of the drone) as a FeatureCollection object
	public static FeatureCollection displayPath(List<Position> path, FeatureCollection featurecollection) {

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

	public static void main( String[] args ) throws JSONException, IOException
	{
		// Read out the input arguments
		String day = args[0];
		String month = args[1];
		String year = args[2];

		double latitude = Double.parseDouble(args[3]);
		double longitude = Double.parseDouble(args[4]);

		int seed = Integer.parseInt(args[5]);

		rnd = new Random(seed);



		System.out.println("THIS IS THE RANDOM SEED: " + rnd);

		String state = args[6];

		String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + year + "/" + month + "/" + day + "/powergrabmap.geojson";

		String mapJSON = JsonParser.readJsonFromUrl(mapString);

		String fileNameGeoJson = state + "-" + day + "-" + month + "-" + year + ".geojson";
		String fileNameTxt = state + "-" + day + "-" + month + "-" + year + ".txt";

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



		Position startPos = new Position(latitude, longitude);

		if (state.equals("stateless")) {
			System.out.println("I am a stateless drone. Beep beep.");
			StatelessDrone stateless = new StatelessDrone(startPos);
			List<Position> flightPath = stateless.playStateless(fileNameTxt);
			FeatureCollection finalFeatureCollection = displayPath(flightPath, collection);
			System.out.println(finalFeatureCollection.toJson());
			writeToFile(fileNameGeoJson, finalFeatureCollection.toJson());

		} else if (state.equals("stateful")) {
			System.out.println("I am a stateful drone. Beep beep.");
			StatefulDrone stateful = new StatefulDrone(startPos);
			List<Position> flightPath = stateful.playStateful(fileNameTxt);
			FeatureCollection finalFeatureCollection = displayPath(flightPath, collection);
			System.out.println(finalFeatureCollection.toJson());
			writeToFile(fileNameGeoJson, finalFeatureCollection.toJson());
		}


	}
}
