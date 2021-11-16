package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.model.Child;
import com.example.parentapp.model.ChildrenManager;
import com.example.parentapp.model.RotationManager;
import com.example.parentapp.model.Task;
import com.example.parentapp.model.TaskManager;

public class TaskAddEditActivity extends AppCompatActivity {
    private static String TASK_INDEX = "task index";
    private static final String APP_PREFERENCES = "app preferences";
    private static final String ROTATION_MANAGER = "rotation manager";
    private static final String TASK_LIST = "task list";
    private ChildrenManager childrenMan;
    private TaskManager taskMan;
    private RotationManager rotationMan;
    private int indexOfTaskClicked;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add_edit);

        childrenMan = ChildrenManager.getInstance();

        taskMan = TaskManager.getInstance();
        rotationMan = RotationManager.getInstance();

        Intent intentLeadingToMe = getIntent();
        indexOfTaskClicked = intentLeadingToMe.getIntExtra(TASK_INDEX, -1);

        if(indexOfTaskClicked == -1)
        {
            task = new Task();
        }
        else
        {
            task = taskMan.getTaskAtIndex(indexOfTaskClicked);
            prefillTaskInfo();
        }

        setUpScreenTitle();
        setUpSaveButton();
        setUpDeleteButton();
        setUpMarkAsDoneButton();
    }

    @Override
    protected void onPause() {
        saveRotationManagerToSharedPreferences();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(infoChangedDetected()) {
            displayConfirmDialogOnUpButtonPressed();
        }
        else {
            finish();
        }
    }

    private void saveRotationManagerToSharedPreferences() {
        String rotationManagerJSON = rotationMan.convertQueuesToJson();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ROTATION_MANAGER, rotationManagerJSON);
        editor.apply();
    }

    private void loadRotationManagerFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String rotationManagerJson = prefs.getString(ROTATION_MANAGER, null);
        rotationMan.convertQueuesFromJson(rotationManagerJson);
    }


    public static Intent makeIntent(Context c, int index){
        Intent intent = new Intent(c, TaskAddEditActivity.class);
        intent.putExtra(TASK_INDEX, index);

        return intent;
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, TaskAddEditActivity.class);
    }

    private void setUpScreenTitle(){
        if(indexOfTaskClicked > -1) {
            TextView titleTV = findViewById(R.id.textViewTitle_addEditTask);
            titleTV.setText(R.string.your_task_details);
        }
    }

    private void setUpSaveButton()
    {
        Button btn = findViewById(R.id.buttonSave_cardViewTaskInfo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    validateAndSaveTaskInfo();
                    finish();
                }
                catch(Exception e) {
                    Toast.makeText(TaskAddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpDeleteButton() {
        Button btn = findViewById(R.id.buttonDelete_cardViewTaskInfo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(indexOfTaskClicked == -1 && !infoChangedDetected())
                {
                    finish();
                }
                else{
                    displayConfirmDialogOnDeletion();
                }

            }
        });
    }

    private void setUpMarkAsDoneButton() {

    }


    private void validateAndSaveTaskInfo() {
        EditText et = findViewById(R.id.editTextTaskName_taskAddEdit);
        String newTaskName = et.getText().toString();

        if (indexOfTaskClicked == -1) {
            task.setName(newTaskName);
            taskMan.addTask(task);
            rotationMan.addNewTaskQueue(childrenMan);
        }
        else {
            taskMan.testNameExistence(newTaskName);
            task.setName(newTaskName);
        }
    }

    private boolean infoChangedDetected()
    {
        boolean changeDetected = false;
        EditText et = findViewById(R.id.editTextTaskName_taskAddEdit);
        String newName = et.getText().toString();

        if(indexOfTaskClicked == -1)
        {
            if(!newName.equals(""))
            {
                changeDetected = true;
            }
        }
        else
        {
            String oldName = taskMan.getTaskAtIndex(indexOfTaskClicked).getName();
            if(!newName.equals(oldName))
            {
                changeDetected = true;
            }
        }
        return changeDetected;
    }

    private void displayConfirmDialogOnDeletion()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskAddEditActivity.this);
        builder.setTitle("Are you sure you want to delete this task?");

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(indexOfTaskClicked > -1)
                {
                    taskMan.removeTask(indexOfTaskClicked);
                    rotationMan.deleteTaskQueue(indexOfTaskClicked+1);
                }
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void prefillTaskInfo(){
        EditText et = findViewById(R.id.editTextTaskName_taskAddEdit);
        et.setText(task.getName());
        et.setSelection(task.getName().length());
    }

    private void displayConfirmDialogOnUpButtonPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskAddEditActivity.this);
        builder.setTitle(R.string.cancel_all_changes);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton(R.string.no, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}