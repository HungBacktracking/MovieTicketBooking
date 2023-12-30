package com.example.cinemax.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cinemax.model.Movie;
import com.example.cinemax.repository.MovieRepository;

public class MovieDetailViewModel extends ViewModel {
    private MovieRepository movieRepository;

    public MovieDetailViewModel() {
        this.movieRepository = MovieRepository.getInstance();
    }

    public LiveData<Movie> getMovie() {
        return this.movieRepository.getMovie();
    }

    public void getMovie(String movieId) {
        this.movieRepository.getMovie(movieId);
    }
}
