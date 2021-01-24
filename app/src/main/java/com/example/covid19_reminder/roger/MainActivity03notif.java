package com.example.covid19_reminder.roger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.aymen.TimerActivity2;

public class MainActivity03notif extends AppCompatActivity {

    private Button btn5;
    private Button btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity03notif);

        this.btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity2 = new Intent(getApplicationContext(), TimerActivity2.class);
                startActivity(otherActivity2);
                finish();
            }
        });

        this.btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity2 = new Intent(getApplicationContext(), TimerActivity2.class);
                startActivity(otherActivity2);
                otherActivity2.putExtra("remind", true);
                finish();
            }
        });

    }
}