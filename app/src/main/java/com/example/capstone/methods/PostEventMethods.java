package com.example.capstone.methods;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PostEventMethods {

    public static boolean isInWishToWatch(AtomicReference<List<VideoContent>> wishToWatch, VideoContent videoContent) {
        AtomicBoolean inWishToWatch = new AtomicBoolean(false);
        wishToWatch.get().forEach(wishVideoContent -> {
            try {
                VideoContent wishToWatchContent = wishVideoContent.fetchIfNeeded();
                if (Objects.equals(wishToWatchContent.getTitle(), videoContent.getTitle())) {
                    inWishToWatch.set(true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return  inWishToWatch.get();
    }


    public static void onSelectDate(Context context, Event event, VideoContent videoContent, TextView tvDate, Button btnSelectDate, Button btnPostEvent) {
        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker =  MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build();
        datePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "datePicker"); // todo does this work?

        datePicker.addOnPositiveButtonClickListener(selection -> {
            onDateSelected(context, event, videoContent, datePicker, btnSelectDate, btnPostEvent, tvDate);
        });
    }

    public static void onDateSelected(Context context, Event event, VideoContent videoContent, MaterialDatePicker<Long> datePicker, Button btnSelectDate, Button btnPostEvent, TextView tvDate) {
        Date date =  new Date(datePicker.getSelection());
        final String[] strDate = {date.toString()};
        String[] strArrayDate = strDate[0].split(" ");
        String dateNum = String.valueOf(Integer.parseInt(strArrayDate[2]) + 1);
        strDate[0] = strArrayDate[1] + " " + dateNum;

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select time")
                .setHour(12)
                .setMinute(0)
                .build();
        timePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "timePicker");
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
            String[] s = strDateTime.split(":");
            String[] newDateArr = s[0].split(" ");
            newDateArr[2] = String.valueOf(timePicker.getHour());
            String militaryDateTimeStr = newDateArr[0] + " " + newDateArr[1] + " " + newDateArr[2] + ":" + s[1];
            try {
                Date newDate = new SimpleDateFormat("MMM dd hh:mm aa yyyy").parse(militaryDateTimeStr + " 2022");
                Date currDate = new Date(System.currentTimeMillis());
                Log.i("VideoContentDetailFragment", "onDateSelected: " + newDate.toString() + " curr: " + currDate.toString());
                if (newDate.before(currDate)) {
                    Toast.makeText(((FragmentActivity) context), "Cannot select a time in the past", Toast.LENGTH_SHORT).show();
                    return;
                }
                event.createEvent(newDate, videoContent);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            List<String> dates = new ArrayList<>();
            dates.add(strDateTime);
            event.setDates(dates);

            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(strDateTime);
            // Then switch button to new one
            btnSelectDate.setVisibility(View.GONE);
            btnPostEvent.setVisibility(View.VISIBLE);
        });
    }

    public static String formatMinutes(int minutes) {
        String strMinutes = "";
        if (minutes < 10) {
            strMinutes += "0";
        }

        strMinutes += String.valueOf(minutes);
        return strMinutes;
    }

    public static Event createEvent(Date date, VideoContent videoContent) {
        Event event = new Event();
        event.setTitle(videoContent.getTitle());

        List<ParseUser> users = new ArrayList<>();
        users.add(ParseUser.getCurrentUser());
        event.setUsers(users);

        event.setNumInterested(1);
        event.setTypeOfContent(videoContent.getTypeOfContent());
        event.setVideoContent(videoContent);
        event.setPosterUrl(videoContent.getPosterUrl());
        event.setBackdropUrl(videoContent.getBackdropUrl());
        event.setIsLive(false);

        List<List<ParseUser>> interestedUsers = new ArrayList<>();
        List<ParseUser> interestedUser = new ArrayList<>();
        interestedUser.add(ParseUser.getCurrentUser());
        interestedUsers.add(interestedUser);
        event.setInterestedUsers(interestedUsers);

        event.setEarliestDate(date);
        event.setEarliestUserIndex(0);
        event.setUniversity(ParseUser.getCurrentUser().getString("university"));

        return event;
    }

}
