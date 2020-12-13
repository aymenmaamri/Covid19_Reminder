package com.example.covid19_reminder.aymen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.covid19_reminder.R;

public class SettingsActivity extends AppCompatActivity {

    public static boolean enableNotifications;
    public static boolean enableVibration;
    public static String homeAddress;
    public static String timeToNotify;
    public static String distanceToNotify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
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

    private void onNotChange() {}

    private void onVibChange() {}

    private void onHomeAddressChange() {}
}