package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Random;

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
        flipGame = new FlipCoinGame();

        setUpCoinFlipOnClick();
        if(childrenManager.getNumberOfChildren() > 0)
        {
            setUpNewFlipCoinGame();
            displayDialogToAskForHeadTailChoice();
        }
    }

    private void displayDialogToAskForHeadTailChoice()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlipCoinActivity.this);
        builder.setTitle("Hello " + flipGame.getPickerName() + "!");
        builder.setMessage("Would you pick head or tail?");

        builder.setPositiveButton("HEAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flipGame.setPickerChoice(FlipCoinGame.FlipOptions.HEAD);
            }
        });

        builder.setNegativeButton("TAIL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flipGame.setPickerChoice(FlipCoinGame.FlipOptions.TAIL);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static Intent makeIntent(Context c)
    {
        Intent intent = new Intent(c, FlipCoinActivity.class);
        return intent;
    }

    private void setUpNewFlipCoinGame()
    {
        GameRotationManager rotation_man = new GameRotationManager(childrenManager, gameHistory);
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
                Random rand = new Random();
                int randomNum = rand.nextInt(100);
                if(randomNum % 2 == 0) //if head
                {
                    flipGame.setResult(FlipCoinGame.FlipOptions.HEAD);
                }
                else
                {
                    flipGame.setResult(FlipCoinGame.FlipOptions.TAIL);
                }
                performFlipCoin();
            }
        });
    }

    private void performFlipCoin()
    {
        final MediaPlayer medPlayer = MediaPlayer.create(this, R.raw.coin_flip_sound);

        ImageView imvCoin = findViewById(R.id.imageViewCoin);
        Animation flip_coin_anim = AnimationUtils.loadAnimation(FlipCoinActivity.this, R.anim.flip_coin_animation);
        flip_coin_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final ImageView coin = findViewById(R.id.imageViewCoin);
                if(flipGame.getResult() == FlipCoinGame.FlipOptions.HEAD)
                {
                    coin.setImageResource(R.drawable.coin_head);
                }
                else
                {
                    coin.setImageResource(R.drawable.coin_tail);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        imvCoin.startAnimation(flip_coin_anim);
        medPlayer.start();
    }
}