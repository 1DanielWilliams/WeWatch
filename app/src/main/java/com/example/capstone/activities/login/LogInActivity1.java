package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.R;

public class LogInActivity1 extends AppCompatActivity {

    private Button btnNextLogin1;
    private Button btnCancelLogin1;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);


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