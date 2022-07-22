package com.example.capstone.activities.feed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.adapters.EventsAdapter;
import com.example.capstone.methods.DeletingEventsMethods;
import com.example.capstone.methods.EndlessRecyclerViewScrollListener;
import com.example.capstone.methods.EventFeedMethods;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Event;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
    private  SubscriptionHandling<Event> subscriptionHandling;
    private ImageButton ibFilterFeed;
    private TextView tvFilterFeed;
    public final static String ASCENDING_DATE = "ascendingDate";
    public final static String POPULAR_FILTER = "popularFilter";
    public final static String RATING_FILTER = "ratingFilter";
    private AtomicReference<String> currFeedFilter;
    private EndlessRecyclerViewScrollListener scrollListener;
//    private ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        listen = true;
        currFeedFilter = new AtomicReference<>(ASCENDING_DATE);
        searchFeed = findViewById(R.id.searchFeed);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        ibFilterFeed = findViewById(R.id.ibFilterFeed);
        tvFilterFeed = findViewById(R.id.tvFilterFeed);
        toolbarTitle.setText(ParseUser.getCurrentUser().getString("university"));

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        rvEvents = findViewById(R.id.rvEvents);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);

        queriedEvents = new ArrayList<>();
        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(this, queriedEvents);

        rvEvents.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvEvents.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvEvents);

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.addAscendingOrder(Event.KEY_EARLIEST_DATE);
        query.whereEqualTo(Event.KEY_UNIVERSITY, ParseUser.getCurrentUser().getString("university"));
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
                                event.setNumInterested(event.getInterestedUsers().get(0).size());
                                event.saveInBackground();
                            } else {
                                break;
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }

                    }
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
                        FeedActivity.this.runOnUiThread(() -> {
                            adapter.itemUpdated(updatedEvent);
                        });
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
                FeedActivity.this.runOnUiThread(() -> Toast.makeText(FeedActivity.this, deletedEvent.getTitle() + " expired", Toast.LENGTH_SHORT).show());
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
            tvFilterFeed.setVisibility(View.GONE);
            ibFilterFeed.setVisibility(View.GONE);
        });

        searchFeed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queriedEvents.clear();
                //query for everything once
                if (query.length() > 0) {
                    for (Event event : allEvents) {
                        if (event.getTitle().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                            queriedEvents.add(event);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    queriedEvents.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        searchFeed.setOnCloseListener(() -> {
            toolbarTitle.setVisibility(View.VISIBLE);
            tvFilterFeed.setVisibility(View.VISIBLE);
            ibFilterFeed.setVisibility(View.VISIBLE);
            queriedEvents.addAll(allEvents);
            adapter.notifyDataSetChanged();
            return false;
        });


        ibFilterFeed.setOnClickListener(v -> currFeedFilter.set(EventFeedMethods.setupFilterMenu(this, toolbarTitle, queriedEvents, adapter, tvFilterFeed, currFeedFilter)));
        tvFilterFeed.setOnClickListener(v -> currFeedFilter.set(EventFeedMethods.setupFilterMenu(this, toolbarTitle, queriedEvents, adapter, tvFilterFeed, currFeedFilter)));
        toolbarTitle.setOnClickListener(v -> currFeedFilter.set(EventFeedMethods.setupFilterMenu(this, toolbarTitle, queriedEvents, adapter, tvFilterFeed, currFeedFilter)));

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //load from database
                EventFeedMethods.loadFromApi(queriedEvents, adapter, currFeedFilter.get());
            }
        };

        rvEvents.addOnScrollListener(scrollListener);

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

        //put code form
    }
}