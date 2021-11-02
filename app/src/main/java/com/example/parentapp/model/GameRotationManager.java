package com.example.parentapp.model;

public class GameRotationManager {
    private int indexOfChildLastPicked;
    private String nameOfChildLastPicked;

    public GameRotationManager()
    {
        indexOfChildLastPicked = -1;
        nameOfChildLastPicked = null;
    }

    public String getNameNextChildToPickHeadTail(ChildrenManager childrenManager)
    {
        //this function is only called when the children list has at least 1 child
        if(nameOfChildLastPicked == null) //no history saved
        {
            return childrenManager.getChildAtIndex(0).getName();
        }
        return childrenManager.getNameNextChildToPick(nameOfChildLastPicked, indexOfChildLastPicked);
    }

    public void setIndexOfChildLastPicked(int i)
    {
        indexOfChildLastPicked = i;
    }

    public void setNameOfChildLastPicked(String name)
    {
        nameOfChildLastPicked = name;
    }

}
