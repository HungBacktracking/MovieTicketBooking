package com.example.cinemax.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.cinemax.model.Actor;
import com.example.cinemax.model.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActorRepository {
    private MutableLiveData<ArrayList<Actor>> actors;
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference myRef = database.getReference();
    public static ActorRepository instance;

    private ActorRepository() {
        this.actors = new MutableLiveData<>();
    }

    public static ActorRepository getInstance() {
        if (instance == null) {
            instance = new ActorRepository();
        }
        return instance;
    }
    public MutableLiveData<ArrayList<Actor>> getActors() {
        return this.actors;
    }
    public void getActors(String movieId) {
        myRef.child("movies").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Movie movie = Movie.fromFirebaseData(snapshot);
                actors.postValue(movie.getActors());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
