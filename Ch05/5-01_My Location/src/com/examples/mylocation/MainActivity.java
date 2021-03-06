package com.examples.mylocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class MainActivity extends Activity {

    private LocationManager mManager;
    private Location mCurrentLocation;
    
    private TextView mLocationView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationView = new TextView(this);
        setContentView(mLocationView);
        
        mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if(!mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Ask the user to enable GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Manager");
            builder.setMessage("We would like to use your location, "
                    + "but GPS is currently disabled.\n"
                    + "Would you like to change these settings now?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Launch settings, allowing user to make a change
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //No location service, no Activity
                    finish();
                }
            });
            builder.create().show();
        }
        //Get a cached location, if it exists
        mCurrentLocation = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateDisplay();
        //Register for updates
        int minTime = 5000;
        float minDistance = 0;
        mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mListener);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        //Disable updates when we are not in the foreground
        mManager.removeUpdates(mListener);
    }
    
    private void updateDisplay() {
        if(mCurrentLocation == null) {
            mLocationView.setText("Determining Your Location...");
        } else {
            mLocationView.setText(String.format("Your Location:\n%.2f, %.2f",
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude()));
        }
    }
    
    private LocationListener mListener = new LocationListener() {
        //New location event
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            updateDisplay();
        }
        
        //The requested provider was disabled in settings
        @Override
        public void onProviderDisabled(String provider) { }
        
        //The requested provider was enabled in settings
        @Override
        public void onProviderEnabled(String provider) { }

        //Availability changes in the requested provider
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
        
    };
}