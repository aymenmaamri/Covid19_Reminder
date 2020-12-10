package com.example.covid19_reminder.yichen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.aymen.SettingsActivity;

import com.example.covid19_reminder.roger.MainActivity01;
import com.example.covid19_reminder.roger.MainActivity02;

public class Main2Activity extends AppCompatActivity {

    public Button timer;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /** Called when the user taps the "TIMER" button */

        /**this.timer = (Button) findViewById(R.id.timer);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity = new Intent(getApplicationContext(), MainActivity01.class);
                startActivity(otherActivity);
                finish();
            }
        });**/



    }

    public void timer(View view) {
        Intent intent = new Intent(this, MainActivity01.class);
        startActivity(intent);
    }


    /** Called when the user taps the "Mask Sale" button */
    public void maskSale(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "Settings" button */
    public void settings(View view)

  }
    private void openmaps() {
    Intent intent = new Intent(this, MapsActivity.class);
    startActivity (intent);
  }



    private void openhomepage() {
        Intent intent = new Intent(this, Main2Activity,class);
        startActivity (intent);
    }


    private void opentimer() {
        Intent intent = new Intent(this, MainActivity02.class);
        startActivity (intent);
    }


    private void opentimer() {
        Intent intent = new Intent(this, MainActivity02.class);
        startActivity (intent);
    }


    private void openmaps() {
        Intent intent = new Intent(this, Main2Activity.class)
        startActivity (intent);
    }
    }
