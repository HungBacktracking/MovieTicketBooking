package com.example.cinemax.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.model.Purchase;

import java.util.ArrayList;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder> {
    ArrayList<Purchase> purchases;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;

    public PurchaseHistoryAdapter(ArrayList<Purchase> purchases, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.purchases = purchases;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public PurchaseHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_history_viewholder, parent, false);
        return new PurchaseHistoryAdapter.ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseHistoryAdapter.ViewHolder holder, int position) {
        Purchase purchase = purchases.get(position);
        holder.movieName.setText(purchase.getMovieName());
        holder.cinemaName.setText(purchase.getCinemaName());
        String realtime = String.valueOf(Integer.parseInt(purchase.getTime()) + 9) + ":30";
        holder.time.setText("Time: " + realtime);
        holder.date.setText("Date: " + purchase.getDate());

        String newSeats = "";
        int size = purchase.getSeat().size();
        for (int i = 0; i <  size; i++) {
            newSeats = newSeats + purchase.getSeat().get(i);
            if (i != size - 1) {
                newSeats += ",";
            }
        }
        holder.seat.setText("Seat: " + newSeats);
        holder.status.setText(purchase.getStatus());

        if (purchase.getStatus().equals("booked")) {
            holder.status.setText("e-ticket has been released");
            holder.status.setBackgroundResource(R.drawable.bg_ticket_purchase_done);
        } else if (purchase.getStatus().equals("inprogress")){
            holder.status.setTextColor(context.getResources().getColor(R.color.white));
            holder.status.setText("please choose your seat!");
            holder.status.setBackgroundResource(R.drawable.bg_ticket_purchase_inprogress);
        } else if (purchase.getStatus().equals("failed")) {
            holder.status.setText("your purchase failed, try again");
            holder.status.setBackgroundResource(R.drawable.bg_ticket_purchase_fail);
        }
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieName;
        TextView cinemaName;
        TextView time;
        TextView date;
        TextView seat;
        TextView status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            movieName = itemView.findViewById(R.id.ticket_movie_name);
            cinemaName = itemView.findViewById(R.id.ticket_cinema);
            time = itemView.findViewById(R.id.ticket_time);
            date = itemView.findViewById(R.id.ticket_date);
            seat = itemView.findViewById(R.id.ticket_seat);
            status = itemView.findViewById(R.id.ticket_status);

            itemView.setOnClickListener(view -> {
                recyclerViewClickInterface.onItemClick(getAdapterPosition());
            });

            itemView.setOnLongClickListener(view -> {
                recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                return true;
            });
        }
    }
}

