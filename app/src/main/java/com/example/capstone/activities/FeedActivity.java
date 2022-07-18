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
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
                }
            }
            allEvents.addAll(events);
            allEvents.removeAll(deletedEvents);
            adapter.notifyDataSetChanged();
        });


        SubscriptionHandling<Event> subscriptionHandling = parseLiveQueryClient.subscribe(query);
        subscriptionHandling.handleSubscribe(q -> {
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Event>() {
                @Override
                public void onEvent(ParseQuery<Event> queried, Event updatedEvent) {
                    FeedActivity.this.runOnUiThread(() -> adapter.itemUpdated(updatedEvent) );
                }
            });

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (queried, createdEvent) -> {
                FeedActivity.this.runOnUiThread(() -> {
                    Toast.makeText(FeedActivity.this, createdEvent.getTitle() + " Created!", Toast.LENGTH_SHORT).show();

                    try {
                        TimeUnit.SECONDS.sleep(1);
                        rvEvents.smoothScrollToPosition(adapter.itemCreated(createdEvent));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

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


    }

}