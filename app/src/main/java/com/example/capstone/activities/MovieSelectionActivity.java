package com.example.capstone.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.example.capstone.R;
import com.example.capstone.adapters.MoviesAdapter;
import com.example.capstone.methods.EndlessRecyclerViewScrollListener;
import com.example.capstone.methods.FetchingVideoContentMethods;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.methods.SearchVideoContentMethods;
import com.example.capstone.models.VideoContent;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MovieSelectionActivity extends AppCompatActivity {

    public static final String TMDB_KEY = "61dda6141b919bc26c4c8a5d43de0b7e";  //getString(R.string.tmdb_api_key);
    private final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_KEY + "&language=en-US&page=";


    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvMovies;
    private MoviesAdapter adapter;
    private List<VideoContent> queriedMovies;
    private List<VideoContent> allMovies;
    private AsyncHttpClient client;
    private SearchView searchMovies;
    private TextView tvToolBarMovies;
    private EndlessRecyclerViewScrollListener scrollListener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_selection);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvMovies = findViewById(R.id.rvMovies);
        searchMovies = findViewById(R.id.searchMovies);
        tvToolBarMovies = findViewById(R.id.tvToolBarMovies);

        toolbar.setContentInsetsAbsolute(0, 0);
        NavigationMethods.setUpNavDrawer(MovieSelectionActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

        allMovies = new ArrayList<>();
        queriedMovies = new ArrayList<>();
        adapter = new MoviesAdapter(this, queriedMovies);

        rvMovies.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMovies.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvMovies);

        client = new AsyncHttpClient();
        FetchingVideoContentMethods.fetchMovies(POPULAR_URL, queriedMovies, allMovies, adapter, client, 1);

        SearchVideoContentMethods.setUpSearchView(searchMovies, queriedMovies, allMovies, tvToolBarMovies, client, adapter, null);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                FetchingVideoContentMethods.fetchMovies(POPULAR_URL, queriedMovies, allMovies, adapter, client, page + 1);
            }
        };

        rvMovies.addOnScrollListener(scrollListener);
    }



}