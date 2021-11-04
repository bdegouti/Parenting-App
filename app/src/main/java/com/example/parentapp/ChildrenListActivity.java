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
import com.example.parentapp.model.ChildrenManager;

public class ChildrenListActivity extends AppCompatActivity {
    private ChildrenManager childrenManager;
    private static final String APP_PREFERENCES = "app preferences";
    private static final String CHILDREN_LIST = "children list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        childrenManager = ChildrenManager.getInstance();

        populateListView();
        registerCallBackListenerForChildrenListView();

        setUpButtonAddNewChild();

        handleEmptyState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateListView();
        handleEmptyState();
    }

    @Override
    protected void onStop() {
        saveChildrenListToSharedPreferences();
        super.onStop();
    }

    private void saveChildrenListToSharedPreferences()
    {
        String listJson = this.childrenManager.convertChildrenListToJson();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CHILDREN_LIST, listJson);
        editor.apply();
    }

    public static String getChildrenListFromSharedPreferences(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return prefs.getString(CHILDREN_LIST, null);
    }

    private void handleEmptyState()
    {
        ImageView ivBigIceCream = findViewById(R.id.imageViewEmptyStateBigIceCream);
        TextView tvNoChild = findViewById(R.id.textViewNoChildrenToShow);
        TextView tvInstruction = findViewById(R.id.textViewAddChildInstruction);
        if(childrenManager.getNumberOfChildren() == 0)
        {
            ivBigIceCream.setVisibility(View.VISIBLE);
            tvNoChild.setVisibility(View.VISIBLE);
            tvInstruction.setVisibility(View.VISIBLE);

            Animation bounce = AnimationUtils.loadAnimation(ChildrenListActivity.this, R.anim.bounce);
            ivBigIceCream.startAnimation(bounce);
        }
        else
        {
            ivBigIceCream.clearAnimation();
            ivBigIceCream.setVisibility(View.INVISIBLE);
            tvNoChild.setVisibility(View.INVISIBLE);
            tvInstruction.setVisibility(View.INVISIBLE);
        }
    }

    public static Intent makeIntent(Context c)
    {
        return new Intent(c, ChildrenListActivity.class);
    }

    private void populateListView()
    {
        ArrayAdapter<Child> childrenListAdapter = new ChildrenListAdapter();
        ListView listview = findViewById(R.id.listVIewChildrenList);
        listview.setAdapter(childrenListAdapter);
        listview.setDivider(null);
    }

    private class ChildrenListAdapter extends ArrayAdapter<Child>{
        public ChildrenListAdapter()
        {
            super(ChildrenListActivity.this, R.layout.child_view, childrenManager.getChildrenList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View childView = convertView;
            if(childView == null)
            {
                childView = getLayoutInflater().inflate(R.layout.child_view, parent, false);
            }

            //fill up this view
            Child currentChild = childrenManager.getChildAtIndex(position);
            TextView textViewName = childView.findViewById(R.id.childView_textViewChildName);
            textViewName.setText(currentChild.getName());

            //switch color
            if(position % 2 != 0)
            {
                ImageView backgroundRect = childView.findViewById(R.id.childView_imageViewBackgroundRectangle);
                backgroundRect.setImageResource(R.drawable.rectangle_white);
            }

            return childView;
        }
    }

    private void registerCallBackListenerForChildrenListView()
    {
        ListView childrenListView = findViewById(R.id.listVIewChildrenList);
        childrenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int index, long id) {
                Intent intent = ChildrenAddEditActivity.makeIntent(ChildrenListActivity.this, index);
                startActivity(intent);
            }
        });
    }

    private void setUpButtonAddNewChild() {
        Button btn = findViewById(R.id.buttonAddChild);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChildrenAddEditActivity.makeIntent(ChildrenListActivity.this);
                startActivity(intent);
            }
        });
    }
}