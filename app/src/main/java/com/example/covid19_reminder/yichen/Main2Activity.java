package com.example.covid19_reminder.yichen;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.aymen.SettingsActivity;
import com.example.covid19_reminder.aymen.TimerActivity2;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main2Activity extends AppCompatActivity {

    //public static List<UserAddress> userAddresses = new ArrayList<UserAddress>();

    private static final String CHANNEL_1_ID = "movement_notifications";
    private static final String CHANNEL_2_ID = "timer_notifications";
    public Address addressToNotify;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private String geofenceID = "testID";

    private static final String TAG = "Main2Activity";
    int LOCATION_REQUEST_CODE = 10001;
    private double curLat,curLng, initialLat, initialLng;

    private  NotificationManagerCompat notificationManager;

    private Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    public Button timer;
    private boolean enableSearch;
    Set<String> trainStations = new HashSet<String>();
    Set<String> addedPlaces = new HashSet<String>();
    ArrayList<Address> stationAddress = new ArrayList<Address>();
    ArrayList<Address> addedAddress = new ArrayList<Address>();
    private boolean enableNotifications;
    private boolean enableVibration;
    private String homeAddress;
    private Address home;
    private boolean atHome;
    private int distanceToNotify;
    private boolean placeFirstVisit = true;
    private boolean enableTrainStations;


//    public void getAddresses(){
//        try {
//            List<Address> addresses = geocoder.getFromLocationName("bahnhof", 10);
//            for(Address address : addresses) {
//                Log.d(TAG, "List: " + address.toString());
//            }
//
//        } catch (IOException e) {
//            Log.d(TAG, "ListFailure: ");
//            e.printStackTrace();
//        }
//    }

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
                        curLat = location.getLatitude();
                        curLng = location.getLongitude();
                        setStations();
                        SharedPreferences sharedPref = getSharedPreferences("Stations", Context.MODE_PRIVATE);
                        double placeLat = Double.longBitsToDouble(sharedPref.getLong("placeLat",Double.doubleToLongBits(curLat)));
                        double placeLng = Double.longBitsToDouble(sharedPref.getLong("placeLng",Double.doubleToLongBits(curLat)));
                        if(getDistance(curLat,curLng,placeLat,placeLng)>=1000){
                            placeFirstVisit = true;
                        }

                        if(getDistance(curLat,curLng,home.getLatitude(),home.getLongitude())<distanceToNotify){
                            atHome = true;
                            Log.d(TAG, "Get home");

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(atHome && enableNotifications 
                        && getDistance(curLat,curLng,home.getLatitude(),home.getLongitude())>=distanceToNotify){
                    pushNotification(2);
                    atHome = false;
                    Log.d(TAG, "Away from home, distance:"+getDistance(curLat,curLng,home.getLatitude(),home.getLongitude()));
                }

                if(enableTrainStations && placeFirstVisit && enableNotifications){
                    for(Address a :stationAddress){
                        checkPlace(extractedAddress,a,1);
                    }
                    for(Address a :addedAddress){
                        checkPlace(extractedAddress,a,1);
                    }
                }



            }

        }
    };


    public void checkPlace(Address extractedAddress, Address a, int mode){
        if ( extractedAddress != null && extractedAddress.getThoroughfare() != null && a != null) {
            //Log.d(TAG, "onAddressExtraction: " + extractedAddress.getThoroughfare());
           // Log.d(TAG, "onAddressExtraction2: " + a.getAddressLine(0));
            if (extractedAddress.getThoroughfare().equals(a.getThoroughfare())) {
                //Log.d(TAG, "SUCCESS: PASSED");
                //mark the current place location
                SharedPreferences sharedPref = getSharedPreferences("Stations", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("placeLat", Double.doubleToLongBits(curLat));
                editor.putLong("placeLng", Double.doubleToLongBits(curLng));
                editor.apply();
                //notify user
                if(mode ==1){
                    pushNotification(1);
                    placeFirstVisit = false;
                }else if(mode ==2){
                    pushNotification(2);
                }
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        createNotificationChannels();

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        enableNotifications = settings.getBoolean("enableNotifications",false);
        enableVibration = settings.getBoolean("enableVibration",false);
        homeAddress = settings.getString("homeAddress","");

        distanceToNotify = (settings.getInt("distanceToNotify",0) +1)*10;
        addedPlaces = settings.getStringSet("addressesToNotify",new HashSet<String>());
        enableTrainStations = settings.getBoolean("enableTrainStations",true);
        
        SharedPreferences sharedPref = getSharedPreferences("Stations", Context.MODE_PRIVATE);
        enableSearch = sharedPref.getBoolean("enableSearch",true);
        initialLat = Double.longBitsToDouble(sharedPref.getLong("initialLat", Double.doubleToLongBits(0.0)));
        initialLng = Double.longBitsToDouble(sharedPref.getLong("initialLng", Double.doubleToLongBits(0.0)));
        trainStations = sharedPref.getStringSet("StationAddress",trainStations);
        setStations();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        notificationManager = NotificationManagerCompat.from(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        if(trainStations.size()>0){
            stationAddress = stringSetToAddresses(trainStations);
        }

        if(addedPlaces.size()>0){
            addedAddress = stringSetToAddresses(addedPlaces);
        }

        Set<String> homeSet =  new HashSet<String>();
        homeSet.add(homeAddress);
        home = stringSetToAddresses(homeSet).get(0);

        //getAddresses();
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
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        enableNotifications = settings.getBoolean("enableNotifications",false);
        enableVibration = settings.getBoolean("enableVibration",false);
        homeAddress = settings.getString("homeAddress","");

        Set<String> homeSet =  new HashSet<String>();
        homeSet.add(homeAddress);
        home = stringSetToAddresses(homeSet).get(0);
        distanceToNotify = (settings.getInt("distanceToNotify",0) +1)*10;
        addedPlaces = settings.getStringSet("addressesToNotify",new HashSet<String>());
        enableTrainStations = settings.getBoolean("enableTrainStations",true);

        Log.d(TAG, "update, add: " + addedPlaces + "; Distance to Notify:"+distanceToNotify);
        if(enableNotifications){
            Log.d(TAG, "enableNotifications");
        }
        if(addedPlaces.size()>0){
            addedAddress = stringSetToAddresses(addedPlaces);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private ArrayList<Address> stringSetToAddresses(Set<String> set){
        ArrayList<Address> addresses = new ArrayList<Address>();
        List<Address> geoResult = new ArrayList<Address>();

        Address temp = null;
        for (String s: set) {
            //Log.d(TAG, "onCreateAddress: " + s);
            try {
                //only full address accepted here
                geoResult = geocoder.getFromLocationName(s, 5);
                if(geoResult.size()!=0){
                    temp = geoResult.get(0);
                    addresses.add(temp);
                }else{
                    Log.d(TAG, "onGetFailure: No location found");
                }
            } catch (IOException e) {
                Log.d(TAG, "onGetFailure: ");
                e.printStackTrace();
            }

            if (temp != null) {
                LatLng latLng = new LatLng(-34, 151);
               // Log.d(TAG, "onLatLng: " + latLng.latitude + " , " + latLng.longitude);
                addGeofence(latLng, 50);
                Log.d(TAG, "add Geofence on: " + temp.getAddressLine(0));
            }
        }

        return addresses;
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
                        //Log.d(TAG, "onSuccess " + location.toString());
                        //Log.d(TAG, "onSuccess " + location.getAccuracy());
                        //Log.d(TAG, "onSuccess " + location.getLongitude());
                        //Log.d(TAG, "onSuccess: this was called from the mainActivity");
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

    public void pushNotification(int mode){
        String text = "";
        if(mode ==1){
           text = "Please put your mask on! You're now in the risk area.";
        }else if(mode ==2){
            text = "Did you bring a mask with you?";
        }
        if(enableNotifications){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_one)
                    .setContentTitle("Covid19 Reminder")
                    .setContentText(text)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();
            Log.d(TAG, "NotificationBuild");

            notificationManager.notify(1, notification);
        }
    }


    private void addGeofence(LatLng latLng, float radius) {
        //Log.d(TAG, "addGeofence: function called");
        //Toast.makeText(this, "addGeofence: function called", Toast.LENGTH_SHORT).show();
        Geofence geofence = geofenceHelper.getGeoFence(geofenceID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeoFencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "permission not granted");
            return;
        }
        //Log.d(TAG, "addGeofence: client called");

        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "onSuccess: Geofence added...");
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

    public void setStations(){
        SharedPreferences sharedPref = getSharedPreferences("Stations", Context.MODE_PRIVATE);
        if(enableSearch && !(curLat==0.0&&curLng==0.0) ){
            enableSearch = false;
            initialLat = curLat;
            initialLng = curLng;

            Object transferData[] = new Object[2];
            GetStationList getNearbyPlaces = new GetStationList();
            String url = getUrl(curLat, curLng);

            transferData[0] = sharedPref;
            transferData[1] = url;
            getNearbyPlaces.execute(transferData);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("initialLat", Double.doubleToLongBits(curLat));
            editor.putLong("initialLng", Double.doubleToLongBits(curLng));
            editor.apply();
        }else if(getDistance(curLat,curLng,initialLat,initialLng) >= 10000){
            enableSearch = true;
        }
        trainStations = sharedPref.getStringSet("StationAddress",trainStations);
        //Log.d("MainActivity", "Station: " + trainStations.size());
    }

    public float getDistance(double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        //Log.d("MainActivity", "Distance moved: " + new Float(distance * meterConversion).floatValue());
        return new Float(distance * meterConversion).floatValue();
    }

    private String getUrl(double latitude, double longtitude) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + latitude + "," + longtitude
                + "&radius="+10000+"&placetypes=train_station");
        googleURL.append("&key=" + "AIzaSyB35M73rZiL-jWGUGpGZOSHtIeyugTxmuE");
        Log.d("MainActivity", "search stations URL: " + googleURL.toString());
        return googleURL.toString();
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


}
