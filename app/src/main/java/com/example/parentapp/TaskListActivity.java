package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.model.Child;
import com.example.parentapp.model.RotationManager;
import com.example.parentapp.model.Task;
import com.example.parentapp.model.TaskManager;

import java.util.ArrayList;

/**
 * TaskListActivity class represents a screen that displays a list of all tasks in the app.
 * This class allows user to click on to an existing task to edit that task's information,
 * it also provides a button for user to add a new task.
 * This class saves and loads the list of all tasks to and from SharedPreferences.
 */
public class TaskListActivity extends AppCompatActivity {
    private TaskManager taskManager;
    private RotationManager rotationManager;
    private static final String APP_PREFERENCES = "app preferences";
    private static final String TASK_LIST = "task list";
    private static final String ROTATION_MANAGER = "rotation manager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        taskManager = TaskManager.getInstance();
        loadTaskListFromSharedPreferences();

        rotationManager = RotationManager.getInstance();
        loadRotationManagerFromSharedPreferences();

        populateListView();
        registerCallBackListenerForTaskListView();
        setupButtonAddNewTask();
        setUpBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateListView();
        handleEmptyState();
        startAnimOnAddChildButton();
    }

    @Override
    protected void onPause() {
        saveTaskListToSharedPreferences();
        saveRotationManagerToSharedPreferences();
        super.onPause();
    }

    private void saveRotationManagerToSharedPreferences() {
        String rotationManagerJSON = rotationManager.convertQueuesToJson();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ROTATION_MANAGER, rotationManagerJSON);
        editor.apply();
    }

    private void loadRotationManagerFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String rotationManagerJson = prefs.getString(ROTATION_MANAGER, null);
        rotationManager.convertQueuesFromJson(rotationManagerJson);
    }

    private void loadTaskListFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String taskListJson = prefs.getString(TASK_LIST, null);
        taskManager.convertTaskListFromJSON(taskListJson);
    }

    private void saveTaskListToSharedPreferences() {
        String taskListJSON = taskManager.convertTaskListToJSON();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TASK_LIST, taskListJSON);
        editor.apply();
    }

    private void populateListView()
    {
        ArrayAdapter<Task> taskListAdapter = new TaskListActivity.TaskListAdapter();
        ListView listview = findViewById(R.id.listView_taskList);
        listview.setAdapter(taskListAdapter);
        listview.setDivider(null);
    }

    private void handleEmptyState()
    {
        ImageView ivBigIceCream = findViewById(R.id.imageViewEmptyStateBigIceCream_taskList);
        TextView tvNoTask = findViewById(R.id.textViewNoTasksToShow);
        TextView tvInstruction = findViewById(R.id.textViewAddTaskInstruction);
        if(taskManager.getSizeOfTaskList() == 0)
        {
            ivBigIceCream.setVisibility(View.VISIBLE);
            tvNoTask.setVisibility(View.VISIBLE);
            tvInstruction.setVisibility(View.VISIBLE);

            Animation bounce = AnimationUtils.loadAnimation(TaskListActivity.this, R.anim.bounce);
            ivBigIceCream.startAnimation(bounce);
        }
        else
        {
            ivBigIceCream.clearAnimation();
            ivBigIceCream.setVisibility(View.INVISIBLE);
            tvNoTask.setVisibility(View.INVISIBLE);
            tvInstruction.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskListAdapter extends ArrayAdapter<Task>{
        public TaskListAdapter()
        {
            super(TaskListActivity.this, R.layout.task_view, taskManager.getTaskList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View taskView = convertView;
            if(taskView == null)
            {
                taskView = getLayoutInflater().inflate(R.layout.task_view, parent, false);
            }

            //fill up this view
            Task currentTask = taskManager.getTaskAtIndex(position);

            //display task name
            TextView textViewName = taskView.findViewById(R.id.taskView_tvTaskName);
            textViewName.setText(currentTask.getName());

            //display child name if children exist
            TextView textChildName = taskView.findViewById(R.id.taskView_tvChildName);
            ArrayList<Child> queueForThisTask = rotationManager.getQueueAtIndex(position+1);
            if(queueForThisTask.size() > 0) {
                Child topChild = queueForThisTask.get(0);
                textChildName.setText(getString(R.string.turn_of, topChild.getName()));
            }
            else{
                textChildName.setText(R.string.no_kids_assigned);
            }

            return taskView;
        }
    }

    private void registerCallBackListenerForTaskListView() {
        ListView taskListView = findViewById(R.id.listView_taskList);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int index, long id) {
                Intent intent = TaskAddEditActivity.makeIntent(TaskListActivity.this, index);
                startActivity(intent);
            }
        });
    }

    private void setupButtonAddNewTask() {
        Button btnAddNewTask = findViewById(R.id.buttonAddTask_taskList);
        btnAddNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TaskAddEditActivity.makeIntent(TaskListActivity.this);
                startActivity(intent);
            }
        });
    }

    private void startAnimOnAddChildButton(){
        Button button = findViewById(R.id.buttonAddTask_taskList);
        Animation drift = AnimationUtils.loadAnimation(TaskListActivity.this, R.anim.drift_from_bottom);
        button.setVisibility(View.VISIBLE);
        button.startAnimation(drift);
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, TaskListActivity.class);
    }

    private void setUpBackButton()
    {
        ImageView back = findViewById(R.id.imageViewBackButton_tasksList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}