package com.example.cinemax;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.cinemax.adapter.ImageAdapter;
import com.example.cinemax.adapter.MovieAdapter;
import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.model.Movie;
import com.example.cinemax.view_models.MainViewModel;

import com.example.cinemax.fragment.UserNavbarFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ViewPager2 mImageRecyclerView;
    CircleIndicator3 mIndicator;
    RecyclerView.Adapter imageAdapter;
    ArrayList<Movie> movies;
    ArrayList<Movie> mHottestMovies;
    RecyclerView movieRecyclerView;
    RecyclerView.Adapter movieAdapter;
    TextView viewAll;
    UserNavbarFragment userNavbarFragment = new UserNavbarFragment();
    MainViewModel mainViewModel;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_user_navbar_fragment, userNavbarFragment).commit();

        movies = new ArrayList<>();
        mainViewModel.getAllMovies();
        ObserverAllMovies();

        mHottestMovies = new ArrayList<>();
        mainViewModel.getHottestMovie();
        ObserverHottestMovie();

        handleViewAll();
        handleDrawer();
    }

    private void handleDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        DatabaseReference userRef = myRef.child("users").child(auth.getCurrentUser().getUid());
        View headerView = navigationView.getHeaderView(0);
        TextView usernameHeader = headerView.findViewById(R.id.username);
        TextView emailHeader = headerView.findViewById(R.id.email);

        emailHeader.setText(auth.getCurrentUser().getEmail());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("username").getValue() != null) usernameHeader.setText(snapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_purchases) {
            Intent intent = new Intent(this, PurchaseHistoryActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_wishlist) {
            Intent intent = new Intent(this, WishlistActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_logout) {
            auth.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    private void ObserverAllMovies() {
        mainViewModel.getMovies().observe(this, movies -> {
            this.movies = movies;
            handleMovieRecyclerView();
        });
    }

    private void ObserverHottestMovie() {
        mainViewModel.getHottestMovies().observe(this, movies -> {
            this.mHottestMovies = movies;
            handleImageRecyclerView();
        });
    }

    private void handleViewAll() {
        viewAll = findViewById(R.id.activity_main_view_all);
        viewAll.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ViewAllActivity.class);
            startActivity(intent);
        });
    }
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mImageRecyclerView == null || mImageRecyclerView.getAdapter() == null || mImageRecyclerView.getAdapter().getItemCount() == 0) {
                return;
            }
            int size = mImageRecyclerView.getAdapter().getItemCount();
            mImageRecyclerView.setCurrentItem((mImageRecyclerView.getCurrentItem() + 1) % size);
        }
    };

    private void handleImageRecyclerView() {
        mImageRecyclerView = findViewById(R.id.activity_main_image_movie);
        mIndicator = findViewById(R.id.activity_main_indicator);
        mImageRecyclerView.setOffscreenPageLimit(3);
        mImageRecyclerView.setClipToPadding(false);
        mImageRecyclerView.setClipChildren(false);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        mImageRecyclerView.setPageTransformer(compositePageTransformer);
        mImageRecyclerView.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        mImageRecyclerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageScrollStateChanged(position);
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 3000);
            }
        });

        imageAdapter = new ImageAdapter(mHottestMovies, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, MovieTrailerActivity.class);
                intent.putExtra("movieId", mHottestMovies.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(int position) {
            }
        }, this);

        mImageRecyclerView.setAdapter(imageAdapter);
        mIndicator.setViewPager(mImageRecyclerView);
    }

    private void handleMovieRecyclerView() {
        movieRecyclerView = findViewById(R.id.activity_main_list_all_movies);
        movieRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        movieAdapter = new MovieAdapter(movies, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, MovieTrailerActivity.class);
                intent.putExtra("movieId", movies.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(int position) {
            }
        }, this);
        movieRecyclerView.setAdapter(movieAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, 3000);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}