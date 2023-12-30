package com.example.cinemax.utils;

public class TimeModel {
    private String time;
    private boolean isBlocked;
    private boolean isSelected;

    public TimeModel(String time, boolean isSelected, boolean isBlocked) {
        this.time = time;
        this.isSelected = isSelected;
        this.isBlocked = isBlocked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public static String showTimeWithFormat(String time) {
        return String.valueOf(Integer.parseInt(time) + 9) + ":30";
    }
}
