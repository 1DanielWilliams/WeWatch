package com.example.capstone.methods;

import android.util.Log;

import com.example.capstone.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BinarySearch {
    public static int DATE_EXIST = -2;
    public static int earliestDateInEvent(List<String> dates, Date newDate) throws ParseException {
        int dateSize = dates.size();
        int low = 0;
        int high = dateSize - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Date midVal = new SimpleDateFormat("MMM dd hh:mm aa yyyy", Locale.US).parse(dates.get(mid) + " " + LocalDate.now().getYear());
            if (midVal.before(newDate)) {
                low = mid + 1;
            } else if (midVal.after(newDate)) {
                high = mid - 1;
            } else if (midVal.equals(newDate)) {
                return DATE_EXIST;
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
