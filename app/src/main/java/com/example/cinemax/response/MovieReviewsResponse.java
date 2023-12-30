package com.example.cinemax.response;

import com.example.cinemax.model.Review;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieReviewsResponse {
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("total_results")
    @Expose
    private int totalResults;
    @SerializedName("results")
    @Expose
    private ArrayList<Review> results;

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public ArrayList<Review> getResults() {
        return results;
    }
}
