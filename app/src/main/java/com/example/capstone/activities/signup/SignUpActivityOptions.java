package com.example.capstone.activities.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.capstone.R;
import com.example.capstone.activities.feed.FeedActivity;
import com.example.capstone.methods.SignUpSpinnerMethods;

public class SignUpActivityOptions extends AppCompatActivity {
    private final String TAG = "SignUpActivity2";
    public static final String SPINNER_FEED_INTENT = "spinnerFeed";
    public static final String SPINNER_MOVIE_INTENT = "spinnerMovie";
    public static final String SPINNER_SHOW_INTENT = "spinnerShow";




    private Toolbar toolbar;
    private Button btnNextCreate;
    private ImageButton upArrowSign2;
    private Spinner spinnerFeed;
    private Spinner spinnerMovie;
    private Spinner spinnerShows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_options);

        spinnerFeed = findViewById(R.id.spinnerFeed);
        spinnerMovie = findViewById(R.id.spinnerMovie);
        spinnerShows = findViewById(R.id.spinnerShow);

        ArrayAdapter<CharSequence> adapterFeed = ArrayAdapter.createFromResource(this, R.array.spinnerFeed, android.R.layout.simple_spinner_item);
        adapterFeed.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerFeed.setAdapter(adapterFeed);

        ArrayAdapter<CharSequence> adapterMovie = ArrayAdapter.createFromResource(this, R.array.spinnerMovie, android.R.layout.simple_spinner_item);
        adapterMovie.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerMovie.setAdapter(adapterMovie);


        ArrayAdapter<CharSequence> adapterShows = ArrayAdapter.createFromResource(this, R.array.spinnerShows, android.R.layout.simple_spinner_item);
        adapterShows.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerShows.setAdapter(adapterShows);

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

            i.putExtra(SPINNER_FEED_INTENT, SignUpSpinnerMethods.feedFilter(spinnerFeed.getSelectedItem().toString()));
            i.putExtra(SPINNER_MOVIE_INTENT, SignUpSpinnerMethods.movieFilter(spinnerMovie.getSelectedItem().toString()));
            i.putExtra(SPINNER_SHOW_INTENT, SignUpSpinnerMethods.showFilter(spinnerShows.getSelectedItem().toString()));

            
            startActivity(i);

        });

    }

}