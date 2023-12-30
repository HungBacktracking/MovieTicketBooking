package com.example.cinemax.model;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Movie {
    private String id;
    private String name;
    private String description;
    private String poster_path;
    private String backdrop_path;
    private String second_backdrop;
    private String trailer_path;
    private double vote_average;
    private long vote_count;
    private String categories;
    private long duration;
    private String releaseDate;
    private double popularity;
    private ArrayList<Actor> actors;


    public Movie() {}

    public Movie(String id, String name, String description, String poster_path, String backdrop_path, String second_backdrop, String trailer_path, double vote_average, long vote_count, String categories, long duration, String releaseDate, double popularity, ArrayList<Actor> actors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.second_backdrop = second_backdrop;
        this.trailer_path = trailer_path;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.categories = categories;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.actors = actors;
    }

    public static Movie fromFirebaseData(DataSnapshot dataSnapshot) {
        long movieId = (long) dataSnapshot.child("id").getValue();
        String id = String.valueOf(movieId);
        String name = (String) dataSnapshot.child("title").getValue();
        String overview = (String) dataSnapshot.child("overview").getValue();
        String releaseDate = (String) dataSnapshot.child("release_date").getValue();
        String poster_path = (String) dataSnapshot.child("poster_path").getValue();
        String backdrop_path = (String) dataSnapshot.child("backdrop_path").getValue();
        String second_backdrop = (String) dataSnapshot.child("second_backdrop").getValue();
        String trailer_path = (String) dataSnapshot.child("trailer_path").getValue();
        double vote_average = (double) dataSnapshot.child("vote_average").getValue();
        long vote_count = (long) dataSnapshot.child("vote_count").getValue();
        String categories = (String) dataSnapshot.child("genres").getValue();
        long duration = (long) dataSnapshot.child("runtime").getValue();
        double popularity = (double) dataSnapshot.child("popularity").getValue();

        ArrayList<Actor> actors = new ArrayList<>();

        int count = 0;
        for (DataSnapshot data : dataSnapshot.child("cast").getChildren()) {
            if (count == 30) break;

            Actor actor = Actor.fromFireBaseData(data);
            actors.add(actor);
            count++;
        }

        return new Movie(id, name, overview, poster_path, backdrop_path, second_backdrop, trailer_path, vote_average, vote_count, categories, duration, releaseDate, popularity, actors);
    }

    public String getDescription() {
        return this.description;
    }
    public String getTitle() {
        return this.name;
    }
    public String getRating() {
        return String.valueOf(this.vote_average);
    }
    public String getDuration() {
        return String.valueOf(this.duration);
    }
    public String getGenre() {
        return this.categories;
    }

    public String getGenre(int count) {
        String[] genres = this.categories.split(" ");

        String genreLimit = "";
        for (int i = 0; i < Math.min(count, genres.length); i++) {
            genreLimit += genres[i];
            if (i + 1 < Math.min(count, genres.length)) genreLimit += " ";
        }

        return genreLimit;
    }

    public String getPosterPath() {
        return this.poster_path;
    }

    public String getBackdropPath() {
        return this.backdrop_path;
    }
    public String getSecondBackdrop() {
        return this.second_backdrop;
    }
    public String getTrailerPath() {
        return this.trailer_path;
    }

    public ArrayList<Actor> getActors() {
        return this.actors;
    }

    public String getId() {
        return this.id;
    }

}
