package com.example.parentapp.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ChildrenManager class contains the list of all children in the app.
 * ChildrenManager class can add a new child, remove a child, and allows editing and retrieving information of a child.
 * This class can also convert itself to json and from json format.
 */
public class ChildrenManager implements Iterable<Child>{
    private ArrayList<Child> childrenList;
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
        //make sure child primary ky is not duplicated
        //in this case, primary key is child name
        int temp = getIndexOfChildName(newChild.getName());
        if(temp > -1)
        {
            throw new IllegalArgumentException("This name has already been taken!");
        }
        childrenList.add(newChild);
        //System.out.println(childrenList.size());
    }

    public void testNameExistence(String newName)
    {
        int temp = getIndexOfChildName(newName);
        if(temp > -1)
        {
            throw new IllegalArgumentException("This name has already been taken!");
        }
    }

    public void replaceChild(int index, Child newChild)
    {
        int temp = getIndexOfChildName(newChild.getName());
        if(temp != index && temp != -1)
        {
            throw new IllegalArgumentException("This name has already been taken!");
        }

        childrenList.remove(index);
        childrenList.add(index, newChild);
    }

    public Child removeChild(int childIndex)
    {
        return childrenList.remove(childIndex);
    }

    public String getNameNextChildToPick(String lastPickChildName, int lastPickChildIndex)
    {
        int indexOfNextChild = -1;

        int lastChildFound = getIndexOfChildName(lastPickChildName);
        if(lastChildFound != -1)
        {
            indexOfNextChild = (lastChildFound + 1) % (childrenList.size());
        }
        else
        {
            if(childrenList.size() > 0)
            {
                if(lastPickChildIndex <= childrenList.size()) {
                    indexOfNextChild = lastPickChildIndex % (childrenList.size());
                }
                else
                {
                    indexOfNextChild = 0;
                }
            }
        }

        if(indexOfNextChild > -1)
        {
            return childrenList.get(indexOfNextChild).getName();
        }
        else
            return null;
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

    public int getNumberOfChildren()
    {
        return childrenList.size();
    }

    @NonNull
    @Override
    public Iterator<Child> iterator() {
        return childrenList.iterator();
    }

    public List<Child> getChildrenList()
    {
        return childrenList;
    }

    public String convertChildrenListToJson()
    {
        Gson gson = new Gson();
        return gson.toJson(childrenList);
    }

    public void convertChildrenListFromJson(String childrenListJson)
    {
        if(childrenListJson != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Child>>() {}.getType();
            childrenList = gson.fromJson(childrenListJson, type);
        }
    }
}
