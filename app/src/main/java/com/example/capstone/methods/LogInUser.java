package com.example.capstone.methods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NavUtils;

import com.example.capstone.activities.feed.FeedActivity;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LogInUser {


    public static void loginUser(Context context, String logIn, String password, EditText etPassword) {

        if (password.equals("")) {
            Toast.makeText(context, "Enter a password", Toast.LENGTH_SHORT).show();
        }

        // Checks if a email was given
        if (logIn.indexOf('@') == -1) {
            loginWtihUsername(context, logIn, password, etPassword);
        } else {
            loginWithEmail(context, logIn, password, etPassword);
        }
    }

    // finds the username of a user by using their email
    public static void loginWithEmail(Context context, String logIn, String password, EditText etPassword) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", logIn);

        query.findInBackground((users, e) -> {
            if (e != null) {
                return;
            } else if (users.size() == 0) {
                Toast.makeText(context, "Incorrect email address", Toast.LENGTH_SHORT).show();
                etPassword.setText("");
                NavUtils.navigateUpFromSameTask((Activity) context);
                return;
            }

            String username = users.get(0).getUsername();
            loginWtihUsername(context, username, password, etPassword);
        });
    }


    public static void loginWtihUsername(Context context, String logIn, String password, EditText etPassword) {
        ParseUser.logInInBackground(logIn, password, (user, e) -> {
            // If the user inputs the wrong password
            if (e != null) {
                Toast.makeText(context, "Incorrect password and or email", Toast.LENGTH_SHORT).show();
                etPassword.setText("");
                return;
            }

            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, FeedActivity.class);
            context.startActivity(i);
            ((Activity) context).finish();
        });
    }
}
