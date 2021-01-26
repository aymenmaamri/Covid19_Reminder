package com.example.covid19_reminder.aymen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import com.example.covid19_reminder.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class PlacesToNotify extends AppCompatActivity {

    boolean enableTrainStations;
    boolean enableSupermarkets;
    boolean enableCityCenter;

    Set<String> addressesToNotify = new HashSet<String>();
    ListView addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_to_notify);

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        enableTrainStations = sharedPref.getBoolean("enableTrainStations",true);
        enableSupermarkets = sharedPref.getBoolean("enableSupermarkets",true);
        enableCityCenter = sharedPref.getBoolean("enableCityCenter",true);
        addressesToNotify = sharedPref.getStringSet("addressesToNotify",new HashSet<String>());

        addressList = (ListView) findViewById(R.id.addresslist);
        addressList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                removeAddress(selectedItem);
            }
        });
        updateList();

        Switch trainStation = (Switch) findViewById(R.id.trainStationsSwitch);
        trainStation.setChecked(enableTrainStations);
        trainStation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableTrainStations = trainStation.isChecked();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("enableTrainStations", enableTrainStations);
                editor.apply();
            }
        });

    }

    public void updateList(){
        String[] items = new String[0];
        items = addressesToNotify.toArray(items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,items);
        addressList.setAdapter(adapter);
    }

    public void removeAddress(String s){
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);
        alertName.setTitle("Do you want to remove this Address? ");
        LinearLayout layoutName = new LinearLayout(this);
        layoutName.setOrientation(LinearLayout.VERTICAL);
        alertName.setView(layoutName);
        alertName.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                addressesToNotify.remove(s);
                updateList();
            }
        });

        alertName.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel(); // closes dialog
            }
        });

        AlertDialog alert = alertName.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.design_default_color_secondary));
    }

    public void addAddress(View view){
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);
        final EditText editTextName1 = new EditText(PlacesToNotify.this);
        alertName.setTitle("Add new Address:");
        alertName.setView(editTextName1);
        LinearLayout layoutName = new LinearLayout(this);
        layoutName.setOrientation(LinearLayout.VERTICAL);
        layoutName.addView(editTextName1); // displays the user input bar
        alertName.setView(layoutName);
        alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                addressesToNotify.add(editTextName1.getText().toString()); // variable to collect user input
                updateList();
            }
        });


        alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel(); // closes dialog
            }
        });

        AlertDialog alert = alertName.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.design_default_color_secondary));
    }


    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enableTrainStations", enableTrainStations);
        editor.putBoolean("enableSupermarkets", enableSupermarkets);
        editor.putBoolean("enableCityCenter", enableCityCenter);
        editor.putStringSet("addressesToNotify", addressesToNotify);
        editor.apply();
        super.onStop();
    }
}