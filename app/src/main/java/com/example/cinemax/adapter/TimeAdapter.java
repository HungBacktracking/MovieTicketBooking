package com.example.cinemax.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.utils.TimeModel;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
    Context context;
    ArrayList<TimeModel> times;
    RecyclerViewClickInterface recyclerViewClickInterface;

    public TimeAdapter(ArrayList<TimeModel> _times, RecyclerViewClickInterface _recyclerViewClickInterface) {
        this.times = _times;
        this.recyclerViewClickInterface = _recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public TimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View inflator = LayoutInflater.from(this.context).inflate(R.layout.viewholder_time, parent, false);

        return new TimeAdapter.ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAdapter.ViewHolder holder, int position) {
        TimeModel time = times.get(position);
        holder.time.setText(TimeModel.showTimeWithFormat(String.valueOf(position)));
        holder.time.setBackgroundResource(R.drawable.bg_time_available);
        holder.time.setTypeface(holder.time.getTypeface(), Typeface.NORMAL);
        holder.time.setTextColor(context.getResources().getColor(R.color.darkgray));
        if (time.isBlocked() && time.isSelected()) {
            holder.time.setBackgroundResource(R.drawable.bg_time_blocked);
            holder.time.setTextColor(context.getResources().getColor(R.color.anothergray));
            holder.time.setEnabled(false);
        } else if (time.isSelected()) {
            holder.time.setBackgroundResource(R.drawable.bg_time_selected);
            holder.time.setTypeface(holder.time.getTypeface(), Typeface.BOLD);
            holder.time.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        public ViewHolder(@NonNull View timeView) {
            super(timeView);

            time = timeView.findViewById(R.id.viewholder_time);
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            time.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
