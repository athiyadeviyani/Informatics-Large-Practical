package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;

// REFERENCE: https://community.apigee.com/questions/52082/how-to-retrive-the-json-data-using-java.html
public class JsonParser {

	/**
	 * Returns an object read by a Reader object as a StringBuilder object
	 * 
	 * @param rd - BufferedReader object
	 * @return StringBuilder object
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Reads JSON data from a given URL
	 * 
	 * @param url - uniform resource locator of the JSON data
	 * @return JSON data as a String object
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			InputStreamReader is_reader = new InputStreamReader(is, Charset.forName("UTF-8"));
			BufferedReader rd = new BufferedReader(is_reader);
			String json = readAll(rd);

			return json;
		} finally {
			is.close();
		}
	}

}
