package com.main.retrace.retrace;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.main.retrace.retrace.supportClasses.Folder;
import com.main.retrace.retrace.supportClasses.FolderAdapter;
import com.main.retrace.retrace.supportClasses.Task;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Stores the folders.
     */
    private ArrayList<Folder> folders;

    /**
     * RecyclerView for the folders.
     */
    private RecyclerView mFoldersRecyclerView;

    /**
     * Adapter for the RecyclerView.
     */
    private RecyclerView.Adapter mAdapter;

    /**
     * LinearLayoutManager for the RecyclerView.
     */
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_folder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        folders = fakeContentCreator();

        // Recycle view setup.
        mFoldersRecyclerView = (RecyclerView) findViewById(R.id.folders_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mFoldersRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FolderAdapter(folders, getApplicationContext());
        mFoldersRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lazy) {
            // To disappear
        } else if (id == R.id.nav_unplaced) {
            // Open unplaced folder

        } else if (id == R.id.nav_new_item) {
            // Add new folder

        } else if (id == R.id.nav_settings) {
            //  Open settings

        } else if (id == R.id.nav_about) {
            // About us

        } else if (id == R.id.nav_logout) {
            // Log out from app

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList<Folder> fakeContentCreator() {
        ArrayList<Folder> folders = new ArrayList<Folder>();

        folders.add(new Folder("Home", new ArrayList<Task>() {{
            add(new Task("Clean the house", null, null));
            add(new Task("Finish the Android Project", null, null));
        }}));
        folders.add(new Folder("Work", new ArrayList<Task>() {{
            add(new Task("Meeting with boss", null, null));
            add(new Task("Clean table", null, null));
            add(new Task("Decide vacations", null, null));
        }}));

        return folders;
    }
}
