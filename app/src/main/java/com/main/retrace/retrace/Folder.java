package com.main.retrace.retrace;

import java.util.ArrayList;

/**
 * This class contains all the information of a folder.
 */
public class Folder {
    /**
     * Title of the folder.
     */
    private String title;

    /**
     * ArrayList with the tasks
     */
    private ArrayList<Task> tasks;

    /**
     * Constructor for the class.
     *
     * @param title of the folder.
     * @param tasks arraylist with all the tasks.
     */
    public Folder(String title, ArrayList<Task> tasks) {
        this.title = title;
        this.tasks = tasks;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
