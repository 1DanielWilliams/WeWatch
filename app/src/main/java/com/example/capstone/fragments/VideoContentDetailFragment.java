package com.example.capstone.fragments;

import static com.example.capstone.methods.BinarySearch.earliestDateInEvent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.R;
import com.example.capstone.activities.FeedActivity;
import com.example.capstone.activities.MovieSelectionActivity;
import com.example.capstone.activities.TVShowSelectionActivity;
import com.example.capstone.methods.BinarySearch;
import com.example.capstone.methods.PostEventMethods;
import com.example.capstone.methods.RemoveFromWishToWatch;
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import okhttp3.Headers;

public class VideoContentDetailFragment extends DialogFragment {


    private static final String ARG_PARAM1 = "videoContent";
    private Event event;

    private VideoContent videoContent;
    private ParseUser user;
    private AtomicReference<List<VideoContent>> wishToWatch;

    public VideoContentDetailFragment() {
        // Required empty public constructor
    }


    public static VideoContentDetailFragment newInstance(VideoContent videoContent) {
        VideoContentDetailFragment fragment = new VideoContentDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, videoContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoContent = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_content_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvRating = view.findViewById(R.id.tvRating);
        TextView tvOverview = view.findViewById(R.id.tvOverview);
        Button btnSelectDate = view.findViewById(R.id.btnSelectDate);
        Button btnPostEvent = view.findViewById(R.id.btnPostEvent);
        TextView tvDateBtn= view.findViewById(R.id.tvDateBtn);
        Button btnWatchLater = view.findViewById(R.id.btnWatchLater);

        try {
            user = ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String title = videoContent.getTitle();
        String posterUrl = videoContent.getPosterUrl();

        tvTitle.setText(title);
        tvRating.setText(Double.toString(videoContent.getVoteAverage() / 2.0d));
        tvOverview.setText(videoContent.getOverview());

        ImageView ivPoster = view.findViewById(R.id.ivPoster);
        int width_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 375, getContext().getResources().getDisplayMetrics());
        int height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 550, getContext().getResources().getDisplayMetrics());


        Glide.with(view).load(posterUrl)
                .override(width_px, height_px)
                .into(ivPoster);
        ivPoster.setColorFilter(Color.argb(60, 0, 0 , 0));


//        btnSelectDate.setOnClickListener(v -> onSelectDate(tvDateBtn, btnSelectDate, btnPostEvent) );
        btnSelectDate.setOnClickListener(v -> {
            event = new Event();
            PostEventMethods.onSelectDate(getActivity(), event, videoContent, tvDateBtn, btnSelectDate, btnPostEvent);
        });


        btnPostEvent.setOnClickListener(v -> postEvent() );

        //check if the user was already added it to their watch later
        wishToWatch = new AtomicReference<>();
        wishToWatch.set(user.getList("wishToWatch"));
        if (wishToWatch.get() == null) {
            wishToWatch.set(new ArrayList<>());
        }

        // determines if the user has already added this content to their wishToWatch
        AtomicBoolean inWishToWatch = new AtomicBoolean(PostEventMethods.isInWishToWatch(wishToWatch, videoContent));

        if (!inWishToWatch.get()) {
            btnWatchLater.setVisibility(View.VISIBLE);
        }

        btnWatchLater.setOnClickListener(v -> {
            wishToWatch.get().add(videoContent);
            user.put("wishToWatch", wishToWatch.get());
            user.saveInBackground();
            btnWatchLater.setVisibility(View.GONE);
        });


    }

    private void postEvent() {

        RemoveFromWishToWatch.removeContent(wishToWatch.get(), user, videoContent);

        // Query to determine if the video is currently listed on the feed tab
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo("title", event.getTitle());
        query.whereEqualTo("typeOfContent", event.getTypeOfContent());
        query.whereEqualTo("university", ParseUser.getCurrentUser().getString("university"));
        query.findInBackground((events, e) -> {

            if (e != null) {
                Log.e("fragment", "done: trouble finding event", e);
                return;
            }

            //new event
            if (events.size() == 0) {
                event.saveInBackground(e1 -> {
                    if (e1 != null) {
                        Log.e("fragment", "done: trouble finding event", e1);
                        return;
                    }
                    Intent i = new Intent(getContext(), FeedActivity.class);
                    startActivity(i);
                    this.dismiss();
                });
            } else {
                Event queriedEvent = events.get(0);

                try {
                    addToExistingEvent(queriedEvent);
                } catch (java.text.ParseException | ParseException ex) {
                    ex.printStackTrace();
                }

                queriedEvent.saveInBackground(e12 -> {
                    if (e12 != null) {
                        Log.e("VideoContentDetailFragment", "done: ERROR saving queried event", e12);
                        return;
                    }
                    Intent i = new Intent(getContext(), FeedActivity.class);
                    startActivity(i);
                    this.dismiss();
                });
            }
        });


    }

    // Adds the new user + time + interested User array to the existing event
    private void addToExistingEvent(Event queriedEvent) throws java.text.ParseException, ParseException {
        //increment the numPosted section of the VideoContent
        try {
            VideoContent videoContent = queriedEvent.getVideContent().fetch();
            videoContent.setNumPosted(videoContent.getNumPosted() + 1);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        //add to the sorted dates array
        List<String> dates = queriedEvent.getDates();
        String newDateStr = event.getDates().get(0);
        Date newDate = new SimpleDateFormat("MMM dd hh:mm aa yyyy", Locale.US).parse(newDateStr + " " + LocalDate.now().getYear());

        int userIndex =  BinarySearch.earliestDateInEvent(dates, newDate);

        if (userIndex == BinarySearch.DATE_EXIST) {
            Toast.makeText(getActivity(), "Date already Exist", Toast.LENGTH_SHORT).show();
            return;
        }

        dates.add(userIndex, newDateStr);


        queriedEvent.setDates(dates);

        //then add to the authors array
        List<ParseUser> authors = queriedEvent.getUsers();
        authors.add(userIndex, event.getUsers().get(0));
        queriedEvent.setUsers(authors);


        //add to the interested users array
        List<List<ParseUser>> interestedUsers = queriedEvent.getInterestedUsers();
        List<ParseUser> interestedUser = new ArrayList<>();
        interestedUser.add(event.getInterestedUsers().get(0).get(0));
        interestedUsers.add(userIndex, interestedUser);

        queriedEvent.setInterestedUsers(interestedUsers);

        // Updates the earliest date if needed
        if (event.getEarliestDate().before(queriedEvent.getEarliestDate())) {
            queriedEvent.setEarliestDate(event.getEarliestDate());
            queriedEvent.setEarliestUserIndex(0);
        }
    }

}