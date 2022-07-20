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
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.R;
import com.example.capstone.adapters.TVshowsAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.methods.SearchVideoContentMethods;
import com.example.capstone.models.VideoContent;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TVShowSelectionActivity extends AppCompatActivity {

    private final String TMDB_KEY = "61dda6141b919bc26c4c8a5d43de0b7e";  //getString(R.string.tmdb_api_key);
    private final String POPULAR_URL = "https://api.themoviedb.org/3/tv/popular?api_key=" + TMDB_KEY + "&language=en-US";

    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvTVshows;
    private TVshowsAdapter adapter;
    private List<VideoContent> allTVShows;
    private List<VideoContent> queriedTVShows;
    private AsyncHttpClient client;
    private SearchView searchTV;
    private TextView tvToolBarTVShows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_tvshow_selection);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvTVshows = findViewById(R.id.rvTVshows);
        searchTV = findViewById(R.id.searchTV);
        tvToolBarTVShows = findViewById(R.id.tvToolBarTVShows);

        toolbar.setContentInsetsAbsolute(0, 0);

        allTVShows = new ArrayList<>();
        queriedTVShows = new ArrayList<>();
        adapter = new TVshowsAdapter(this, queriedTVShows);

        NavigationMethods.setUpNavDrawer(TVShowSelectionActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

        rvTVshows.setAdapter(adapter);
        rvTVshows.setLayoutManager(new LinearLayoutManager(this));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvTVshows);

        client = new AsyncHttpClient();
        client.get(POPULAR_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i("MovieSelectionActivity", "Results: " + results.toString());
                    List<VideoContent> tvShows = VideoContent.fromJsonArray(results, "TV Show");
                    queriedTVShows.addAll(tvShows);
                    allTVShows.addAll(tvShows);
                    int size = queriedTVShows.size();
                    for (int i = 0; i < size; i++) {
                        VideoContent tvShow = queriedTVShows.get(i);
                        int id = tvShow.getTmdbID();
                        String watchProvidersUrl = "https://api.themoviedb.org/3/tv/" + id + "/watch/providers?api_key=" + MovieSelectionActivity.TMDB_KEY;
                        client.get(watchProvidersUrl, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject object = json.jsonObject;
                                List<String> platforms = new ArrayList<>();
                                try {
                                    JSONArray results = object.getJSONObject("results").getJSONObject("US").getJSONArray("flatrate");
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject platform = results.getJSONObject(i);
//                                        Log.i("VideoContent", "onSuccess: " + platform.get("provider_name").toString());
                                        platforms.add(platform.get("provider_name").toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                tvShow.setPlatforms(platforms);

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("TVShowselectionActivity", "onFailure: ", throwable);
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("MovieSelectionActivity", "onSuccess: ", e);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("TVShowSelectionActivity", "onFailure: ", throwable);
            }
        });
        SearchVideoContentMethods.setUpSearchView(searchTV, queriedTVShows, allTVShows, tvToolBarTVShows, client, null, adapter);


    }
}