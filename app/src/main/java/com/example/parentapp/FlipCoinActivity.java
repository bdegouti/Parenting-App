package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.parentapp.model.ChildrenManager;
import com.example.parentapp.model.FlipCoinGame;
import com.example.parentapp.model.FlipCoinGameHistory;
import com.example.parentapp.model.GameRotationManager;

public class FlipCoinActivity extends AppCompatActivity {
    private FlipCoinGameHistory gameHistory;
    private ChildrenManager childrenManager;
    private FlipCoinGame flipGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);

        gameHistory = FlipCoinGameHistory.getInstance();
        childrenManager = ChildrenManager.getInstance();

        setUpNewFlipCoinGame();


        setUpCoinFlipOnClick();
    }

    public static Intent makeIntent(Context c)
    {
        Intent intent = new Intent(c, FlipCoinActivity.class);
        return intent;
    }

    private void setUpNewFlipCoinGame()
    {
        GameRotationManager rotation_man = new GameRotationManager(childrenManager, gameHistory);
        flipGame = new FlipCoinGame();

        flipGame.setPickerName(rotation_man.getNameNextChildToPickHeadTail());
        flipGame.setPickerIndex(rotation_man.getIndexNextChildToPickHeadTail());
    }

    private void setUpCoinFlipOnClick()
    {
        ImageView imageViewCoin = findViewById(R.id.imageViewCoin);
        imageViewCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "You are tossing the coin!", Toast.LENGTH_SHORT).show();

                performFlipCoin();
            }
        });
    }

    private void performFlipCoin()
    {
        MediaPlayer medPlayer = MediaPlayer.create(this, R.raw.coin_flip_sound);
        medPlayer.start();

        ImageView imvCoin = findViewById(R.id.imageViewCoin);
        Animation flip_coin_anim = AnimationUtils.loadAnimation(FlipCoinActivity.this, R.anim.flip_coin_animation);
        imvCoin.startAnimation(flip_coin_anim);
    }
}