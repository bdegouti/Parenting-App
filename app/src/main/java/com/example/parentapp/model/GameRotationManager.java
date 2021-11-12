package com.example.parentapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * GameRotationManager class is a auxiliary class that stores the information of the last child who gets to pick heads/tails.
 * This class stores the name and index of that child, and it communicates with ChildrenManager class to retrieve the
 * information of the next child that gets to pick heads/tails.
 */
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

    public List<Child> getQueue(ChildrenManager childrenManager, int onHoldChildIndex)
    {
        List<Child> queue = new ArrayList<>();

        int size = childrenManager.getNumberOfChildren();
        int startIndex = (onHoldChildIndex + 1) % size;

        for(int i=0; i <= size-2; i++)
        {
            int currentIndex = (startIndex + i) % size;
            Child child = childrenManager.getChildAtIndex(currentIndex);
            queue.add(child);
        }

        Child nobody = new Child();
        queue.add(nobody);

        return queue;
    }

}
