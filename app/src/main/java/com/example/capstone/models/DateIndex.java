package com.example.capstone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("DateIndex")
public class DateIndex extends ParseObject {
    public static final String KEY_USER_INDEX = "userIndex";
    public static final String KEY_DATE = "date";


    public DateIndex() {}

    public void setUserIndex(int userIndex) { put(KEY_USER_INDEX, userIndex); }
    public int getUserIndex() {return getInt(KEY_USER_INDEX); }

    public void setDate(String date) {put(KEY_DATE, date); }
    public String getDate() {return getString(KEY_DATE); }

    public DateIndex(String date, int userIndex) {
        setDate(date);
        setUserIndex(userIndex);
    }

}
