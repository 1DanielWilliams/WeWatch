package com.example.capstone.models;

import java.util.List;

public class GroupDetail {
    private String name;
    private String id;
    private Message message; //recent message
    private List<String> users;
    private TypingDetail typing_detail;

    // Empty constructor for firebase
    public GroupDetail() {}

    public GroupDetail(String name, String id, Message message) {
        this.name = name;
        this.id = id;
        this.message = message;
    }

    public GroupDetail(String name, String id, Message message, List<String> users) {
        this.name = name;
        this.id = id;
        this.message = message;
        this.users = users;
    }

    public TypingDetail getTyping_detail() {
        return typing_detail;
    }

    public void setTyping_detail(TypingDetail typing_detail) {
        this.typing_detail = typing_detail;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
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
