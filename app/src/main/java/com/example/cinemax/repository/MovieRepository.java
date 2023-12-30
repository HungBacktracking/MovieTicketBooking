package com.example.cinemax.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.cinemax.model.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MovieRepository {
    private MutableLiveData<Movie> mMovie;
    private MutableLiveData<ArrayList<Movie>> mMovies;
    private MutableLiveData<ArrayList<Movie>> mHottestMovies;
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference myRef = database.getReference();
    public static MovieRepository instance;
    private MovieRepository() {
        this.mMovie = new MutableLiveData<>();
        this.mMovies = new MutableLiveData<>();
        this.mHottestMovies = new MutableLiveData<>();
    }
    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }
    public MutableLiveData<Movie> getMovie() {
        return this.mMovie;
    }
    public void getMovie(String movieId) {
        myRef.child("movies").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Movie movie = Movie.fromFirebaseData(snapshot);
                mMovie.postValue(movie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public MutableLiveData<ArrayList<Movie>> getMovies() {
        return this.mMovies;
    }
    public void getAllMovies() {
        myRef.child("movies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Movie> movies = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Movie movie = Movie.fromFirebaseData(dataSnapshot);
                    movies.add(movie);
                }
                mMovies.postValue(movies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<ArrayList<Movie>> getHottestMovies() {
        return this.mHottestMovies;
    }
    public void getHottestMovie() {
        myRef.child("movies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Movie> movies = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Movie movie = Movie.fromFirebaseData(dataSnapshot);
                    if (dataSnapshot.hasChild("is_hot")) {
                        if (dataSnapshot.child("is_hot").getValue().toString().equals("true")) {
                            movies.add(movie);
                        }
                    }
                }
                mHottestMovies.postValue(movies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
