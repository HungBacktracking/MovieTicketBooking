package com.example.cinemax.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String getCurrentDate() {
        return "Mon, 18 Dec, 2023";
    }
    public static String getNextDay(String currentDate) {
        return getOffsetDate(currentDate, 1);
    }
    public static String getPreviousDay(String currentDate) {
        return getOffsetDate(currentDate, -1);
    }
    public static String getOffsetDate(String currentDate, int days) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(currentDate));
            calendar.add(Calendar.DAY_OF_YEAR, days);
            Date newDate = calendar.getTime();
            return dateFormat.format(newDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDateInNumberFormat(String _date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM, yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMyyyy");
        try {
            Date date = inputFormat.parse(_date);
            String resultDate = outputFormat.format(date);

            return resultDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
