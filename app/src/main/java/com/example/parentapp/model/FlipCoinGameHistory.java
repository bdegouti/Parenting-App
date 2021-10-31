package com.example.parentapp.model;

import java.util.ArrayList;
import java.util.List;

public class FlipCoinGameHistory {
    private final List<FlipCoinGame> gameList;
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

    public void addNewFlipCoinGame(FlipCoinGame game)
    {
        //always add the new game at the front of the array list
        //so when we print out list view it lists from latest game to oldest
        gameList.add(0, game);
    }

    public String getNameOfChildLastPicked()
    {
        if(gameList.size() > 0) {
            return gameList.get(0).getPickerName();
        }
        return null;
    }

    public int getIndexOfChildLastPicked()
    {
        if(gameList.size() > 0) {
            return gameList.get(0).getPickerIndex();
        }
        return -1;
    }

}
