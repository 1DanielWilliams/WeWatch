package com.example.capstone.methods;

import android.util.Log;

import com.example.capstone.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BinarySearch {
    public static int earliestDateInEvent(List<String> dates, Date newDate) throws ParseException {
        int dateSize = dates.size();

        int low = 0;
        int high = dateSize - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Date midVal = new SimpleDateFormat("MMM dd HH:mm aa yyyy").parse(dates.get(mid) + " 2022"); //todo replace 2022

            if (midVal.before(newDate)) {
                low = mid + 1;
            } else if (midVal.after(newDate)) {
                high = mid - 1;
            }

        }
        return low;
    }

    public static int indexOfEvents(List<Event> events, Date newDate) {
        int eventsSize = events.size();

        int low = 0;
        int high = eventsSize - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Date midVal = events.get(mid).getEarliestDate();
            if (midVal.before(newDate)) {
                low = mid + 1;
            } else if (midVal.after(newDate)) {
                high = mid - 1;
            }

        }
        return low;
    }
}
