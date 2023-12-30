package com.example.cinemax.model;

public class User {
    private String userId;
    private String profilePic;
    private String username;

    public User(String userId, String profilePic, String username) {
        this.userId = userId;
        this.profilePic = profilePic;
        this.username = username;
    }

    public User(String userId, String profilePic) {
        this.userId = userId;
        this.profilePic = profilePic;
    }


    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getProfilePic() {
        return profilePic;
    }

}

