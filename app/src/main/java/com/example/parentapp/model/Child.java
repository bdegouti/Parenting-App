package com.example.parentapp.model;

public class Child {
    private String name;

    public Child()
    {
        name = "Fill your name please!";
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        if(name == null || name.equals(""))
        {
            throw new IllegalArgumentException("Child name cannot be empty!");
        }
        this.name = name;
    }
}
