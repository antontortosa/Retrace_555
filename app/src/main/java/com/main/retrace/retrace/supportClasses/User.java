package com.main.retrace.retrace.supportClasses;

/**
 * Class to handle User information.
 */
public class User {

    /**
     * Contains the username.
     */
    private String username;

    /**
     * Contains the email.
     */
    private String email;

    /**
     * Default constructor.
     */
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /**
     * Constructor
     *
     * @param username
     * @param email
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}