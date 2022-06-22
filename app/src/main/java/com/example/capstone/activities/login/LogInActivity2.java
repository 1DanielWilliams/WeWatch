package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.R;
import com.example.capstone.activities.logOrSignActivity;

public class LogInActivity2 extends AppCompatActivity {

    private Button btnCancelLogin2;
    private Button btnLogin2; // SIGN UP BUTTON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_login2);

        btnCancelLogin2 = findViewById(R.id.btnCancelLogin2);
        btnLogin2 = findViewById(R.id.btnLogin2);

        btnCancelLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity2.this, logOrSignActivity.class);
                startActivity(i);
            }
        });

        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity2.this, logOrSignActivity.class);

            }
        });
    }
}