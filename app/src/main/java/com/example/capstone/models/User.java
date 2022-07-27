package com.example.capstone.models;

public class User {
    private String object_ID;
    private String username;
    private String screenName;

    // Empty constructor for firebase
    public User() {}

    public User(String object_ID, String username, String screenName) {
        this.object_ID = object_ID;
        this.username = username;
        this.screenName = screenName;
    }

    public String getObject_ID() {
        return this.object_ID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getScreenName() {
        return this.screenName;
    }
}