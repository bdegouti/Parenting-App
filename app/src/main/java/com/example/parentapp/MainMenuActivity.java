package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setUpButtonTimer();
        setUpButtonGameHistory();
        setUpButtonFlipCoin();
        setUpButtonConfigureChildren();

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
    }

    private void setUpButtonTimer()
    {
        Button btnTimer = findViewById(R.id.buttonTimer);
        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = Timer.makeLaunchIntent(MainMenuActivity.this);
                startActivity(i);
            }
        });
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
    }
}

