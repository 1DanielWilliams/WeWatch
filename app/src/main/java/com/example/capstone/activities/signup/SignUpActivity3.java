package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.methods.LogInUser;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SignUpActivity3 extends AppCompatActivity {

    private final String TAG = "SignUpActivity3";


    private Toolbar toolbar;
    private Button btnCreateAccount2;
    private ImageButton upArrowSign3;
    private EditText etFinalName;
    private EditText etFinalEmail;
    private EditText etFinalPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_signup3);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        btnCreateAccount2 = findViewById(R.id.btnCreateAccount2);
        upArrowSign3 = findViewById(R.id.upArrowSign3);
        upArrowSign3.setOnClickListener(v -> NavUtils.navigateUpFromSameTask(SignUpActivity3.this));


        etFinalName = findViewById(R.id.etSignName);
        etFinalEmail = findViewById(R.id.etSignEmail);
        etFinalPassword = findViewById(R.id.etSignPassword);

        Intent previousIntent = getIntent();
        String screenName = previousIntent.getStringExtra("screenName");
        String email = previousIntent.getStringExtra("email");
        String password = previousIntent.getStringExtra("password");

        etFinalName.setText(screenName);
        etFinalEmail.setText(email);
        etFinalPassword.setText(password);


        // Check authenticate user and log them in/sign them up
        btnCreateAccount2.setOnClickListener(v -> {

            ParseUser user = new ParseUser();
            user.setEmail(email);
            user.setPassword(password);
            user.put("screenName", screenName);

            // determines the university an individual attends
            String university = email.split("@")[1].replace(".edu", "");
            user.put("university", university);

            signupUser(user, screenName, email, password);

        });
    }

    private void signupUser(ParseUser user, String screenName, String email, String password) {

        String screeNameLower = screenName.toLowerCase();
        // Generates unique username for each user
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereStartsWith("username", screeNameLower);
        query.findInBackground((users, e) -> {

            if (e != null) {
                Log.e(TAG, "done: error finding usernames that start with screenName", e);
                return;
            }

            String username;
            int size = users.size();
            if (size == 0) {
                username = screeNameLower;
                user.setUsername(username);
            } else {
                username = screeNameLower + String.valueOf(size + 1);
                user.setUsername(username);
            }

            // Signs a user up with this unique username
            user.signUpInBackground(e1 -> {
                if (e1 != null) {
                    Log.e(TAG, "done: Error creating account ", e1);
                    return;
                }

                LogInUser.loginUser(SignUpActivity3.this, username, password, etFinalPassword);
            });
        });
    }
}