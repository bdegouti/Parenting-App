package com.example.parentapp.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RotationManager {
    private ArrayList<ArrayList<Child>> childrenQueues;
    private static RotationManager instance;
    //singleton implementation
    private RotationManager() {
        childrenQueues = new ArrayList<>();
        ArrayList<Child> gameQueue = new ArrayList<>();
        childrenQueues.add(gameQueue);
    }

    public static RotationManager getInstance() {
        if(instance == null)
        {
            instance = new RotationManager();
        }
        return instance;
    }
    ////////////////////////////

    public ArrayList<Child> getQueueAtIndex(int index) {
        return childrenQueues.get(index);
    }

    public void addChildToAllQueues(Child newKid) {
        for(ArrayList<Child> queue : childrenQueues)
        {
            if(queue.isEmpty()) {
                queue.add(newKid);
            }
            else {
                int lastIndex = queue.size() - 1;
                queue.add(lastIndex, newKid);
            }
        }
    }

    public void removeChildFromQueues(Child removedKid) {
        for(ArrayList<Child> queue : childrenQueues)
        {
            queue.remove(removedKid);
        }
    }

    public void rotateQueueAtIndex(int index)
    {
        ArrayList<Child> targetQ = childrenQueues.get(index);
        Child current = targetQ.remove(0);
        targetQ.add(current);
    }

    public boolean isQueueEmpty(int index)
    {
        ArrayList<Child> targetQ = childrenQueues.get(index);
        return targetQ.isEmpty();
    }

    public ArrayList<Child> getQueueWithNobodyAtTheEnd(int queueIndex)
    {
        ArrayList<Child> targetQ = childrenQueues.get(queueIndex);
        ArrayList<Child> resultQ = new ArrayList<>();
        for(int i=1; i < targetQ.size(); i++)
        {
            resultQ.add(targetQ.get(i));
        }
        resultQ.add(new Child()); //add the "nobody" child

        return resultQ;
    }

    public void moveKidAtThisIndexUpFront(int queueIndex, int childIndex)
    {
        ArrayList<Child> targetQ = childrenQueues.get(queueIndex);
        Child targetChild = targetQ.remove(childIndex);
        targetQ.add(0, targetChild);
    }

    public String convertQueuesToJson()
    {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ArrayList<Child>>>(){}.getType();
        return gson.toJson(childrenQueues, type);
    }

    public void convertQueuesFromJson(String queuesJson)
    {
        if(queuesJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayList<Child>>>() {}.getType();
            childrenQueues = gson.fromJson(queuesJson, type);
        }
    }
}
