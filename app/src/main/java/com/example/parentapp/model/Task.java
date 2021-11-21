package com.example.parentapp.model;

/**
 * Task class represents a task with a name (string) attribute.
 */
public class Task {
    String name;

    public Task()
    {
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
}
