package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.model.FlipCoinGame;
import com.example.parentapp.model.TaskManager;
import com.example.parentapp.model.WhoseTurn;

import java.util.ArrayList;

public class TaskHistoryActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_INDEX = "task index";
    private TaskManager taskManager;
    private int taskIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        taskManager = TaskManager.getInstance();

        Intent intentLeadToMe = getIntent();
        taskIndex = intentLeadToMe.getIntExtra(EXTRA_TASK_INDEX, -1);

        populateTaskHistoryListView();
        setupBackButton();
        setUpTitle();
        handleEmptyState();
    }

    private void setUpTitle() {
        TextView tvTitle = findViewById(R.id.textview_Title_taskHistory);
        tvTitle.setText(getString(R.string.historyOfSomeTask, taskManager.getTaskAtIndex(taskIndex).getName()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setupBackButton() {
        ImageView ivBackButton = findViewById(R.id.imageViewBackButton_taskHistory);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public static Intent makeIntent(Context context, int taskIndex){
        Intent intent = new Intent(context, TaskHistoryActivity.class);
        intent.putExtra(EXTRA_TASK_INDEX, taskIndex);
        return intent;
    }

    private void populateTaskHistoryListView()
    {
        ArrayAdapter<WhoseTurn> taskHistoryListAdapter = new TaskHistoryActivity.TaskHistoryListAdapter();
        ListView listview = findViewById(R.id.listview_taskHistory);
        listview.setAdapter(taskHistoryListAdapter);
        listview.setDivider(null);
    }

    private class TaskHistoryListAdapter extends ArrayAdapter<WhoseTurn>{
        public TaskHistoryListAdapter()
        {
            super(TaskHistoryActivity.this,
                    R.layout.whose_turn_view,
                    taskManager.getTaskAtIndex(taskIndex).getTaskHistory());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View taskView = convertView;
            if (taskView == null) {
                taskView = getLayoutInflater().inflate(R.layout.whose_turn_view, parent, false);
            }

            ArrayList<WhoseTurn> whoseTurnArrayList = taskManager.getTaskAtIndex(taskIndex).getTaskHistory();
            WhoseTurn currentTurn = whoseTurnArrayList.get(position);


            ImageView ivChildPhoto = taskView.findViewById(R.id.imageview_childPhotoWhoseTurn);
            if(currentTurn.getChild().getPortrait() == null){
                ivChildPhoto.setImageResource(R.drawable.ice_cream);
            }
            else {
                ivChildPhoto.setImageBitmap(currentTurn.getChild().getPortrait());
            }

            TextView tvChildName = taskView.findViewById(R.id.whoseTurnView_textview_childName);
            tvChildName.setText(currentTurn.getChild().getName());

            TextView tvDateTime = taskView.findViewById(R.id.whoseTurnView_textView_dateTime);
            tvDateTime.setText(currentTurn.getDate());

            return taskView;
        }
    }

    private void handleEmptyState()
    {
        ImageView ivBigIceCream = findViewById(R.id.taskHistory_imageview_iceCreamEmptyState);
        TextView tvNoHistory= findViewById(R.id.taskHistory_textview_NoHistoryRecorded);
        TextView tvInstruction = findViewById(R.id.taskHistory_textview_instructionsEmptyState);
        if(taskManager.getTaskAtIndex(taskIndex).isHistoryEmpty())
        {
            ivBigIceCream.setVisibility(View.VISIBLE);
            tvNoHistory.setVisibility(View.VISIBLE);
            tvInstruction.setVisibility(View.VISIBLE);

            Animation bounce = AnimationUtils.loadAnimation(TaskHistoryActivity.this, R.anim.bounce);
            ivBigIceCream.startAnimation(bounce);
        }
        else
        {
            ivBigIceCream.clearAnimation();
            ivBigIceCream.setVisibility(View.INVISIBLE);
            tvNoHistory.setVisibility(View.INVISIBLE);
            tvInstruction.setVisibility(View.INVISIBLE);
        }
    }
}