package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.capstone.R;

public class ConversationDetailActivity extends AppCompatActivity {
    private ImageButton upArrowProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_detail_conversation);

        upArrowProfile = findViewById(R.id.upArrowProfile);

        upArrowProfile.setOnClickListener(v -> finish());
    }
}