package com.example.capstone.methods;

import com.example.capstone.activities.feed.FeedActivity;
import com.example.capstone.adapters.EventsAdapter;
import com.example.capstone.models.Event;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventFeedMethods {
    public static void loadFromApi(Date eventDate, List<Event> queriedEvents, EventsAdapter adapter, String currFeedFilter) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereGreaterThan(Event.KEY_EARLIEST_DATE, eventDate);
        if (Objects.equals(currFeedFilter, FeedActivity.ASCENDING_DATE)) {
            query.addAscendingOrder(Event.KEY_EARLIEST_DATE);
        } //todo other conditions
        query.setLimit(20);
        query.findInBackground((events, e) -> {
            queriedEvents.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }
}
