package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.model.Child;
import com.example.parentapp.model.ChildrenManager;
import com.example.parentapp.model.FlipCoinGame;
import com.example.parentapp.model.FlipCoinGameHistory;
import com.example.parentapp.model.RotationManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * FlipCoinActivity class represents the screen that allows user to flip a coin.
 * This class personalizes the new coin-flip game to be associated with a particular child who chooses heads/tails,
 * and saves this new game into game history.
 * When there is no children in the system, this class will just flip the coin without asking user to pick heads/tails,
 * and without saving the new game into game history.
 * FlipCoinActivity saves and loads coin-flip game history to and from SharedPreferences.
 * It also saves the child who gets to pick into rotation manager, and saves that into SharedPreferences.
 */
public class FlipCoinActivity extends AppCompatActivity {
    private FlipCoinGameHistory gameHistory;
    private FlipCoinGame flipGame;
    private RotationManager rotationMan;
    private ArrayList<Child> gameQueue;
    private static final String APP_PREFERENCES = "app preferences";
    private static final String GAME_LIST = "game list";
    private static final String ROTATION_MANAGER = "rotation manager";
    private boolean gameAlreadySaved;
    private boolean dialogDecisionMade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);

        gameHistory = FlipCoinGameHistory.getInstance();
        flipGame = new FlipCoinGame();

        rotationMan = RotationManager.getInstance();
        loadLastPickerDataToGameRotationManagerFromSharedPrefs();
        gameQueue = rotationMan.getQueueAtIndex(0);

        gameAlreadySaved = false;
        dialogDecisionMade = false;

        setUpCoinFlipOnClick();
        setUpOnClickImageViewHistoryFlipCoin();
        setUpBackButton();

        if(!gameQueue.isEmpty())
        {
            setUpNewFlipCoinGame();
            displayDialogToAskForHeadTailChoice();
            registerCallBackListenerForChildrenQueue();
            setUpCancelButton();
        }
        else
        {
            dialogDecisionMade = true;
            startAnimationCardViewFlipResult();
        }
    }

    @Override
    protected void onPause() {
        if(dialogDecisionMade && !gameAlreadySaved) {
            performAutoSaveFlipGame();
            saveLastPickerDataFromGameRotationManagerToSharedPrefs();
            saveGameHistoryToSharedPreferences();
        }
        super.onPause();
    }

    private void saveLastPickerDataFromGameRotationManagerToSharedPrefs()
    {
        String rotationJson = rotationMan.convertQueuesToJson();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ROTATION_MANAGER, rotationJson);
        editor.apply();
    }

    private void loadLastPickerDataToGameRotationManagerFromSharedPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String rotation_manager_json = prefs.getString(ROTATION_MANAGER, null);
        rotationMan.convertQueuesFromJson(rotation_manager_json);
    }

    private void performAutoSaveFlipGame() {
        TextView tvResult = findViewById(R.id.textViewFlipResult);
        String result = tvResult.getText().toString();
        if(!result.equals("") && !result.equals(getString(R.string.three_dots)))
        {
            gameHistory.addNewFlipCoinGame(flipGame);
            Toast.makeText(FlipCoinActivity.this, getString(R.string.flip_coin_result_has_been_saved), Toast.LENGTH_SHORT).show();
            gameAlreadySaved = true;

            if(!flipGame.getPickerName().equals("Nobody")) {
                rotationMan.rotateQueueAtIndex(0);
            }
        }
    }

    private void displayDialogToAskForHeadTailChoice()
    {
        Dialog dialog = new Dialog(FlipCoinActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_heads_tails);

        //set up picker name
        TextView pickerTv = dialog.findViewById(R.id.dialogHeadsTails_textViewHiPicker);
        pickerTv.setText(getString(R.string.hi_its_someone_turn_to_pick, flipGame.getPickerName()));

        //set up buttons
        Button tailBtn = dialog.findViewById(R.id.dialogHeadsTails_buttonTail);
        tailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipGame.setPickerChoice(FlipCoinGame.FlipOptions.TAIL);
                dialogDecisionMade = true;
                dialog.dismiss();
                startAnimationCardViewFlipResult();
            }
        });

        Button headBtn = dialog.findViewById(R.id.dialogHeadsTails_buttonHead);
        headBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipGame.setPickerChoice(FlipCoinGame.FlipOptions.HEAD);
                dialogDecisionMade = true;
                dialog.dismiss();
                startAnimationCardViewFlipResult();
            }
        });

        Button selectAnotherKidBtn = dialog.findViewById(R.id.dialogHeadsTails_buttonSelectAnotherKid);
        selectAnotherKidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView cv_select = findViewById(R.id.cardView_selectAnotherKid_flipCoin);
                populateChildrenQueueInsideCardView();
                Animation drift = AnimationUtils.loadAnimation(FlipCoinActivity.this, R.anim.drift_from_bottom);
                dialog.dismiss();
                cv_select.setVisibility(View.VISIBLE);
                cv_select.startAnimation(drift);
            }
        });

        Button viewHistoryBtn = dialog.findViewById(R.id.dialogHeadsTails_buttonViewCoinFlipHistory);
        viewHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = GameHistoryActivity.makeLaunchIntent(FlipCoinActivity.this);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void populateChildrenQueueInsideCardView()
    {
        List<Child> alternateChildrenQ = rotationMan.getQueueWithNobodyAtTheEnd(0);
        ChildrenQueueAdapter adapter = new ChildrenQueueAdapter(alternateChildrenQ);

        ListView listView = findViewById(R.id.listView_selectAnotherChild_flipCoin);
        listView.setAdapter(adapter);
        listView.setDivider(null);
    }

    private class ChildrenQueueAdapter extends ArrayAdapter<Child> {
        public ChildrenQueueAdapter(List<Child> childrenQueue)
        {
            super(FlipCoinActivity.this, R.layout.child_view, childrenQueue);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View childView = convertView;
            if(childView == null)
            {
                childView = getLayoutInflater().inflate(R.layout.child_view, parent, false);
            }

            //fill up this view
            List<Child> alternateChildrenQ = rotationMan.getQueueWithNobodyAtTheEnd(0);
            Child currentChild = alternateChildrenQ.get(position);

            TextView textViewName = childView.findViewById(R.id.childView_textViewChildName);
            textViewName.setText(currentChild.getName());

            return childView;
        }
    }

    private void registerCallBackListenerForChildrenQueue()
    {
        ListView listView = findViewById(R.id.listView_selectAnotherChild_flipCoin);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int index, long id) {
                CardView cv_select = findViewById(R.id.cardView_selectAnotherKid_flipCoin);
                cv_select.setVisibility(View.INVISIBLE);

                List<Child> alternateChildrenQ = rotationMan.getQueueWithNobodyAtTheEnd(0);
                Child clickedChild = alternateChildrenQ.get(index);

                if (clickedChild.getName().equals("Nobody"))
                {
                    flipGame.setPickerName("Nobody");
                    dialogDecisionMade = true;
                    startAnimationCardViewFlipResult();
                }
                else
                {
                    rotationMan.moveKidAtThisIndexUpFront(0, index);
                    flipGame.setPickerName(gameQueue.get(0).getName());
                    displayDialogToAskForHeadTailChoice();
                }
            }
        });
    }

    private void setUpCancelButton()
    {
        Button btn = findViewById(R.id.buttonCancel_cardViewSelectAKid_flipCoin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView cv_select = findViewById(R.id.cardView_selectAnotherKid_flipCoin);
                cv_select.setVisibility(View.INVISIBLE);
                displayDialogToAskForHeadTailChoice();
            }
        });
    }

    public static Intent makeIntent(Context c)
    {
        return new Intent(c, FlipCoinActivity.class);
    }

    private void setUpNewFlipCoinGame()
    {
        Child current = gameQueue.get(0);
        flipGame.setPickerName(current.getName());
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

    private void setUpOnClickImageViewHistoryFlipCoin(){
        ImageView historyIV = findViewById(R.id.imageViewHistoryFlipCoin);
        historyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = GameHistoryActivity.makeLaunchIntent(FlipCoinActivity.this);
                startActivity(intent);
            }
        });
    }

    private void setUpBackButton()
    {
        ImageView back = findViewById(R.id.imageViewBackButton_flipCoin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}