package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.adapters.EventsAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Event;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private RecyclerView rvEvents;
    private EventsAdapter adapter;
    private List<Event> allEvents;
    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        rvEvents = findViewById(R.id.rvEvents);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);

        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(this, allEvents);

        rvEvents.setAdapter(adapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // TODO: do someting to get all the events
        // add it to the list and notify the adapter

        drawerLayout = findViewById(R.id.drawerLayout);
        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        NavigationMethods.setUpNavDrawer(FeedActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);
//        navDrawerFeed.setCheckedItem(R.id.optFeed);

    }
}