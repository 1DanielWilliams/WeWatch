package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.capstone.R;
import com.example.capstone.methods.OnETChange;

import java.util.regex.Pattern;

public class SignUpActivityCredentials extends AppCompatActivity {

    private final String TAG = "SignUpActivity1";


    private Button btnNextSignup;
    private Button btnCancelSignup;
    private Toolbar toolbar;
    private EditText etSignName;
    private EditText etSignEmail;
    private EditText etSignPassword;
    private ImageView ivXName;
    private ImageView ivCheckmarkName;

    private ImageView ivXEmail;
    private ImageView ivCheckmarkEmail;
    private ImageView ivXPassword;
    private ImageView ivCheckmarkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_credentials);

//        SignUpActivityCredentials.this.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        btnNextSignup = findViewById(R.id.btnSelectDate);
        btnCancelSignup = findViewById(R.id.btnCancelSignup);
        ivXName = findViewById(R.id.ivXName);
        ivCheckmarkName = findViewById(R.id.ivCheckmarkName);
        ivXEmail = findViewById(R.id.ivXEmail);
        ivCheckmarkEmail = findViewById(R.id.ivCheckmarkEmail);
        etSignEmail = findViewById(R.id.etSignEmail);
        etSignName = findViewById(R.id.etSignName);
        etSignPassword = findViewById(R.id.etSignPassword);
        ivXPassword = findViewById(R.id.ivXPassword);
        ivCheckmarkPassword = findViewById(R.id.ivCheckmarkPassword);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        btnNextSignup.setOnClickListener(v -> {
            signUserUp();
        });

        btnCancelSignup.setOnClickListener(v -> NavUtils.navigateUpFromSameTask(SignUpActivityCredentials.this));

        OnETChange.emailVerification(etSignEmail, ivXEmail, ivCheckmarkEmail);
        OnETChange.nameVerification(etSignName, ivXName, ivCheckmarkName);
        OnETChange.validatePassword(etSignPassword, ivXPassword, ivCheckmarkPassword, btnNextSignup);

    }

    private void signUserUp() {
        etSignName = findViewById(R.id.etSignName);
        etSignEmail = findViewById(R.id.etSignEmail);
        etSignPassword = findViewById(R.id.etSignPassword);

        String screenName = etSignName.getText().toString();
        String password = etSignPassword.getText().toString(); // TODO: green check stuf
        String email = etSignEmail.getText().toString();

        boolean invalidPassword = password.length() < 7 || !Pattern.matches(".*[0-9].*", password) || !Pattern.matches(".*[^a-zA-Z0-9].*", password);
        if(!email.endsWith(".edu") ) {
            Toast.makeText(this, "Enter a .edu email", Toast.LENGTH_SHORT).show();
            etSignEmail.setText("");
            return;
        } else if (screenName.length() == 0) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (invalidPassword) {
            Toast.makeText(this, "Password must be at least 8 characters, contain a number, and a special character", Toast.LENGTH_SHORT).show();
            etSignPassword.setText("");
            return;
        }


        Intent i = new Intent(SignUpActivityCredentials.this, SignUpActivityOptions.class);
        i.putExtra("screenName", screenName);
        i.putExtra("password", password);
        i.putExtra("email", email);

        startActivity(i);


    }
}