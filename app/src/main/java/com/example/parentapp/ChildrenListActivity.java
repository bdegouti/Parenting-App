package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.model.Child;
import com.example.parentapp.model.ChildrenManager;

import java.util.List;

public class ChildrenListActivity extends AppCompatActivity {
    private ChildrenManager childrenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);


        childrenManager = ChildrenManager.getInstance();
        registerCallBackListenerForChildrenListView();

        setUpToolbar();
        setUpButtonAddNewChild();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateListView();
    }

    public static Intent makeIntent(Context c)
    {
        Intent intent = new Intent(c, ChildrenListActivity.class);
        return intent;
    }

    private void populateListView()
    {
        ArrayAdapter<Child> childrenListAdapter = new ChildrenListAdapter();
        ListView listview = findViewById(R.id.listVIewChildrenList);
        listview.setAdapter(childrenListAdapter);
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

    private void setUpToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbarChildrenList);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
}