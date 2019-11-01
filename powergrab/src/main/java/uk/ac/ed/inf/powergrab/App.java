package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static List<Station> stations = new ArrayList<Station>();
	
	public static void writeToFile(String fileName, String str) 
			  throws IOException {
			    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			    writer.write(str);
			     
			    writer.close();
			}
	
	public static FeatureCollection displayPath(List<Position> path, FeatureCollection featurecollection) {
		// create a list of points
		List<Point> points = new ArrayList<Point>();
		// for every position, add that position as a point (fromlnglat) into new list
		for (Position position : path) {
			points.add(Point.fromLngLat(position.longitude, position.latitude));
		}
		// create a new linestring fromlnglat
		LineString myLineString = LineString.fromLngLats(points);
		// and then create a new feature from linestring *fromgeometry
		Feature myFeature = Feature.fromGeometry(myLineString);
		// list<f> fs = fc.features()
		List<Feature> myFeatures = featurecollection.features();
		// featureList.add(feature)
		myFeatures.add(myFeature);
		// featurecollection = fromfeatures(featurelist)
		
		
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
        
        //int seed = 6000;
        
        rnd = new Random(seed);
        
        
        
        System.out.println("THIS IS THE RANDOM SEED: " + rnd);
        
        String state = args[6];
        
        String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + year + "/" + month + "/" + day + "/powergrabmap.geojson";
        
        String mapJSON = JsonParser.readJsonFromUrl(mapString);
        
        String fileName = day + month + year + "_" + seed + "_" + state + ".geojson";
        
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
        
//        for (Station station : stations) {
//        	System.out.println("coins: " + station.coins);
//        	System.out.println("power: " + station.power);
//        	System.out.println("latitude: " + station.position.latitude);
//        	System.out.println("longitude: " + station.position.longitude);
//        	System.out.println("-----------------");
//        }
        
        
        Position startPos = new Position(latitude, longitude);
        
        if (state.equals("stateless")) {
        	StatelessDrone stateless = new StatelessDrone(startPos);
        	// Stateless stateless = new Stateless(startPos);
        	List<Position> flightPath = stateless.playStateless();
        	FeatureCollection finalFeatureCollection = displayPath(flightPath, collection);
        	System.out.println(finalFeatureCollection.toJson());
        	writeToFile(fileName, finalFeatureCollection.toJson());
       	
        } else if (state.equals("stateful")) {
        	System.out.println("I am a stateful drone. Beep beep.");
        	StatefulDrone stateful = new StatefulDrone(startPos);
        	List<Position> flightPath = stateful.playStateful();
        	FeatureCollection finalFeatureCollection = displayPath(flightPath, collection);
        	System.out.println(finalFeatureCollection.toJson());
        	writeToFile(fileName, finalFeatureCollection.toJson());
        }
        

    }
}
