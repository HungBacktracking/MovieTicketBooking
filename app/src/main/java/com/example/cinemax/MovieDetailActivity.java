package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
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
import com.example.cinemax.fragment.CinemaSelectFragment;
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

public class MovieDetailActivity extends AppCompatActivity implements WishlistHelper.HandlerDialogListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference userPurchaseRef;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Movie movie;
    String movieId;
    MovieDetailViewModel movieDetailViewModel;

    VideoView videoView;
    private long purchaseCount;

    private int minLinesDetail = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getStringExtra("movieId");

        movieDetailViewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        movieDetailViewModel.getMovie(movieId);
        ObserverMovieChange();

        BookingRepository.getInstance().resetCurrentPurchase();
        BookingRepository.getInstance().setMovieId(movieId);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_movie_detail_frame_layout, new CinemaSelectFragment())
                .commit();

        userPurchaseRef = myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("purchases");
        setPurchaseCount();

        handleBookingBackward();
        handleBookingForward();
        handleWishlist();


//        WebView webView = findViewById(R.id.webView);
//        String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/tUesv5u5bvA\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
//        webView.loadData(video, "text/html","utf-8");
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
    }

    private void setPurchaseCount() {
        userPurchaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                purchaseCount = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void handleWishlist() {
        ImageView wishlistButton = findViewById(R.id.activity_movie_detail_wishlist_btn);
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WishlistHelper wishlistHelper = new WishlistHelper(MovieDetailActivity.this);
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
        TextView movieDescription = findViewById(R.id.card_movie_item_description);
        TextView movieRating = findViewById(R.id.card_movie_item_rating);
        TextView movieDuration = findViewById(R.id.card_movie_item_duration);
        TextView movieGenre = findViewById(R.id.card_movie_item_genre);
        ImageView moviePoster = findViewById(R.id.activity_movie_detail_backdrop);

        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());
        movieRating.setText(movie.getRating());
        movieDuration.setText(movie.getDuration() + " mins");
        movieGenre.setText(movie.getGenre(3));


        Screen screen = new Screen(this);
        int targetWidth = screen.getScreenWidth();
        int targetHeight = screen.getScreenHeight() / 2;
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
        ImageView backButton = findViewById(R.id.activity_movie_detail_backward_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void handleBookingForward() {
        ImageView bookButton = findViewById(R.id.activity_movie_detail_forward_btn);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BookingRepository.getInstance().getDate().isEmpty()) {
                    ProgressHelper.dismissDialog();
                    Toast.makeText(MovieDetailActivity.this, "Please choose the date", Toast.LENGTH_SHORT).show();
                } else if (BookingRepository.getInstance().getMovieId().isEmpty() || BookingRepository.getInstance().getCinemaId().isEmpty()) {
                    ProgressHelper.dismissDialog();
                    Toast.makeText(MovieDetailActivity.this, "Please choose the cinema and time", Toast.LENGTH_SHORT).show();
                } else {
                    String choseDate = BookingRepository.getInstance().getDateInNumberFormat();
                    String cinemaId = BookingRepository.getInstance().getCinemaId();
                    String purchaseId = movieId + cinemaId + choseDate + BookingRepository.getInstance().getTime() + String.valueOf(purchaseCount);

                    BookingRepository.getInstance().setPurchaseId(purchaseId);
                    BookingRepository.getInstance().setStatus("inprogress");
                    BookingRepository.getInstance().setMovieName(movie.getTitle());

                    userPurchaseRef.child(purchaseId).setValue(BookingRepository.getInstance().getCurrentPurchase()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ProgressHelper.dismissDialog();
                            if (task.isSuccessful()) {
                                ProgressHelper.showDialog(MovieDetailActivity.this, "Continue...");
                                Intent intent = new Intent(MovieDetailActivity.this, BookingActivity.class);
                                intent.putExtra("movieName", movie.getTitle());
                                intent.putExtra("movieBackdrop", movie.getBackdropPath());
                                intent.putExtra("purchaseId", purchaseId);
                                startActivity(intent);

                            } else {
                                Toast.makeText(MovieDetailActivity.this, "Error in booking", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
                    Toast.makeText(MovieDetailActivity.this, "Added " + movie.getTitle() + " to wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Server excess rating...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}