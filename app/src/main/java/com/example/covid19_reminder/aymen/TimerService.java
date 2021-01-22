package com.example.covid19_reminder.aymen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    final Timer timer = new Timer();
    long[] timeRemaining = null;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        timeRemaining = new long[]{intent.getLongExtra("TimeValue", 14400)};


        timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    Intent intentLocale = new Intent();
                    intentLocale.setAction("Counter");

                    timeRemaining[0]--;
                    if (timeRemaining[0] <= 0) {
                        timer.cancel();
                    }
                    intentLocale.putExtra("TimeRemaining", timeRemaining[0]);
                    sendBroadcast(intentLocale);
                }
            }, 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        if (timeRemaining[0] > 0) {
            Intent pauseIntent = new Intent();
            pauseIntent.setAction("returnRemainingTime");
            pauseIntent.putExtra("TimeRemainingWhenPaused", timeRemaining[0]);
            timer.cancel();
            sendBroadcast(pauseIntent);
        } else {
            timer.cancel();
        }
    }
}
