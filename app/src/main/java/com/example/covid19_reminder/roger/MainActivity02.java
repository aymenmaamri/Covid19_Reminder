package com.example.covid19_reminder.roger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.ibra.Notification2Activity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity02 extends AppCompatActivity {

    TextView timerText;
    Button stopStartButton;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    private int messageCount = 0;
    private static Uri alarmSound;
    private final long[] pattern = { 100, 300, 300, 300};

    boolean timerStarted = false;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main02);

            timerText = (TextView) findViewById(R.id.timerText);
            stopStartButton = (Button) findViewById(R.id.startStopButton);

            timer = new Timer();


        }
    public void resetTapped(View view)
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(timerTask != null)
                {
                    timerTask.cancel();
                    setButtonUI("START", R.color.green);
                    time = 0.0;
                    timerStarted = false;
                    timerText.setText(formatTime(0,0,0));


                }
            }
        });



        resetAlert.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });

        resetAlert.show();

    }

    public void startStopTapped(View view)
    {
        if(timerStarted == false)
        {
            timerStarted = true;
            setButtonUI("PAUSE", R.color.red);

            startTimer();
        }
        else
        {
            timerStarted = false;
            setButtonUI("CONTINUE", R.color.green);

            timerTask.cancel();
        }

    }

    private void setButtonUI(String start, int color)
    {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(this, color));
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        timerText.setText(getTimerText());

                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        if(seconds == 4)
        {
           crateNotif();
        }


        return formatTime(seconds, minutes, hours);
    }

    private void crateNotif() {

        String message = "You will need to change your mask";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity02.this)
                .setSmallIcon(R.drawable.ic_baseline_priority_high_24)
                .setContentTitle("Covid19_Reminder")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setVibrate(pattern);

        Intent intent = new Intent(MainActivity02.this, MainActivity03notif.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity02.this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);

    }

    /*@Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //editor.putLong("millisLeft", time);
        editor.putBoolean("timerRunning", timerStarted);
        //editor.putLong("endTime", timerTask);
        editor.apply();
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        timerStarted = prefs.getBoolean("timerRunning", false);
        getTimerText();

        if (timerStarted) {
            //time = prefs.getLong("endTime", 0);
            time = time - System.currentTimeMillis();
            if (time < 0) {
                time = 0;
                timerStarted = false;
                getTimerText();
            } else {
                startTimer();
            }
        }*/
}