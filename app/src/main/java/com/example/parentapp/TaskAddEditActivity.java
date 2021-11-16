package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TaskAddEditActivity extends AppCompatActivity {
    private static String TASK_INDEX = "task index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add_edit);
    }

    public static Intent makeIntent(Context c, int index){
        Intent intent = new Intent(c, TaskAddEditActivity.class);
        intent.putExtra(TASK_INDEX, index);

        return intent;
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, TaskAddEditActivity.class);
    }




}