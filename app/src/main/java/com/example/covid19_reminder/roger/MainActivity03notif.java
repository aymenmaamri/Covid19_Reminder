package com.example.covid19_reminder.roger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.covid19_reminder.R;

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
                SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("remindLater", false);
                finish();
            }
        });

        this.btn4 = (Button) findViewById(R.id.remindMe);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("remindLater", true);
                editor.apply();
                finish();
            }
        });
    }
}