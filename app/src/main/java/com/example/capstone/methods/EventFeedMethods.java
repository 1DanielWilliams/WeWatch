package com.example.capstone.methods;

import android.content.Context;
import android.view.Gravity;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.capstone.R;
import com.example.capstone.activities.feed.FeedActivity;
import com.example.capstone.adapters.EventsAdapter;
import com.example.capstone.models.Event;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class EventFeedMethods {
    public static void loadFromApi(List<Event> queriedEvents, EventsAdapter adapter, String currFeedFilter) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo(Event.KEY_UNIVERSITY, ParseUser.getCurrentUser().getString("university"));
        if (Objects.equals(currFeedFilter, FeedActivity.ASCENDING_DATE)) {
            Date eventDate = queriedEvents.get(queriedEvents.size() - 1).getEarliestDate();
            query.whereGreaterThan(Event.KEY_EARLIEST_DATE, eventDate);
            query.addAscendingOrder(Event.KEY_EARLIEST_DATE);
        } else if (Objects.equals(currFeedFilter, FeedActivity.POPULAR_FILTER)) {
            int lowestNum = queriedEvents.get(queriedEvents.size() - 1).getNumInterested();
            query.whereLessThan(Event.KEY_NUM_INTERESTED, lowestNum);
            query.addDescendingOrder(Event.KEY_NUM_INTERESTED);
        } else if (currFeedFilter == FeedActivity.RATING_FILTER) {
            double lowestRating = queriedEvents.get(queriedEvents.size() - 1).getVoteAverage();
            query.whereLessThan(Event.KEY_VOTE_AVERAGE, lowestRating);
            query.addDescendingOrder(Event.KEY_VOTE_AVERAGE);
        }
        query.setLimit(20);
        query.findInBackground((events, e) -> {
            queriedEvents.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }

    public static String setupFilterMenu(Context context, TextView toolbarTitle, List<Event> queriedEvents, EventsAdapter adapter, TextView tvFilterFeed, final AtomicReference<String> currFeedFilter) {
        PopupMenu filterMenu  = new PopupMenu(context, toolbarTitle, Gravity.CENTER, 0, R.style.PopupMenuMoreCentralized); //todo want it to bein the middle
        filterMenu.getMenuInflater().inflate(R.menu.popup_menu_feed_filter, filterMenu.getMenu());
        filterMenu.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();

            if(itemID == R.id.optionPopular) {
                //if events is not on feed
                if (!Objects.equals(currFeedFilter.get(), FeedActivity.POPULAR_FILTER)) {
                    // query for popular filter
                    ParseQuery<Event> query12 = ParseQuery.getQuery(Event.class);
                    query12.whereEqualTo(Event.KEY_UNIVERSITY, ParseUser.getCurrentUser().getString("university"));
                    query12.addDescendingOrder(Event.KEY_NUM_INTERESTED);
                    query12.setLimit(20);
                    query12.findInBackground((events, e) -> {
                        queriedEvents.clear();
                        queriedEvents.addAll(events);
                        adapter.notifyDataSetChanged();
                        tvFilterFeed.setText("Popular");
                    });
                    currFeedFilter.set(FeedActivity.POPULAR_FILTER);
                }

                return true;
            } else if (itemID == R.id.optionAscendingDate) {
                if (!Objects.equals(currFeedFilter.get(), FeedActivity.ASCENDING_DATE)) {
                    ParseQuery<Event> query12 = ParseQuery.getQuery(Event.class);
                    query12.whereEqualTo(Event.KEY_UNIVERSITY, ParseUser.getCurrentUser().getString("university"));
                    query12.addAscendingOrder(Event.KEY_EARLIEST_DATE);
                    query12.setLimit(20);
                    query12.findInBackground((events, e) -> {
                        queriedEvents.clear();
                        queriedEvents.addAll(events);
                        adapter.notifyDataSetChanged();
                        tvFilterFeed.setText("By date");
                    });
                    currFeedFilter.set(FeedActivity.ASCENDING_DATE);
                }
                return true;
            } else if (itemID == R.id.optionRating) {
                if (!Objects.equals(currFeedFilter.get(), FeedActivity.RATING_FILTER)) {
                    // query for ratings filter
                    ParseQuery<Event> query12 = ParseQuery.getQuery(Event.class);
                    query12.whereEqualTo(Event.KEY_UNIVERSITY, ParseUser.getCurrentUser().getString("university"));
                    query12.addDescendingOrder(Event.KEY_VOTE_AVERAGE);
                    query12.setLimit(20);
                    query12.findInBackground((events, e) -> {
                        queriedEvents.clear();
                        queriedEvents.addAll(events);
                        adapter.notifyDataSetChanged();
                        tvFilterFeed.setText("By rating");
                    });
                    currFeedFilter.set(FeedActivity. RATING_FILTER);
                }
                return true;
            }
            return false;
        });
        filterMenu.show();

        return currFeedFilter.get();
    }


}
