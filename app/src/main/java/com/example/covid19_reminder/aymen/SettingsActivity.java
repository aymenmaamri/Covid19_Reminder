package com.example.covid19_reminder.aymen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


    private static final int MY_PERMISSION_REQUEST_ACCESS_LOCATION = 1;
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
            }
        });

        //save the vibration status
        Switch vibration = (Switch) findViewById(R.id.vibrationSwitch);
        vibration.setChecked(enableVibration);
        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableVibration = vibration.isChecked();
            }
        });

        //save the notification time
        Spinner spinnerTime = (Spinner) findViewById(R.id.timeToNotifySpinner);
        spinnerTime.setSelection(timeToNotify);
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeToNotify = spinnerTime.getSelectedItemPosition();
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
                            user_location.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Address : </b><br></font>"
                                            + addresses.get(0).getAddressLine(0)
                            ));
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



    private void fetchLocation() {
       if (ContextCompat.checkSelfPermission(
                SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            if(ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("access location permission is required")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSION_REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_REQUEST_ACCESS_LOCATION);
            }
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Double latitude = location.getLatitude();
                                Double longitude = location.getLongitude();
                                user_location.setText("Latitude = " + latitude + ",longitude = " + longitude + " address: " +  location.toString());
                            } else {
                                user_location.setText("location is undefined");
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }  else {

            }
        }
    }









}