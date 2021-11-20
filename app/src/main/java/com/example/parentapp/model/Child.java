package com.example.parentapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Child class represents a child entity with a name attribute in the app
 */
public class Child {
    private String name;
    private String portrait;

    public Child()
    {
        name = "Nobody";
        portrait = null;
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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
