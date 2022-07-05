package com.example.capstone.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.capstone.R;
import com.example.capstone.activities.login.LogInActivity1;
import com.example.capstone.activities.signup.SignUpActivity1;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Objects;

public class logOrSignActivity extends AppCompatActivity {

    private Button btnCreateAccount;
    private Button btnLogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_or_sign);

        if (ParseUser.getCurrentUser() != null) {
            goToFeed();
        }

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnLogIn = findViewById(R.id.btnLogIn);

        btnCreateAccount.setOnClickListener(v -> {
            Intent i = new Intent(logOrSignActivity.this, SignUpActivity1.class);
            startActivity(i);
        });

        btnLogIn = findViewById(R.id.btnLogIn);

        btnLogIn.setOnClickListener(v -> {
            Intent i = new Intent(logOrSignActivity.this, LogInActivity1.class);
            startActivity(i);
        });


    }

    private void goToFeed() {
        Intent i = new Intent(this, FeedActivity.class);
        startActivity(i);
        finish();
    }

}
