package com.example.capstone.models;

import java.util.Date;

public class Message {

    private long date_time;
    private String message_content;
    private String senderID;

    // Empty constructor for firebase
    public Message() {}

    public Message(String message_content, String senderID) {
        this.date_time = System.currentTimeMillis();
        this.message_content = message_content;
        this.senderID = senderID;
    }
    public Message(String message_content, String senderID, long date) {
        this.date_time = date;
        this.message_content = message_content;
        this.senderID = senderID;
    }

    public long getDate_time() {
        return date_time;
    }

    public String getMessage_content() {
        return message_content;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
