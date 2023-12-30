package com.example.cinemax.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cinemax.model.Cinema;
import com.example.cinemax.repository.CinemaRepository;

import java.util.ArrayList;

public class CinemaSelectingViewModel extends ViewModel {
    private CinemaRepository cinemaRepository;

    public CinemaSelectingViewModel() {
        this.cinemaRepository = CinemaRepository.getInstance();
    }

    public LiveData<ArrayList<Cinema>> getCinemas() {
        return this.cinemaRepository.   getCinemas();
    }

    public void getCinemaInDate(String movieId, String fullDate) {
        this.cinemaRepository.getCinemaInDate(movieId, fullDate);
    }

}
