package com.example.capstone.methods;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BinarySearch {
    public static int earliestDate(List<String> dates, Date newDate) throws ParseException {
        int dateSize = dates.size();
        int beg = 0;
        int end = dateSize -1;
        int result = -1;
        int mid = -1;
        while (beg <= end) {
            mid = (beg + end) / 2;
            Date queriedDate = new SimpleDateFormat("MMM dd HH:mm aa yyyy").parse(dates.get(mid) + " 2022"); //todo replace 2022
            if (queriedDate.after(newDate)) {
                beg = mid + 1;
                result = mid;
            } else {
                end = mid - 1;
            }
        }

        if (result == -1) {
            return dateSize;
        }
        return result - 1;
    }
}
