package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.activities.FeedActivity;
import com.example.capstone.activities.logOrSignActivity;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LogInActivity2 extends AppCompatActivity {

    private final String TAG = "LogInActivity2";


    private Button btnCancelLogin;
    private Button btnLogin2;
    private Toolbar toolbar;
    private EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_login2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);


        btnCancelLogin = findViewById(R.id.btnCancelLogin2);
        btnLogin2 = findViewById(R.id.btnLogin2);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        btnCancelLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity2.this, logOrSignActivity.class);
                NavUtils.navigateUpTo(LogInActivity2.this, i);
            }
        });

        // authenticates user and logs them in
        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logIn = getIntent().getStringExtra("logIn");
                String password = etLoginPassword.getText().toString();

                // Checks if a email was given
                if (logIn.indexOf('@') == -1) {
                    loginUser(logIn, password);
                } else {
                    loginWithEmail(logIn, password);
                }
            }
        });
    }

    // finds the username of a user by using their email
    private void loginWithEmail(String logIn, String password) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", logIn);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "done: error", e);
                    return;
                } else if (users.size() == 0) {
                    Toast.makeText(LogInActivity2.this, "Incorrect email address", Toast.LENGTH_SHORT).show();
                    etLoginPassword.setText("");
                    NavUtils.navigateUpFromSameTask(LogInActivity2.this);
                    return;
                }

                String username = users.get(0).getUsername();
                loginUser(username, password);
            }
        });
    }


    private void loginUser(String logIn, String password) {
        ParseUser.logInInBackground(logIn, password, (user, e) -> {
            // If the user inputs the wrong password
            if (e != null) {
                Log.e(TAG, "done: trouble logging in", e);
                Toast.makeText(LogInActivity2.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                etLoginPassword.setText("");
                return;
            }

            Toast.makeText(LogInActivity2.this, "Success", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LogInActivity2.this, FeedActivity.class);
            startActivity(i);
            finish();
        });
    }
}