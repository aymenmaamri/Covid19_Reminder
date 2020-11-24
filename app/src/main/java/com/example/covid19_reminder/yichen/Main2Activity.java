package com.example.covid19_reminder.yichen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.aymen.SettingsActivity;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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
}