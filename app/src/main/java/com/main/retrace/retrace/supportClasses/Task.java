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
     * Empty constructor for FireBase compatibility.
     */
    public Task() {
    }

    /**
     * Constructor with all the parameters for FireBase compatibility.
     *
     * @param name           of the task.
     * @param done           is the task completed?
     * @param creationDate   of the task.
     * @param dueDate        of the task.
     * @param completionDate of the task.
     */
    public Task(String name, Boolean done, Date creationDate, Date dueDate, Date completionDate) {
        this.name = name;
        this.done = done;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
    }

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
