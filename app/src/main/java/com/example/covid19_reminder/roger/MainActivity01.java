package com.example.covid19_reminder.roger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.yichen.Main2Activity;

public class MainActivity01 extends AppCompatActivity {

    private ImageView imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);

        /** Called when the user taps the "Start" button */

        this.imageButton = (ImageView) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivity2 = new Intent(getApplicationContext(), MainActivity02.class);
                startActivity(otherActivity2);
                finish();
            }
        });

    }
}