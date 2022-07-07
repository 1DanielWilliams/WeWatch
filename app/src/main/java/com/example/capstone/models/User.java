package com.example.capstone.models;

public class User {
    private String object_ID;
    private String username;
    private String screenName;
//    private String groupIDs; will add later

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


/*
messages:
    $groupID
        message object
            message_content
            senderID
            time_date,
        message object
users:
    userobject
        groups:
            group ids
        userID
        username
        screenName,
    userobject
groupDetails:
    name
    id
    members
        userids
    recent message
        message object
 */