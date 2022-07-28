package com.example.capstone.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.R;
import com.example.capstone.adapters.MoviesAdapter;
import com.example.capstone.methods.EndlessRecyclerViewScrollListener;
import com.example.capstone.methods.FetchingVideoContentMethods;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.methods.SearchVideoContentMethods;
import com.example.capstone.models.VideoContent;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Headers;

public class MovieSelectionActivity extends AppCompatActivity {

    public static final String TMDB_KEY = "61dda6141b919bc26c4c8a5d43de0b7e";
    public static final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_KEY + "&language=en-US&page=";


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
    public static final String POPULAR_FILTER = "popular";
    public static final String TOP_RATED_FILTER = "topRatedFilter";
    public static final String NOW_PLAYING_FILTER = "nowPlayingFilter";
    private AtomicReference<String> currFilter;
    private TextView tvFilterMovies;
    private ImageButton ibFilterMovies;
    private AtomicBoolean infiniteScroll;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_selection);

        currFilter = new AtomicReference<>(ParseUser.getCurrentUser().getString("defaultMovie"));
        infiniteScroll = new AtomicBoolean(true);
        ibFilterMovies = findViewById(R.id.ibFilterMovies);
        tvFilterMovies = findViewById(R.id.tvFilterMovies);
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

        if (Objects.equals(currFilter.get(), MovieSelectionActivity.POPULAR_FILTER)){
            FetchingVideoContentMethods.fetchMovies(POPULAR_URL, queriedMovies, allMovies, adapter, client,  1);
            tvFilterMovies.setText("Popular");
        } else if (Objects.equals(currFilter.get(), MovieSelectionActivity.NOW_PLAYING_FILTER)) {
            FetchingVideoContentMethods.fetchMovies(FetchingVideoContentMethods.NOW_PLAYING_URL_MOVIES, queriedMovies, allMovies, adapter, client, 1);
            tvFilterMovies.setText("Top Rated");
        } else if (Objects.equals(currFilter.get(), MovieSelectionActivity.TOP_RATED_FILTER)) {
            FetchingVideoContentMethods.fetchMovies(FetchingVideoContentMethods.TOP_RATED_URL_MOVIES, queriedMovies, allMovies, adapter, client, 1);
            tvFilterMovies.setText("Now Playing");
        }

        SearchVideoContentMethods.setUpSearchView(searchMovies, queriedMovies, allMovies, tvToolBarMovies, client, adapter, null, tvFilterMovies, ibFilterMovies, infiniteScroll);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (Objects.equals(currFilter.get(), MovieSelectionActivity.POPULAR_FILTER)){
                    FetchingVideoContentMethods.fetchMovies(POPULAR_URL, queriedMovies, allMovies, adapter, client, page + 1);
                } else if (Objects.equals(currFilter.get(), MovieSelectionActivity.NOW_PLAYING_FILTER)) {
                    FetchingVideoContentMethods.fetchMovies(FetchingVideoContentMethods.NOW_PLAYING_URL_MOVIES, queriedMovies, allMovies, adapter, client, page + 1);
                } else if (Objects.equals(currFilter.get(), MovieSelectionActivity.TOP_RATED_FILTER)) {
                    FetchingVideoContentMethods.fetchMovies(FetchingVideoContentMethods.TOP_RATED_URL_MOVIES, queriedMovies, allMovies, adapter, client, page + 1);
                }
            }
        };

        rvMovies.addOnScrollListener(scrollListener);

        ibFilterMovies.setOnClickListener(v -> FetchingVideoContentMethods.setUpFilterMenuMovies(this, tvToolBarMovies, queriedMovies, allMovies, adapter, client, tvFilterMovies, currFilter));
        tvFilterMovies.setOnClickListener(v -> FetchingVideoContentMethods.setUpFilterMenuMovies(this, tvToolBarMovies, queriedMovies, allMovies, adapter, client, tvFilterMovies, currFilter));
        tvToolBarMovies.setOnClickListener(v -> FetchingVideoContentMethods.setUpFilterMenuMovies(this, tvToolBarMovies, queriedMovies, allMovies, adapter, client, tvFilterMovies, currFilter));

        rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.setScrollUp(dy >= 0);
            }
        });
    }



}