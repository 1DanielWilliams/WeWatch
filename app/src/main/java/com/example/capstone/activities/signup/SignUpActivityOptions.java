package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.capstone.R;

public class SignUpActivityOptions extends AppCompatActivity {
    private final String TAG = "SignUpActivity2";


    private Toolbar toolbar;
    private Button btnNextCreate;
    private ImageButton upArrowSign2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_options);

        upArrowSign2 = findViewById(R.id.upArrowSign2);
        upArrowSign2.setOnClickListener(v -> {
            NavUtils.navigateUpFromSameTask(this);
        });


        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);


        btnNextCreate = findViewById(R.id.btnNextCreate);

        btnNextCreate.setOnClickListener(v -> {

            String screenName = getIntent().getStringExtra("screenName");
            String password = getIntent().getStringExtra("password");
            String email = getIntent().getStringExtra("email");

            Intent i = new Intent(SignUpActivityOptions.this, SignUpActivityConfirmation.class);
            i.putExtra("screenName", screenName);
            i.putExtra("password", password);
            i.putExtra("email", email);
            startActivity(i);

        });

    }

}