package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.model.ChildrenManager;
import com.example.parentapp.model.FlipCoinGame;
import com.example.parentapp.model.FlipCoinGameHistory;
import com.example.parentapp.model.GameRotationManager;
import com.google.gson.Gson;

import java.util.Random;

public class FlipCoinActivity extends AppCompatActivity {
    private FlipCoinGameHistory gameHistory;
    private ChildrenManager childrenManager;
    private FlipCoinGame flipGame;
    private GameRotationManager rotationManager;
    private static final String APP_PREFERENCES = "app preferences";
    private static final String GAME_LIST = "game list";
    private static final String ROTATION_MANAGER = "rotation manager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);

        gameHistory = FlipCoinGameHistory.getInstance();
        childrenManager = ChildrenManager.getInstance();
        flipGame = new FlipCoinGame();

        rotationManager = new GameRotationManager();
        loadLastPickerDataToGameRotationManagerFromSharedPrefs();

        setUpCoinFlipOnClick();

        if(childrenManager.getNumberOfChildren() > 0)
        {
            setUpNewFlipCoinGame();
            displayDialogToAskForHeadTailChoice();
        }
        else
        {
            startAnimationCardViewFlipResult();
        }
    }

    @Override
    protected void onPause() {
        performAutoSaveFlipGame();
        saveLastPickerDataFromGameRotationManagerToSharedPrefs();
        saveGameHistoryToSharedPreferences();
        super.onPause();
    }

    private void saveLastPickerDataFromGameRotationManagerToSharedPrefs()
    {
        Gson gson = new Gson();
        String rotationJson = gson.toJson(this.rotationManager);

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ROTATION_MANAGER, rotationJson);
        editor.apply();
    }

    private void loadLastPickerDataToGameRotationManagerFromSharedPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String rotation_manager_json = prefs.getString(ROTATION_MANAGER, null);

        if(rotation_manager_json != null)
        {
            Gson gson = new Gson();
            this.rotationManager = gson.fromJson(rotation_manager_json, GameRotationManager.class);
        }
    }

    private void performAutoSaveFlipGame() {
        if(childrenManager.getNumberOfChildren() > 0)
        {
            TextView tvResult = findViewById(R.id.textViewFlipResult);
            String result = tvResult.getText().toString();
            if(!result.equals("") && !result.equals(getString(R.string.three_dots)))
            {
                gameHistory.addNewFlipCoinGame(flipGame);
                Toast.makeText(FlipCoinActivity.this, getString(R.string.flip_coin_result_has_been_saved), Toast.LENGTH_SHORT).show();

                //only when this coin flip is saved can we officially save this child as a last picker
                rotationManager.setNameOfChildLastPicked(flipGame.getPickerName());
                rotationManager.setIndexOfChildLastPicked(flipGame.getPickerIndex());
            }
        }
    }

    private void displayDialogToAskForHeadTailChoice()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlipCoinActivity.this);
        builder.setTitle(getString(R.string.hi_its_someone_turn_to_pick, flipGame.getPickerName()));
        builder.setMessage(R.string.would_you_pick_head_or_tail);

        builder.setPositiveButton(R.string.head, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flipGame.setPickerChoice(FlipCoinGame.FlipOptions.HEAD);
            }
        });

        builder.setNegativeButton(R.string.tail, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flipGame.setPickerChoice(FlipCoinGame.FlipOptions.TAIL);
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startAnimationCardViewFlipResult();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static Intent makeIntent(Context c)
    {
        return new Intent(c, FlipCoinActivity.class);
    }

    private void setUpNewFlipCoinGame()
    {
        String nameCurrentChildWhoIsPicking = rotationManager.getNameNextChildToPickHeadTail(this.childrenManager);
        flipGame.setPickerName(nameCurrentChildWhoIsPicking);
        flipGame.setPickerIndex(childrenManager.getIndexOfChildName(nameCurrentChildWhoIsPicking));
    }

    private void setUpCoinFlipOnClick()
    {
        ImageView imageViewCoin = findViewById(R.id.imageViewCoin);
        imageViewCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clean up result text view
                TextView tvResult = findViewById(R.id.textViewFlipResult);
                tvResult.setText(R.string.three_dots);
                //set datetime
                flipGame.setLocalDateTime();
                //generate random flip
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
                imageViewCoin.setOnClickListener(null);
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
                displayFlipResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        imvCoin.startAnimation(flip_coin_anim);
        medPlayer.start();
    }

    private void displayFlipResult()
    {
        TextView tvResult = findViewById(R.id.textViewFlipResult);
        tvResult.setText(getString(R.string.its_head_tail, flipGame.getResult().toString()));
    }


    private void saveGameHistoryToSharedPreferences()
    {
        String gameListJson = this.gameHistory.convertHistoryToJson();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GAME_LIST, gameListJson);
        editor.apply();
    }

    public static String getGameHistoryFromSharedPreferences(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return prefs.getString(GAME_LIST, null);
    }

    private void startAnimationCardViewFlipResult()
    {
        CardView cv = findViewById(R.id.cardViewFlipResult_flipCoin);
        Animation drift = AnimationUtils.loadAnimation(FlipCoinActivity.this, R.anim.drift_from_bottom);
        cv.setVisibility(View.VISIBLE);
        cv.startAnimation(drift);
    }
}