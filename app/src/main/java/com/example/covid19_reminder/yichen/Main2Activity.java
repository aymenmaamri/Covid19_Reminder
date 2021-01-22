package com.example.covid19_reminder.yichen;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.aymen.SettingsActivity;
import com.example.covid19_reminder.aymen.TimerActivity;
import com.example.covid19_reminder.aymen.TimerActivity2;
import com.example.covid19_reminder.roger.MainActivity01;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    public static List<UserAddress> userAddresses = new ArrayList<UserAddress>();

    private static final String CHANNEL_1_ID = "movement_notifications";
    private static final String CHANNEL_2_ID = "timer_notifications";
    public Address addressToNotify;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private String geofenceID = "testID";

    private static final String TAG = "Main2Activity";
    int LOCATION_REQUEST_CODE = 10001;

    private  NotificationManagerCompat notificationManager;

    private Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    public Button timer;





    public void getAddresses(){
        try {
            List<Address> addresses = geocoder.getFromLocationName("bahnhof", 10);
            for(Address address : addresses) {
                Log.d(TAG, "List: " + address.toString());
            }

        } catch (IOException e) {
            Log.d(TAG, "ListFailure: ");
            e.printStackTrace();
        }
    }








    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Address extractedAddress = null;
                try {
                    if (location != null) {
                        extractedAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (extractedAddress != null && extractedAddress.getThoroughfare() != null && addressToNotify != null) {
                    Log.d(TAG, "onAddressExtraction: " + extractedAddress.getThoroughfare());
                    Log.d(TAG, "onAddressExtraction2: " + addressToNotify.getAddressLine(0));
                    if (extractedAddress.getThoroughfare().equals(addressToNotify.getThoroughfare())) {
                        Log.d(TAG, "SUCCESS: PASSED");
                        //notify user
                        pushNotification();
                    }
                }

            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        createNotificationChannels();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        notificationManager = NotificationManagerCompat.from(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


        try {
            addressToNotify = geocoder.getFromLocationName("Lodyweg 1 C", 1).get(0);
            Log.d(TAG, "onCreateAddress: " + addressToNotify.toString());

        } catch (IOException e) {
            Log.d(TAG, "onGetFailure: ");
            e.printStackTrace();
        }

        if (addressToNotify != null) {
            //LatLng latLng = new LatLng(addressToNotify.getLongitude(), addressToNotify.getLatitude());
            LatLng latLng = new LatLng(-34, 151);
            Log.d(TAG, "onLatLng: " + latLng.latitude + " , " + latLng.longitude);
            addGeofence(latLng, 50);
        }

        getAddresses();
        //pushNotification();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //getLastLocation();
            checkSettingsAndStartLocationUpdates();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //settings of device are OK
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(Main2Activity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "onSuccess " + location.toString());
                        Log.d(TAG, "onSuccess " + location.getAccuracy());
                        Log.d(TAG, "onSuccess " + location.getLongitude());
                        Log.d(TAG, "onSuccess: this was called from the mainActivity");
                    } else {
                        Log.d(TAG, "onSuccess: Location was null");
                    }
                }
            });

            locationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
                }
            });
        }

    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialogue");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                //getLastLocation();
                checkSettingsAndStartLocationUpdates();
            } else {
                //not granted

            }
        }
    }





    public void createNotificationChannels () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel movementNotifications = new NotificationChannel(
                    CHANNEL_1_ID,
                    "movement notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            movementNotifications.setDescription("this a channel for movement notifications");

            NotificationChannel timerNotifications = new NotificationChannel(
                    CHANNEL_2_ID,
                    "movement notification",
                    NotificationManager.IMPORTANCE_LOW
            );
            movementNotifications.setDescription("this a channel for timer notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(movementNotifications);
            manager.createNotificationChannel(timerNotifications);
        }
    }

    public void pushNotification(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("test notification")
                .setContentText("this is a notification from channel 1")
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();
        Log.d(TAG, "NotificationBuild");

        notificationManager.notify(1, notification);
    }






    private void addGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "addGeofence: function called");
        //Toast.makeText(this, "addGeofence: function called", Toast.LENGTH_SHORT).show();
        Geofence geofence = geofenceHelper.getGeoFence(geofenceID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeoFencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "permission not granted");
            return;
        }
        Log.d(TAG, "addGeofence: client called");
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Geofence added...");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMsg = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailureGeofence: " + errorMsg);
                    }
                });
    }






    public void timer(View view) {
        Intent intent = new Intent(this, TimerActivity2.class);
        startActivity(intent);
    }


    /** Called when the user taps the "Mask Sale" button */
    public void maskSale(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "Settings" button */
    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    public void btn1(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void btn2(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
    public void btn3(View view) {
        Intent intent = new Intent(this, MainActivity01.class);
        startActivity(intent);
    }
    public void btn4(View view) {
        Intent intent = new Intent(this, MainActivity01.class);
        startActivity(intent);
    }

    public void btn5(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }





}
