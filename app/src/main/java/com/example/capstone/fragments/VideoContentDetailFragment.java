package com.example.capstone.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.transition.TransitionInflater;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.methods.PostEventMethods;
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
//
        TransitionInflater inflater = TransitionInflater.from(requireContext());
//        setEnterTransition(inflater.inflateTransition(R.transition.fade));
//        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_content_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getDialog().getWindow().getAttributes().windowAnimations = R.transition.slide_in_right;
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
        ImageButton ibBackContentDetail = view.findViewById(R.id.ibBackContentDetail);

        ibBackContentDetail.setOnClickListener(v -> this.dismiss());

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

        if (inWishToWatch.get()) {
            btnWatchLater.setEnabled(false);
        }


        btnWatchLater.setOnClickListener(v -> {
            wishToWatch.get().add(videoContent);
            user.put("wishToWatch", wishToWatch.get());
            user.saveInBackground();
            btnWatchLater.setEnabled(false);
        });


    }

}