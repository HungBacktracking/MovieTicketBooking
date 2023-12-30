package com.example.cinemax.request;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.cinemax.model.Review;
import com.example.cinemax.response.MovieReviewsResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieReviewsApiClient {
    private MutableLiveData<ArrayList<Review>> mReviews;
    private static MovieReviewsApiClient instance;
    public static MovieReviewsApiClient getInstance() {
        if (instance == null) {
            instance = new MovieReviewsApiClient();
        }
        return instance;
    }
    private MovieReviewsApiClient() {
        mReviews = new MutableLiveData<>();
    }
    public MutableLiveData<ArrayList<Review>> getMovieReviews() {
        return mReviews;
    }
    public void getMovieReviews(String movie_id) {
        Call<MovieReviewsResponse> call = Service.getMovieApi().searchMovie(Integer.parseInt(movie_id),Service.API_KEY, "en-US", 1);
        call.enqueue(new Callback<MovieReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieReviewsResponse> call, @NonNull Response<MovieReviewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieReviewsResponse reviewsResponse = response.body();
                    ArrayList<Review> reviews = reviewsResponse.getResults();
                    mReviews.postValue(reviews);
                } else {
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieReviewsResponse> call, @NonNull Throwable t) {

            }
        });
    }
}
