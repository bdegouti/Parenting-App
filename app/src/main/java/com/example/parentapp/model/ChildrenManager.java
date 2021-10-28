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
}
