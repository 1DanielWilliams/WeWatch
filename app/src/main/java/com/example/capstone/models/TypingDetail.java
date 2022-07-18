package com.example.capstone.models;

public class TypingDetail {
    private boolean isTyping;
    private String typingUserID;
    private String typingUsername;
    private String typingScreenName;

    public TypingDetail(){}

    public TypingDetail(boolean isTyping, String typingUserID, String typingUsername, String typingScreenName) {
        this.isTyping = isTyping;
        this.typingUserID = typingUserID;
        this.typingUsername = typingUsername;
        this.typingScreenName = typingScreenName;
    }
    public TypingDetail(boolean isTyping) {
        this.isTyping = isTyping;
        this.typingUserID = "";
        this.typingUsername = "";
        this.typingScreenName = "";
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public String getTypingUserID() {
        return typingUserID;
    }

    public void setTypingUserID(String typingUserID) {
        this.typingUserID = typingUserID;
    }

    public String getTypingUsername() {
        return typingUsername;
    }

    public void setTypingUsername(String typingUsername) {
        this.typingUsername = typingUsername;
    }

    public String getTypingScreenName() {
        return typingScreenName;
    }

    public void setTypingScreenName(String typingScreenName) {
        this.typingScreenName = typingScreenName;
    }
}
