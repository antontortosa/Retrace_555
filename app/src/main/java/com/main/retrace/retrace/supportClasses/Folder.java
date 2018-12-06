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
     * Color of the folder.
     */
    private String color;

    /**
     * Empty constructor for FireBase compatibility.
     */
    public Folder() {
    }

    /**
     * Constructor for the class.
     *
     * @param title       of the folder.
     * @param location    of the folder.
     * @param tasks       HashMap with all the tasks.
     * @param color color of the folder.
     */
    public Folder(String title, LatLngCus location, HashMap<String, Task> tasks, String color) {
        this.title = title;
        this.tasks = tasks;
        this.location = location;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    public LatLngCus getLocation() {
        return location;
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }
}
