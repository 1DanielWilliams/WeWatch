package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.R;

public class SignUpActivity1 extends AppCompatActivity {

    private Button btnNextSignup1;
    private Button btnCancelSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        btnNextSignup1 = findViewById(R.id.btnNextSignup1);
        btnCancelSignup = findViewById(R.id.btnCancelSignup);

        btnNextSignup1.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity1.this, SignUpActivity2.class);
            startActivity(i);
        });

        btnCancelSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SignUpActivity1.this);
            }
        });

    }
}