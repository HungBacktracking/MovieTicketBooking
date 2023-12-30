package com.example.cinemax.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class Screen {
    private int screenWidth;
    private int screenHeight;
    private Activity mActivity;

    public Screen(Activity activity) {
        this.mActivity = activity;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;
    }

    public void destructor() {
        this.mActivity = null;
        this.screenHeight = 0;
        this.screenWidth = 0;
    }

    public int getScreenWidth() {
        return this.screenWidth;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }
}
