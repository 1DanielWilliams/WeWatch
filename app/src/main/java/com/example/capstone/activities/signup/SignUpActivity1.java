package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.R;

public class SignUpActivity1 extends AppCompatActivity {

    private Button btnNextCreate1;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        btnNextCreate1 = findViewById(R.id.btnNextCreate1);
        btnCancel = findViewById(R.id.btnCancel);

        btnNextCreate1.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity1.this, SignUpActivity2.class);
            startActivity(i);
        });

//        btnCancel.setStateListAnimator(null);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SignUpActivity1.this);
            }
        });

    }
}