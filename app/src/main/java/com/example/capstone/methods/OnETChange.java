package com.example.capstone.methods;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.capstone.models.TypingDetail;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseUser;

import java.util.regex.Pattern;

public class OnETChange {

    public static void buttonToBlack(EditText et, Button btn) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn.setBackgroundColor(Color.argb(255, 0, 0, 0));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void nameVerification(EditText et, ImageView ivX, ImageView ivCheck) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    ivX.setVisibility(View.GONE);
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ivX.setVisibility(View.VISIBLE);
                    ivCheck.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static void emailVerification(EditText et, ImageView ivX, ImageView ivCheck) {
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(s.toString().endsWith(".edu") && s.toString().contains("@")) {
                    ivX.setVisibility(View.GONE);
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ivX.setVisibility(View.VISIBLE);
                    ivCheck.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void validatePassword(EditText et, ImageView ivX, ImageView ivCheck, Button btn) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                //if the password is less than 7 characters long, does not contain a number, or does not contain a character
                if (s.length() < 7 || !Pattern.matches(".*[0-9].*", s) || !Pattern.matches(".*[^a-zA-Z0-9].*", s)) {
                    ivX.setVisibility(View.VISIBLE);
                    ivCheck.setVisibility(View.GONE);
                    btn.setBackgroundColor(Color.argb(255, 128, 128, 128));

                } else {
                    ivX.setVisibility(View.GONE);
                    ivCheck.setVisibility(View.VISIBLE);
                    btn.setBackgroundColor(Color.argb(255, 0, 0, 0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public static void typingIndicator(FirebaseDatabase database, String groupDetailKey, EditText etMessageContent, ParseUser currUser) {
        DatabaseReference typingDetailRef = database.getReference("group_details/" + groupDetailKey + "/typing_detail");

        etMessageContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TypingDetail typingDetail = new TypingDetail(true, currUser.getObjectId(), currUser.getUsername(), currUser.getString("screenName"));
                typingDetailRef.setValue(typingDetail);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TypingDetail typingDetail = new TypingDetail(false);
                    typingDetailRef.setValue(typingDetail);
                }
            }
        });
    }
}
