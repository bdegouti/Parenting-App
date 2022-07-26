package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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
    private final int DEFAULT_RATE = 1;

    // default time is set to 1 minute in milliseconds
    private final TimeManager timeManager = new TimeManager(MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);

    private Button startCount;
    private TextView timeRemaining;
    private Button rateOfSpeed;
    private TextView rateDisplay;

    // storing the result of duration that the  user selected and
    // the default is set to 1 minute
    private String results = "1 minute";

    private CountDownTimer countDownTimer;
    private boolean isRunning;
    private double timeLeft = timeManager.getMinuteInMillis();
    private double timeLeftRated = timeManager.getMinuteInMillis();
    private double rate = timeManager.getRateOfSpeed();

    private MediaPlayer musicPlayer;
    private Vibrator vibrator;

    private NotificationHelper notificationHelper;

    private int tempRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle(getString(R.string.count_down_timer));

        notificationHelper = new NotificationHelper(this);

        setUpChooseTimeButton();
        setUpStartCancelButton();
        setUpRateOfSpeedButton();
        updateTimer(timeManager.getMinuteInMillis());
        startAnimationTimer();
        setUpBackButton();
        resetProgressPieChart();
    }

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
                            cancelTimer();
                            updateTimer(timeManager.getMinuteInMillis());
                            break;
                        case "2 minutes":
                            timeManager.setMinuteInMillis(2 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            cancelTimer();
                            updateTimer(timeManager.getMinuteInMillis());
                            break;
                        case "3 minutes":
                            timeManager.setMinuteInMillis(3 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            cancelTimer();
                            updateTimer(timeManager.getMinuteInMillis());
                            break;
                        case "5 minutes":
                            timeManager.setMinuteInMillis(5 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            cancelTimer();
                            updateTimer(timeManager.getMinuteInMillis());
                            break;
                        case "10 minutes":
                            timeManager.setMinuteInMillis(10 * MILLISECOND_TO_SECOND * MINUTES_TO_SECONDS);
                            cancelTimer();
                            updateTimer(timeManager.getMinuteInMillis());
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
                                        cancelTimer();
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
                            updateTimer(timeManager.getMinuteInMillis());
                            break;
                    }
                    updateTimer(timeManager.getMinuteInMillis());
                    rateDisplay = findViewById(R.id.txtRateDisplay);
                    rateDisplay.setText(getString(R.string.time_at_100_percent));
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
                    getWindow(). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    private void setUpRateOfSpeedButton() {
        rateOfSpeed = findViewById(R.id.btnRateOfSpeed);
        rateOfSpeed.setVisibility(View.INVISIBLE);
        rateDisplay = findViewById(R.id.txtRateDisplay);
        rateDisplay.setVisibility(View.INVISIBLE);

        rateOfSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog rateDialog = new Dialog(TimerActivity.this);

                rateDialog.setContentView(R.layout.rate_of_speed_custom_dialog);
                rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView txtRate = rateDialog.findViewById(R.id.txtRate);
                SeekBar seekBarRate = rateDialog.findViewById(R.id.seekBarRate);
                Button saveButton = rateDialog.findViewById(R.id.btnSaveRate);
                Button standardRateButton = rateDialog.findViewById(R.id.btnCustomRate);

                final double[] rateTemp = {100};
                seekBarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        txtRate.setText(progress +  "%");
                        rateTemp[0] = progress;
                        rate = (double) progress / 100;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }

                });

                standardRateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int standardRate[] = getResources().getIntArray(R.array.standard_rate);
                        String standardRateString[] = getResources().getStringArray(R.array.standard_rate_string);
                        AlertDialog standardRateDialog;
                        AlertDialog.Builder standardRateBuilder = new AlertDialog.Builder(TimerActivity.this);

                        standardRateBuilder.setTitle("Choose standard rate");
                        standardRateBuilder.setSingleChoiceItems(standardRateString, -1, ((dialogInterface, position) -> {
                            tempRate = standardRate[position];
                        }));

                        standardRateBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                txtRate.setText(tempRate +"%");
                                rateTemp[0] = tempRate;
                                rate = (double) tempRate / 100;
                                seekBarRate.setProgress(tempRate);
                            }
                        });

                        standardRateDialog = standardRateBuilder.create();
                        standardRateDialog.show();

                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rateDialog.dismiss();
                        timeManager.setRateOfSpeed(rateTemp[0] / 100);
                        rateDisplay.setText(getString(R.string.time_at_some_percent, (int)(rate*100)));
                        pauseTimer();
                        startTimer();
                    }
                });

                rateDialog.show();
            }
        });
    }

    // Some of the code below was adapted from the Youtube video linked:
    // https://www.youtube.com/watch?v=MDuGwI6P-X8
    private void startTimer() {
        timeLeftRated = timeLeft;
        timeLeftRated /= rate;
        countDownTimer = new CountDownTimer((long) timeLeftRated, (long) (1000 / rate)) {
            @Override
            public void onTick(long milliSecUntilFinished) {
                rateOfSpeed.setVisibility(View.VISIBLE);
                rateDisplay.setVisibility(View.VISIBLE);
                timeLeft -= 1000;
                updateTimer(timeLeft);

                int progressPercentage = (int)((timeLeft / timeManager.getMinuteInMillis()) * 100);
                ProgressBar pieChart = findViewById(R.id.progressBar_timer);
                pieChart.setProgress(progressPercentage);
            }

            @Override
            public void onFinish() {
                resetProgressPieChart();

                timeLeft = timeManager.getMinuteInMillis();
                rate = DEFAULT_RATE;
                isRunning = false;
                updateTimer(timeManager.getMinuteInMillis());
                startCount.setText(R.string.start);
                rateOfSpeed.setVisibility(View.INVISIBLE);
                rateDisplay.setVisibility(View.INVISIBLE);
                rateDisplay.setText(getString(R.string.time_at_100_percent));

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
        rateOfSpeed.setVisibility(View.INVISIBLE);
        rateDisplay.setVisibility(View.INVISIBLE);
        countDownTimer.cancel();
        isRunning = false;
        startCount.setText(R.string.start);
    }

    private void cancelTimer() {
        if (isRunning) {
            countDownTimer.cancel();
        }
        rateOfSpeed.setVisibility(View.INVISIBLE);
        rateDisplay.setVisibility(View.INVISIBLE);
        isRunning = false;
        updateTimer(timeManager.getMinuteInMillis());
        startCount.setText(R.string.start);
        timeLeft = timeManager.getMinuteInMillis();
        rate = DEFAULT_RATE;

        resetProgressPieChart();
    }

    private void updateTimer(double time) {
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

    private void resetProgressPieChart()
    {
        ProgressBar pieChart = findViewById(R.id.progressBar_timer);
        pieChart.setProgress(100);
    }
}