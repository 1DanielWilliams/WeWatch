package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.R;
import com.example.capstone.activities.FeedActivity;
import com.example.capstone.activities.logOrSignActivity;

public class LogInActivity2 extends AppCompatActivity {

    private Button btnCancelLogin2;
    private Button btnLogin2;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_login2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);


        btnCancelLogin2 = findViewById(R.id.btnCancelLogin2);
        btnLogin2 = findViewById(R.id.btnLogin2);

        btnCancelLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity2.this, logOrSignActivity.class);
                NavUtils.navigateUpTo(LogInActivity2.this, i);
            }
        });

        // authenticate user and log them in
        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity2.this, FeedActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}