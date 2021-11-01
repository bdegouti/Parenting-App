package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.model.FlipCoinGame;
import com.example.parentapp.model.FlipCoinGameHistory;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class GameHistoryActivity extends AppCompatActivity {

    FlipCoinGameHistory flipCoinGameHistory;

    public static Intent makeLaunchIntent(Context c) { return new Intent(c, GameHistoryActivity.class); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flipCoinGameHistory = FlipCoinGameHistory.getInstance();
        setContentView(R.layout.activity_game_history);

        populateGameHistoryListView();
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