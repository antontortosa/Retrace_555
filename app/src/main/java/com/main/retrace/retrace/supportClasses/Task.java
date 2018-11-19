package com.main.retrace.retrace.supportClasses;

import java.util.Date;

/**
 * Class for all the information regarding a Task.
 */
public class Task {
    /**
     * Name of the task.
     */
    private String name;

    /**
     * If the task is done it is true.
     */
    private Boolean done;

    /**
     * When the task was created.
     */
    private Date creationDate;

    /**
     * When the task is due.
     */
    private Date dueDate;

    /**
     * When the task was completed.
     */
    private Date completionDate;

    /**
     * Constructor for the task. By default the {@link Task#completionDate} is null.
     *
     * @param name         of the task.
     * @param creationDate of the task.
     * @param dueDate      of the task.
     */
    public Task(String name, Date creationDate, Date dueDate) {
        this.name = name;
        this.done = false;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.completionDate = null;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return done;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }
}
