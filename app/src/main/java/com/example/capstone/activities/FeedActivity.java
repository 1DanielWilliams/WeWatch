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
import android.widget.TextView;

import com.example.capstone.R;
import com.example.capstone.adapters.EventsAdapter;
import com.example.capstone.methods.DeletingEventsMethods;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Event;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private RecyclerView rvEvents;
    private EventsAdapter adapter;
    private List<Event> allEvents;
    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ParseLiveQueryClient parseLiveQueryClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(ParseUser.getCurrentUser().getString("university"));

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        rvEvents = findViewById(R.id.rvEvents);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);

        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(this, allEvents);

        rvEvents.setAdapter(adapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvEvents);

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.addAscendingOrder("earliestDate");
        query.whereEqualTo("university", ParseUser.getCurrentUser().getString("university"));
        query.setLimit(20);
        query.findInBackground((events, e) -> {
            if (e != null) {
                Log.e("FeedActivity", "done: ", e);
                return;
            }

            List<Event> deletedEvents = new ArrayList<>();
            for (Event event : events ) {
                List<String> dates = event.getDates();
                if (event.getEarliestDate().before(new Date(System.currentTimeMillis())) && dates.size() == 1) {

                    //removes groupID from the parse users
                    String groupChatID = (dates.get(0) + event.getTitle() + event.getTypeOfContent()).replace(".", "");
                    DeletingEventsMethods.removeChatId(groupChatID, event);
                    deletedEvents.add(event);
                    event.deleteInBackground();

                } else if (event.getEarliestDate().before(new Date(System.currentTimeMillis())) && dates.size() > 1) {

                    int indexToRemove = 0;

                    List<List<ParseUser>> interestedUsers = event.getInterestedUsers();
                    interestedUsers.remove(indexToRemove);
                    event.setInterestedUsers(interestedUsers);

                    List<ParseUser> authors = event.getUsers();
                    authors.remove(indexToRemove);
                    event.setUsers(authors);

                    //removes the group from the user
                    String groupChatID = (dates.get(0) + event.getTitle() + event.getTypeOfContent()).replace(".", "");
                    DeletingEventsMethods.removeChatId(groupChatID, event);


                    dates.remove(0);
                    event.setDates(dates);

                    //gets the new earliest date and updates earliestUserIndex
                    try {
                        event.setEarliestDate(new SimpleDateFormat("MMM dd HH:mm aa yyyy").parse(dates.get(0) + " 2022"));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    event.setEarliestUserIndex(0);
                    event.saveInBackground();

                }
            }
            allEvents.addAll(events);
            allEvents.removeAll(deletedEvents);
            adapter.notifyDataSetChanged();
        });




        drawerLayout = findViewById(R.id.drawerLayout);
        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        NavigationMethods.setUpNavDrawer(FeedActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);


    }

}