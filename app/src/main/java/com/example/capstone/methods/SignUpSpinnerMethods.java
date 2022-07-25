package com.example.capstone.methods;

import android.util.Log;

import com.example.capstone.activities.MovieSelectionActivity;
import com.example.capstone.activities.TVShowSelectionActivity;
import com.example.capstone.activities.feed.FeedActivity;

public class SignUpSpinnerMethods {

    public static String feedFilter(String selectedFeedFilter) {
        switch (selectedFeedFilter) {
            case "By date":
                return FeedActivity.ASCENDING_DATE;
            case "Popular":
                return FeedActivity.POPULAR_FILTER;
            case "Highly rated":
                return FeedActivity.RATING_FILTER;
            default:
                return "";
        }
    }


    public static String movieFilter(String selectedMovieFilter) {
        switch (selectedMovieFilter) {
            case "Popular":
                return MovieSelectionActivity.POPULAR_FILTER;
            case "Top Rated":
                return MovieSelectionActivity.TOP_RATED_FILTER;
            case "Now Playing":
                return MovieSelectionActivity.NOW_PLAYING_FILTER;
            default:
                return "";
        }
    }

    public static String showFilter (String selectedShowFilter) {
        switch (selectedShowFilter) {
            case "Popular":
                return TVShowSelectionActivity.POPULAR_FILTER;
            case "Top Rated":
                return TVShowSelectionActivity.TOP_RATED_FILTER;
            case "On Air":
                return TVShowSelectionActivity.ON_AIR;
            default:
                return "";
        }
    }
}
