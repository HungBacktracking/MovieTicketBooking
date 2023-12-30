package com.example.cinemax.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinemax.R;
import com.example.cinemax.model.Movie;
import com.example.cinemax.utils.Screen;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements Filterable {
    ArrayList<Movie> movies;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;

    public MovieAdapter(ArrayList<Movie> movies, RecyclerViewClickInterface recyclerViewClickInterface, Context context) {
        this.movies = movies;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.context = context;
    }

    public ArrayList<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = View.inflate(parent.getContext(), R.layout.movie_holder, null);
        return new MovieAdapter.ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.movieName.setText(movie.getTitle());
        String duration = movie.getDuration();
        int totalMinutes = Integer.parseInt(duration);

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        String formattedTime = hours + "h" + minutes + "m";
        holder.movieDuration.setText(formattedTime);
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()).placeholder(R.drawable.bg_image_main_activity).override(500,550).fitCenter().into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();
                ArrayList<Movie> filteredList = new ArrayList<>();

                if (searchString.isEmpty()) {
                    results.values = movies;
                } else if (searchString.contains("filter")) {
                    String[] filter = searchString.split(" ");
                    for (Movie movie : movies) {
                        if (movie.getGenre().toLowerCase().contains(filter[1])) {
                            filteredList.add(movie);
                        }
                    }
                    results.values = filteredList;
                } else {
                    for (Movie movie : movies) {
                        if (movie.getTitle().toLowerCase().contains(searchString)) {
                            filteredList.add(movie);
                        }
                    }
                    results.values = filteredList;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                setMovies((ArrayList<Movie>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieName;
        TextView movieDuration;
        ImageView movieImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            movieName = itemView.findViewById(R.id.movie_holder_name);
            movieImage = itemView.findViewById(R.id.movie_holder_image);
            movieDuration = itemView.findViewById(R.id.movie_holder_duration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }


}

