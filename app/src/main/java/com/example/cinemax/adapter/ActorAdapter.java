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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.cinemax.R;
import com.example.cinemax.model.Actor;
import com.example.cinemax.model.Movie;
import com.example.cinemax.utils.Screen;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ViewHolder> {
    ArrayList<Actor> actors;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;

    public ActorAdapter(ArrayList<Actor> actors, RecyclerViewClickInterface recyclerViewClickInterface, Context context) {
        this.actors = actors;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.context = context;
    }

    public ArrayList<Actor> getActors() {
        return this.actors;
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = View.inflate(parent.getContext(), R.layout.actor_holder, null);
        return new ActorAdapter.ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdapter.ViewHolder holder, int position) {
        Actor actor = actors.get(position);
        holder.actorName.setText(actor.getActorName());
        holder.actorCharacter.setText(actor.getActorCharacter());

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + actor.getProfilePic())
                .override(350,350)
                .fitCenter()
                .transform(new CenterCrop(), new CircleCrop())
                .into(holder.actorImage);
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView actorName;
        TextView actorCharacter;
        ImageView actorImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            actorName = itemView.findViewById(R.id.actor_holder_name);
            actorCharacter = itemView.findViewById(R.id.actor_holder_character);
            actorImage = itemView.findViewById(R.id.actor_holder_image);

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

