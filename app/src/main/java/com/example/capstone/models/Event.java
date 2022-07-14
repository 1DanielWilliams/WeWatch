package com.example.capstone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String KEY_AUTHORS = "authors";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NUM_INTERESTED = "numInterested";
    public static final String KEY_POSTER_URL = "posterUrl";
    public static final String KEY_INTERESTED_USERS = "interestedUsers";
    public static final String KEY_IS_LIVE = "isLive";
    public static final String KEY_VIDEO_CONTENT = "videoContent";
    public static final String KEY_BACKDROP_URL = "backdropUrl";
    public static final String KEY_DATES = "dates";
    public static final String KEY_TYPE_OF_CONTENT = "typeOfContent";
    public static final String KEY_EARLIEST_DATE = "earliestDate";
    public static final String KEY_EARLIEST_USER_INDEX = "earliestUserIndex";
    public static final String KEY_UNIVERSITY = "university";
    public static final String KEY_TO_DELETE = "toDelete";


    public Event() {}

    public void setToDelete(boolean toDelete) { put(KEY_TO_DELETE, toDelete);}
    public boolean getToDelete() {return getBoolean(KEY_TO_DELETE); }

    public void setTitle(String title) { put(KEY_TITLE, title); }
    public String getTitle() {return  getString(KEY_TITLE); }

    public void setUsers(List<ParseUser> users) { put(KEY_AUTHORS, users); }
    public List<ParseUser> getUsers() {return  getList(KEY_AUTHORS); }

    public void setNumInterested(int numInterested) {put(KEY_NUM_INTERESTED, numInterested); }
    public int getNumInterested() {return getInt(KEY_NUM_INTERESTED); }

    public void setPosterUrl(String posterUrl) { put(KEY_POSTER_URL, posterUrl); }
    public String getPosterUrl() {return  getString(KEY_POSTER_URL); }

    public void setInterestedUsers(List<List<ParseUser>> interestedUsers) {put(KEY_INTERESTED_USERS, interestedUsers);}
    public List<List<ParseUser>> getInterestedUsers() {return getList(KEY_INTERESTED_USERS); }

    public void setIsLive(boolean isLive) { put(KEY_IS_LIVE, isLive); }
    public boolean getIsLive() {return getBoolean(KEY_IS_LIVE); }

    public void setVideoContent(VideoContent videoContent) { put(KEY_VIDEO_CONTENT, videoContent); }
    public VideoContent getVideContent() { return (VideoContent) get(KEY_VIDEO_CONTENT); }

    public void setBackdropUrl(String backdropUrl) { put(KEY_BACKDROP_URL, backdropUrl); }
    public String getBackdropUrl() {return  getString(KEY_BACKDROP_URL); }

    public void setDates(List<DateIndex> dates) {put(KEY_DATES, dates);}
    public List<DateIndex> getDates() {return getList(KEY_DATES); }

    public void setTypeOfContent(String typeOfContent) { put(KEY_TYPE_OF_CONTENT, typeOfContent); }
    public String getTypeOfContent() {return  getString(KEY_TYPE_OF_CONTENT); }

    public void setEarliestDate(Date earliestDate) { put(KEY_EARLIEST_DATE, earliestDate);}
    public Date getEarliestDate() {return getDate(KEY_EARLIEST_DATE); }

    public void setEarliestUserIndex(int earliestUserIndex) {put(KEY_EARLIEST_USER_INDEX, earliestUserIndex); }
    public int getEarliestUserIndex() {return getInt(KEY_EARLIEST_USER_INDEX); }

    public void setUniversity(String university) {put(KEY_UNIVERSITY, university);}
    public String getUniversity() {return getString(KEY_UNIVERSITY); }

//    public List<Integer> availablePlatforms; //can use an array of constants to represent the key

}
