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
        gameList.add(game);
    }

    public void removeGameAtIndex(int index)
    {
        gameList.remove(index);
    }

}
