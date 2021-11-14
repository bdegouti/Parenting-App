package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setUpHyperLink();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, HelpActivity.class);
    }

    private void setUpHyperLink()
    {
        TextView resourcesTv = findViewById(R.id.textViewResourceLinks);
        resourcesTv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}