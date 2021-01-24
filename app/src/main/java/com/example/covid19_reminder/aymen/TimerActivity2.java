package com.example.covid19_reminder.aymen;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.covid19_reminder.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimerActivity2 extends AppCompatActivity {

    private static final String TAG = "TimerActivity2";
    private static final String KEY_WEARS = "wearsList";

    private TextView mTextViewCountDown;
    private TextView mWearsView;
    private ImageButton mButtonStartPause;
    private ImageButton mButtonReset;
    private EditText mEditTextInput;
    private Button mButtonSet;

    private long timeInSeconds = 10;
    private long timeLeft;
    private boolean mTimerRunning = false;
    private boolean mTimerPaused = false;
    private boolean remindLater = false;

    public List<String> wears = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE},
                PackageManager.PERMISSION_GRANTED);

        updateTimeSetting();
        mTextViewCountDown = findViewById(R.id.text_view_timer);
        mWearsView = findViewById(R.id.wears_view);
        mWearsView.setMovementMethod(new ScrollingMovementMethod());
        mButtonStartPause = findViewById(R.id.start_btn);
        mButtonReset = findViewById(R.id.reset_btn);

        mEditTextInput = findViewById(R.id.added_time);
        mButtonSet = findViewById(R.id.button_set);


        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTimerRunning){
                    startTimer();
                    mTimerRunning = true;
                } else {
                    pauseTimer();
                    mTimerRunning = false;
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    stopTimer();
                    mTimerRunning = false;
            }
        });

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMillis() > 0) {
                    timeInSeconds = getMillis();
                    formatTime(timeInSeconds);
                    closeKeyboard();
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Counter");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timeLeft = intent.getLongExtra("TimeRemaining", 0)+1;
                formatTime(timeLeft);
                if(intent.getBooleanExtra("StopTimer",false)){
                    stopTimer();
                };

            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void updateTimeSetting(){
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //TODO: Change time to hours (*3600)
        timeInSeconds = sharedPref.getInt("timeToNotify",3) + 1;
    }

    private void startTimer(){
        Intent intentService = new Intent(this, TimerService.class);
        if(remindLater){
            remindLater = false;
            timeInSeconds = 1800;
            intentService.putExtra("TimeValue", timeInSeconds);
            startService(intentService);
        } else if (timeLeft > 0 && !mTimerRunning){
            intentService.putExtra("TimeValue", timeLeft);
            startService(intentService);
        }else {
            intentService.putExtra("TimeValue", timeInSeconds);
            startService(intentService);
        }
        mButtonStartPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        mEditTextInput.setEnabled(false);
        mButtonSet.setEnabled(false);
        mTimerRunning = true;
        mTimerPaused = false;
    }

    private void pauseTimer(){
        Intent pauseIntent = new Intent(this, TimerService.class);
        pauseIntent.setAction("pauseTimer");
        stopService(pauseIntent);
        mTimerPaused = true;
        mTimerRunning = false;
        mButtonStartPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
    }

    private void stopTimer(){
        stopService(new Intent(this, TimerService.class));
        mButtonStartPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        mEditTextInput.setEnabled(true);
        mButtonSet.setEnabled(true);
        if(mTimerRunning){
            saveWear();
            updateWears();
        }
        timeLeft = 0;
        mTimerRunning = false;
        mTimerPaused = false;
        updateTimeSetting();
        formatTime(timeInSeconds);
    }

    private long getMillis() {
        String input = mEditTextInput.getText().toString();
        if (input.length() == 0){
            Toast.makeText(TimerActivity2.this, "Field can not be empty", Toast.LENGTH_SHORT).show();
            return 0;
        }

        long millisInput = Long.parseLong(input) * 3600;
        if (millisInput == 0){
            Toast.makeText(TimerActivity2.this, "please enter a positive number", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return millisInput;
    }

    private void formatTime(long s){
        int hours = (int) (s / 3600);
        int minutes = (int) (s % 3600) / 60;
        int seconds = (int) s % 60;

        String timeLeftFormatted;
        if(hours > 0){
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        }else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void saveWear(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        int hours = 0;
        int minutes = 0;
        String toSave = "";
        long timeWorn = (timeInSeconds - timeLeft) / 60;
        if (timeWorn >= 60) {
            hours = (int)(timeWorn / 3600);
            minutes = (int)((timeWorn % 3600 / 60));
        } else {
            if (timeWorn > 0) {
                minutes = (int) timeWorn;
            } else {
                minutes = 1;
            }
        }
        if (hours > 0){
            toSave = String.format((formatter.format(date) + ", %d hours %02d minutes"), hours, minutes);
        } else {
            toSave = String.format((formatter.format(date) + ", %02d minutes"), minutes);
        }

        wears.add(toSave);
    }

    private void updateWears(){
        String text = "";
        for(String line : wears){
            text = text + "\n" + line;
        }
        mWearsView.setText(text);
    }

    public void clearWears(View view){
        wears.clear();
        mWearsView.setText("");
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(wears);
        editor.putString("save", json);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("timeLeft", timeLeft);
        editor.putBoolean("isTimerPaused", mTimerPaused);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean timePaused = preferences.getBoolean("isTimerPaused", false);
        mTimerRunning = preferences.getBoolean("timerRunning", false);
        remindLater = preferences.getBoolean("remindLater", false);
        if (timePaused) {
            timeLeft = preferences.getLong("timeLeft", 0);
            formatTime(timeLeft);
            mButtonStartPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }else if(!mTimerRunning){
            formatTime(timeInSeconds);
            Log.d(TAG, "Test OK");
            mButtonStartPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            mTimerPaused = false;
        }else{
            Log.d(TAG, "Test OK");
            formatTime(timeInSeconds);
            mButtonStartPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
            mTimerRunning = true;
            mTimerPaused = false;
        }
        Gson gson = new Gson();
        String json = preferences.getString("save", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        if(gson.fromJson(json, type) != null) {
            wears = gson.fromJson(json, type);
            updateWears();
        }

        if(remindLater){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("remindLater", false);
            editor.apply();
            timeLeft = 1800;
            formatTime(timeLeft);
            startTimer();
        }

    }
}
