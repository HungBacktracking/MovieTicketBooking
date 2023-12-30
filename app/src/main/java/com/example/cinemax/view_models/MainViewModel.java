package com.example.cinemax.view_models;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cinemax.model.Movie;
import com.example.cinemax.repository.MovieRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private MovieRepository movieRepository;
    public MainViewModel() {
        this.movieRepository = MovieRepository.getInstance();
    }

    public void getAllMovies() {
        this.movieRepository.getAllMovies();
    }
    public LiveData<ArrayList<Movie>> getMovies() {
        return this.movieRepository.getMovies();
    }
    public void getHottestMovie() {
        this.movieRepository.getHottestMovie();
    }
    public LiveData<ArrayList<Movie>> getHottestMovies() {
        return this.movieRepository.getHottestMovies();
    }
}