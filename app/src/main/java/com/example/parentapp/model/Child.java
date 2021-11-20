package com.example.parentapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

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

    public Bitmap getPortrait() {
        if (portrait != null) {
             return decodeBase64(portrait);
        }
        return null;
    }

    public void setPortrait(Bitmap portrait) {
        if (portrait != null) {
            this.portrait = encodeBitmapToString(portrait);
        } else {
            this.portrait = null;
        }
    }

    // The two method below I adapted from:
    // https://stackoverflow.com/questions/18072448/how-to-save-image-in-shared-preference-in-android-shared-preference-issue-in-a
    private String encodeBitmapToString(Bitmap portrait) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        portrait.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedByte = Base64.decode(encodedImage, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
