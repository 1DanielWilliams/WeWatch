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
                .placeholder(R.drawable.poster_no_image_available)
                .error(R.drawable.poster_no_image_available)
                .into(ivPoster);
        ivPoster.setColorFilter(Color.argb(60, 0, 0 , 0));


        btnSelectDate.setOnClickListener(v -> {
            event = new Event();
            PostEventMethods.onSelectDate(getActivity(), event, videoContent, tvDateBtn, btnSelectDate, btnPostEvent);
        });


        btnPostEvent.setOnClickListener(v -> {
            PostEventMethods.postEvent(getActivity(), event, wishToWatch, user, videoContent);
            this.dismiss();
        });

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

}