package com.example.parentapp.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Task class represents a task with a name (string) attribute.
 */
public class Task {
    String name;
    ArrayList<WhoseTurn> taskHistory;

    public Task()
    {
        taskHistory = new ArrayList<>();
        this.name = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.equals("")){
            throw new IllegalArgumentException("Task name cannot be empty.");
        }
        this.name = name;
    }

    public void addTurn(Child child){
        WhoseTurn wt = new WhoseTurn(child);
        taskHistory.add(0, wt);
    }

    public ArrayList<WhoseTurn> getTaskHistory(){
        return taskHistory;
    }

    public void updateChildName(String oldName, String newName){
        for(WhoseTurn wt : taskHistory){
            if(wt.getChild().getName().equals(oldName)){
                wt.getChild().setName(newName);
            }
        }
    }

    public void updateChildPortrait(String name, Bitmap newPortrait){
        for(WhoseTurn wt : taskHistory){
            if(wt.getChild().getName().equals(name)){
                wt.getChild().setPortrait(newPortrait);
            }
        }
    }

    public boolean isHistoryEmpty(){
        return taskHistory.isEmpty();
    }
}
