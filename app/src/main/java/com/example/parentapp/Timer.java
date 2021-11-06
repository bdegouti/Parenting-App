package com.example.parentapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.helper.NotificationHelper;
import com.example.parentapp.model.TimeManager;

import java.time.LocalDateTime;
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
    private Vibrator vibrator;

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
                AlertDialog durationDialog;
                AlertDialog.Builder durationBuilder = new AlertDialog.Builder(Timer.this);

                durationBuilder.setTitle("Choose your duration:");

                durationBuilder.setSingleChoiceItems(durations, -1,
                        ((dialogInterface, position) -> {
                            results = durations[position];
                        }));

                durationBuilder.setPositiveButton("OK", ((dialogInterface, i) -> {
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
                        case "10 minutes":
                            timeManager.setMinuteInMillis(10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            timeLeft = 10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                            break;

                        case "Others":
                            EditText customDuration = new EditText(Timer.this);
                            AlertDialog customDurationDialog;
                            AlertDialog.Builder customDurationBuilder = new AlertDialog.Builder(Timer.this);

                            customDuration.setHint("Minutes");

                            customDurationBuilder.setTitle("Customize your duration in minutes:");
                            customDurationBuilder.setView(customDuration);

                            customDurationBuilder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        int duration = Integer.parseInt(customDuration.getText().toString());
                                        if (duration <= 0) {
                                            throw new IllegalArgumentException();
                                        }
                                        timeManager.setMinuteInMillis((long) duration * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                                        timeLeft = (long) duration * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                                        updateTimer(timeManager.getMinuteInMillis());
                                    } catch (Exception e) {
                                        Toast.makeText(Timer.this, "Please enter a valid number for minutes.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            customDurationBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(Timer.this, "CANCELED", Toast.LENGTH_SHORT).show();
                                }
                            });

                            customDurationDialog = customDurationBuilder.create();
                            customDurationDialog.show();

                            break;
                    }
                    updateTimer(timeManager.getMinuteInMillis());
                }));

                durationBuilder.setNegativeButton("CANCEL", ((dialogInterface, i) -> {
                    Toast.makeText(
                            Timer.this, "CANCELED", Toast.LENGTH_SHORT).show();
                }));

                durationDialog = durationBuilder.create();
                durationDialog.show();
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
                startCount.setText(R.string.start);

                startVibration();
                playMusic();

                sendNotification();

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(Timer.this);

                builder.setTitle("Times up!");

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopMusic();
                        stopVibration();
                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        }.start();

        isRunning = true;
        startCount.setText(R.string.pause);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
        startCount.setText(R.string.start);
    }

    private void cancelTimer() {
        if (isRunning) {
            countDownTimer.cancel();
        }
        isRunning = false;
        timeLeft = timeManager.getMinuteInMillis();
        updateTimer(timeLeft);
        startCount.setText(R.string.start);
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
            musicPlayer.setVolume(1, 1);
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

    private void startVibration() {
        long[] pattern = {100, 500, 100, 200, 100, 200, 100, 500, 100};
        VibrationEffect effect = VibrationEffect.createWaveform(pattern, 0);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(effect);
    }

    private void stopVibration() {
        this.vibrator.cancel();
    }

    private void sendNotification() {
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("Times Up!",
                "Click on this message to return to that app screen and stop alarm sound.");
        notificationHelper.getManager().notify(1, nb.build());
    }

    public static Intent makeLaunchIntent(Context c) { return new Intent(c, Timer.class); }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}