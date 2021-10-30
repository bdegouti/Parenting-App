package com.example.parentapp.model;

import java.util.ArrayList;
import java.util.List;

public class ChildrenManager {
    private final List<Child> childrenList;
    private static ChildrenManager instance;

    //singleton implementation
    private ChildrenManager()
    {
        childrenList = new ArrayList<>();
    }

    public static ChildrenManager getInstance()
    {
        if(instance == null)
        {
            instance = new ChildrenManager();
        }
        return instance;
    }
    //end of singleton implementation

    public Child getChildAtIndex(int index)
    {
        return childrenList.get(index);
    }

    public void addChild(Child newChild)
    {
        childrenList.add(newChild);
    }

    public void addChild(String name)
    {
        childrenList.add(new Child(name));
    }

    public void removeChild(int childIndex)
    {
        childrenList.remove(childIndex);
    }

    public void editChildName(int childIndex, String newName)
    {
        Child child = childrenList.get(childIndex);
        child.setName(newName);
    }

    public String getNameNextChildToPick(String lastPickChildName, int lastPickChildIndex)
    {
        int indexLastChild = getIndexOfChildName(lastPickChildName);

        int indexOfNextChild;
        if(indexLastChild != -1) //if last child is still on the children list
        {
            indexOfNextChild = (lastPickChildIndex + 1) % (childrenList.size());
        }
        else
        {
            indexOfNextChild = lastPickChildIndex % (childrenList.size());
        }

        return childrenList.get(indexOfNextChild).getName();
    }

    public int getIndexOfChildName(String name)
    {
        int result = -1;
        for(int i=0; i < childrenList.size(); i++)
        {
            if(childrenList.get(i).getName().equals(name))
            {
                result = i;
                break;
            }
        }
        return result;
    }
}
