package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.helper.NotificationHelper;
import com.example.parentapp.model.TimeManager;

import java.util.Locale;

/* - count down for 1,2,3,5, and 10 minutes and a custom duration(always in the whole number)
   - when the timer is running, be able to reset (stop and reset time to the last selected starting time)
     or pause. While paused to be able to reset or resume the timer.
   - when the timer reaches 0, plays a sound for alarm and vibrate
   - when the timer is running, be able to still run after exiting the application
   - when the timer expired, pops a notification that can stop the alarm sound
*/
public class Timer extends AppCompatActivity {
    private final int MILLISECOND_TO_SECOND = 1000;
    private final int MINUTES_TO_SECONDS = 60;

    // default time is set to 1 minute in milliseconds
    private final TimeManager timeManager = new TimeManager(MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);

    private Button startCount;
    private TextView timeRemaining;

    // storing the result of time user select and default is set to 1 minute
    private String results = "1 minute";

    private CountDownTimer countDownTimer;
    private boolean isRunning;
    private long timeLeft = timeManager.getMinuteInMillis();

    private MediaPlayer musicPlayer;

    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle("Count Down Timer");

        notificationHelper = new NotificationHelper(this);

        setUpChooseTimeButton();
        setUpStartCancelButton();
        updateTimer(timeLeft);
    }

    private void setUpChooseTimeButton() {
        String[] durations = getResources().getStringArray(R.array.durations_for_count_down);

        Button time = findViewById(R.id.btnChooseTime);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(Timer.this);

                builder.setTitle("Choose your duration:");

                builder.setSingleChoiceItems(durations, -1,
                        ((dialogInterface, position) -> results = durations[position]));

                builder.setPositiveButton("OK", ((dialogInterface, i) -> {
                        switch (results) {
                            case "1 minute":
                                timeManager.setMinuteInMillis(MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                                timeLeft = MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                                break;
                            case "2 minutes":
                                timeManager.setMinuteInMillis(2 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                                timeLeft = 2 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                                break;
                            case "3 minutes":
                                timeManager.setMinuteInMillis(3 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                                timeLeft = 3 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                                break;
                            case "5 minutes":
                                timeManager.setMinuteInMillis(5 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                                timeLeft = 5 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                                break;
                            default:
                                timeManager.setMinuteInMillis(10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                                timeLeft = 10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                                break;
                        }
                        updateTimer(timeManager.getMinuteInMillis());
                }));

                builder.setNegativeButton("CANCEL", ((dialogInterface, i) -> Toast.makeText(
                        Timer.this, "CANCELED", Toast.LENGTH_SHORT).show()));

                dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void setUpStartCancelButton() {
        Button cancelTimer = findViewById(R.id.btnCancelCountDown);
        startCount = findViewById(R.id.btnStartCounting);
        timeRemaining = findViewById(R.id.txtTimeRemaining);

        startCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        cancelTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTimer();
            }
        });
    }

    // Some of the code below was adapted from the Youtube video linked:
    // https://www.youtube.com/watch?v=MDuGwI6P-X8
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, MINUTES_TO_SECONDS) {
            @Override
            public void onTick(long milliSecUntilFinished) {
                timeLeft = milliSecUntilFinished;
                updateTimer(timeLeft);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeLeft = timeManager.getMinuteInMillis();
                updateTimer(timeLeft);
                startCount.setText("Start");

                playMusic();

                sendNotification();

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(Timer.this);

                builder.setTitle("Times up!");

                builder.setPositiveButton("OK", (((dialogInterface, i) -> stopMusic())));

                dialog = builder.create();
                dialog.show();
            }
        }.start();

        isRunning = true;
        startCount.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
        startCount.setText("Start");
    }

    private void cancelTimer() {
        timeLeft = timeManager.getMinuteInMillis();
        updateTimer(timeLeft);
    }

    private void updateTimer(long time) {
        int minutes = (int) (time / MILLISECOND_TO_SECOND) / MINUTES_TO_SECONDS;
        int seconds = (int) (time / MILLISECOND_TO_SECOND) % MINUTES_TO_SECONDS;

        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeRemaining.setText(timeLeft);
    }

    private void playMusic() {
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
            musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playMusic();
                }
            });
        }
        musicPlayer.start();
    }

    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("Times Up!",
                "Click on this message to return to that app screen and stop alarm sound.");
        notificationHelper.getManager().notify(1, nb.build());
    }

    public static Intent makeLaunchIntent(Context c) { return new Intent(c, Timer.class); }
}