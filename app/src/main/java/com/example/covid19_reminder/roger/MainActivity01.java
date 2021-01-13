package com.example.covid19_reminder.roger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.yichen.Main2Activity;

public class MainActivity01 extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);

        /** Called when the user taps the "Start" button */

        this.imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity2 = new Intent(getApplicationContext(), MainActivity04bring.class);
                startActivity(otherActivity2);
                finish();
            }
        });

    }
}