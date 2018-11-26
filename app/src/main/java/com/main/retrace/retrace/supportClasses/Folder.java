package com.main.retrace.retrace.supportClasses;

import java.util.HashMap;

/**
 * This class contains all the information of a folder.
 */
public class Folder {
    /**
     * Title of the folder.
     */
    private String title;

    /**
     * Location of the folder.
     */
    private LatLngCus location;

    /**
     * HashMap with the tasks
     */
    private HashMap<String, Task> tasks;

    /**
     * Empty constructor for FireBase compatibility.
     */
    public Folder() {
    }

    /**
     * Constructor for the class.
     *
     * @param title    of the folder.
     * @param location of the folder.
     * @param tasks    HashMap with all the tasks.
     */
    public Folder(String title, LatLngCus location, HashMap<String, Task> tasks) {
        this.title = title;
        this.tasks = tasks;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public LatLngCus getLocation() {
        return location;
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }
}
