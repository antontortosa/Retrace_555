package com.main.retrace.retrace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.main.retrace.retrace.supportClasses.DatabaseManager;
import com.main.retrace.retrace.supportClasses.Folder;
import com.main.retrace.retrace.supportClasses.FolderAdapter;
import com.main.retrace.retrace.supportClasses.LatLngCus;
import com.main.retrace.retrace.supportClasses.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.maps.android.SphericalUtil;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

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

    /**
     * Location client
     * */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Last Known Location
     * */
    private LatLngCus LKL;

    private final static String userId = "-LRdRidR3LUhzN_xSTcT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFusedLocationClient = getFusedLocationProviderClient(this);
        //FIX POSITION FOR TESTING
        LKL = new LatLngCus(41.925017,-87.659908);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_folder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to create folder activity
                Intent i = new Intent(Home.this, EditFolderActivity.class);
                startActivity(i);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
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

        if (id == R.id.nav_unplaced) {
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
            LinkedHashMap<String,Folder> auxfolders = new LinkedHashMap<String, Folder>();
            LinkedHashMap<String, Integer> fdistances = new LinkedHashMap<String, Integer>();
            LinkedHashMap<String, Integer> ordered = new LinkedHashMap<String, Integer>();

            //For each entry in the folders hashmap we calculate its postition
            for (String x : folders.keySet()) {
                fdistances.put(x, calculateOrder(folders.get(x).getLocation()));
            }
            //Reorder the folders
            fdistances.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> ordered.put(x.getKey(), x.getValue()));
            //store them ordered
            for (String x : ordered.keySet()) {
                auxfolders.put(x,folders.get(x));
            }
            // specify an adapter (see also next example), we couldn't do this because the data wasn't loaded.

            mAdapter = new FolderAdapter(auxfolders, getApplicationContext());
            mFoldersRecyclerView.setAdapter(mAdapter);
            populateNavView();
        }
    }

    private void populateNavView(){
        LinkedHashMap<String, Folder> folders_linked = new LinkedHashMap<String, Folder>(folders);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem aux_menu;
        for(Folder f : folders_linked.values()){
            getLastLocation();
            aux_menu = menu.add(R.id.lazy_group, Menu.NONE, calculateOrder(f.getLocation()),f.getTitle());
            aux_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //TODO
                    //Intent to folder view
                    return true;
                }
            });
        }
    }

    public int calculateOrder(LatLngCus location){
        double distance;
        if(location!=null && LKL!=null){distance = SphericalUtil.computeDistanceBetween(location.getLatLng(), LKL.getLatLng());}
        else{distance = 997;}
        return (int)distance%999;
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.d("MapDemoActivity", msg);
        // You can now create a LatLng Object for use with maps
        LKL = new LatLngCus(location.getLatitude(), location.getLongitude());
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },0);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        Log.d("MapDemoActivity", "Sucess trying to get last GPS location");
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

}
