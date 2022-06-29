package com.example.capstone.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.R;
import com.example.capstone.adapters.MoviesAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.VideoContent;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MovieSelectionActivity extends AppCompatActivity {

    private final String TMDB_KEY = "61dda6141b919bc26c4c8a5d43de0b7e";  //getString(R.string.tmdb_api_key);
    private final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_KEY + "&language=en-US";

    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvMovies;
    private MoviesAdapter adapter;
    private List<VideoContent> allVideoContents;
    private AsyncHttpClient client;



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

        
        allVideoContents = new ArrayList<>();
        adapter = new MoviesAdapter(this, allVideoContents);

        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvMovies);

        client = new AsyncHttpClient();

        client.get(POPULAR_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // turn into a json object
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("MovieSelectionActivity", "Results: " + results.toString());

                    allVideoContents.addAll(VideoContent.fromJsonArray(results, "Movie"));
                    Log.i("MovieSelectionActivity", "onSuccess: " + allVideoContents.size());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("MovieSelectionActivity", "onSuccess: ", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("MovieSelectionActivity", "onFailure: ", throwable);
            }
        });
        // Do something to add to the rv
    }



}