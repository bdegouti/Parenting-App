package com.example.parentapp.model;

public class Child {
    private String name;

    public Child()
    {
        name = "Fill your name please!";
    }

    public Child(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
