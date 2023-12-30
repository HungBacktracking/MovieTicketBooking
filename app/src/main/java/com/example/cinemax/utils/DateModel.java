package com.example.cinemax.utils;

import android.util.Log;

import androidx.annotation.NonNull;

public class DateModel {
    private String date;
    private boolean isSelected;

    public DateModel(String date, boolean isSelected) {
        this.date = date;
        this.isSelected = isSelected;
    }

    @NonNull
    @Override
    public String toString() {
        return this.date;
    }

    public String getDate() {
        return date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static String getDay(String dateModel) {
        String dateStr = dateModel.split(", ")[1];
        return dateStr.split(" ")[0];
    }

    public static String getDayOfWeek(String dateModel) {
        String date = dateModel.split(", ")[0];
        String day = dateModel.split(", ")[1];
        String dateStr = date + ", " + day;
        return dateStr;
    }
}
