package com.demo.lior.app.proximitymap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Lior esabag
 */
public class ProximityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("ON PROXIMITY FIRED");

        // Key for determining whether user is leaving or entering
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
            Toast.makeText(context, "Welcome to you Area", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
            Toast.makeText(context, "You had left the area", Toast.LENGTH_LONG).show();
        }
    }
}