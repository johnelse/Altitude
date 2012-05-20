package se.johnel.altitude;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class AltitudeActivity extends Activity implements LocationListener {
	private AltitudeFinder finder;
	private LocationManager locationManager;
	private String provider;
	
	private static String makeError(String message) {
		return String.format("Error: %s", message);
	}
	
	private void showText(int id, String message) {
		final TextView outputText = (TextView) findViewById(id);
		outputText.setText(message);
	}
	
	private void showLocation(Location location) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		String message = String.format("Location is: %f, %f", lat, lng);
		this.showText(R.id.location, message);
	}
	
	private void showAltitude(double altitude) {
		String message = String.format("Altitude is: %f", altitude);
		this.showText(R.id.altitude, message);
	}
	
	private void showDebug(String message) {
		final TextView debugText = (TextView) findViewById(R.id.debug);
		debugText.append(message);
		debugText.append("\n");
	}
	
	private void updateAll(Location location) {
		this.showLocation(location);
		if (finder == null) {
			finder = new AltitudeFinder(location);
		}
		else {
			finder.setLocation(location);
		}
		
		try {
			finder.updateAltitude();
			showAltitude(finder.getAltitude());
		}
		catch (JSONException e) {
			showDebug(e.toString());
			showText(R.id.altitude, makeError("could not parse response"));
		}
		catch (IOException e) {
			showDebug(e.toString());
			showText(R.id.altitude, makeError("service did not respond"));
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        
        Toast.makeText(this, "Using provider " + provider, Toast.LENGTH_LONG).show();
        
        if (location != null) {
        	this.showLocation(location);
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	locationManager.requestLocationUpdates(provider, 1000, 1, this);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	locationManager.removeUpdates(this);
    }

	@Override
	public void onLocationChanged(Location location) {
		this.updateAll(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.finder;
	}
}