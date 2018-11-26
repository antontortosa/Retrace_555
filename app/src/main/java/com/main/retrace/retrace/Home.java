package com.main.retrace.retrace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.SphericalUtil;
import com.main.retrace.retrace.supportClasses.CircleTransform;
import com.main.retrace.retrace.supportClasses.DatabaseManager;
import com.main.retrace.retrace.supportClasses.Folder;
import com.main.retrace.retrace.supportClasses.FolderAdapter;
import com.main.retrace.retrace.supportClasses.LatLngCus;
import com.main.retrace.retrace.supportClasses.Task;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * RecyclerView for the folders.
     */
    private RecyclerView mFoldersRecyclerView;

    /**
     * Adapter for the RecyclerView.
     */
    private FolderAdapter mAdapter;

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
     * Reference to the user.
     */
    private FirebaseUser user;

    /**
     * Location client
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Last Known Location
     */
    private LatLngCus LKL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Receiving user info after login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("Login", "User info received correctly at the Home Activity.");
            this.user = user;
        } else {
            // No user is signed in
            Log.d("Login", "User shouldn't be here without logging in first. ");
            finish();
        }

        dbManager = new DatabaseManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.add_folder);
        mFusedLocationClient = getFusedLocationProviderClient(this);
        //FIX POSITION FOR TESTING
        LKL = new LatLngCus(41.925017, -87.659908);

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

        // Set user name & profile picture
        View headerView = navigationView.getHeaderView(0);
        ImageView imageView = headerView.findViewById(R.id.nav_imageView);
        Drawable imageDrawable = imageView.getDrawable();

        Picasso.with(Home.this)
                .load(user.getPhotoUrl())
                .placeholder(imageDrawable)
                .resize(100, 100)
                .centerCrop()
                .transform(new CircleTransform())
                .into(imageView);

        ((TextView) headerView.findViewById(R.id.nav_userTextview)).setText(user.getDisplayName());
        ((TextView) headerView.findViewById(R.id.nav_emailTextView)).setText(user.getEmail());

        // Progress view setup.
        mProgressView = findViewById(R.id.loading_progress);

        // Recycle view setup.
        mFoldersRecyclerView = findViewById(R.id.folders_recycler_view);
        mFoldersRecyclerView.setVisibility(View.GONE);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mFoldersRecyclerView.setLayoutManager(mLayoutManager);

        // Show the progress since we are loading the data from the Database.
        showProgress(true);

        // Depending if the Database a different thing is executed.
        final boolean databaseEmpty = false;
        if (databaseEmpty) {
            Log.d("DB", "Creating faking content for the Database.");
            HashMap<String, Folder> folders = fakeContentCreator();
            dbManager.setFolders(folders);
            populateDatabase("bNaSdXkbmzQ7tRsRDizZ11pUorx1");
        }

        // Remember that onResume is also executed after this.
    }

    @Override
    public void onResume() {
        super.onResume();

        checkIntent();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            Log.d("Login", "User logged out correctly, redirecting to log in.");
                            startActivity(new Intent(Home.this, LoginActivity.class));
                        }
                    });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
     * Triggers the update of the database so it is updated with the value of the folders attribute.
     */
    private void populateDatabase(String userId) {
        for (String s : dbManager.getFolders().keySet()) {
            Folder folder = dbManager.getFolders().get(s);
            String folderId = dbManager.writeFolder(userId, folder.getTitle(), folder.getLocation());
            for (String s1 : folder.getTasks().keySet()) {
                dbManager.writeTask(userId, folderId, folder.getTasks().get(s1));
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
            LinkedHashMap<String, Folder> auxfolders = new LinkedHashMap<String, Folder>();
            LinkedHashMap<String, Integer> fdistances = new LinkedHashMap<String, Integer>();
            LinkedHashMap<String, Integer> ordered = new LinkedHashMap<String, Integer>();

            //For each entry in the folders hashmap we calculate its postition
            for (String x : dbManager.getFolders().keySet()) {
                fdistances.put(x, calculateOrder(dbManager.getFolders().get(x).getLocation()));
            }
            //Reorder the folders
            fdistances.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> ordered.put(x.getKey(), x.getValue()));
            //store them ordered
            for (String x : ordered.keySet()) {
                auxfolders.put(x, dbManager.getFolders().get(x));
            }
            // specify an adapter (see also next example), we couldn't do this because the data wasn't loaded.
            mAdapter = new FolderAdapter(dbManager, getApplicationContext());
            // Setting folders ordered.
            mAdapter.setmFolderData(auxfolders);
            mFoldersRecyclerView.setAdapter(mAdapter);
            populateNavView();
        }
    }

    public FirebaseUser getUser() {
        return user;
    }

    private void populateNavView() {
        LinkedHashMap<String, Folder> folders_linked = new LinkedHashMap<String, Folder>(dbManager.getFolders());
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem aux_menu;
        for (Folder f : folders_linked.values()) {
            getLastLocation();
            aux_menu = menu.add(R.id.lazy_group, Menu.NONE, calculateOrder(f.getLocation()), f.getTitle());
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

    public int calculateOrder(LatLngCus location) {
        double distance;
        if (location != null && LKL != null) {
            distance = SphericalUtil.computeDistanceBetween(location.getLatLng(), LKL.getLatLng());
        } else {
            distance = 997;
        }
        return (int) distance % 999;
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
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

    private void checkIntent() {
        // Checking if this comes from the EditFolder Activity
        String folderName = getIntent().getStringExtra("FolderName");
        if (folderName != null) {
            String folderId = getIntent().getStringExtra("FolderId");
            // Maybe it is an edit
            if (folderId == null) {
                Log.d("Home", "Adding new folder after + was pressed.");
                LatLngCus latLngCus = new LatLngCus(getIntent().getDoubleExtra("Lat", 0), getIntent().getDoubleExtra("Long", 0));
                dbManager.writeFolder(folderName, latLngCus);
            } else {
                // It is an edit!
                Log.d("Home", "Editing existing folder from EditFolderActivity.");
                LatLngCus latLngCus = new LatLngCus(getIntent().getDoubleExtra("Lat", 0), getIntent().getDoubleExtra("Long", 0));
                dbManager.editFolder(folderId, folderName, latLngCus);
            }
        }
    }
}
