package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.net.MalformedURLException;
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
        
        if (state.equals("stateless")) {
        	// do 
        	System.out.println("I am a stateless drone");
 
        } else {
        	// do something else
        	System.out.println("I am a stateful drone");
        }
    }
}
