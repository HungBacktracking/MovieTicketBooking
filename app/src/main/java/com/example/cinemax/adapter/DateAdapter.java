package com.example.cinemax.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.utils.DateModel;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    Context context;
    ArrayList<DateModel> dates;
    RecyclerViewClickInterface recyclerViewClickInterface;

    public DateAdapter(ArrayList<DateModel> _dates, RecyclerViewClickInterface _recyclerViewClickInterface) {
        this.dates = _dates;
        this.recyclerViewClickInterface = _recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public DateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflator = LayoutInflater.from(this.context).inflate(R.layout.viewholder_calendar, parent, false);

        return new DateAdapter.ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull DateAdapter.ViewHolder holder, int position) {
        DateModel dateModel = dates.get(position);
        String date = dateModel.getDate();
        String[] parts = date.split(", ");

        if (parts.length >= 2) {
            holder.day.setText(parts[0]);

            holder.date.setText(parts[1].split(" ")[0]);
        }

        if (dateModel.isSelected()) {
            holder.background.setBackgroundResource(R.drawable.bg_time_selected);

            holder.day.setTextColor(context.getResources().getColor(R.color.black));
            holder.date.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            holder.background.setBackgroundResource(R.drawable.bg_time_available);

            holder.day.setTextColor(Color.parseColor("#C4C9DF"));
            holder.date.setTextColor(Color.parseColor("#C4C9DF"));
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView day;
        LinearLayout background;

        public ViewHolder(@NonNull View calendarView) {
            super(calendarView);

            background = calendarView.findViewById(R.id.viewholder_calendar_background);
            date = calendarView.findViewById(R.id.viewholder_calendar_date);
            day = calendarView.findViewById(R.id.viewholder_calendar_day);

            calendarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

            calendarView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
