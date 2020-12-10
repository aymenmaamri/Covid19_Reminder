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
