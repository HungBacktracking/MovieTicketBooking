package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.adapter.WishlistAdapter;
import com.example.cinemax.model.Movie;
import com.example.cinemax.repository.BookingRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WishlistActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    RecyclerView wishlistRecyclerView;
    RecyclerView.Adapter wishlistAdapter;
    DatabaseReference userRef;
    ArrayList<Movie> wishList;
    ArrayList<Movie> removedWishList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        mAuth = FirebaseAuth.getInstance();
        userRef = myRef.child("users").child(mAuth.getCurrentUser().getUid());

        wishList = new ArrayList<>();
        removedWishList = new ArrayList<>();

        handleWishlistRecyclerView();
        getWishlistData(userRef);

        ImageView backBtn = findViewById(R.id.activity_wishlist_backward_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(removeWishListCallBack);
        itemTouchHelper.attachToRecyclerView(wishlistRecyclerView);
    }

    ItemTouchHelper.SimpleCallback removeWishListCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            Movie movie = wishList.get(position);
            String movieName = movie.getTitle();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    wishList.remove(position);
                    removedWishList.add(movie);
                    wishlistAdapter.notifyItemRemoved(position);
                    Snackbar.make(wishlistRecyclerView, movieName + " deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    wishList.add(position, movie);
                                    wishlistAdapter.notifyItemInserted(position);

                                    int pos = 0;
                                    for (Movie data : removedWishList) {
                                        if (data == movie) {
                                            removedWishList.remove(pos);
                                            break;
                                        }
                                        pos++;
                                    }

                                    String movieId = movie.getId();
                                    userRef.child("wishlist").child(movieId).setValue(movieId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            } else {

                                            }
                                        }
                                    });
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(WishlistActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(WishlistActivity.this, R.color.red_delete))
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(WishlistActivity.this, R.color.red_delete))
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(WishlistActivity.this, R.color.white))
                    .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void getWishlistData(DatabaseReference userRef) {
        userRef.child("wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> wishlist = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    wishlist.add(dataSnapshot.getValue(String.class));
                }
                for (String movie : wishlist) {
                    myRef.child("movies").child(movie).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Movie wishlistMovie = Movie.fromFirebaseData(snapshot);
                            wishList.add(wishlistMovie);
                            wishlistAdapter.notifyItemInserted(wishList.size() - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void handleWishlistRecyclerView() {
        wishlistRecyclerView = findViewById(R.id.wishlist_recycler_view);
        wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        wishlistAdapter = new WishlistAdapter(wishList, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
                Movie movie = wishList.get(position);
                Intent intent = new Intent(WishlistActivity.this, MovieTrailerActivity.class);
                intent.putExtra("movieId", movie.getId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(int position) {}
        }, this);
        wishlistRecyclerView.setAdapter(wishlistAdapter);
    }

    private void removeMovieFromWishlist(String movieId) {
        userRef.child("wishlist").child(movieId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(WishlistActivity.this, "Server excess rating...", Toast.LENGTH_SHORT).show();
                }
            }
        });;
    }

    @Override
    protected void onStop() {
        super.onStop();

        for (Movie movie : removedWishList) {
            removeMovieFromWishlist(movie.getId());
        }
    }
}