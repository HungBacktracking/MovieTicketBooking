package com.example.cinemax.request;

import com.example.cinemax.response.MovieReviewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {
    @GET("/3/movie/{movie_id}/reviews")
    Call<MovieReviewsResponse> searchMovie(
            @Path("movie_id") int movie_id,
            @Query("api_key") String key,
            @Query("language") String language,
            @Query("page") int page
    );
}
