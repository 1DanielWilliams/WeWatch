package com.example.capstone.models;

import java.util.List;

public class GroupDetail {
    private String name;
    private String id;
    private Message message; //recent message

    // Empty constructor for firebase
    public GroupDetail() {}

    public GroupDetail(String name, String id, Message message) {
        this.name = name;
        this.id = id;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
