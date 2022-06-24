package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.adapters.TVshowsAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Movie;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class TVShowSelectionActivity extends AppCompatActivity {

    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvTVshows;
    private TVshowsAdapter adapter;
    private List<Movie> allTVShows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_tvshow_selection);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvTVshows = findViewById(R.id.rvTVshows);

        toolbar.setContentInsetsAbsolute(0, 0);


        allTVShows = new ArrayList<>();
        adapter = new TVshowsAdapter(this, allTVShows);

        NavigationMethods.setUpNavDrawer(TVShowSelectionActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

        rvTVshows.setAdapter(adapter);
        rvTVshows.setLayoutManager(new LinearLayoutManager(this));

        // add something to the layout
    }
}