package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.cinemax.dialog.ProgressHelper;
import com.example.cinemax.dialog.WishlistHelper;
import com.example.cinemax.fragment.ActorFragment;
import com.example.cinemax.fragment.CinemaSelectFragment;
import com.example.cinemax.fragment.ReviewFragment;
import com.example.cinemax.model.Movie;
import com.example.cinemax.utils.DateModel;
import com.example.cinemax.utils.Screen;
import com.example.cinemax.view_models.MovieDetailViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.cinemax.repository.BookingRepository;
import com.google.firebase.database.ValueEventListener;

public class MovieTrailerActivity extends AppCompatActivity implements WishlistHelper.HandlerDialogListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Movie movie;
    String movieId;
    MovieDetailViewModel movieDetailViewModel;

    private int minLinesDetail = 2;
    boolean isAnimationRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        movieId = getIntent().getStringExtra("movieId");

        movieDetailViewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        movieDetailViewModel.getMovie(movieId);
        ObserverMovieChange();

        BookingRepository.getInstance().resetCurrentPurchase();
        BookingRepository.getInstance().setMovieId(movieId);

        handleSessionShow();

        handleBookingBackward();
        handleBookingForward();
        handleWishlist();

        handleHeadActivityScroll();
    }

    private void handleHeadActivityScroll() {
        TextView secondMovieTitle = findViewById(R.id.second_movie_title);
        ImageView backwardButton = findViewById(R.id.activity_movie_trailer_backward_btn);
        ImageView wishlistButton = findViewById(R.id.activity_movie_trailer_wishlist_btn);

        View headActivity = findViewById(R.id.head_activity);
        ImageView backdropView = findViewById(R.id.activity_movie_trailer_backdrop);

        final int transparentColor = getResources().getColor(android.R.color.transparent);
        final int whiteColor = getResources().getColor(R.color.pearl);
        final int blackColor = getResources().getColor(R.color.black);
        final int blueColor = getResources().getColor(R.color.blue);


        final ValueAnimator whiteAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), transparentColor, whiteColor);
        whiteAnimator.setDuration(1000); // Set duration to 1 second
        whiteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        final ValueAnimator blackAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), transparentColor, blackColor);
        blackAnimator.setDuration(1000); // Set duration to 1 second
        blackAnimator.setInterpolator(new AccelerateDecelerateInterpolator());


        NestedScrollView scrollView = findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int[] locationHeadActivity = new int[2];
            int[] locationBackdropView = new int[2];

            headActivity.getLocationOnScreen(locationHeadActivity);
            backdropView.getLocationOnScreen(locationBackdropView);

            int headActivityBottom = locationHeadActivity[1] + headActivity.getHeight();
            int backdropBottom = locationBackdropView[1] + backdropView.getHeight();

            if (headActivityBottom >= backdropBottom) {
                if (!isAnimationRunning) {
                    isAnimationRunning = true;
                    whiteAnimator.start();
                    blackAnimator.start();

                    backwardButton.setImageResource(R.drawable.blue_back);
                    wishlistButton.setImageResource(R.drawable.baseline_blue_add_24);
                }
            } else {
                if (isAnimationRunning) {
                    isAnimationRunning = false;
                    whiteAnimator.reverse();
                    blackAnimator.reverse();

                    backwardButton.setImageResource(R.drawable.back);
                    wishlistButton.setImageResource(R.drawable.baseline_add_24);

                }
            }
        });

        whiteAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            headActivity.setBackgroundColor(color);
        });

        blackAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            secondMovieTitle.setTextColor(color);
        });
    }

    private void handleTrailer() {
        ImageView trailerImage = findViewById(R.id.trailer_image);

        Screen screen = new Screen(this);
        int targetWidth = screen.getScreenWidth() * 2 / 3;
        int targetHeight = screen.getScreenHeight() / 3;
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + movie.getSecondBackdrop()).placeholder(R.drawable.movie_detail_background_image).override(targetWidth, targetHeight).fitCenter().into(trailerImage);
        screen.destructor();

        trailerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieTrailerActivity.this, PlayingTrailerActivity.class);
                intent.putExtra("videoId", movie.getTrailerPath());
                startActivity(intent);
            }
        });
    }

    private void handleSessionShow() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_movie_trailer_frame_actor_layout, new ActorFragment())
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_movie_trailer_frame_review_layout, new ReviewFragment())
                .commit();
    }

    private void handleWishlist() {
        ImageView wishlistButton = findViewById(R.id.activity_movie_trailer_wishlist_btn);
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WishlistHelper wishlistHelper = new WishlistHelper(MovieTrailerActivity.this);
                wishlistHelper.show(getSupportFragmentManager(), "wishlist");
            }
        });
    }

    private void ObserverMovieChange() {
        movieDetailViewModel.getMovie().observe(this, movie -> {
            if (movie != null) {
                this.movie = movie;
                handleCardMovieItem();
            }
        });
    }

    private void handleCardMovieItem() {
        CardView cardView = findViewById(R.id.card_movie_item);
        TextView movieTitle = findViewById(R.id.card_movie_item_name);
        TextView secondMovieTitle = findViewById(R.id.second_movie_title);
        TextView trailerTitle = findViewById(R.id.movie_name_in_trailer);
        TextView movieDescription = findViewById(R.id.card_movie_item_description);
        TextView movieRating = findViewById(R.id.card_movie_item_rating);
        TextView movieDuration = findViewById(R.id.card_movie_item_duration);
        TextView movieGenre = findViewById(R.id.card_movie_item_genre);
        ImageView moviePoster = findViewById(R.id.activity_movie_trailer_backdrop);

        movieTitle.setText(movie.getTitle());
        secondMovieTitle.setText(movie.getTitle());
        trailerTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());
        movieRating.setText(movie.getRating());
        movieDuration.setText(movie.getDuration() + " mins");
        movieGenre.setText(movie.getGenre(3));
        handleTrailer();

        Screen screen = new Screen(this);
        int targetWidth = screen.getScreenWidth();
        int targetHeight = screen.getScreenHeight() * 3 / 5;
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + movie.getBackdropPath()).placeholder(R.drawable.movie_detail_background_image).override(targetWidth, targetHeight).fitCenter().into(moviePoster);
        screen.destructor();

        ImageButton movieExpand = findViewById(R.id.card_movie_item_expand_button);
        movieExpand.setOnClickListener(v -> {
            if (movieDescription.getMaxLines() == minLinesDetail) {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                movieDescription.setMaxLines(Integer.MAX_VALUE);
                movieExpand.setImageResource(R.drawable.baseline_expand_less_24);
            } else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                movieDescription.setMaxLines(minLinesDetail);
                movieExpand.setImageResource(R.drawable.baseline_expand_more_24);
            }
        });
    }



    private void handleBookingBackward() {
        ImageView backButton = findViewById(R.id.activity_movie_trailer_backward_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void handleBookingForward() {
        ImageView bookButton = findViewById(R.id.activity_movie_trailer_forward_btn);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieTrailerActivity.this, MovieDetailActivity.class);
                intent.putExtra("movieId", movieId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressHelper.dismissDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressHelper.dismissDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();

        isAnimationRunning = false;
        BookingRepository.getInstance().resetCurrentPurchase();
        BookingRepository.getInstance().setMovieId(movieId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BookingRepository.getInstance().resetCurrentPurchase();
        BookingRepository.getInstance().setMovieId(movieId);
    }

    @Override
    public void handle() {
        String movieId = BookingRepository.getInstance().getMovieId();
        String userId = mAuth.getCurrentUser().getUid();
        myRef.child("users").child(userId).child("wishlist").child(movieId).setValue(movieId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MovieTrailerActivity.this, "Added " + movie.getTitle() + " to wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MovieTrailerActivity.this, "Server excess rating...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}