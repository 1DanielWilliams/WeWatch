package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.capstone.FeedActivity;
import com.example.capstone.R;

public class LogInActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

//        if (ParseUser.getCurrentUser() != null) {
//            goFeedActivity();
//        }
    }

    private void goFeedActivity() {
        Intent i = new Intent(this, FeedActivity.class);
        startActivity(i);
        finish();
    }
}