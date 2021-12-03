package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TakeBreathActivity extends AppCompatActivity {
    private static final String APP_PREFERENCES = "app preferences";
    private static final String NUMBER_OF_BREATHS = "number of breaths";
    private int totalNumOfBreaths_g;
    private int numOfBreathsLeft;
    int tempNumOfBreaths;
    private final State state_readyToStart = new ReadyToStartState();
    private final State state_waitingToInhale = new WaitingToInhale();
    private final State state_inhaling = new InhalingState();
    private final State state_inhaleFor3s = new InhaleFor3s();
    private final State state_inhaleFor10s = new InhaleFor10s();
    private final State state_doneInhale = new DoneInhale();
    private final State state_exhale = new ExhaleState();
    private final State state_exhale3s = new Exhale3sState();
    private final State state_doneExhale = new DoneExhaleState();
    private State currentState = new IdleState();
    MediaPlayer inhaleMusic;
    MediaPlayer exhaleMusic;

    // ************************************************************
    // State Pattern states
    // ************************************************************
    private abstract class State {
        // Empty implementations, so derived class don't need to
        // override methods they don't care about.
        void handleEnter() {}
        void handleExit() {}
        void handleButtonActionDown(){}
        void handleButtonActionUp(){}
    }

    /////// STATE: READY TO START //////////////////////////////////////////
    private class ReadyToStartState extends State {
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "READY TO START STATE", Toast.LENGTH_SHORT).show();

            //start over number of breaths
            numOfBreathsLeft = totalNumOfBreaths_g;

            //set up card view to customize number of breaths
            CardView cv = findViewById(R.id.cardView_chooseNumberOfBreaths_takeBreaths);
            cv.setVisibility(View.VISIBLE);
            setUpButtonChooseNumberOfBreaths();

            //set up text
            TextView introTV = findViewById(R.id.textView_takeBreath);
            introTV.setText(getString(R.string.lets_take_n_breaths_together, totalNumOfBreaths_g));

            //set up big button
            renameButton(R.id.button_takeBreath, "Begin");
            changeBackgroundColorBigButton(R.drawable.circle_teal);

            //set up sound effects
            inhaleMusic = MediaPlayer.create(TakeBreathActivity.this, R.raw.inhale_music);
            exhaleMusic = MediaPlayer.create(TakeBreathActivity.this, R.raw.exhale_music);
        }

        @Override
        void handleExit() {
            CardView cv = findViewById(R.id.cardView_chooseNumberOfBreaths_takeBreaths);
            cv.setVisibility(View.INVISIBLE);
        }

        @Override
        void handleButtonActionUp() {
            setState(state_waitingToInhale);
        }
    }

    //////////// STATE: WAITING TO INHALE ////////////////////////////////////////////////////////
    private class WaitingToInhale extends State {
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "WAITING TO INHALE STATE", Toast.LENGTH_SHORT).show();
            renameButton(R.id.button_takeBreath, getString(R.string.in_capitalized));
            resetText(R.id.textView_takeBreath, getString(R.string.hold_button_and_breath_in));
            changeBackgroundColorBigButton(R.drawable.circle_teal);
        }

        @Override
        void handleButtonActionDown() {
            setState(state_inhaling);
        }
    }

    ///////////////// STATE: INHALING ///////////////////////////////////
    private class InhalingState extends State{
        Handler handlerInhalingState = new Handler();
        Runnable switchToInhaleFor3s = new Runnable() {
            @Override
            public void run() {
                setState(state_inhaleFor3s);
            }
        };

        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "INHALING STATE", Toast.LENGTH_SHORT).show();
            handlerInhalingState.postDelayed(switchToInhaleFor3s, 3000);
            //start sound & animation
            startInhaleMusic();
            startAnimationForBigButton(R.anim.scale_up);
            changeBackgroundColorBigButton(R.drawable.circle_green);
        }

        @Override
        void handleExit() {
            handlerInhalingState.removeCallbacks(switchToInhaleFor3s);
        }

        @Override
        void handleButtonActionUp() {
            setState(state_waitingToInhale);
            stopInhaleMusic();
            clearAnimationForBigButton();
        }
    }

    /////////////// STATE: INHALE FOR 3S //////////////////////////////////////////////
    private class InhaleFor3s extends State{
        Handler handlerInhaleFor3s = new Handler();
        Runnable switchToInhaleFor10s = new Runnable() {
            @Override
            public void run() {
                setState(state_inhaleFor10s);
            }
        };

        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "INHALE FOR 3S STATE", Toast.LENGTH_SHORT).show();
            handlerInhaleFor3s.postDelayed(switchToInhaleFor10s, 7000);
            renameButton(R.id.button_takeBreath, getString(R.string.out_capitalized));
        }

        @Override
        void handleExit() {
            handlerInhaleFor3s.removeCallbacks(switchToInhaleFor10s);
        }

        @Override
        void handleButtonActionUp() {
            setState(state_doneInhale);
        }
    }

    /////////////// STATE: INHALE FOR 10S //////////////////////////////////////////////
    private class InhaleFor10s extends State{
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "INHALE FOR 10S STATE", Toast.LENGTH_SHORT).show();
            //change text view
            TextView tv = findViewById(R.id.textView_takeBreath);
            tv.setText(getString(R.string.release_button_and_breath_out));

            clearAnimationForBigButton();
            stopInhaleMusic();
        }

        @Override
        void handleButtonActionUp() {
            setState(state_doneInhale);
        }
    }

    /////////////// STATE: DONE INHALE ///////////////////////////////////////////////////////
    private class DoneInhale extends State {
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "DONE INHALE STATE", Toast.LENGTH_SHORT).show();
            clearAnimationForBigButton();
            stopInhaleMusic();
            setState(state_exhale);
        }
    }

    /////////////// STATE: EXHALE ////////////////////////////////////////////
    private class ExhaleState extends State {
        Handler handlerExhaleState = new Handler();
        Runnable switchToExhale3s = new Runnable() {
            @Override
            public void run() {
                setState(state_exhale3s);
            }
        };

        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "EXHALE STATE", Toast.LENGTH_SHORT).show();

            TextView tv = findViewById(R.id.textView_takeBreath);
            tv.setText(getString(R.string.slowly_breathe_out));
            renameButton(R.id.button_takeBreath, getString(R.string.out_capitalized));
            //start exhale sound & animation
            startExhaleMusic();
            startAnimationForBigButton(R.anim.scale_down);
            changeBackgroundColorBigButton(R.drawable.circle_blue);

            handlerExhaleState.postDelayed(switchToExhale3s, 3000);
        }
    }

    //////////////// STATE: EXHALE 3S //////////////////////////////////////
    private class Exhale3sState extends State{
        Handler handlerExhale3sState = new Handler();
        Runnable switchToDoneExhale = new Runnable() {
            @Override
            public void run() {
                setState(state_doneExhale);
            }
        };

        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "EXHALE FOR 3S STATE", Toast.LENGTH_SHORT).show();
            numOfBreathsLeft--;
            TextView tv = findViewById(R.id.textView_takeBreath);
            tv.setText(getString(R.string.you_have_some_breaths_left, numOfBreathsLeft));

            if(numOfBreathsLeft > 0)
            {
                renameButton(R.id.button_takeBreath, getString(R.string.in_capitalized));
            }
            else
            {
                renameButton(R.id.button_takeBreath, getString(R.string.good_job));
            }

            handlerExhale3sState.postDelayed(switchToDoneExhale, 7000);
        }

        @Override
        void handleButtonActionDown() {
            handlerExhale3sState.removeCallbacks(switchToDoneExhale);
            setState(state_doneExhale);
        }
    }

    /////////////////// STATE: DONE EXHALE //////////////////////////////////////
    private class DoneExhaleState extends State {
        @Override
        void handleEnter() {
            clearAnimationForBigButton();
            stopExhaleMusic();
            changeBackgroundColorBigButton(R.drawable.circle_teal);

            Toast.makeText(TakeBreathActivity.this, "DONE EXHALE STATE", Toast.LENGTH_SHORT).show();

            if(numOfBreathsLeft > 0) {
                setState(state_waitingToInhale);
            }
            else {
                renameButton(R.id.button_takeBreath, getString(R.string.done));
                Button bigBtn = findViewById(R.id.button_takeBreath);
                bigBtn.setClickable(false);
            }
        }
    }

    //////////////// STATE: IDLE ////////////////////////////////////////////////////////////////////
    private class IdleState extends State{}


    /////////////// PLAIN OLD JAVA CODE //////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);

        loadNumOfBreathsFromSharedPreferences();

        setUpBigButton();
        setUpBackButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setState(state_readyToStart);
    }

    @Override
    protected void onPause() {
        releaseAllMediaPlayers();
        saveNumOfBreathsToSharedPreferences();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadNumOfBreathsFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        totalNumOfBreaths_g = prefs.getInt(NUMBER_OF_BREATHS, 3);
    }

    private void saveNumOfBreathsToSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NUMBER_OF_BREATHS, totalNumOfBreaths_g);
        editor.apply();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpBigButton()
    {
        Button mainBtn = findViewById(R.id.button_takeBreath);
        mainBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    currentState.handleButtonActionDown();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    currentState.handleButtonActionUp();
                }
                return true;
            }
        });
    }

    public void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }

    private void renameButton(int btnId, String btnName)
    {
        Button btn = findViewById(btnId);
        btn.setText(btnName);
    }

    private void resetText(int textViewId, String textStr)
    {
        TextView tv = findViewById(textViewId);
        tv.setText(textStr);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }

    private void setUpButtonChooseNumberOfBreaths()
    {
        String[] breaths_options = getResources().getStringArray(R.array.number_of_breaths);
        tempNumOfBreaths = totalNumOfBreaths_g;

        CardView cv = findViewById(R.id.cardView_chooseNumberOfBreaths_takeBreaths);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder breathsOptionsBuilder = new AlertDialog.Builder(TakeBreathActivity.this);
                breathsOptionsBuilder.setTitle("Choose number of breaths:");

                breathsOptionsBuilder.setSingleChoiceItems(R.array.number_of_breaths, tempNumOfBreaths-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int positionClicked) {
                        tempNumOfBreaths = Integer.parseInt(breaths_options[positionClicked]);
                    }
                });

                breathsOptionsBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        totalNumOfBreaths_g = tempNumOfBreaths;
                        numOfBreathsLeft = totalNumOfBreaths_g;
                        //update the text
                        TextView introTV = findViewById(R.id.textView_takeBreath);
                        introTV.setText(getString(R.string.lets_take_n_breaths_together, totalNumOfBreaths_g));
                    }
                });

                breathsOptionsBuilder.setNegativeButton(R.string.cancel, null);

                AlertDialog breathsOptionsDialog = breathsOptionsBuilder.create();
                breathsOptionsDialog.show();
            }
        });
    }

    private void startAnimationForBigButton(int animID) {
        Button bigBtn = findViewById(R.id.button_takeBreath);
        Animation anim = AnimationUtils.loadAnimation(TakeBreathActivity.this, animID);
        bigBtn.startAnimation(anim);
    }

    private void clearAnimationForBigButton() {
        Button bigBtn = findViewById(R.id.button_takeBreath);
        bigBtn.clearAnimation();
    }

    private void startInhaleMusic()
    {
        inhaleMusic.start();
    }

    private void stopInhaleMusic()
    {
        inhaleMusic.pause();
        inhaleMusic.seekTo(1000);
    }

    private void startExhaleMusic()
    {
        exhaleMusic.start();
    }

    private void stopExhaleMusic()
    {
        exhaleMusic.pause();
        exhaleMusic.seekTo(0);
    }

    private void releaseAllMediaPlayers()
    {
        //release media players
        inhaleMusic.release();
        inhaleMusic = null;
        exhaleMusic.release();
        exhaleMusic = null;
    }

    private void setUpBackButton() {
        ImageView back = findViewById(R.id.imageView_backButton_takeBreaths);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void changeBackgroundColorBigButton(int drawableResID)
    {
        Button btn = findViewById(R.id.button_takeBreath);
        btn.setBackgroundResource(drawableResID);
    }
}