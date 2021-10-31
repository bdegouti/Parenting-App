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
        //this function is only called when the children list has at least 1 child
        String lastChildName = gameHistory.getNameOfChildLastPicked();
        if(lastChildName == null) //no history saved
        {
            return childrenManager.getChildAtIndex(0).getName();
        }
        int lastChildIndex = gameHistory.getIndexOfChildLastPicked();
        return childrenManager.getNameNextChildToPick(lastChildName, lastChildIndex);
    }

    public int getIndexNextChildToPickHeadTail()
    {
        //this function is only called when the children list has at least 1 child
        int result;
        String nextChildName = getNameNextChildToPickHeadTail();
        result = childrenManager.getIndexOfChildName(nextChildName);
        return result;
    }

}
