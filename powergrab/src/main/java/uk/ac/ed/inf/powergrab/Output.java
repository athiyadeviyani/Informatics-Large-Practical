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

}
