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
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.methods.LogInUser;
import com.example.capstone.methods.SignUpErrorsUtil;
import com.example.capstone.models.UserPublicColumns;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class SignUpActivityConfirmation extends AppCompatActivity {

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
        setContentView(com.example.capstone.R.layout.activity_signup_confirmation);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        btnCreateAccount2 = findViewById(R.id.btnCreateAccount2);
        upArrowSign3 = findViewById(R.id.upArrowSign3);
        upArrowSign3.setOnClickListener(v -> NavUtils.navigateUpFromSameTask(SignUpActivityConfirmation.this));


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

            String defaultFeed = previousIntent.getStringExtra(SignUpActivityOptions.SPINNER_FEED_INTENT);
            String defaultShow = previousIntent.getStringExtra(SignUpActivityOptions.SPINNER_SHOW_INTENT);
            String defaultMovie = previousIntent.getStringExtra(SignUpActivityOptions.SPINNER_MOVIE_INTENT);

            user.put("defaultFeed", defaultFeed);
            user.put("defaultShow", defaultShow);
            user.put("defaultMovie", defaultMovie);

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
            } else {
                username = screeNameLower + String.valueOf(size + 1);
            }
            user.setUsername(username);


            // Signs a user up with this unique username
            user.signUpInBackground(signUpError -> {
                if (signUpError != null) {
                    Toast.makeText(this, SignUpErrorsUtil.errorMessage(signUpError.getCode()), Toast.LENGTH_SHORT).show();
                    return;
                }
                UserPublicColumns userPublicColumns = new UserPublicColumns();
                userPublicColumns.saveInBackground();
                userPublicColumns.setUserId(user.getObjectId());
                userPublicColumns.setGroupChatIds(new ArrayList<>());
                userPublicColumns.setWatchedContent(new ArrayList<>());
                userPublicColumns.saveInBackground();

                LogInUser.loginUser(SignUpActivityConfirmation.this, username, password, etFinalPassword);
            });
        });
    }
}