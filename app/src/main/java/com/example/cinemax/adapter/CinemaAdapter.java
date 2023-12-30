package com.example.cinemax.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.model.Cinema;

import java.util.ArrayList;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.ViewHolder> {
    RecyclerViewClickInterface recyclerViewClickInterface;
    ArrayList<RecyclerView.Adapter> TimeAdapter;
    ArrayList<Cinema> cinemas;
    Context context;
    public CinemaAdapter(ArrayList<Cinema> cinemas, RecyclerViewClickInterface recyclerViewClickInterface, Context context, ArrayList<RecyclerView.Adapter> TimeAdapter) {
        this.context = context;
        this.cinemas = cinemas;
        this.TimeAdapter = TimeAdapter;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public CinemaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = View.inflate(parent.getContext(), R.layout.cinema_time_viewholder, null);
        return new CinemaAdapter.ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaAdapter.ViewHolder holder, int position) {
        Cinema cinema = cinemas.get(position);

        holder.cinemaName.setText(cinema.getName());
        holder.cinemaName.setTextColor(context.getResources().getColor(R.color.darkgray));

        holder.timeRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.timeRecyclerView.setAdapter(TimeAdapter.get(position));
    }

    @Override
    public int getItemCount() {
        return cinemas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cinemaName;
        RecyclerView timeRecyclerView;
        public ViewHolder(@NonNull View itemView)  {
            super(itemView);
            cinemaName = itemView.findViewById(R.id.cinema_name);
            timeRecyclerView = itemView.findViewById(R.id.cinema_time_recyclerview);

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

