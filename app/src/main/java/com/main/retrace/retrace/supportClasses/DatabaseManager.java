package com.main.retrace.retrace.supportClasses;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
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

    private final static String USER_CHILDNAME = "users";
    private final static String FOLDER_CHILDNAME = "folders";
    private final static String TASK_CHILDNAME = "tasks";
    private final static String FOLDER_KEY_TITLE = "title";
    private final static String FOLDER_KEY_LOCATION = "location";
    private final static String FOLDER_KEY_COLOR = "color";
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
    /**
     * Reference to folders.
     */
    private HashMap<String, Folder> folders;
    /**
     * Reference to the user.
     */
    private FirebaseUser user;

    /**
     * Default glorious constructor for the king of the databases. Folders is constructed but it is empty until it gets the data from the database (it is not instantaneous).
     *
     * @param home reference to home activity.
     */
    public DatabaseManager(final Home home) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.home = home;
        this.user = home.getUser();

        // Construct folders
        folders = new HashMap<String, Folder>();

        // Build reference to simplify code later on.
        folderReference = databaseReference.child(FOLDER_CHILDNAME).child(user.getUid());

        ValueEventListener foldersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DB", "Data Changed, elements: " + dataSnapshot.getChildrenCount());

                // We clear it to make sure we only have updated data.
                folders.clear();

                for (DataSnapshot folderSnapshot : dataSnapshot.getChildren()) {
                    folders.put(folderSnapshot.getKey(), folderSnapshot.getValue(Folder.class));
                }

                home.updateUI(true);
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
     * Creates a new folder. Use only to manually insert data.
     *
     * @param userId of the desired user.
     * @param title  of the folder.
     * @return the folderId.
     */
    public String writeFolder(String userId, String title, LatLngCus location, String color) {
        String folderId = databaseReference.child(FOLDER_CHILDNAME).child(userId).push().getKey();
        databaseReference.child(FOLDER_CHILDNAME).child(userId).child(folderId).child(FOLDER_KEY_TITLE).setValue(title);
        databaseReference.child(FOLDER_CHILDNAME).child(userId).child(folderId).child(FOLDER_KEY_LOCATION).setValue(location);
        return folderId;
    }

    /**
     * Creates a new folder into the user that it is currently logged in.
     *
     * @param title    of the folder.
     * @param location of the folder.
     * @param color    of the folder.
     */
    public void writeFolder(String title, LatLngCus location, String color) {
        String folderId = databaseReference.child(FOLDER_CHILDNAME).child(user.getUid()).push().getKey();
        folderReference.child(folderId).child(FOLDER_KEY_TITLE).setValue(title);
        folderReference.child(folderId).child(FOLDER_KEY_LOCATION).setValue(location);
        folderReference.child(folderId).child(FOLDER_KEY_COLOR).setValue(color);
        Log.d("DatabaseManager | Folder", "New folder created: " + title + ", id: " + folderId);
    }

    /**
     * This method edits an existing folder.
     *
     * @param folderId of the existing folder.
     * @param title    the new title.
     * @param location the new location.
     * @param color    the new color.
     */
    public void editFolder(String folderId, String title, LatLngCus location, String color) {
        folderReference.child(folderId).child(FOLDER_KEY_TITLE).setValue(title);
        folderReference.child(folderId).child(FOLDER_KEY_LOCATION).setValue(location);
        folderReference.child(folderId).child(FOLDER_KEY_COLOR).setValue(color);
        Log.d("DatabaseManager | Folder", "Folder edited: " + title + ", id: " + folderId);
    }

    /**
     * This method removes a folder.
     *
     * @param folderId of the folder to remove.
     */
    public void removeFolder(String folderId) {
        folderReference.child(folderId).removeValue();
        Log.d("DatabaseManager | Folder", "Folder removed, id: " + folderId);
    }

    /**
     * This method adds a task to the database. Use only to manually insert data into a specific user/folder.
     *
     * @param userId of the user to add the tasks to.
     * @param task   the new task.
     * @return the taskId.
     */
    public String writeTask(String userId, String folderId, Task task) {
        String taskId = databaseReference.child(FOLDER_CHILDNAME).child(userId).child(folderId).child(TASK_CHILDNAME).push().getKey();
        databaseReference.child(FOLDER_CHILDNAME).child(userId).child(folderId).child(TASK_CHILDNAME).child(taskId).setValue(task);
        return taskId;
    }

    /**
     * This method adds a task to the database into the user that it is currently logged in.
     *
     * @param task the new task.
     */
    public void writeTask(String folderId, Task task) {
        String taskId = databaseReference.child(FOLDER_CHILDNAME).child(user.getUid()).child(folderId).child(TASK_CHILDNAME).push().getKey();
        folderReference.child(folderId).child(TASK_CHILDNAME).child(taskId).setValue(task);
        Log.d("DatabaseManager | Task", "New task created: " + task.getName() + ", id: " + taskId);
    }

    /**
     * This method removes the task.
     *
     * @param folderId of the folder where the task is.
     * @param taskId   of the task to remove.
     */
    public void removeTask(String folderId, String taskId) {
        folderReference.child(folderId).child(TASK_CHILDNAME).child(taskId).removeValue();
        Log.d("DatabaseManager | Task", "Task removed, id: " + taskId);
    }

    /**
     * Deprecated because the table has changed. This method creates a new user. Should only be used for testing.
     *
     * @param user the new user.
     * @return the userId.
     */
    @Deprecated
    private String writeUser(User user) {
        String userId = databaseReference.child(USER_CHILDNAME).push().getKey();
        databaseReference.child(USER_CHILDNAME).child(userId).setValue(user);
        return userId;
    }

    /**
     * Getter for the folders.
     *
     * @return the folders.
     */
    public HashMap<String, Folder> getFolders() {
        return folders;
    }

    /**
     * Setter for the folders. Only use to manually override the folders!
     *
     * @param folders the new folders.
     */
    public void setFolders(HashMap<String, Folder> folders) {
        this.folders = folders;
    }

    public Home getHome() {
        return home;
    }
}
