package com.example.cinemax.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

public class Actor {
    private String profilePic;
    private String name;
    private String character;

    public Actor(String profilePic, String name, String character) {
        this.profilePic = profilePic;
        this.name = name;
        this.character = character;
    }

    public String getActorName() {
        return name;
    }
    public String getActorCharacter() {
        return character;
    }
    public String getProfilePic() {
        return profilePic;
    }

    public static Actor fromFireBaseData(DataSnapshot data) {
        String profilePic = (String) data.child("profile_path").getValue();
        String name = (String) data.child("original_name").getValue();
        String character = (String) data.child("character").getValue();

        return new Actor(profilePic, name, character);
    }

}

