package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.FeedActivity;
import com.example.capstone.R;
import com.example.capstone.activities.signup.SignUpActivity1;

public class LogInActivity1 extends AppCompatActivity {

    private Button btnNextLogin1;
    private Button btnCancelLogin1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        btnNextLogin1 = findViewById(R.id.btnNextLogin1);
        btnCancelLogin1 = findViewById(R.id.btnCancelLogin1);

        btnNextLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity1.this, LogInActivity2.class);
                startActivity(i);
            }
        });

        btnCancelLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(LogInActivity1.this);
            }
        });
    }
}