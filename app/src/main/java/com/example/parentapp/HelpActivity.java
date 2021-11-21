package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * HelpActivity represents a screen that provides user with information about the Parent app.
 * This screen introduces the functionalities that the app provides to users,
 * it also displays information and links about the developers, and resources used in the app.
 */
public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setUpHyperLink();
        startAnimationSideBar();
        startAnimationScrollView();
        setUpBackButton();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, HelpActivity.class);
    }

    private void setUpHyperLink()
    {
        TextView resourcesTv = findViewById(R.id.textViewResourceLinks);
        resourcesTv.setMovementMethod(LinkMovementMethod.getInstance());

        TextView authorsTv = findViewById(R.id.textViewAuthorList_help);
        authorsTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void startAnimationSideBar()
    {
        ImageView sideBar = findViewById(R.id.imageViewsSidebar_help);
        Animation slide = AnimationUtils.loadAnimation(HelpActivity.this, R.anim.slide_from_left);
        sideBar.setVisibility(View.VISIBLE);
        sideBar.startAnimation(slide);
    }

    private void startAnimationScrollView()
    {
        ScrollView sv = findViewById(R.id.scrollView_help);
        Animation drift = AnimationUtils.loadAnimation(HelpActivity.this, R.anim.drift_from_bottom);
        sv.setVisibility(View.VISIBLE);
        sv.startAnimation(drift);
    }

    private void setUpBackButton()
    {
        ImageView back = findViewById(R.id.imageViewBackButton_help);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}