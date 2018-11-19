package com.main.retrace.retrace.supportClasses;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    private ArrayList<Folder> folders;

    private final static String USER_CHILDNAME = "users";
    private final static String TASK_CHILDNAME = "tasks";
    private final static String FOLDER_CHILDNAME = "folders";
    private final static String FOLDER_TITLE = "folderName";

    public DatabaseManager(String userId, ArrayList<Folder> folders) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.folders = folders;

        folderReference = databaseReference.child(FOLDER_CHILDNAME).child(userId);

        ValueEventListener foldersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("DB", String.valueOf(snapshot.getChildrenCount()));
                for (DataSnapshot folderSnapshot : snapshot.getChildren()) {
                    getFolders().add(folderSnapshot.getValue(Folder.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("DB", "loadFolder:onCancelled", databaseError.toException());
                // ...
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
    public String writeFolder(String userId, String title) {
        String folderId = folderReference.push().getKey();
        folderReference.child(folderId).child(FOLDER_TITLE).setValue(title);
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

    public ArrayList<Folder> getFolders() {
        return folders;
    }
}
