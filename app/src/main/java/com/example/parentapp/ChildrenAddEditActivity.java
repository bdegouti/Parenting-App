package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parentapp.model.Child;
import com.example.parentapp.model.ChildrenManager;

public class ChildrenAddEditActivity extends AppCompatActivity {
    private static final String EXTRA_CHILD_INDEX = "child index";
    private int indexOfChildClicked;
    private ChildrenManager childrenManager;
    private Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_add_edit);

        childrenManager = ChildrenManager.getInstance();
        child = new Child();

        //extract all extras
        Intent intentLeadingToMe = getIntent();
        indexOfChildClicked = intentLeadingToMe.getIntExtra(EXTRA_CHILD_INDEX, -1);

        setUpToolBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flip_coin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home)
        {
            //make dialog
            finish();
        }
        else if(itemId == R.id.action_save)
        {
            try{
                validateChildInfo();
                finish();
            }
            catch(IllegalArgumentException e)
            {
                Toast.makeText(ChildrenAddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbarAddNewChild);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static Intent makeIntent(Context c)
    {
        Intent intent = new Intent(c, ChildrenAddEditActivity.class);
        return intent;
    }

    public static Intent makeIntent(Context c, int childIndex)
    {
        Intent intent = new Intent(c, ChildrenAddEditActivity.class);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        return intent;
    }

    private void validateChildInfo()
    {
        EditText et = findViewById(R.id.editTextNewChildName);
        String newChildName = et.getText().toString();

        child.setName(newChildName);
        childrenManager.addChild(child);
    }
}