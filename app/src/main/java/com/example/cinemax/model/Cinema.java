package com.example.cinemax.model;



import com.example.cinemax.utils.TimeModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class
Cinema {
    private String id;
    private String name;

    private String date;
    private ArrayList<TimeModel> time;

    public Cinema(String id, String name) {
        this.id = id;
        this.name = name;
        this.time = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Cinema() {}

    public static Cinema fromFirebaseData(DataSnapshot dataSnapshot) {
        String id = (String) dataSnapshot.child("id").getValue();
        String name = (String) dataSnapshot.child("name").getValue();
        return new Cinema(id,name);
    }

    public void setTime(ArrayList<String> time) {
        for (int i = 0; i < 12; i++) {
            this.time.add(new TimeModel("", true, true));
        }

        for (String t : time) {
            TimeModel timeModel = this.time.get(Integer.parseInt(t));
            timeModel.setTime(t);
            timeModel.setBlocked(false);
            timeModel.setSelected(false);
        }
    }

    public ArrayList<TimeModel> getTime() {
        return this.time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
