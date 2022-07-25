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
import com.example.capstone.adapters.TVshowsAdapter;
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
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Headers;

public class TVShowSelectionActivity extends AppCompatActivity {

    public static final String TMDB_KEY = "61dda6141b919bc26c4c8a5d43de0b7e";
    public static final String POPULAR_URL = "https://api.themoviedb.org/3/tv/popular?api_key=" + TMDB_KEY + "&language=en-US&page=";

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
    public static final String POPULAR_FILTER = "popular";
    public static final String TOP_RATED_FILTER = "topRatedFilter";
    public static final String ON_AIR = "onAirFilter";
    private AtomicReference<String> currFilter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private TextView tvFilterShows;
    private ImageButton ibFilterShows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_tvshow_selection);

        currFilter = new AtomicReference<>(ParseUser.getCurrentUser().getString("defaultShow"));
        tvFilterShows = findViewById(R.id.tvFilterShows);
        ibFilterShows = findViewById(R.id.ibFilterShows);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTVshows.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvTVshows);

        client = new AsyncHttpClient();

        if(Objects.equals(currFilter.get(), TVShowSelectionActivity.POPULAR_FILTER)) {
            FetchingVideoContentMethods.fetchTvShows(client, POPULAR_URL, queriedTVShows, allTVShows, adapter, 1);
            tvFilterShows.setText("Popular");
        } else if (Objects.equals(currFilter.get(), TVShowSelectionActivity.ON_AIR)) {
            FetchingVideoContentMethods.fetchTvShows(client, FetchingVideoContentMethods.ON_AIR_URL_SHOWS, queriedTVShows, allTVShows, adapter, 1);
            tvFilterShows.setText("Top Rated");
        } else if (Objects.equals(currFilter.get(), TVShowSelectionActivity.TOP_RATED_FILTER)) {
            FetchingVideoContentMethods.fetchTvShows(client, FetchingVideoContentMethods.TOP_RATED_URL_SHOWS, queriedTVShows, allTVShows, adapter, 1);
            tvFilterShows.setText("On Air");
        }

        SearchVideoContentMethods.setUpSearchView(searchTV, queriedTVShows, allTVShows, tvToolBarTVShows, client, null, adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(Objects.equals(currFilter.get(), TVShowSelectionActivity.POPULAR_FILTER)) {
                    FetchingVideoContentMethods.fetchTvShows(client, POPULAR_URL, queriedTVShows, allTVShows, adapter, page + 1);
                } else if (Objects.equals(currFilter.get(), TVShowSelectionActivity.ON_AIR)) {
                    FetchingVideoContentMethods.fetchTvShows(client, FetchingVideoContentMethods.ON_AIR_URL_SHOWS, queriedTVShows, allTVShows, adapter, page + 1);
                } else if (Objects.equals(currFilter.get(), TVShowSelectionActivity.TOP_RATED_FILTER)) {
                    FetchingVideoContentMethods.fetchTvShows(client, FetchingVideoContentMethods.TOP_RATED_URL_SHOWS, queriedTVShows, allTVShows, adapter, page + 1);
                }
            }
        };
        rvTVshows.addOnScrollListener(scrollListener);

        ibFilterShows.setOnClickListener(v -> FetchingVideoContentMethods.setUpFilterMenuShows(this, tvToolBarTVShows, currFilter, queriedTVShows, tvFilterShows, client, allTVShows, adapter));
        tvFilterShows.setOnClickListener(v -> FetchingVideoContentMethods.setUpFilterMenuShows(this, tvToolBarTVShows, currFilter, queriedTVShows, tvFilterShows, client, allTVShows, adapter));
        tvToolBarTVShows.setOnClickListener(v -> FetchingVideoContentMethods.setUpFilterMenuShows(this, tvToolBarTVShows, currFilter, queriedTVShows, tvFilterShows, client, allTVShows, adapter));

    }
}