package com.example.covid19_reminder.aymen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.example.covid19_reminder.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity{


    private FusedLocationProviderClient fusedLocationProviderClient;
    TextView user_location;

    public static boolean enableNotifications = false;
    public static boolean enableVibration = false;
    public static String homeAddress = "";
    public static int timeToNotify = 0;
    public static int distanceToNotify = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        enableNotifications = sharedPref.getBoolean("enableNotifications",false);
        enableVibration = sharedPref.getBoolean("enableVibration",false);
        homeAddress = sharedPref.getString("homeAddress","");
        timeToNotify = sharedPref.getInt("timeToNotify",0);
        distanceToNotify = sharedPref.getInt("distanceToNotify",0);


        //save the home address from input
        EditText input_address = (EditText) findViewById(R.id.homeAddress);
        input_address.setText(homeAddress);
        input_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                homeAddress = input_address.getText().toString();
                return false;
            }
        });


        //save the notification status
        Switch notifications = (Switch) findViewById(R.id.notSwitch);
        notifications.setChecked(enableNotifications);
        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableNotifications = notifications.isChecked();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("enableNotifications", enableNotifications);
                editor.apply();

            }
        });

        //save the vibration status
        Switch vibration = (Switch) findViewById(R.id.vibrationSwitch);
        vibration.setChecked(enableVibration);
        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableVibration = vibration.isChecked();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("enableVibration", enableVibration);
                editor.apply();
            }
        });

        //save the notification time
        Spinner spinnerTime = (Spinner) findViewById(R.id.timeToNotifySpinner);
        spinnerTime.setSelection(timeToNotify);
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeToNotify = spinnerTime.getSelectedItemPosition();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("timeToNotify",timeToNotify);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                distanceToNotify = 0;
            }
        });

        //save the notification distance
        Spinner spinnerDistance = (Spinner) findViewById(R.id.distanceToNotifySpinner);
        spinnerDistance.setSelection(distanceToNotify);
        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                distanceToNotify = spinnerDistance.getSelectedItemPosition();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("distanceToNotify",distanceToNotify);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                distanceToNotify = 0;
            }
        });

    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }


    public void toOtherLocations(View view) {
        Intent intent = new Intent(this, PlacesToNotify.class);
        startActivity(intent);
    }

    public void getMyCurrentLocation(View view) {
        user_location = findViewById(R.id.homeAddress);

        if(ActivityCompat.checkSelfPermission(SettingsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        try {
                            Geocoder geocoder = new Geocoder(SettingsActivity.this
                                    , Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            user_location.setText(addresses.get(0).getAddressLine(0));
                            homeAddress = addresses.get(0).getAddressLine(0);
                            SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("homeAddress",homeAddress);
                            editor.apply();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        user_location.setText("location failed");
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(SettingsActivity.this
            , new  String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }
//    public static boolean enableNotifications = false;
//    public static boolean enableVibration = false;
//    public static String homeAddress = "";
//    public static int timeToNotify = 0;
//    public static int distanceToNotify = 0;

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enableNotifications", enableNotifications);
        editor.putBoolean("enableVibration",enableVibration);
        editor.putString("homeAddress",homeAddress);
        editor.putInt("timeToNotify",timeToNotify);
        editor.putInt("distanceToNotify",distanceToNotify);
        editor.apply();
        super.onStop();
    }












}