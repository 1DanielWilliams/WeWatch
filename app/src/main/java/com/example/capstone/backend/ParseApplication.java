package com.example.capstone.backend;

import android.app.Application;

import com.example.capstone.R;
import com.parse.Parse;

public class ParseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //register different classes here

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .server(getString(R.string.back4app_server_url))
                .clientKey(getString(R.string.back4app_client_key))
                .build());
    }
}
