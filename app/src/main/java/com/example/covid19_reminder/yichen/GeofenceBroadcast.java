package com.example.covid19_reminder.yichen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcast extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // an Intent broadcast.
            Log.d(TAG, "onReceive: recieving geofence works");
            Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();

            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            if (geofencingEvent.hasError()) {
                Log.d(TAG, "onReceive: Error recieving geofence");
                return;
            }

            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : geofenceList) {
                Log.d(TAG, "onReceive: " + geofence.getRequestId() + "test geofence");
            }

            int transitionType = geofencingEvent.getGeofenceTransition();

            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}