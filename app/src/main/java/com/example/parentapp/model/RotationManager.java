package com.example.parentapp.model;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * RotationManager class is a utility class that handles the rotation of the children for the flip coin game and all the tasks.
 * This class contains a list of all the queues of children for each activity (game/task/etc.) that needs rotation.
 * This class supports adding/removing/editing children information like their names and photos.
 * This class rotates a queue when the child has had his/her turn for a particular task.
 * It also can add/remove queues on the adding/removing of tasks.
 * This class converts its list of children queues from json and to json format.
 */
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
        String name = removedKid.getName();
        for(int i=0; i < childrenQueues.size(); i++)
        {
            int targetChildIndex = getIndexOfChildName(i, name);
            if(targetChildIndex > -1)
            {
                childrenQueues.get(i).remove(targetChildIndex);
            }
        }
    }

    public void renameChildInAllQueues(String oldName, String newName)
    {
        for(int i=0; i < childrenQueues.size(); i++)
        {
            int targetChildIndex = getIndexOfChildName(i, oldName);
            if(targetChildIndex > -1)
            {
                childrenQueues.get(i).get(targetChildIndex).setName(newName);
            }
        }
    }

    public int getIndexOfChildName(int queueIndex, String name)
    {
        ArrayList<Child> queue = childrenQueues.get(queueIndex);
        int result = -1;
        for(int i=0; i < queue.size(); i++) {
            if(queue.get(i).getName().equals(name)) {
                result = i;
                break;
            }
        }
        return result;
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
        for(int i=0; i < targetQ.size(); i++)
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

    public void addNewTaskQueue(ChildrenManager childrenManager)
    {
        ArrayList<Child> newQ = new ArrayList<>();
        for(Child child : childrenManager)
        {
            newQ.add(child);
        }
        childrenQueues.add(newQ);
    }

    public void deleteTaskQueue(int queueIndex)
    {
        childrenQueues.remove(queueIndex);
    }

    public void updateChildPhotoOnAllQueues(String childName, Bitmap newBitmapPhoto) {
        for(int i=0; i < childrenQueues.size(); i++) {
            int childIndex = getIndexOfChildName(i, childName);
            if(childIndex > -1) {
                childrenQueues.get(i).get(childIndex).setPortrait(newBitmapPhoto);
            }
        }
    }
}
