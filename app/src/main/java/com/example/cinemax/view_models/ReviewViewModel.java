package com.example.cinemax.view_models;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cinemax.model.Review;
import com.example.cinemax.repository.ReviewRepository;

import java.util.ArrayList;

public class ReviewViewModel extends ViewModel {
    private ReviewRepository reviewRepository;
    public ReviewViewModel() {
        this.reviewRepository = ReviewRepository.getInstance();
    }
    public LiveData<ArrayList<Review>> getMovieReviews() {
        return this.reviewRepository.getMovieReviews();
    }
    public void getMovieReviews(String movie_id) {
        this.reviewRepository.getMovieReviews(movie_id);
    }
}
