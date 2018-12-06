package com.main.retrace.retrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /**
     * This method is called when the go Back Button is clicked.
     *
     * @param view the view where the button was clicked.
     */
    public void goToHome(View view) {
        startActivity(new Intent(AboutActivity.this, Home.class));
        finish();
    }
}
