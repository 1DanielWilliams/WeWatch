package com.example.capstone.models;

import java.util.List;

public class GroupChat {
    private String name;
    private String id;
    private List<User> users;
    private List<Message> messages;

    // Empty constructor for firebase
    public GroupChat() {}

    public GroupChat(String name, String id, List<User> users, List<Message> messages) {
        this.name = name;
        this.id = id;
        this.users = users;
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
