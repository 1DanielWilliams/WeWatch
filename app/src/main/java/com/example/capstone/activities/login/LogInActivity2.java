package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.activities.FeedActivity;
import com.example.capstone.activities.logOrSignActivity;
import com.example.capstone.methods.LogInUser;
import com.example.capstone.methods.OnETChange;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LogInActivity2 extends AppCompatActivity {

    private Button btnCancelLogin;
    private Button btnLogin;
    private Toolbar toolbar;
    private EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_login2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);


        btnCancelLogin = findViewById(R.id.btnCancelLogin2);
        btnLogin = findViewById(R.id.btnLogin2);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        OnETChange.buttonToBlack(etLoginPassword, btnLogin);

        btnCancelLogin.setOnClickListener(v -> {
            Intent i = new Intent(LogInActivity2.this, logOrSignActivity.class);
            NavUtils.navigateUpTo(LogInActivity2.this, i);
        });

        // authenticates user and logs them in
        btnLogin.setOnClickListener(v -> {
            String logIn = getIntent().getStringExtra("logIn");
            String password = etLoginPassword.getText().toString();

            LogInUser.loginUser(this, logIn, password, etLoginPassword);
        });
    }

}