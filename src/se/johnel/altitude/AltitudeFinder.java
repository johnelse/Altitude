package se.johnel.altitude;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import android.location.Location;

public class AltitudeFinder {
	private Location location;
	private double altitude;
	private static String serviceUrl = "https://maps.googleapis.com/maps/api/elevation/json?locations=%f,%f&sensor=true";
	
	private static InputStream openUrl(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		return response.getEntity().getContent();
	}
	
	private static String getUrlContent(String url) throws IOException {
		InputStream stream = openUrl(url);
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();
		
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		return builder.toString();
	}
	
	private String makeUrl() {
		return String.format(serviceUrl,
				this.location.getLatitude(),
				this.location.getLongitude());
	}
	
	public void updateAltitude() throws IOException, JSONException {
		String url = this.makeUrl();
		String content = getUrlContent(url);
		JSONObject json = new JSONObject(content);
		JSONArray results = json.getJSONArray("results");
		JSONObject result = results.getJSONObject(0);
		this.altitude = result.getDouble("elevation");
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public double getAltitude() {
		return this.altitude;
	}
	
	public AltitudeFinder(Location location) {
		this.location = location;
	}
}
