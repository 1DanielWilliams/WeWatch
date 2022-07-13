package com.example.capstone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("UserPublicColumns")
public class UserPublicColumns extends ParseObject {
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_GROUPCHAT_IDS = "groupChatIDs";

    public UserPublicColumns() {}

    public void setUserId(String userId) {put(KEY_USER_ID, userId); }
    public String getUserId() {return getString(KEY_USER_ID); }

    public void setGroupChatIds(List<String> groupChatIds) {put(KEY_GROUPCHAT_IDS, groupChatIds); }
    public List<String> getGroupChatIds() {return getList(KEY_GROUPCHAT_IDS); }
}
