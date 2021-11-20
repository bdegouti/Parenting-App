package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.helper.NotificationHelper;
import com.example.parentapp.model.TimeManager;

import java.util.Locale;

/**
 * TimerActivity class allows user to set up a countdown timer of a specific duration.
 * This class provides countdown durations of 1,2,3,5 and 10 minutes, and any custom duration of a whole minute.
 * This class allows user to pause timer when it is running, resume timer, and reset timer.
 * When the countdown reaches 0, this class plays music and vibrates.
 * It also pops up a notification box so user can click OK to stop the music/vibration.
 * This class allows user to exit the screen while the timer is still running.
 */
public class TimerActivity extends AppCompatActivity {
    private final int MILLISECOND_TO_SECOND = 1000;
    private final int MINUTES_TO_SECONDS = 60;

    // default time is set to 1 minute in milliseconds
    private final TimeManager timeManager = new TimeManager(MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);

    private Button startCount;
    private TextView timeRemaining;

    // storing the result of duration that the  user selected and
    // the default is set to 1 minute
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
        setTitle(getString(R.string.count_down_timer));

        notificationHelper = new NotificationHelper(this);

        setUpChooseTimeButton();
        setUpStartCancelButton();
        updateTimer(timeLeft);
        startAnimationTimer();
        setUpBackButton();
    }

    private void setUpChooseTimeButton() {
        String[] durations = getResources().getStringArray(R.array.durations_for_count_down);

        Button time = findViewById(R.id.btnChooseTime);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog durationDialog;
                AlertDialog.Builder durationBuilder = new AlertDialog.Builder(TimerActivity.this);

                durationBuilder.setTitle(R.string.choose_your_duration);

                durationBuilder.setSingleChoiceItems(durations, -1,
                        ((dialogInterface, position) -> {
                            results = durations[position];
                        }));

                durationBuilder.setPositiveButton(R.string.ok, ((dialogInterface, i) -> {
                    switch (results) {
                        case "1 minute":
                            timeManager.setMinuteInMillis(MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            timeLeft = MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                            cancelTimer();
                            updateTimer(timeLeft);
                            break;
                        case "2 minutes":
                            timeManager.setMinuteInMillis(2 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            timeLeft = 2 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                            cancelTimer();
                            updateTimer(timeLeft);
                            break;
                        case "3 minutes":
                            timeManager.setMinuteInMillis(3 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            timeLeft = 3 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                            cancelTimer();
                            updateTimer(timeLeft);
                            break;
                        case "5 minutes":
                            timeManager.setMinuteInMillis(5 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            timeLeft = 5 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                            cancelTimer();
                            updateTimer(timeLeft);
                            break;
                        case "10 minutes":
                            timeManager.setMinuteInMillis(10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            timeLeft = 10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS;
                            cancelTimer();
                            updateTimer(timeLeft);
                            break;

                        case "Others":
                            EditText customDuration = new EditText(TimerActivity.this);
                            AlertDialog customDurationDialog;
                            AlertDialog.Builder customDurationBuilder = new AlertDialog.Builder(TimerActivity.this);

                            customDuration.setHint(R.string.minutes);

                            customDurationBuilder.setTitle(R.string.customize_your_duration_in_minutes);
                            customDurationBuilder.setView(customDuration);

                            customDurationBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
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
                                        Toast.makeText(TimerActivity.this, getString(R.string.please_enter_a_positive_integer_for_minutes), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            customDurationBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(TimerActivity.this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                                }
                            });

                            customDurationDialog = customDurationBuilder.create();
                            customDurationDialog.show();

                            cancelTimer();
                            updateTimer(timeLeft);
                            break;
                    }
                    updateTimer(timeManager.getMinuteInMillis());
                }));

                durationBuilder.setNegativeButton(R.string.cancel, ((dialogInterface, i) -> {
                    Toast.makeText(TimerActivity.this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);

                builder.setTitle(getString(R.string.times_up));

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
        NotificationCompat.Builder notificationBuilder = notificationHelper.getChannelNotification(getString(R.string.times_up),
                getString(R.string.click_on_this_message_to_return));
        notificationHelper.getManager().notify(1, notificationBuilder.build());
    }

    private void startAnimationTimer()
    {
        Animation drift = AnimationUtils.loadAnimation(TimerActivity.this, R.anim.drift_from_bottom);

        startDriftAnimationForButton(R.id.btnChooseTime, drift);
        startDriftAnimationForButton(R.id.btnCancelCountDown, drift);
        startDriftAnimationForButton(R.id.btnStartCounting, drift);

        ImageView box = findViewById(R.id.imageViewGradientBox);
        box.setVisibility(View.VISIBLE);
        box.startAnimation(drift);

        CardView cv = findViewById(R.id.cardViewTimeLeft_timer);
        cv.setVisibility(View.VISIBLE);
        cv.startAnimation(drift);
    }

    private void startDriftAnimationForButton(int resId, Animation anim)
    {
        Button btn = findViewById(resId);
        btn.setVisibility(View.VISIBLE);
        btn.startAnimation(anim);
    }

    public static Intent makeLaunchIntent(Context c) { return new Intent(c, TimerActivity.class); }

    @Override
    public void onBackPressed() {
        if (isRunning) {
            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    private void setUpBackButton()
    {
        ImageView back = findViewById(R.id.imageViewBackButton_timer);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}