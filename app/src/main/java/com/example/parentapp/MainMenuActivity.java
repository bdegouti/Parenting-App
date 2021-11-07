package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.parentapp.model.ChildrenManager;
import com.example.parentapp.model.FlipCoinGameHistory;

/**
 * MainMenuActivity class provides a means to navigate between all other activities.
 * It has button that leads user to a new flip coin game, to game history list, to the countdown timer, and to children list.
 */
public class MainMenuActivity extends AppCompatActivity {
    private ChildrenManager childrenManager;
    private FlipCoinGameHistory flipCoinGameHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        childrenManager = ChildrenManager.getInstance();
        flipCoinGameHistory = FlipCoinGameHistory.getInstance();
        loadChildrenListFromSharedPreferences();
        loadGameListFromSharedPreferences();

        setUpAnimationForMainMenuTitle();
        setUpButtonFlipCoin();
        setUpButtonGameHistory();
        setUpButtonTimer();
        setUpButtonConfigureChildren();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void loadGameListFromSharedPreferences()
    {
        String gameListJson = FlipCoinActivity.getGameHistoryFromSharedPreferences(MainMenuActivity.this);
        this.flipCoinGameHistory.convertSaveHistoryFromJson(gameListJson);
    }

    private void loadChildrenListFromSharedPreferences(){
        String childrenListJson = ChildrenListActivity.getChildrenListFromSharedPreferences(MainMenuActivity.this);
        this.childrenManager.convertChildrenListFromJson(childrenListJson);
    }

    private void setUpAnimationForMainMenuTitle()
    {
        Animation floating_anim = AnimationUtils.loadAnimation(MainMenuActivity.this, R.anim.floating);
        TextView tv = findViewById(R.id.textViewMainMenu);
        tv.startAnimation(floating_anim);
    }

    private void setUpButtonFlipCoin()
    {
        Button btnFlipCoin = findViewById(R.id.buttonFlipCoin);
        btnFlipCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = FlipCoinActivity.makeIntent(MainMenuActivity.this);
                startActivity(intent);
            }
        });
        startAnimationOnButton(R.id.buttonFlipCoin, 100);
    }

    private void setUpButtonGameHistory() {
        Button btnGH = findViewById(R.id.buttonFlipCoinHistory);
        btnGH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = GameHistoryActivity.makeLaunchIntent(MainMenuActivity.this);
                startActivity(intent);
            }
        });
        startAnimationOnButton(R.id.buttonFlipCoinHistory, 300);
    }

    private void setUpButtonTimer()
    {
        Button btnTimer = findViewById(R.id.buttonTimer);
        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TimerActivity.makeLaunchIntent(MainMenuActivity.this);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        startAnimationOnButton(R.id.buttonTimer, 500);
    }

    private void setUpButtonConfigureChildren()
    {
        Button btn = findViewById(R.id.buttonConfigureChildren);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChildrenListActivity.makeIntent(MainMenuActivity.this);
                startActivity(intent);
            }
        });
        startAnimationOnButton(R.id.buttonConfigureChildren, 700);
    }

    private void startAnimationOnButton(int resId, int delayedTime)
    {
        Button btn = findViewById(resId);
        Animation anim = AnimationUtils.loadAnimation(MainMenuActivity.this, R.anim.drift_from_bottom);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(View.VISIBLE);
                btn.startAnimation(anim);
            }
        }, delayedTime);
    }
}

