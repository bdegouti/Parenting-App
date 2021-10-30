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
        setUpButtonFlipCoin();
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
}

