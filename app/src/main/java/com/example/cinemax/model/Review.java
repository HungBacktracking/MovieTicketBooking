package com.example.cinemax.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Review implements Parcelable {
    String author;
    AuthorDetails author_details;
    String content;

    public Review(String author, AuthorDetails authorDetails, String content) {
        this.author = author;
        this.author_details = authorDetails;
        this.content = content;
    }

    protected Review(@NonNull Parcel in) {
        author = in.readString();
        author_details = in.readParcelable(AuthorDetails.class.getClassLoader());
        content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @NonNull
        @Override
        public Review createFromParcel(@NonNull Parcel in) {
            return new Review(in);
        }

        @NonNull
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public AuthorDetails getAuthorDetails() {
        return author_details;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeParcelable(author_details, i);
        parcel.writeString(content);
    }

    public static class AuthorDetails implements Parcelable {
        String name;
        String username;
        String avatarPath;
        String rating;

        protected AuthorDetails(@NonNull Parcel in) {
            name = in.readString();
            username = in.readString();
            avatarPath = in.readString();
            rating = in.readString();
        }

        public static final Creator<AuthorDetails> CREATOR = new Creator<AuthorDetails>() {
            @NonNull
            @Override
            public AuthorDetails createFromParcel(@NonNull Parcel in) {
                return new AuthorDetails(in);
            }

            @NonNull
            @Override
            public AuthorDetails[] newArray(int size) {
                return new AuthorDetails[size];
            }
        };

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public String getAvatarPath() {
            return avatarPath;
        }

        public String getRating() {
            if (rating == null) {
                rating = "null";
            }
            return rating;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeString(username);
            parcel.writeString(avatarPath);
            parcel.writeString(rating);
        }
    }
}
