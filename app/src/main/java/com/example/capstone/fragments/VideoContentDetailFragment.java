package com.example.capstone.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.models.VideoContent;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoContentDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoContentDetailFragment extends DialogFragment {


    private static final String ARG_PARAM1 = "videoContent";

    private VideoContent videoContent;

    public VideoContentDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoContentDetail.
     */
    public static VideoContentDetailFragment newInstance(VideoContent videoContent) {
        VideoContentDetailFragment fragment = new VideoContentDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, videoContent); //todo: is it parceable?
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoContent = getArguments().getParcelable(ARG_PARAM1);  //todo: is it parceable?
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
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
        // todo: create event object

        //   this.dismiss(); closes fragment

        tvTitle.setText(videoContent.getTitle());
        tvRating.setText(Double.toString(videoContent.getVoteAverage() / 2.0d));
        tvOverview.setText(videoContent.getOverview());

        ImageView ivPoster = view.findViewById(R.id.ivPoster);
        Glide.with(view).load(videoContent.getPosterUrl())
                .override(1000, 1400)
                .into(ivPoster);
        ivPoster.setColorFilter(Color.argb(60, 0, 0 , 0));

        // set listener for select date button
        btnSelectDate.setOnClickListener(v -> {

            onSelectDate(tvDateBtn, btnSelectDate, btnPostEvent);
        });

        // todo: set on click listener for post

    }

    private String formatMinutes(int minutes) {
        String strMinutes = "";
        if (minutes < 10) {
            strMinutes += "0";
        }

        strMinutes += String.valueOf(minutes);
        return strMinutes;
    }

    private void onSelectDate(TextView tvDate, Button btnSelectDate, Button btnPostEvent) {
        // todo: intialize post object
        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker =  MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build();
        datePicker.show(requireActivity().getSupportFragmentManager(), "datePicker");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            onDateSelected(datePicker, btnSelectDate, btnPostEvent, tvDate);
        });
    }

    private void onDateSelected(MaterialDatePicker<Long> datePicker, Button btnSelectDate, Button btnPostEvent, TextView tvDate) {
        // todo: add date to post object
        Date date =  new Date(datePicker.getSelection());
        final String[] strDate = {date.toString()};
        String[] strArrayDate = strDate[0].split(" ");
        strDate[0] = strArrayDate[1] + " " + strArrayDate[2];

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select time")
                .setHour(12)
                .setMinute(0)
                .build();
        timePicker.show(requireActivity().getSupportFragmentManager(), "timePicker");

        timePicker.addOnPositiveButtonClickListener(v1 -> {

            int hour = timePicker.getHour();
            Log.i("fragment", "onViewCreated: " + hour);
            String strDateTime;
            if (hour > 12) {
                hour -= 12;
                strDateTime =  strDate[0] + " " + String.valueOf(hour) +":" + formatMinutes(timePicker.getMinute()) + " PM";
            } else if (hour == 12){
                strDateTime =  strDate[0] + " " + String.valueOf(hour) +":" + formatMinutes(timePicker.getMinute()) + " PM";

            } else {
                strDateTime =  strDate[0] + " " + String.valueOf(hour) +":" + formatMinutes(timePicker.getMinute()) + " AM";
            }
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(strDateTime);
            // Then switch button to new one
            btnSelectDate.setVisibility(View.GONE);
            btnPostEvent.setVisibility(View.VISIBLE);
        });
    }

}