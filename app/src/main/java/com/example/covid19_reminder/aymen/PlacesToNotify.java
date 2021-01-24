package com.example.covid19_reminder.aymen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.covid19_reminder.R;


public class PlacesToNotify extends AppCompatActivity {

    boolean enableTrainStations;
    boolean enableSupermarkets;
    boolean enableCityCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_to_notify);

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        enableTrainStations = sharedPref.getBoolean("enableTrainStations",true);
        enableSupermarkets = sharedPref.getBoolean("enableSupermarkets",true);
        enableCityCenter = sharedPref.getBoolean("enableCityCenter",true);

        Switch trainStation = (Switch) findViewById(R.id.trainStationsSwitch);
        trainStation.setChecked(enableTrainStations);
        trainStation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableTrainStations = trainStation.isChecked();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("enableTrainStations", enableTrainStations);
            }
        });

        Switch supermarkets = (Switch) findViewById(R.id.supermarketsSwitch);
        supermarkets.setChecked(enableSupermarkets);
        supermarkets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableSupermarkets = supermarkets.isChecked();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("enableSupermarkets", enableSupermarkets);
            }
        });

        Switch cityCenter = (Switch) findViewById(R.id.cityCenterSwitch);
        cityCenter.setChecked(enableCityCenter);
        cityCenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableCityCenter = cityCenter.isChecked();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("enableCityCenter", enableCityCenter);
            }
        });

    }


    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enableTrainStations", enableTrainStations);
        editor.putBoolean("enableSupermarkets", enableSupermarkets);
        editor.putBoolean("enableCityCenter", enableCityCenter);
        editor.apply();
        super.onStop();
    }
}