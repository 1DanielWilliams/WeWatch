package com.example.capstone.models;

import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class Event {
    public String objectId;
    public ParseUser user;
    public String imagePath;
    public String title;
    public int numInterested;
    public Date createdAt;
    public boolean isLive;
    public Date updatedAt;
    public String postImage;
    public List<ParseUser> interestedUsers;
    public String typeOfContent;
    public List<Integer> availablePlatforms; //can use an array of constants to represent the key


    //function for turning JSON object into event object

    //funciton for turning JSON array into list of events
}
