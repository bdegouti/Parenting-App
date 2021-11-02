package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

    public static Intent makeLaunchIntent(Context c) { return new Intent(c, GameHistoryActivity.class); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        flipCoinGameHistory = FlipCoinGameHistory.getInstance();
        populateGameHistoryListView();
        setupClearHistoryButton();
        setClickableStatusForClearHistoryButton();
    }

    @Override
    protected void onStop() {
        saveGameListToSharedPreferences();
        super.onStop();
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
                setClickableStatusForClearHistoryButton();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    get date and time, who got to pick, result (as check or X f/example)
    */


    private void populateGameHistoryListView()
    {
        ArrayAdapter<FlipCoinGame> gameHistoryListAdapter = new GameHistoryListAdapter();
        ListView listview = findViewById(R.id.listViewGameList);
        listview.setAdapter(gameHistoryListAdapter);
    }

    private class GameHistoryListAdapter extends ArrayAdapter<FlipCoinGame>{
        public GameHistoryListAdapter()
        {
            super(GameHistoryActivity.this,
                    R.layout.game_view,
                    flipCoinGameHistory.getGameHistoryList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View gameView= convertView;
            if(gameView == null)
            {
                gameView = getLayoutInflater().inflate(R.layout.game_view, parent, false);
            }

            //fill up this view
            FlipCoinGame currentGame = flipCoinGameHistory.getGameAtIndex(position);
            TextView tvName = gameView.findViewById(R.id.gameView_textviewPickerName);
            TextView tvDaT = gameView.findViewById(R.id.gameView_textViewCreationDateTime);
            TextView tvResult = gameView.findViewById(R.id.gameView_textviewFlipResult);
            ImageView resultIcon = gameView.findViewById(R.id.gameView_imageviewResultIcon);



            if(currentGame.getPickerChoice() == currentGame.getResult()){
                resultIcon.setBackgroundResource(R.drawable.win_icon);
            }
            else{
                resultIcon.setBackgroundResource(R.drawable.lose_icon);
            }
            StringBuilder resultString = new StringBuilder();
            resultString.append("Picked: " + currentGame.getPickerChoice().toString()
            + " | Result: " + currentGame.getResult().toString());
            DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
            tvName.setText(currentGame.getPickerName());
            tvDaT.setText(currentGame.getCreationDateTime().format(dtf));
            tvResult.setText(resultString);

            return gameView;
        }
    }
}