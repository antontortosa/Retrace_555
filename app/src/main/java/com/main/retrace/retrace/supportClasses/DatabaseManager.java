package com.main.retrace.retrace.supportClasses;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.main.retrace.retrace.Home;

import java.util.HashMap;

/**
 * This class helps with the management of the database.
 */
public class DatabaseManager {

    /**
     * Reference to the database.
     */
    private DatabaseReference databaseReference;

    /**
     * Direct reference to the folders of the user.
     */
    private DatabaseReference folderReference;

    /**
     * Reference to home.
     */
    private Home home;

    private HashMap<String, Folder> folders;

    private final static String USER_CHILDNAME = "users";
    private final static String FOLDER_CHILDNAME = "folders";
    private final static String TASK_CHILDNAME = "tasks";

    private final static String FOLDER_KEY_TITLE = "title";
    private final static String FOLDER_KEY_LOCATION = "location";

    public DatabaseManager(final String userId, HashMap<String, Folder> folders, final Home home) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.folders = folders;
        this.home = home;

        // Build reference to simplify code later on.
        folderReference = databaseReference.child(FOLDER_CHILDNAME).child(userId);

        ValueEventListener foldersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DB", "Data Changed, elements: " +dataSnapshot.getChildrenCount());

                for (DataSnapshot folderSnapshot : dataSnapshot.getChildren()) {
                    getFolders().put(folderSnapshot.getKey(), folderSnapshot.getValue(Folder.class));
                }
                home.showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("DB", "loadFolder:onCancelled", databaseError.toException());
            }
        };
        folderReference.addValueEventListener(foldersListener);
    }

    /**
     * Creates a new folder.
     *
     * @param userId for who the folder is created.
     * @param title  of the folder.
     * @return the folderId.
     */
    public String writeFolder(String userId, String title, LatLngCus location) {
        String folderId = folderReference.push().getKey();
        folderReference.child(folderId).child(FOLDER_KEY_TITLE).setValue(title);
        folderReference.child(folderId).child(FOLDER_KEY_LOCATION).setValue(location);
        return folderId;
    }

    /**
     * This method adds a task to the database.
     *
     * @param task the new task.
     * @return the taskId.
     */
    public String writeTask(String userId, String folderId, Task task) {
        String taskId = folderReference.child(folderId).child(TASK_CHILDNAME).push().getKey();
        folderReference.child(folderId).child(TASK_CHILDNAME).child(taskId).setValue(task);
        return taskId;
    }

    /**
     * This method creates a new user.
     *
     * @param user the new user.
     * @return the userId.
     */
    public String writeUser(User user) {
        String userId = databaseReference.child(USER_CHILDNAME).push().getKey();
        databaseReference.child(USER_CHILDNAME).child(userId).setValue(user);
        return userId;
    }

    public HashMap<String, Folder> getFolders() {
        return folders;
    }
}
