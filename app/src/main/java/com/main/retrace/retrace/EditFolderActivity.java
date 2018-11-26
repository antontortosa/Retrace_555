package com.main.retrace.retrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.main.retrace.retrace.supportClasses.LatLngCus;

public class EditFolderActivity extends AppCompatActivity {

    /**
     * Reference to the EditText with the folder name.
     */
    private EditText editTextFolderName;
    /**
     * Reference to the location.
     */
    private LatLngCus location;
    /**
     * Reference to the save button.
     */
    private Button saveButton;
    /**
     * Reference to the folderId in case it was sent on the intent.
     */
    private String folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);
        this.editTextFolderName = findViewById(R.id.folderNameEdit);
        editTextFolderName.addTextChangedListener(filterTextWatcher);
        saveButton = findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

        // Let's check if this comes from an already existing folder.
        Intent intent = getIntent();
        String folderName = intent.getStringExtra("FolderName");
        if (folderName != null) {
            editTextFolderName.setText(folderName, TextView.BufferType.EDITABLE);
            folderId = intent.getStringExtra("FolderId");
            location = new LatLngCus(getIntent().getDoubleExtra("Lat", 0), getIntent().getDoubleExtra("Long", 0));
        }
    }

    public void saveFolder(View view) {
        //Save folder and go back
        Intent i = new Intent(EditFolderActivity.this, Home.class);
        i.putExtra("FolderName", editTextFolderName.getText().toString());
        i.putExtra("FolderId", folderId);
        i.putExtra("Lat", 41.8789);
        i.putExtra("Long", -87.6358);
        startActivity(i);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // DO THE CALCULATIONS HERE AND SHOW THE RESULT AS PER YOUR CALCULATIONS
            if (!s.toString().equals("")) {
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
