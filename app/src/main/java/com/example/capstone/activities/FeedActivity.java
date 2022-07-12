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
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Event;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class FeedActivity extends AppCompatActivity {
    private RecyclerView rvEvents;
    private EventsAdapter adapter;
    private List<Event> allEvents;
    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(ParseUser.getCurrentUser().getString("university"));

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

            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                List<String> dates = event.getDates();
                if (event.getEarliestDate().before(new Date(System.currentTimeMillis())) && dates.size() == 1) {
                    //removes groupID from the parse users
                    String groupChatID = (event.getDates().get(event.getEarliestUserIndex()) + event.getTitle() + event.getTypeOfContent()).replace(".", "");
                    ParseQuery<ParseUser> parseUserQuery = ParseUser.getQuery();
                    List<String> groupChatIDs = new ArrayList<>();
                    groupChatIDs.add(groupChatID);
                    parseUserQuery.whereContainedIn("groupChatID", groupChatIDs);
                    parseUserQuery.findInBackground((users, e1) -> users.forEach(parseUser -> {
                        List<String> IDs = parseUser.getList("groupChatID");
                        IDs.remove(IDs.indexOf(groupChatID));
                        parseUser.put("groupChatID", IDs);
                        parseUser.saveInBackground();
                    }));


                    events.remove(i);
                    event.deleteInBackground();
                    i--;
                    Log.i("feedActivity", "onCreate: " + event.getEarliestDate().toString());

                } else if (event.getEarliestDate().before(new Date(System.currentTimeMillis())) && dates.size() > 1) {
                    //find the next earliest date and update the earliestuserIndex + earliest date
                    int dateSize = dates.size();
                    List<Date> dateList = new ArrayList<>();
                    for (int j = 0; j < dateSize; j ++) {
                        String dateStr = dates.get(i);
                        try {
                            Date date = new SimpleDateFormat("MMM dd HH:mm aa yyyy").parse(dateStr + " 2022");
                            dateList.add(date);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                    //removes old entry of date from event
                    Date minDate = Collections.min(dateList);
                    int indexToRemove = dateList.indexOf(minDate);
                    dateList.remove(indexToRemove);

                    List<List<ParseUser>> interestedUsers = event.getInterestedUsers();
                    interestedUsers.remove(indexToRemove);
                    event.setInterestedUsers(interestedUsers);

                    dates.remove(indexToRemove);
                    event.setDates(dates);

                    List<ParseUser> authors = event.getUsers();
                    authors.remove(indexToRemove);
                    event.setUsers(authors);

                    //finds the new earliest date and updates earliestUserIndex
                    minDate = Collections.min(dateList);
                    int earliestUserIndex = dateList.indexOf(minDate);
                    event.setEarliestDate(minDate);
                    event.setEarliestUserIndex(earliestUserIndex);

                    event.saveInBackground();

                    //removes groupID from the parse users
                    String groupChatID = (event.getDates().get(event.getEarliestUserIndex()) + event.getTitle() + event.getTypeOfContent()).replace(".", "");
                    ParseQuery<ParseUser> parseUserQuery = ParseUser.getQuery();
                    List<String> groupChatIDs = new ArrayList<>();
                    groupChatIDs.add(groupChatID);
                    parseUserQuery.whereContainedIn("groupChatID", groupChatIDs);
                    parseUserQuery.findInBackground((users, e1) -> users.forEach(parseUser -> {
                        List<String> IDs = parseUser.getList("groupChatID");
                        IDs.remove(IDs.indexOf(groupChatID));
                        parseUser.put("groupChatID", IDs);
                        parseUser.saveInBackground();
                    }));

                }
            }
            allEvents.addAll(events);
            adapter.notifyDataSetChanged();
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        NavigationMethods.setUpNavDrawer(FeedActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);


    }
}