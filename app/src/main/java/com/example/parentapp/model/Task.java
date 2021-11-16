package com.example.parentapp.model;

public class Task {
    String name;
    public Task(String name){
        this.name = name;
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
}
