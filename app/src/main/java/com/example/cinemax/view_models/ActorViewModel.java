package com.example.cinemax.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cinemax.model.Actor;
import com.example.cinemax.model.Movie;
import com.example.cinemax.repository.ActorRepository;
import com.example.cinemax.repository.MovieRepository;

import java.util.ArrayList;

public class ActorViewModel extends ViewModel {
    private ActorRepository actorRepository;

    public ActorViewModel() {
        this.actorRepository = ActorRepository.getInstance();
    }

    public LiveData<ArrayList<Actor>> getActors() {
        return this.actorRepository.getActors();
    }

    public void getActors(String movieId) {
        this.actorRepository.getActors(movieId);
    }
}
