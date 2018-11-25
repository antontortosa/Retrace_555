package com.main.retrace.retrace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.main.retrace.retrace.supportClasses.LatLngCus;

public class EditFolderActivity extends AppCompatActivity {

    EditText newName;
    LatLngCus location;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);
        this.newName = findViewById(R.id.folderNameEdit);
        newName.addTextChangedListener(filterTextWatcher);
        saveButton = findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

    }

    public void saveFolder(View view){
        //TODO
        //Save folder and go back
        Intent i = new Intent(EditFolderActivity.this, Home.class);
        startActivity(i);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // DO THE CALCULATIONS HERE AND SHOW THE RESULT AS PER YOUR CALCULATIONS
            if(!s.toString().equals("")){
                saveButton.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
