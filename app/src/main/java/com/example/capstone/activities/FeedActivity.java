package com.example.capstone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.adapters.EventsAdapter;
import com.example.capstone.methods.DeletingEventsMethods;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Event;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FeedActivity extends AppCompatActivity {
    private boolean listen;
    private RecyclerView rvEvents;
    private EventsAdapter adapter;
    private List<Event> queriedEvents;
    private List<Event> allEvents;
    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ParseLiveQueryClient parseLiveQueryClient;
    private SearchView searchFeed;
    private NavigableMap<String, Event> allEventsMap;
    private  SubscriptionHandling<Event> subscriptionHandling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        listen = true;
        searchFeed = findViewById(R.id.searchFeed);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(ParseUser.getCurrentUser().getString("university"));

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        rvEvents = findViewById(R.id.rvEvents);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);

        allEventsMap = new TreeMap<>();
        queriedEvents = new ArrayList<>();
        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(this, queriedEvents);

        rvEvents.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvEvents.setLayoutManager(linearLayoutManager);

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
            Date currDate = new Date(System.currentTimeMillis());
            for (Event event : events ) {
                List<String> dates = event.getDates();
                if (event.getEarliestDate().before(currDate) && dates.size() == 1) {

                    //removes groupID from the parse users
                    String groupChatID = (dates.get(0) + event.getTitle() + event.getTypeOfContent()).replace(".", "");
                    DeletingEventsMethods.removeChatId(groupChatID, event);
                    deletedEvents.add(event);
                    event.deleteInBackground();

                } else if (event.getEarliestDate().before(currDate) && dates.size() > 1) {
                    // serial search through each date in dates
                        // checks if the date is invalid if so does this, on else break
                    int currYear = LocalDate.now().getYear();
                    for (String dateStr : dates) {
                        try {
                            Date date = new SimpleDateFormat("MMM dd hh:mm aa yyyy").parse(dateStr + " " + currYear);
                            if (date.before(currDate)) {
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
                                    event.setEarliestDate(new SimpleDateFormat("MMM dd hh:mm aa yyyy").parse(dates.get(0) + " " + currYear));
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }

                                event.setEarliestUserIndex(0);
                                event.saveInBackground();
                            } else {
                                break;
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }

                    }
                } else { // not deleting the event
                    allEventsMap.put(event.getTitle().toLowerCase(Locale.ROOT), event);
                }
            }
            allEvents.addAll(events);
            queriedEvents.addAll(events);
            queriedEvents.removeAll(deletedEvents);
            adapter.notifyDataSetChanged();
        });


        subscriptionHandling = parseLiveQueryClient.subscribe(query);
        subscriptionHandling.handleSubscribe(q -> {
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Event>() {
                @Override
                public void onEvent(ParseQuery<Event> queried, Event updatedEvent) {
                    if (listen) {
                        FeedActivity.this.runOnUiThread(() -> adapter.itemUpdated(updatedEvent) );
                    }
                }
            });

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (queried, createdEvent) -> {
                if (listen) {
                    FeedActivity.this.runOnUiThread(() -> {
                        Toast.makeText(FeedActivity.this, createdEvent.getTitle() + " Created!", Toast.LENGTH_SHORT).show();

                        try {
                            TimeUnit.SECONDS.sleep(1);
                            // todo, do not automatically scroll: add button to scroll to event
                            rvEvents.smoothScrollToPosition(adapter.itemCreated(createdEvent));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, (query1, deletedEvent) -> {
                Toast.makeText(FeedActivity.this, deletedEvent.getTitle() + " expired", Toast.LENGTH_SHORT).show();
            });

        });


        drawerLayout = findViewById(R.id.drawerLayout);
        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        NavigationMethods.setUpNavDrawer(FeedActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

        searchFeed.setQueryHint("Start typing");

        searchFeed.setOnSearchClickListener(v -> {
            queriedEvents.clear();
            adapter.notifyDataSetChanged();
            toolbarTitle.setVisibility(View.GONE);
        });

        searchFeed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queriedEvents.clear();
                //query for everything once
                if (newText.length() > 0) {
                    Collection<Event> events = getByPrefix(allEventsMap, newText.toLowerCase(Locale.ROOT)).values();
                    // todo if empty display something
                    queriedEvents.addAll(events);
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        searchFeed.setOnCloseListener(() -> {
            toolbarTitle.setVisibility(View.VISIBLE);
            queriedEvents.addAll(allEvents);
            adapter.notifyDataSetChanged();
            return false;
        });

    }

    public SortedMap<String, Event> getByPrefix(NavigableMap<String, Event> myMap, String prefix ) {
        return myMap.subMap( prefix, prefix + Character.MAX_VALUE );
    }

    @Override
    protected void onPause() {
        super.onPause();
        listen = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listen = true;
    }
}