package com.example.covid19_reminder.aymen;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.covid19_reminder.R;

public class SettingsActivity extends AppCompatActivity {

    public static boolean enableNotifications = false;
    public static boolean enableVibration = false;
    public static String homeAddress = "";
    public static int timeToNotify = 0;
    public static int distanceToNotify = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

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

}