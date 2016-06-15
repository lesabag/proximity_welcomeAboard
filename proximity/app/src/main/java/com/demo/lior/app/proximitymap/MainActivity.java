package com.demo.lior.app.proximitymap;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
    Lior esabag
    Determine if you entered the proximity zoon/area
    First fire get default false - means exit the area.
    Proximity receiver wake up while you will enter the zoon
 */
public class MainActivity extends Activity implements LocationListener {

    LocationManager locationManager;
    /*
        TelAviv geo location
     */
    double latitude = 32.0666700;
    double longitude = 34.7666700;
    float radius = 30000; //radius from current position in meters
    Button fireBtn;
    Context context;
    EditText newLatitude;
    EditText newLongtitude;
    TextView addressTv;
    List<Address> addresses = null;

    private static final long LOCATION_REFRESH_TIME = 60000; // in Milliseconds
    private static final float MINIMUM_DISTANCE_CHANGE_FOR_UPDATE = 1f; // in Meters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Button fireBtn = (Button) findViewById(R.id.Fire);
        addressTv = (TextView) findViewById(R.id.address);
        newLatitude = (EditText) findViewById(R.id.point_latitude);
        newLongtitude = (EditText) findViewById(R.id.point_longitude);

        newLatitude.setText("Latitude: " + latitude);
        newLongtitude.setText("Longitude: " + longitude);
        System.out.println("onStart");

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
            //String add = getText(R.string.currentLoc).toString();
            addressTv.setText(getText(R.string.currentLoc) + addresses.get(0).getAddressLine(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fireBtn != null;

        fireBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProximityReceiver.class);
//			intent.putExtra("state", "proximityalert");
                sendBroadcast(intent);

                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.addProximityAlert(latitude, longitude, radius, -1, pi);
            }
        });
    }

    public void onResume() {
        super.onResume();
        System.out.println("onResume");
        // Getting Google Play availability status
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), "Google Play Services Available - SUCCESS", Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
            Toast.makeText(getApplicationContext(), "Google Play Services OUT OF DATE - FAILURE", Toast.LENGTH_LONG).show();
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (provider == null) {
            System.out.println("No location provider found!");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATE,
                (android.location.LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        newLatitude.setText("latitude: " + latitude);
        newLongtitude.setText("longitude: " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {

    }
}
