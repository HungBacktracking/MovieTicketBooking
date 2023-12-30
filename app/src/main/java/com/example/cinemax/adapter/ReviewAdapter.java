package com.example.cinemax.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    ArrayList<Review> reviews;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;

    private int minLinesDetail = 5;

    public ReviewAdapter(ArrayList<Review> reviews, RecyclerViewClickInterface recyclerViewClickInterface, Context context) {
        this.reviews = reviews;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.context = context;
    }
    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_holder, parent, false);
        return new ReviewAdapter.ReviewViewHolder(inflator);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        String rating = review.getAuthorDetails().getRating();
        if (rating == "null") {
            rating = "No rating data";
            holder.rating.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        else holder.rating.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        holder.rating.setText(rating);

        holder.expandButton.setOnClickListener(v -> {
            if (holder.content.getMaxLines() == minLinesDetail) {
                TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                holder.content.setMaxLines(Integer.MAX_VALUE);
                holder.expandButton.setText("LESS");
            } else {
                TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                holder.content.setMaxLines(minLinesDetail);
                holder.expandButton.setText("MORE");
            }
        });

        holder.content.setOnClickListener(v -> {
            if (holder.content.getMaxLines() == minLinesDetail) {
                TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                holder.content.setMaxLines(Integer.MAX_VALUE);
                holder.expandButton.setText("LESS");
            } else {
                TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                holder.content.setMaxLines(minLinesDetail);
                holder.expandButton.setText("MORE");
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView author;
        TextView rating;
        TextView content;
        TextView expandButton;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_movie_item);
            author = itemView.findViewById(R.id.card_movie_item_author_name);
            rating = itemView.findViewById(R.id.card_movie_item_author_rating);
            content = itemView.findViewById(R.id.card_movie_item_author_description);
            expandButton = itemView.findViewById(R.id.read_more);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
