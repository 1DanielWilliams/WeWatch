package com.example.capstone.methods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.capstone.activities.FeedActivity;
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
                Date newDate = new SimpleDateFormat("MMM dd HH:mm aa yyyy", Locale.US).parse(militaryDateTimeStr + " 2022");
                Date currDate = new Date(System.currentTimeMillis());
                Log.i("VideoContentDetailFragment", "onDateSelected: " + newDate.toString() + " curr: " + currDate.toString());
                if (newDate.before(currDate)) {
                    Toast.makeText(((FragmentActivity) context), "Cannot select a time in the past", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("PostEvenMethods", "onDateSelected: " + newDate.toString() + " strDate: " + militaryDateTimeStr);
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



    public static void postEvent(Context context, Event event, AtomicReference<List<VideoContent>> wishToWatch, ParseUser user, VideoContent videoContent) {

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
                    Intent i = new Intent(context, FeedActivity.class);
                    context.startActivity(i);
                });
            } else {
                Event queriedEvent = events.get(0);

                try {
                    addToExistingEvent(context, queriedEvent, event);
                } catch (java.text.ParseException | ParseException ex) {
                    ex.printStackTrace();
                }

                queriedEvent.saveInBackground(e12 -> {
                    if (e12 != null) {
                        Log.e("VideoContentDetailFragment", "done: ERROR saving queried event", e12);
                        return;
                    }
                    Intent i = new Intent(context, FeedActivity.class);
                    context.startActivity(i);
                });
            }
        });


    }


    // Adds the new user + time + interested User array to the existing event
    public static void addToExistingEvent(Context context, Event queriedEvent, Event event) throws java.text.ParseException, ParseException {
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
            Toast.makeText(((FragmentActivity) context), "Date already Exist", Toast.LENGTH_SHORT).show();
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
