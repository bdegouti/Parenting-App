package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.model.FlipCoinGame;
import com.example.parentapp.model.FlipCoinGameHistory;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class GameHistoryActivity extends AppCompatActivity {
    FlipCoinGameHistory flipCoinGameHistory;
    private static final String APP_PREFERENCES = "app preferences";
    private static final String GAME_LIST = "game list";

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, GameHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        flipCoinGameHistory = FlipCoinGameHistory.getInstance();
        populateGameHistoryListView();
        setupClearHistoryButton();
        setClickableStatusForClearHistoryButton();

        handleEmptyGameHistory();
    }

    private void handleEmptyGameHistory() {
        ImageView ivNoGamesIcon = findViewById(R.id.imageViewEmptyStateNoGamesIcon);
        TextView tvNoGamesText = findViewById(R.id.textEmptyStateViewNoGames);
        TextView tvInstruction = findViewById(R.id.textViewNoHistoryInstruction);

        if(flipCoinGameHistory.getNumberOfGames() == 0) {
            ivNoGamesIcon.setVisibility(View.VISIBLE);
            tvNoGamesText.setVisibility(View.VISIBLE);
            tvInstruction.setVisibility(View.VISIBLE);

            Animation empty_box_bounce = AnimationUtils.loadAnimation(GameHistoryActivity.this, R.anim.empty_box_jump);
            Animation text_shudder = AnimationUtils.loadAnimation(GameHistoryActivity.this, R.anim.text_shudder);

            ivNoGamesIcon.startAnimation(empty_box_bounce);
            tvNoGamesText.startAnimation(text_shudder);
            tvInstruction.startAnimation(text_shudder);
        }
        else {
            ivNoGamesIcon.clearAnimation();
            tvNoGamesText.clearAnimation();
            tvInstruction.clearAnimation();

            ivNoGamesIcon.setVisibility(View.INVISIBLE);
            tvNoGamesText.setVisibility(View.INVISIBLE);
            tvInstruction.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        saveGameListToSharedPreferences();
        super.onPause();
    }

    private void saveGameListToSharedPreferences()
    {
        String listJson = this.flipCoinGameHistory.convertHistoryToJson();
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GAME_LIST, listJson);
        editor.apply();
    }

    private void setClickableStatusForClearHistoryButton()
    {
        Button btnClearHist = findViewById(R.id.buttonClearGameHistory);
        if(flipCoinGameHistory.getNumberOfGames() == 0)
        {
            btnClearHist.setClickable(false);
            btnClearHist.setAlpha(0.4f);
        }
    }

    private void setupClearHistoryButton() {
        Button clearGameButton = findViewById(R.id.buttonClearGameHistory);
        clearGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               displayDialogToConfirmClearHistory();
            }
        });
    }

    private void displayDialogToConfirmClearHistory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameHistoryActivity.this);
        builder.setTitle(R.string.clear_all_coin_flip_history);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flipCoinGameHistory.clearHistory();
                populateGameHistoryListView();
                handleEmptyGameHistory();
                setClickableStatusForClearHistoryButton();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void populateGameHistoryListView()
    {
        ArrayAdapter<FlipCoinGame> gameHistoryListAdapter = new GameHistoryListAdapter();
        ListView listview = findViewById(R.id.listViewGameList);
        listview.setAdapter(gameHistoryListAdapter);
        listview.setDivider(null);
    }

    private class GameHistoryListAdapter extends ArrayAdapter<FlipCoinGame>{
        public GameHistoryListAdapter()
        {
            super(GameHistoryActivity.this,
                    R.layout.game_view_card,
                    flipCoinGameHistory.getGameHistoryList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View gameView = convertView;
            if(gameView == null)
            {
                gameView = getLayoutInflater().inflate(R.layout.game_view_card, parent, false);
            }

            //fill up this view
            FlipCoinGame currentGame = flipCoinGameHistory.getGameAtIndex(position);
            TextView tvName = gameView.findViewById(R.id.gameViewCard_textViewPickerName);
            TextView tvDaT = gameView.findViewById(R.id.gameViewCard_textViewCreationTime);
            TextView tvResult = gameView.findViewById(R.id.gameViewCard_textViewResultFlip);
            ImageView resultIcon = gameView.findViewById(R.id.gameViewCard_imageViewWinningSymbol);

            //set up icon
            if(currentGame.getPickerChoice() == currentGame.getResult()){
                resultIcon.setBackgroundResource(R.drawable.win_icon);
            }
            else{
                resultIcon.setBackgroundResource(R.drawable.lose_icon);
            }

            //display datetime creation
            DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
            tvDaT.setText(currentGame.getCreationDateTimeString());

            //display picker name
            tvName.setText(currentGame.getPickerName());

            //display flip result
            tvResult.setText(getString(R.string.picked_vs_result,
                    currentGame.getPickerChoice().toString(),
                    currentGame.getResult().toString()));

            return gameView;
        }
    }
}