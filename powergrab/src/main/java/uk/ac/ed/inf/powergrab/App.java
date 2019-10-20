package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
//import java.net.URL;
//import com.mapbox.geojson.*;
import java.util.List;

import org.json.JSONException;

import com.mapbox.geojson.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JSONException, IOException
    {
        // Read out the input arguments
        String day = args[0];
        String month = args[1];
        String year = args[2];
        
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        
        int seed = Integer.parseInt(args[5]);
        
        String state = args[6];
        
        String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + year + "/" + month + "/" + day + "/powergrabmap.geojson";
        
        String mapJSON = JsonParser.readJsonFromUrl(mapString);
        
        FeatureCollection collection = FeatureCollection.fromJson(mapJSON);
        List<Feature> features = collection.features();
        
        //Stateless stateless = new Stateless();
        
        //Geometry featureGeo = features.get(1).coordinates();
        
        Point point1 = (Point) features.get(1).geometry();
        
        List<Double> coordinate = point1.coordinates();
        System.out.println(coordinate);
        
        System.out.println(features.get(1).properties().get("coins"));
        
        System.out.println(features.get(1).properties().get("power"));
        
        System.out.println(features.size()); // 50
        
        System.out.println(features.get(1).getNumberProperty("coins"));
        
        
        double[][] importantFeatures = new double[50][4]; // store longitude, latitude, coins and power
        
        int n = features.size();
        
        for (int i = 0; i < n; i++) {
        	
        	Feature feature = features.get(i); 
        	
            List<Double> coordinates = ((Point) feature.geometry()).coordinates();
            
        	importantFeatures[i][0] = coordinates.get(0); // longitude
        	importantFeatures[i][1] = coordinates.get(1); // latitude
        	
        	importantFeatures[i][2] = Double.parseDouble(feature.getStringProperty("coins"));
        	importantFeatures[i][3] = Double.parseDouble(feature.getStringProperty("power"));
        	
        }
        
        // PRINT OUT 2D ARRAY
        System.out.println(Arrays.deepToString(importantFeatures));
        // TEST THE ARRAY
//        for (int i = 0; i < n; i++) {
//        	System.out.println(("-------------------"));
//        	for (int j = 0; j < 4; j++) {
//        		System.out.println(importantFeatures[i][j]);
//        	}
//        }
//        
        Position startPos = new Position(latitude, longitude);
        
        if (state.equals("stateless")) {
        	// do double coins, double power, int moves, Position pos
        	Stateless stateless = new Stateless(startPos);
        	System.out.println(stateless.startPos.latitude);
        	System.out.println("I am a stateless drone");
        	
        	while (stateless.power > 0 && stateless.moves < 250) {
        		double[] distances = new double[n];
        	
        		for (int i = 0; i < n; i++) {
        			Feature feature = features.get(i); 
                	
                    List<Double> coordinates = ((Point) feature.geometry()).coordinates();
                    Position pos = new Position(coordinates.get(0), coordinates.get(1));

        			distances[i] = Position.distance(stateless.startPos, pos);
//        			System.out.println(stateless.startPos.latitude);
//        			System.out.println(pos.latitude);
        		}
        		
        		System.out.println(Arrays.toString(distances));
        		int closestStationIndex = Position.getMinIndex(distances);
        		System.out.println(closestStationIndex);
        		
        		Feature closestStation = features.get(closestStationIndex);
        		List<Double> closestStationCoordinates = ((Point) closestStation.geometry()).coordinates();
        		Direction closestStationDirection = Position.getDirection(new Position(closestStationCoordinates.get(0), closestStationCoordinates.get(1)));
        		System.out.println(closestStationDirection);
        		
        		stateless.move(closestStationDirection);
        		stateless.coins += Double.parseDouble(closestStation.getStringProperty("coins"));
        		stateless.startPos = new Position(closestStationCoordinates.get(0), closestStationCoordinates.get(1));
        		
        		System.out.println("DRONE DEETS");
        		System.out.println(stateless.power);
        		System.out.println(stateless.coins);
        		System.out.println(stateless.moves);
        	}
 
        } else if (state.equals("stateful")){
        	// do something else
        	System.out.println("I am a stateful drone");
        } else {
        	System.out.println("ERROR: DRONE NOT FOUND.");
        }
    }
}
