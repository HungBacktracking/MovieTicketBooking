package com.example.cinemax.repository;

import androidx.lifecycle.LiveData;

import com.example.cinemax.model.Review;
import com.example.cinemax.request.MovieReviewsApiClient;

import java.util.ArrayList;

public class ReviewRepository {
    private MovieReviewsApiClient movieReviewsApiClient;
    private static ReviewRepository instance;
    private ReviewRepository() {
        movieReviewsApiClient = MovieReviewsApiClient.getInstance();
    }
    public static ReviewRepository getInstance() {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }
    public LiveData<ArrayList<Review>> getMovieReviews() {
        return movieReviewsApiClient.getMovieReviews();
    }
    public void getMovieReviews(String movie_id) {
        movieReviewsApiClient.getMovieReviews(movie_id);
    }
}
