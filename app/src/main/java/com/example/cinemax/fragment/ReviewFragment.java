package com.example.cinemax.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.R;
import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.adapter.ReviewAdapter;
import com.example.cinemax.model.Review;
import com.example.cinemax.repository.BookingRepository;
import com.example.cinemax.view_models.ReviewViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewFragment extends Fragment {
    RecyclerView reviewRecyclerView;
    RecyclerView.Adapter reviewAdapter;
    ReviewViewModel reviewViewModel;
    ArrayList<Review> mReviews;
    View reviewView;

    TextView overallRating;
    TextView numberOfRating;
    TextView emptyShowView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        reviewView = inflater.inflate(R.layout.review_fragment, container, false);
        reviewRecyclerView = reviewView.findViewById(R.id.fragment_review_view);

        overallRating = reviewView.findViewById(R.id.overall_rating);
        numberOfRating = reviewView.findViewById(R.id.number_of_rating);
        emptyShowView = reviewView.findViewById(R.id.emptyView);

        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        mReviews = new ArrayList<>();

        reviewViewModel.getMovieReviews(BookingRepository.getInstance().getMovieId());
        ObserverAnyChange();

        return reviewView;
    }

    private void ObserverAnyChange() {
        reviewViewModel.getMovieReviews().observe(getViewLifecycleOwner(), reviews -> {
            if (reviews != null) {
                emptyShowView.setVisibility(View.GONE);
                overallRating.setVisibility(View.VISIBLE);
                numberOfRating.setVisibility(View.VISIBLE);

                mReviews = reviews;
                ConfigureReviewRecyclerView();
            }
            else {
                overallRating.setVisibility(View.GONE);
                numberOfRating.setVisibility(View.GONE);
                emptyShowView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void ConfigureReviewRecyclerView() {
        overallRating.setText(getOverallRating());
        numberOfRating.setText("(" + String.valueOf(mReviews.size()) + " votes from TMDB Community)");

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        reviewAdapter = new ReviewAdapter(mReviews, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onLongItemClick(int position) {
            }
        }, getActivity());
        reviewRecyclerView.setAdapter(reviewAdapter);
    }

    public String getOverallRating() {
        int size = 0;
        double sum = 0;
        for (Review review : mReviews) {
            String val = review.getAuthorDetails().getRating();
            if (val == "null") continue;

            size++;
            sum += Double.parseDouble(val);
        }
        if (sum == 0) return "No data about rating";

        sum /= size;
        return String.format("%.1f", sum);
    }
}

