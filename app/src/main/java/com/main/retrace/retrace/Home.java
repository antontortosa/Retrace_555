package com.main.retrace.retrace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.main.retrace.retrace.supportClasses.DatabaseManager;
import com.main.retrace.retrace.supportClasses.Folder;
import com.main.retrace.retrace.supportClasses.FolderAdapter;
import com.main.retrace.retrace.supportClasses.LatLngCus;
import com.main.retrace.retrace.supportClasses.Task;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Stores the folders.
     */
    private HashMap<String, Folder> folders;

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

    /**
     * Progress bar view.
     */
    private View mProgressView;

    /**
     * Reference to the database manager.
     */
    private DatabaseManager dbManager;

    private final static String userId = "-LRdRidR3LUhzN_xSTcT";

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

        // Progress view setup.
        mProgressView = findViewById(R.id.loading_progress);

        // Recycle view setup.
        mFoldersRecyclerView = (RecyclerView) findViewById(R.id.folders_recycler_view);
        mFoldersRecyclerView.setVisibility(View.GONE);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mFoldersRecyclerView.setLayoutManager(mLayoutManager);

        // Show the progress since we are loading the data from the Database.
        showProgress(true);

        // Depending if the Database a different thing is executed.
        final boolean databaseEmpty = false;
        if (databaseEmpty) {
            Log.d("DB", "Creating faking content for the Database.");
            folders = fakeContentCreator();
            dbManager = new DatabaseManager(userId, folders, this);
            populateDatabase();
        } else {
            folders = new HashMap<String, Folder>();
            dbManager = new DatabaseManager(userId, folders, this);
        }
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

    /**
     * Creates a set of fake data.
     *
     * @return an ArrayList with the folders.
     */
    private HashMap<String, Folder> fakeContentCreator() {
        HashMap<String, Folder> folders = new HashMap<String, Folder>();
        // Location corresponds to 1237 Fullerton
        folders.put("0", new Folder("Home", new LatLngCus(41.925017, -87.659908), new HashMap<String, Task>() {{
            put("0", new Task("Clean the house", new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime(), null));
            put("1", new Task("Finish the Android Project", new GregorianCalendar(2015, Calendar.MARCH, 16).getTime(), new GregorianCalendar(2018, Calendar.NOVEMBER, 26).getTime()));
        }}));
        // Location corresponds to Wishnick hall.
        folders.put("1", new Folder("Work", new LatLngCus(41.835118, -87.627608), new HashMap<String, Task>() {{
            put("0", new Task("Meeting with boss", null, null));
            put("1", new Task("Clean table", null, null));
            put("2", new Task("Decide vacations", null, null));
        }}));

        return folders;
    }

    /**
     * Populates the database with the folders.
     */
    private void populateDatabase() {
        Iterator<String> folderIterator = folders.keySet().iterator();
        while (folderIterator.hasNext()) {
            Folder folder = folders.get(folderIterator.next());
            String folderId = dbManager.writeFolder(userId, folder.getTitle(), folder.getLocation());
            Iterator<String> taskIterator = folder.getTasks().keySet().iterator();
            while (taskIterator.hasNext()) {
                dbManager.writeTask(userId, folderId, folder.getTasks().get(taskIterator.next()));
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form. It only shows the progress spinner if the api running on the phone supports it.
     */
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFoldersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFoldersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        if (!show) {
            // specify an adapter (see also next example), we couldn't do this because the data wasn't loaded.
            mAdapter = new FolderAdapter(new LinkedHashMap<String, Folder>(folders), getApplicationContext());
            mFoldersRecyclerView.setAdapter(mAdapter);
        }
    }
}
