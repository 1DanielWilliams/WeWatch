package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.adapters.MoviesAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Movie;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MovieSelectionActivity extends AppCompatActivity {

    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvMovies;
    private MoviesAdapter adapter;
    private List<Movie> allMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_selection);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvMovies = findViewById(R.id.rvMovies);

        toolbar.setContentInsetsAbsolute(0, 0);
        NavigationMethods.setUpNavDrawer(MovieSelectionActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);


        allMovies = new ArrayList<>();
        adapter = new MoviesAdapter(this, allMovies);

        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        // DO osmehting to add to the rv
    }



}