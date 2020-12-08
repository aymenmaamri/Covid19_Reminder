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
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications1);

        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmaps();
            }
        });
    }
            private void openmaps() {
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity (intent);
            }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications1);

        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhomepage();
            }
        });
    }
    private void openhomepage() {
        Intent intent = new Intent(this, activity_main2.xml);
        startActivity (intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications2);

        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opentimer();
            }
        });
    }
    private void opentimer() {
        Intent intent = new Intent(this, MainActivity02.class);
        startActivity (intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification3);

        btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opentimer();
            }
        });
    }
    private void opentimer() {
        Intent intent = new Intent(this, MainActivity02.class);
        startActivity (intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification3);

        btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmaps();
            }
        });
    }
    private void openmaps() {
        Intent intent = new Intent(this, Main2Activity.class)
        startActivity (intent);
    }
    }
