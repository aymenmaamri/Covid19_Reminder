package com.example.covid19_reminder.roger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covid19_reminder.R;

import java.util.Locale;


public class MainActivity02 extends AppCompatActivity {

        private static final long START_TIME_IN_MILLIS = 14400000;
        private TextView mTextViewCountDown;
        private Button mButtonStartPause;
        private Button mButtonReset;
        private CountDownTimer mCountDownTimer;
        private boolean mTimerRunning;
        private long mTimeLeftInMillis = START_TIME_IN_MILLIS;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main02);

            /** Called when the user taps the "START" and "PAUSE" button */

            mTextViewCountDown = findViewById(R.id.text_view_countdown);
            mButtonStartPause = findViewById(R.id.button_start_pause);
            mButtonReset = findViewById(R.id.button_reset);
            mButtonStartPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** verifed when the time are running*/
                    if (mTimerRunning) {
                        pauseTimer();
                    } else {
                        startTimer();
                    }
                }
            });
            /** Called when the user taps the "RESET"  button */
            mButtonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetTimer();
                }
            });
            updateCountDownText();
        }
        private void startTimer() {
            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                }
                @Override
                public void onFinish() {
                    mTimerRunning = false;
                    mButtonStartPause.setText("Start");
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                    mButtonReset.setVisibility(View.VISIBLE);
                }
            }.start();
            mTimerRunning = true;
            mButtonStartPause.setText("pause");
            mButtonReset.setVisibility(View.INVISIBLE);
        }
        private void pauseTimer() {
            mCountDownTimer.cancel();
            mTimerRunning = false;
            mButtonStartPause.setText("Start");
            mButtonReset.setVisibility(View.VISIBLE);
        }
        private void resetTimer() {
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            updateCountDownText();
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.VISIBLE);
        }

    /** das muss noch gemacht verden: Es gibt hier Rechnungsprobleme */
        private void updateCountDownText() {
            int heures = (int) (mTimeLeftInMillis / 1000) / 3600;
            int minutes = (int) (mTimeLeftInMillis / 1000) / 240;
            int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", heures, minutes, seconds);
            mTextViewCountDown.setText(timeLeftFormatted);
        }

    }
