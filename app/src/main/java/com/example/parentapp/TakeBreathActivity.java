package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TakeBreathActivity extends AppCompatActivity {
    private int totalNumOfBreaths_g = 3;
    private int numOfBreathsLeft;
    int tempNumOfBreaths = totalNumOfBreaths_g;
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

    // ************************************************************
    // State Pattern states
    // ************************************************************
    // State Pattern's base states
    private abstract class State {
        // Empty implementations, so derived class don't need to
        // override methods they don't care about.
        void handleEnter() {}
        void handleExit() {}
        void handleButtonActionDown(){}
        void handleButtonActionUp(){}
    }

    /////// READY TO START //////////////////////////////////////////
    private class ReadyToStartState extends State {
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "READY TO START STATE", Toast.LENGTH_SHORT).show();
            renameButton(R.id.button_takeBreath, "Begin");
            setUpButtonChooseNumberOfBreaths();
        }

        @Override
        void handleExit() {
            Button btn = findViewById(R.id.buttonChooseNumberOfBreaths_takeBreath);
            btn.setVisibility(View.INVISIBLE);
        }

        @Override
        void handleButtonActionDown() {
            //do nothing
        }

        @Override
        void handleButtonActionUp() {
            setState(state_waitingToInhale);
        }


    }

    //////////// WAITING TO INHALE ////////////////////////////////////////////////////////
    private class WaitingToInhale extends State {
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "WAITING TO INHALE STATE", Toast.LENGTH_SHORT).show();
            renameButton(R.id.button_takeBreath, "IN");
            resetText(R.id.textView_takeBreath, "Hold button and breath in.");
        }

        @Override
        void handleButtonActionDown() {
            setState(state_inhaling);
        }

        @Override
        void handleButtonActionUp() {
            //do nothing
        }
    }

    ///////////////// INHALING ///////////////////////////////////
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
            //TODO: start animation and sound
        }

        @Override
        void handleExit() {
            handlerInhalingState.removeCallbacks(switchToInhaleFor3s);
        }

        @Override
        void handleButtonActionDown() {

        }

        @Override
        void handleButtonActionUp() {
            setState(state_waitingToInhale);
        }
    }

    /////////////// INHALE FOR 3S //////////////////////////////////////////////
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
        }

        @Override
        void handleExit() {
            handlerInhaleFor3s.removeCallbacks(switchToInhaleFor10s);
        }

        @Override
        void handleButtonActionDown() {

        }

        @Override
        void handleButtonActionUp() {
            setState(state_doneInhale);
        }
    }

    /////////////// INHALE FOR 10S //////////////////////////////////////////////
    private class InhaleFor10s extends State{
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "INHALE FOR 10S STATE", Toast.LENGTH_SHORT).show();
            //change text view
            TextView tv = findViewById(R.id.textView_takeBreath);
            tv.setText("Release button and breathe out.");

            //TODO: stop animation and sound
        }

        @Override
        void handleButtonActionUp() {
            setState(state_doneInhale);
        }
    }


    /////////////// DONE INHALE ///////////////////////////////////////////////////////
    private class DoneInhale extends State {
        @Override
        void handleEnter() {
            Toast.makeText(TakeBreathActivity.this, "DONE INHALE STATE", Toast.LENGTH_SHORT).show();
            //TODO: top animation and sound
            setState(state_exhale);
        }

        @Override
        void handleExit() {
            super.handleExit();
        }
    }


    /////////////// EXHALE ////////////////////////////////////////////
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
            renameButton(R.id.button_takeBreath, "OUT");
            //TODO: start exhale animation and sound
            handlerExhaleState.postDelayed(switchToExhale3s, 3000);
        }

    }


    //////////////// EXHALE 3S //////////////////////////////////////
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
            String temp = "" + numOfBreathsLeft + " breaths left!";
            Toast.makeText(TakeBreathActivity.this, temp, Toast.LENGTH_SHORT).show();

            if(numOfBreathsLeft > 0)
            {
                renameButton(R.id.button_takeBreath, "IN");
            }
            else
            {
                renameButton(R.id.button_takeBreath, "Good job!");
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
            //TODO: stop exhale animation and sound
            Toast.makeText(TakeBreathActivity.this, "DONE EXHALE STATE", Toast.LENGTH_SHORT).show();

            if(numOfBreathsLeft > 0)
            {
                setState(state_waitingToInhale);
            }
            else {
                Toast.makeText(TakeBreathActivity.this, "YOU DONE!", Toast.LENGTH_SHORT).show();
                setState(state_readyToStart);
            }
        }
    }

    //////////////// IDLE ////////////////////////////////////////////////////////////////////
    private class IdleState extends State{};


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);

        numOfBreathsLeft = totalNumOfBreaths_g;

        setState(state_readyToStart);
        setUpButtonsG();
    }

    private void setUpButtonsG()
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

        Button btn = findViewById(R.id.buttonChooseNumberOfBreaths_takeBreath);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder breathsOptionsBuilder = new AlertDialog.Builder(TakeBreathActivity.this);
                breathsOptionsBuilder.setTitle("Choose number of breaths:");

                breathsOptionsBuilder.setSingleChoiceItems(R.array.number_of_breaths, 2, new DialogInterface.OnClickListener() {
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
                    }
                });

                breathsOptionsBuilder.setNegativeButton(R.string.cancel, null);

                AlertDialog breathsOptionsDialog = breathsOptionsBuilder.create();
                breathsOptionsDialog.show();
            }
        });
    }
}