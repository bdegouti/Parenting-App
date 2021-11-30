package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TakeBreathActivity extends AppCompatActivity {
    private int totalNumOfBreaths_g = 3;
    private final State state_readyToStart = new ReadyToStartState();
    private final State state_waitingToInhale = new WaitingToInhale();
    private final State state_inhaling = new InhalingState();
    private final State state_inhaleFor3s = new InhaleFor3s();
    private State currentState = new IdleState();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private long startTimeSinceInButtonPressed;
    private long timeInterval;

    // ************************************************************
    // State Pattern states
    // ************************************************************

    // State Pattern's base states
    private abstract class State {
        // Empty implementations, so derived class don't need to
        // override methods they don't care about.
        void handleEnter() {}
        void handleExit() {}
        void handleClickOn() {}
        void handlePressOn(){}
    }

    /////// READY TO START //////////////////////////////////////////
    private class ReadyToStartState extends State {
        int tempNumOfBreaths = totalNumOfBreaths_g;
        @Override
        void handleEnter() {
            renameButton(R.id.button_takeBreath, "Begin");
            setUpTakeBreathButtonForReadyToStart();
            setUpButtonChooseNumberOfBreaths();
        }

        @Override
        void handleExit() {
            Button btn = findViewById(R.id.buttonChooseNumberOfBreaths_takeBreath);
            btn.setVisibility(View.INVISIBLE);
            Button beginBtn = findViewById(R.id.button_takeBreath);
            beginBtn.setOnClickListener(null);
        }

        private void setUpButtonChooseNumberOfBreaths()
        {
            int[] breaths_options = getResources().getIntArray(R.array.number_of_breaths);

            Button btn = findViewById(R.id.buttonChooseNumberOfBreaths_takeBreath);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog breathsOptionsDialog;
                    AlertDialog.Builder breathsOptionsBuilder = new AlertDialog.Builder(TakeBreathActivity.this);

                    breathsOptionsBuilder.setTitle("Choose number of breaths:");

                    breathsOptionsBuilder.setSingleChoiceItems(R.array.number_of_breaths, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int positionClicked) {
                            tempNumOfBreaths = breaths_options[positionClicked];
                        }
                    });

                    breathsOptionsBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            totalNumOfBreaths_g = tempNumOfBreaths;
                        }
                    });

                    breathsOptionsBuilder.setNegativeButton(R.string.cancel, null);
                }
            });
        }

        private void setUpTakeBreathButtonForReadyToStart() {
            Button btn = findViewById(R.id.button_takeBreath);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setState(state_waitingToInhale);
                }
            });
        }
    }

    //////////// WAITING TO INHALE ////////////////////////////////////////////////////////
    private class WaitingToInhale extends State {
        @Override
        void handleEnter() {
            renameButton(R.id.button_takeBreath, "IN");
            resetText(R.id.textView_takeBreath, "Hold button and breath in.");
            setUpTakeBreathButton();
        }

        @Override
        void handlePressOn() {

        }


    }

    ///////////////// INHALING ///////////////////////////////////
    private class InhalingState extends State{
        @Override
        void handleEnter() {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    setState(state_inhaleFor3s);
                }
            };
            executor.schedule(task, 3, TimeUnit.SECONDS);
            //TODO: start animation and sound
        }

        @Override
        void handleExit() {

        }
    }

    /////////////// INHALE FOR 3S //////////////////////////////////////////////
    private class InhaleFor3s extends State{
        @Override
        void handleEnter() {
            super.handleEnter();
        }
    }

    /////////////// INHALE FOR 10S //////////////////////////////////////////////
    private class InhaleFor10s extends State{
        @Override
        void handleEnter() {

        }
    }



    //////////////// IDLE ////////////////////////////////////////////////////////////////////
    private class IdleState extends State{};


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);

        setState(state_readyToStart);
        setUpTakeBreathButton();
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

    private void setUpTakeBreathButton(){
        Button inBtn = findViewById(R.id.button_takeBreath);
        inBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    //USER START PRESSING THE BUTTON
                    startTimeSinceInButtonPressed = System.currentTimeMillis();
                    setState(state_inhaling);
                    addScheduleTaskForExecutor(state_inhaleFor3s, 3);
                    addScheduleTaskForExecutor(state_;


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //USER STOP PRESSING THE BUTTON
                    timeInterval = System.currentTimeMillis() - startTimeSinceInButtonPressed;
                    if(timeInterval < 3000)
                    {
                        executor.shutdown();
                        setState(state_waitingToInhale);
                    }
                    else if(timeInterval < 10000)
                    {
                        setState();
                    }

                }
            }
        });
    }

    private void addScheduleTaskForExecutor(State state, long time){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                setState(state);
            }
        };
        executor.schedule(task, time, TimeUnit.SECONDS);
    }
}