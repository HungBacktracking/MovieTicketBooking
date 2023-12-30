package com.example.cinemax.request;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static final Retrofit retrofit = retrofitBuilder.build();
    private static final MovieAPI movieApi = retrofit.create(MovieAPI.class);
    public static MovieAPI getMovieApi() {
        return movieApi;
    }
    public static String API_KEY = "8408441732f46b7e4da7e61754e6e34e";
}
