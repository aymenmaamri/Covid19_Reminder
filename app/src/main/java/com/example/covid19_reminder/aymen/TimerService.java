package com.example.covid19_reminder.aymen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.covid19_reminder.R;
import com.example.covid19_reminder.roger.MainActivity03notif;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    final Timer timer = new Timer();
    long[] timeRemaining = null;
    private final String CHANNEL_ID = "Timer Notifications";
    private final int NOTIFICATION_ID = 001;
    private static Uri alarmSound;
    private final long[] pattern = { 300, 300, 300, 300};
    private boolean enableNotifications;
    private boolean enableVibration;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        long defaulttime = (sharedPref.getInt("timeToNotify",3) + 1 )*3600;
        timeRemaining = new long[]{intent.getLongExtra("TimeValue", defaulttime)};
        enableNotifications = sharedPref.getBoolean("enableNotifications", true);
        enableVibration = sharedPref.getBoolean("enableVibration", true);

        timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    Intent intentLocale = new Intent();
                    intentLocale.setAction("Counter");

                    timeRemaining[0]--;
                    if (timeRemaining[0] <= 0) {
                        timer.cancel();
                        intentLocale.putExtra("StopTimer", true);
                        sendBroadcast(intentLocale);
                        if(enableNotifications){
                            createNotif();
                        }
                    }
                    intentLocale.putExtra("TimeRemaining", timeRemaining[0]);
                    sendBroadcast(intentLocale);
                }
            }, 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotif() {
        String message = "You will need to change your mask";
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.ic_baseline_priority_high_24)
                .setContentTitle("Covid19 Reminder")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        if(enableVibration){
            builder.setVibrate(pattern);
        }else{
            builder.setVibrate(new long[]{0L});
        }

        Intent intent = new Intent(TimerService.this, MainActivity03notif.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getActivity(TimerService.this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
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
