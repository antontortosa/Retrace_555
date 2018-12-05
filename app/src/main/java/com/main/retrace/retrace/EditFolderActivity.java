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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.main.retrace.retrace.supportClasses.LatLngCus;

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
        addLocation = findViewById(R.id.addLocation);
        // By default the color is white.
        folderColor = "ffffff";

        // Let's check if this comes from an already existing folder.
        Intent intent = getIntent();
        String folderName = intent.getStringExtra("FolderName");
        if (folderName != null) {
            editTextFolderName.setText(folderName, TextView.BufferType.EDITABLE);
            folderId = intent.getStringExtra("FolderId");
            location = new LatLngCus(getIntent().getDoubleExtra("Lat", 0), getIntent().getDoubleExtra("Long", 0), getIntent().getStringExtra("Place"));
            addLocation.setText(location.getPlace());

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
}
