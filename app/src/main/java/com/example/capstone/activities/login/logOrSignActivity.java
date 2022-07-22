package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.capstone.R;
import com.example.capstone.activities.feed.FeedActivity;
import com.example.capstone.activities.signup.SignUpActivityCredentials;
import com.parse.ParseUser;

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
            Intent i = new Intent(logOrSignActivity.this, SignUpActivityCredentials.class);
            startActivity(i);
        });

        btnLogIn = findViewById(R.id.btnLogIn);

        btnLogIn.setOnClickListener(v -> {
            Intent i = new Intent(logOrSignActivity.this, UsernameLogInActivity.class);
            startActivity(i);
        });


    }

    private void goToFeed() {
        Intent i = new Intent(this, FeedActivity.class);
        startActivity(i);
        finish();
    }

}
