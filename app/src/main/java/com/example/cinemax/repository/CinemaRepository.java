package com.example.cinemax.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cinemax.model.Cinema;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CinemaRepository {
    private MutableLiveData<ArrayList<Cinema>> cinemas;
    private static CinemaRepository instance;

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference myRef = database.getReference();

    private CinemaRepository() {
        this.cinemas = new MutableLiveData<>();
    }

    public static CinemaRepository getInstance() {
        if (instance == null) {
            instance = new CinemaRepository();
        }
        return instance;
    }

    public LiveData<ArrayList<Cinema>> getCinemas() {
        return this.cinemas;
    }

    public void getCinemaInDate(String movieId, String fullDate) {
        myRef.child("cinema").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Cinema> cinemaList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.hasChild(movieId) && dataSnapshot.child(movieId).hasChild(fullDate)) {
                        Cinema cinema = Cinema.fromFirebaseData(dataSnapshot);
                        ArrayList<String> times = new ArrayList<>();

                        for (DataSnapshot time : dataSnapshot.child(movieId).child(fullDate).getChildren()) {
                            times.add(time.getKey());
                        }
                        cinema.setTime(times);
                        cinemaList.add(cinema);
                    }
                }
                cinemas.postValue(cinemaList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
