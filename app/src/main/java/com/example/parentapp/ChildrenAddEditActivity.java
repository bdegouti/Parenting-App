package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
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

        //extract all extras
        Intent intentLeadingToMe = getIntent();
        indexOfChildClicked = intentLeadingToMe.getIntExtra(EXTRA_CHILD_INDEX, -1);

        if(indexOfChildClicked == -1)
        {
            child = new Child();
        }
        else
        {
            child = childrenManager.getChildAtIndex(indexOfChildClicked);
            prefillChildInfo();
        }
        setUpToolBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_child, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home)
        {
            if(infoChangedDetected()) {
                displayConfirmDialogOnUpButtonPressed();
            }
            else {
                finish();
            }
        }
        else if(itemId == R.id.action_delete)
        {
            if(indexOfChildClicked == -1 && !infoChangedDetected())
            {
                finish();
            }
            else {
                displayConfirmDialogOnDeletion();
            }
        }
        else if(itemId == R.id.action_save)
        {
            try{
                validateAndSaveChildInfo();
                finish();
            }
            catch(IllegalArgumentException e)
            {
                Toast.makeText(ChildrenAddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
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

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbarAddNewChild);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(indexOfChildClicked > -1)
        {
            toolbar.setTitle("Edit Child Information");
        }
    }

    public static Intent makeIntent(Context c)
    {
        return new Intent(c, ChildrenAddEditActivity.class);
    }

    public static Intent makeIntent(Context c, int childIndex)
    {
        Intent intent = new Intent(c, ChildrenAddEditActivity.class);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        return intent;
    }

    private void prefillChildInfo()
    {
        EditText etName = findViewById(R.id.editTextNewChildName);
        etName.setText(childrenManager.getChildAtIndex(indexOfChildClicked).getName());
    }

    private void validateAndSaveChildInfo()
    {
        EditText et = findViewById(R.id.editTextNewChildName);
        String newChildName = et.getText().toString();

        child.setName(newChildName);

        if(indexOfChildClicked == -1)
        {
            childrenManager.addChild(child);
        }
        else
        {
            childrenManager.replaceChild(indexOfChildClicked, child);
        }
    }

    private void displayConfirmDialogOnDeletion()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenAddEditActivity.this);
        builder.setTitle("Are you sure you want to delete this child?");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(indexOfChildClicked > -1)
                {
                    childrenManager.removeChild(indexOfChildClicked);
                }
                finish();
            }
        });

        builder.setNegativeButton("CANCEL", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayConfirmDialogOnUpButtonPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenAddEditActivity.this);
        builder.setTitle("Cancel all changes?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean infoChangedDetected()
    {
        boolean changeDetected = false;
        EditText et = findViewById(R.id.editTextNewChildName);
        String newName = et.getText().toString();

        if(indexOfChildClicked == -1)
        {
            if(!newName.equals(""))
            {
                changeDetected = true;
            }
        }
        else
        {
            String oldName = childrenManager.getChildAtIndex(indexOfChildClicked).getName();
            if(!newName.equals(oldName))
            {
                changeDetected = true;
            }
        }

        return changeDetected;
    }

}