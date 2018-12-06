package com.main.retrace.retrace;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.main.retrace.retrace.supportClasses.LatLngCus;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class EditFolderActivity extends AppCompatActivity {

    /**
     * Reference to the EditText with the folder name.
     */
    private EditText editTextFolderName;
    /**
     * Reference to the TextView with the folder location.
     */
    private TextView addLocation;
    /**
     * Reference to the location.
     */
    private LatLngCus location;
    /**
     * Reference to the color button.
     */
    private Button colorButton;
    /**
     * Reference to the save button.
     */
    private Button saveButton;
    /**
     * Reference to the folderId in case it was sent on the intent.
     */
    private String folderId;
    /**
     * Reference to the folder color.
     */
    private String folderColor;


    private int PLACE_PICKER_REQUEST = 1;
    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // DO THE CALCULATIONS HERE AND SHOW THE RESULT AS PER YOUR CALCULATIONS
            if (!s.toString().equals("")) {
                //TODO: Check if the folder name is already in use
                saveButton.setEnabled(true);
                saveButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);

        this.editTextFolderName = findViewById(R.id.folderNameEdit);
        editTextFolderName.addTextChangedListener(filterTextWatcher);
        saveButton = findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);
        saveButton.setTextColor(getResources().getColor(R.color.grey, null));
        colorButton = findViewById(R.id.buttonColor);
        addLocation = findViewById(R.id.addLocation);
        // By default the color is white.
        folderColor = getResources().getString(0 + R.color.folderDefault);
        colorButton.setBackgroundColor(Color.parseColor(folderColor));

        // Let's check if this comes from an already existing folder.
        Intent intent = getIntent();
        String folderName = intent.getStringExtra("FolderName");
        if (folderName != null) {
            editTextFolderName.setText(folderName, TextView.BufferType.EDITABLE);
            folderId = intent.getStringExtra("FolderId");
            location = new LatLngCus(getIntent().getDoubleExtra("Lat", 0), getIntent().getDoubleExtra("Long", 0), getIntent().getStringExtra("Place"));
            addLocation.setText(location.getPlace());
            folderColor = intent.getStringExtra("Color");
            colorButton.setBackgroundColor(Color.parseColor(folderColor));
        }
    }

    public void saveFolder(View view) {
        if (location == null) {
            // No locatio, so saving nulls.
            location = new LatLngCus(null, null, "No Place");
        }

        //Save folder and go back
        Intent i = new Intent(EditFolderActivity.this, Home.class);
        i.putExtra("FolderName", editTextFolderName.getText().toString());
        i.putExtra("FolderId", folderId);
        i.putExtra("Lat", location.getLatitude());
        i.putExtra("Long", location.getLongitude());
        i.putExtra("Place", location.getPlace());
        i.putExtra("Color", folderColor);
        startActivity(i);
        finish();

    }

    public void setLocation(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                location = new LatLngCus(place.getLatLng().latitude, place.getLatLng().longitude, place.getName().toString());
                addLocation.setText(place.getName());
            }
        }
    }

    public void setColor(View view) {
        int cl = Color.parseColor(folderColor);
        final ColorPicker cp = new ColorPicker(EditFolderActivity.this, Color.red(cl), Color.green(cl), Color.blue(cl));
        cp.show();

        cp.enableAutoClose(); // Enable auto-dismiss for the dialog

        /* Set a new Listener called when user click "select" */
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {

                folderColor = String.format("#%06X", (0xFFFFFF & color));
                colorButton.setBackgroundColor(Color.parseColor(folderColor));
                // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                //cp.dismiss();
            }
        });
    }
}
