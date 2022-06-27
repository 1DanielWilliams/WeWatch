package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.capstone.R;

public class SignUpActivity1 extends AppCompatActivity {

    private final String TAG = "SignUpActivity1";


    private Button btnNextSignup1;
    private Button btnCancelSignup;
    private Toolbar toolbar;
    private EditText etSignName;
    private EditText etSignEmail;
    private EditText etSignPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        btnNextSignup1 = findViewById(R.id.btnNextSignup1);
        btnCancelSignup = findViewById(R.id.btnCancelSignup);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        btnNextSignup1.setOnClickListener(v -> {
            signUserUp();
        });

        btnCancelSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SignUpActivity1.this);
            }
        });



    }

    private void signUserUp() {
        etSignName = findViewById(R.id.etFinalName);
        etSignEmail = findViewById(R.id.etFinalEmail);
        etSignPassword = findViewById(R.id.etFinalPassword);

        String screenName = etSignName.getText().toString(); //TODO: green check stuff
        String password = etSignPassword.getText().toString(); // TODO: green check stuf
        String email = etSignEmail.getText().toString(); // TODO: must be .edu email (cna demonstrate this with green check

        Intent i = new Intent(SignUpActivity1.this, SignUpActivity2.class);
        i.putExtra("screenName", screenName);
        i.putExtra("password", password);
        i.putExtra("email", email);

        startActivity(i);

    }
}