package com.example.capstone.backend;

import android.app.Application;

import com.example.capstone.R;
import com.example.capstone.models.Event;
import com.example.capstone.models.User;
import com.example.capstone.models.UserPublicColumns;
import com.example.capstone.models.VideoContent;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(VideoContent.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(UserPublicColumns.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server("https://wewatch.b4a.io")
                .build());
    }
}
