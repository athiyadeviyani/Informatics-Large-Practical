package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Output {

	/**
	 * Writes the input string parameter to the specified input file.
	 * 
	 * @param fileName - file to be written to
	 * @param str      - String object to be written to the file
	 * @throws IOException
	 */
	public static void writeToFile(String fileName, String str) throws IOException {
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

}
