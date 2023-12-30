package com.example.cinemax.model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Purchase {
    private String id;
    private String movieId;
    private String movieName;
    private String cinemaId;
    private String cinemaName;
    private String date;
    private String time;
    private ArrayList<Integer> seat;
    private String status;


    public Purchase() {
        this.cinemaId = "";
        this.time = "";
        this.date = "";
        this.movieId = "";
        seat = new ArrayList<>();
    }

    public Purchase(String purchaseId, String movieId, String movieName, String cinemaId, String cinemaName, String date, String time, ArrayList<Integer> seat, String status) {
        this.id = purchaseId;
        this.movieId = movieId;
        this.movieName = movieName;
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.date = date;
        this.time = time;
        this.seat = seat;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Integer> getSeat() {
        return seat;
    }

    public void setSeat(ArrayList<Integer> seat) {
        this.seat = seat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addSeat(int seat) {
        if (this.seat == null) {
            this.seat = new ArrayList<>();
            this.seat.add(seat);
        } else {
            this.seat.add(seat);
        }
    }

    public void removeSeat(int seat) {
        for (int i = 0; i < this.seat.size(); i++) {
            if (this.seat.get(i) == seat) {
                this.seat.remove(i);
                break;
            }
        }
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }


    public static Purchase fromFirebaseData(DataSnapshot dataSnapshot) {
        String purchaseId = (String) dataSnapshot.child("id").getValue();
        String movieName = (String) dataSnapshot.child("movieName").getValue();
        String cinemaName = (String) dataSnapshot.child("cinemaName").getValue();
        String movieId = (String) dataSnapshot.child("movieId").getValue();
        String cinemaId = (String) dataSnapshot.child("cinemaId").getValue();
        String date = (String) dataSnapshot.child("date").getValue();
        String time = (String) dataSnapshot.child("time").getValue();
        String seatStr = (String) dataSnapshot.child("seat").getValue();
        ArrayList<Integer> seat = Seat.convertStringSeatToInteger(seatStr);
        String status = (String) dataSnapshot.child("status").getValue();

        return new Purchase(purchaseId, movieId, movieName, cinemaId, cinemaName, date, time, seat, status);
    }
}
