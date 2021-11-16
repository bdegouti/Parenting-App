package com.example.parentapp.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Task> taskList;
    private static TaskManager instance;

    private TaskManager()
    {
        taskList = new ArrayList<>();
    }

    public static TaskManager getInstance()
    {
        if(instance == null)
        {
            instance = new TaskManager();
        }
        return instance;
    }

    public void addTask(Task t){
        testNameExistence(t.getName());
        taskList.add(t);
    }

    public void removeTask(int index){
        taskList.remove(index);
    }

    public Task getTaskAtIndex(int index){
        return taskList.get(index);
    }

    public void testNameExistence(String name){
        if(getIndexAtName(name) > -1){
            throw new IllegalArgumentException("This task name has already been taken");
        }
    }

    public int getIndexAtName(String name){
        int index = -1;
        for(int i = 0; i < taskList.size(); i++){
            if(taskList.get(i).getName().equals(name)){
                index = i;
                break;
            }
        }
        return index;
    }

    public int getSizeOfTaskList(){
        return taskList.size();
    }

    public ArrayList<Task> getTaskList(){
        return taskList;
    }

    public String convertTaskListToJSON(){
        Gson gson = new Gson();
        return gson.toJson(taskList);
    }

    public void convertTaskListFromJSON(String jsonString){
        if(jsonString != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Task>>() {}.getType();
            taskList = gson.fromJson(jsonString, type);
        }
    }
}
