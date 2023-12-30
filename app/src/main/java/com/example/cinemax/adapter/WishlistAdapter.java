package com.example.cinemax.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinemax.R;
import com.example.cinemax.model.Movie;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    ArrayList<Movie> movies;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;
    public WishlistAdapter(ArrayList<Movie> movies, RecyclerViewClickInterface recyclerViewClickInterface, Context context) {
        this.movies = movies;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.context = context;
    }
    @NonNull
    @Override
    public WishlistAdapter.WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_holder, parent, false);
        return new WishlistAdapter.WishlistViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.WishlistViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.movieName.setText(movie.getTitle());

        String duration = movie.getDuration();
        int totalMinutes = Integer.parseInt(duration);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        String formattedTime = hours + "h" + minutes + "m";
        holder.movieDuration.setText(formattedTime);

        holder.movieGenre.setText(movie.getGenre());

        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()).placeholder(R.drawable.bg_image_main_activity).override(290,300).fitCenter().into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView movieName;
        ImageView movieImage;
        TextView movieDuration;
        TextView movieGenre;
        ImageView deleteBtn;
        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);

            movieName = itemView.findViewById(R.id.wishlist_holder_name);
            movieImage = itemView.findViewById(R.id.wishlist_holder_image);
            movieDuration = itemView.findViewById(R.id.wishlist_holder_duration);
            movieGenre = itemView.findViewById(R.id.wishlist_holder_genre);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}

