package com.example.parentapp.model;

public class GameRotationManager {
    private ChildrenManager childrenManager;
    private FlipCoinGameHistory gameHistory;

    public GameRotationManager(ChildrenManager child_manager, FlipCoinGameHistory game_history)
    {
        this.childrenManager = child_manager;
        this.gameHistory = game_history;
    }

    public String getNameNextChildToPickHeadTail()
    {
        String lastChildName = gameHistory.getNameOfChildLastPicked();
        int lastChildIndex = gameHistory.getIndexOfChildLastPicked();
        return childrenManager.getNameNextChildToPick(lastChildName, lastChildIndex);
    }

    public int getIndexNextChildToPickHeadTail()
    {
        int result;
        String nextChildName = getNameNextChildToPickHeadTail();
        result = childrenManager.getIndexOfChildName(nextChildName);
        return result;
    }

}
