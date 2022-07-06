package com.example.capstone.models;

import java.util.Date;

public class Message {

    private Date date_time;
    private String message_content;
    private User sender;

    // Empty constructor for firebase
    public Message() {}

    public Message(String message_content, User sender) {
        this.date_time = new Date(System.currentTimeMillis());
        this.message_content = message_content;
        this.sender = sender;
    }

    public Date getDate_time() {
        return date_time;
    }

    public String getMessage_content() {
        return message_content;
    }

    public User getSender() {
        return sender;
    }

}
