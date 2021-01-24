package com.example.covid19_reminder.roger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.aymen.TimerActivity2;
import com.example.covid19_reminder.yichen.MapsActivity;

public class MainActivity04bring extends AppCompatActivity {

    private Button btn2;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity04bring);

        this.btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity2 = new Intent(getApplicationContext(), TimerActivity2.class);
                startActivity(otherActivity2);
                finish();
            }
        });

        this.btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity2 = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(otherActivity2);
                finish();
            }
        });

    }
}