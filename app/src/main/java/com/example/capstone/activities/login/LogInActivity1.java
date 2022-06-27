package com.example.capstone.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone.R;

public class LogInActivity1 extends AppCompatActivity {
    
    private final String TAG = "LogInActivity1";

    private Button btnNextLogin1;
    private Button btnCancelLogin;
    private Toolbar toolbar;
    private EditText etLoginUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);


        btnNextLogin1 = findViewById(R.id.btnNextLogin1);
        btnCancelLogin = findViewById(R.id.btnCancelLogin1);
        etLoginUser = findViewById(R.id.etLoginUser);

        btnNextLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logIn = etLoginUser.getText().toString();

                if (!logIn.equals("")) {
                    Intent i = new Intent(LogInActivity1.this, LogInActivity2.class);
                    i.putExtra("logIn", logIn);
                    startActivity(i);
                } else {
                    Toast.makeText(LogInActivity1.this, "Enter a username or email", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnCancelLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(LogInActivity1.this);
            }
        });
    }
}