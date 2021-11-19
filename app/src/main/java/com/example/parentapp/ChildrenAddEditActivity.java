package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.model.Child;
import com.example.parentapp.model.ChildrenManager;
import com.example.parentapp.model.RotationManager;

/**
 * ChildrenAddEditActivity represents the screen that support adding a new child, or editing information of an existing child.
 * This class saves the information of the new child (or changed information of an existing child) into ChildrenManager class,
 * which will be used throughout the app, including the FlipCoinActivity.
 */
public class ChildrenAddEditActivity extends AppCompatActivity {
    private static final String APP_PREFERENCES = "app preferences";
    private static final String ROTATION_MANAGER = "rotation manager";
    private static final String EXTRA_CHILD_INDEX = "child index";
    private int indexOfChildClicked;
    private ChildrenManager childrenManager;
    private RotationManager rotationMan;
    private Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_add_edit);

        childrenManager = ChildrenManager.getInstance();

        rotationMan = RotationManager.getInstance();
        loadRotationManagerFromSharedPreferences();

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

        setUpScreenTitle();
        setUpSaveButton();
        setUpDeleteButton();
        startAnimationInputCardViewAndSideBar();
        setUpBackButton();
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
        etName.setSelection(etName.getText().length());
    }

    private void validateAndSaveChildInfo()
    {
        EditText et = findViewById(R.id.editTextNewChildName);
        String newChildName = et.getText().toString();

        if(indexOfChildClicked == -1)
        {
            child.setName(newChildName);
            childrenManager.addChild(child);
            rotationMan.addChildToAllQueues(child);
        }
        else
        {
            childrenManager.testNameExistence(newChildName, indexOfChildClicked);
            String oldName = child.getName();
            child.setName(newChildName);
            rotationMan.renameChildInAllQueues(oldName, child.getName());
        }
    }

    private void displayConfirmDialogOnDeletion()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenAddEditActivity.this);
        builder.setTitle(R.string.are_you_sure_you_want_to_delete_this_child);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(indexOfChildClicked > -1)
                {
                    Child removedChild = childrenManager.removeChild(indexOfChildClicked);
                    rotationMan.removeChildFromQueues(removedChild);
                }
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayConfirmDialogOnUpButtonPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenAddEditActivity.this);
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

    private void setUpScreenTitle()
    {
        if(indexOfChildClicked > -1)
        {
            TextView title = findViewById(R.id.textViewTitleAddEditChild);
            title.setText(R.string.editing_your_child);
        }
    }

    private void setUpSaveButton()
    {
        Button btn = findViewById(R.id.buttonSaveAddEditChild);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    validateAndSaveChildInfo();
                    finish();
                }
                catch(IllegalArgumentException e)
                {
                    Toast.makeText(ChildrenAddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpDeleteButton()
    {
        Button btn = findViewById(R.id.buttonDeleteAddEditChild);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(indexOfChildClicked == -1 && !infoChangedDetected())
                {
                    finish();
                }
                else {
                    displayConfirmDialogOnDeletion();
                }
            }
        });
    }

    private void startAnimationInputCardViewAndSideBar()
    {
        ImageView sideBar = findViewById(R.id.imageViewRightSideBar);
        Animation slide = AnimationUtils.loadAnimation(ChildrenAddEditActivity.this, R.anim.slide_from_left);
        sideBar.setVisibility(View.VISIBLE);
        sideBar.startAnimation(slide);

        CardView cv = findViewById(R.id.cardView_addEditChild);
        Animation drift = AnimationUtils.loadAnimation(ChildrenAddEditActivity.this, R.anim.drift_from_bottom);
        cv.setVisibility(View.VISIBLE);
        cv.startAnimation(drift);
    }

    private void saveRotationManagerToSharedPreferences()
    {
        String rotationJson = rotationMan.convertQueuesToJson();

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ROTATION_MANAGER, rotationJson);
        editor.apply();
    }

    private void loadRotationManagerFromSharedPreferences()
    {
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String rotation_manager_json = prefs.getString(ROTATION_MANAGER, null);
        rotationMan.convertQueuesFromJson(rotation_manager_json);
    }

    private void setUpBackButton()
    {
        ImageView back = findViewById(R.id.imageViewBackButton_addEditChild);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}