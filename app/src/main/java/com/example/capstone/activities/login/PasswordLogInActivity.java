package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.capstone.R;
import com.example.capstone.methods.LogInUser;
import com.example.capstone.methods.OnETChange;

public class PasswordLogInActivity extends AppCompatActivity {

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
            Intent i = new Intent(PasswordLogInActivity.this, logOrSignActivity.class);
            NavUtils.navigateUpTo(PasswordLogInActivity.this, i);
        });

        // authenticates user and logs them in
        btnLogin.setOnClickListener(v -> {
            String logIn = getIntent().getStringExtra("logIn");
            String password = etLoginPassword.getText().toString();

            LogInUser.loginUser(this, logIn, password, etLoginPassword);
        });
    }

}