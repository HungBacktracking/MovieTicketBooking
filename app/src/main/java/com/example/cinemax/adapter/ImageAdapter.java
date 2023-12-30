package com.example.cinemax.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinemax.model.Movie;
import com.example.cinemax.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{
    ArrayList<Movie> movies;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;

    public ImageAdapter(ArrayList<Movie> movies, RecyclerViewClickInterface recyclerViewClickInterface, Context context) {
        this.movies = movies;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_main_activity_viewholder, parent, false);
        return new ImageAdapter.ImageViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        String url = movies.get(position).getPosterPath();
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + url).placeholder(R.drawable.bg_image_main_activity).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View parent) {
            super(parent);

            imageView = parent.findViewById(R.id.movie_hottest_image);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}

