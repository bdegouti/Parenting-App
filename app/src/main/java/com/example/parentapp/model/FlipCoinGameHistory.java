package com.example.parentapp.model;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * FlipCoinGameHistory class consists of a list of all coin-flips of the children.
 * This class supports adding a new coin-flip game into the list, retrieving a game at a given index,
 * and clearing all games in its list.
 * This class can also convert itself to Json and from Json format.
 */
public class FlipCoinGameHistory {
    private ArrayList<FlipCoinGame> gameList;
    //singleton implementation
    private static FlipCoinGameHistory instance;
    private FlipCoinGameHistory()
    {
        gameList = new ArrayList<>();
    }

    public static FlipCoinGameHistory getInstance()
    {
        if(instance == null)
        {
            instance = new FlipCoinGameHistory();
        }
        return instance;
    }
    //end of singleton implementation

    public FlipCoinGame getGameAtIndex(int index)
    {
        return this.gameList.get(index);
    }

    public List<FlipCoinGame> getGameHistoryList() {
        return gameList;
    }

    public void addNewFlipCoinGame(FlipCoinGame game)
    {
        //always add the new game at the front of the array list
        //so when we print out list view it lists from latest game to oldest
        gameList.add(0, game);
    }

    public void clearHistory(){
        gameList.clear();
    }

    public int getNumberOfGames()
    {
        return this.gameList.size();
    }

    public String convertHistoryToJson()
    {
        Type type = new TypeToken<ArrayList<FlipCoinGame>>(){}.getType();
        Gson gson = new Gson();
        return gson.toJson(this.gameList, type);
    }

    public void convertSaveHistoryFromJson(String listAsJson)
    {
        if(listAsJson != null && !listAsJson.equals(""))
        {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FlipCoinGame>>(){}.getType();
            this.gameList = gson.fromJson(listAsJson, type);
        }
    }

    public void updatePickerName(String oldName, String newName) {
        for(FlipCoinGame flipGame: gameList) {
            if(flipGame.getPickerName().equals(oldName)) {
                flipGame.setPickerName(newName);
            }
        }
    }

    public void updatePickerPhoto(String name, Bitmap photo) {
        for(FlipCoinGame flipGame: gameList){
            if(flipGame.getPickerName().equals(name)) {
                flipGame.setPickerPhoto(photo);
            }
        }
    }
}
