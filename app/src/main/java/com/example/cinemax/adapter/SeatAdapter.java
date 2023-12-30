package com.example.cinemax.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.model.Seat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
    Context context;
    ArrayList<Seat> seats;
    RecyclerViewClickInterface recyclerViewClickInterface;

    public SeatAdapter(ArrayList<Seat> _seats, RecyclerViewClickInterface _recyclerViewClickInterface) {
        this.seats = _seats;
        this.recyclerViewClickInterface = _recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public SeatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View inflator = View.inflate(this.context, R.layout.seat_viewholder, null);
        return new ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatAdapter.ViewHolder holder, int position) {
        Seat seat = seats.get(position);
        if (seat.isBooked()) {
            holder.seatButton.setBackgroundResource(R.drawable.bg_seat_booked);
        } else if (seat.isAvailable()) {
            holder.seatButton.setBackgroundResource(R.drawable.bg_seat_available);
        } else if (!seat.isAvailable()) {
            holder.seatButton.setBackgroundResource(R.drawable.bg_seat_your_selection);
        }
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView seatButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            seatButton = itemView.findViewById(R.id.seat_viewholder_seat_button);

            seatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            seatButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
