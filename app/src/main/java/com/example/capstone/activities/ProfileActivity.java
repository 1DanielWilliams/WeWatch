package com.example.capstone.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.capstone.R;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton upArrowProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_profile);

        upArrowProfile = findViewById(R.id.upArrowProfile);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        upArrowProfile.setOnClickListener(v -> {
            finish();
        });

    }
}