package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.cinemax.model.Movie;
import com.example.cinemax.adapter.MovieAdapter;
import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.fragment.UserNavbarFragment;
import com.example.cinemax.view_models.ViewAllViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ViewAllActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView viewAllMovieRecyclerView;
    MovieAdapter movieAdapter;
    ArrayList<Movie> movies;
    ViewAllViewModel viewAllViewModel;
    UserNavbarFragment userNavbarFragment = new UserNavbarFragment();
    DrawerLayout drawerLayout;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        viewAllViewModel = new ViewModelProvider(this).get(ViewAllViewModel.class);
        viewAllViewModel.getAllMovies();
        ObserverAnyChange();

        Bundle bundle = new Bundle();
        bundle.putString("titleHeader", "Browse");

        userNavbarFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.view_all_user_navbar_fragment, userNavbarFragment).commit();

        handleSearchBar();
        handleFilterGenre();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void ObserverAnyChange() {
        viewAllViewModel.getMovies().observe(this, movies -> {
            this.movies = movies;
            handleMovieViewAllRecyclerView();
        });
    }

    private void handleFilterGenre() {
        ImageView filterGenre = findViewById(R.id.view_all_content_filter);
        filterGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterMenu(view);
            }

            private void showFilterMenu(View view) {
                PopupMenu popupMenu = new PopupMenu(ViewAllActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_genre, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.genre_action) {
                            movieAdapter.setMovies(movies);
                            movieAdapter.getFilter().filter("filter Action");
                        } else if (item.getItemId() == R.id.genre_adventure) {
                            movieAdapter.setMovies(movies);
                            movieAdapter.getFilter().filter("filter Adventure");
                        } else if (item.getItemId() == R.id.genre_animation) {
                            movieAdapter.setMovies(movies);
                            movieAdapter.getFilter().filter("filter Animation");
                        } else if (item.getItemId() == R.id.genre_comedy) {
                            movieAdapter.setMovies(movies);
                            movieAdapter.getFilter().filter("filter Comedy");
                        } else if (item.getItemId() == R.id.genre_crime) {
                            movieAdapter.setMovies(movies);
                            movieAdapter.getFilter().filter("filter Crime");
                        } else if (item.getItemId() == R.id.genre_drama) {
                            movieAdapter.setMovies(movies);
                            movieAdapter.getFilter().filter("filter Drama");
                        } else if (item.getItemId() == R.id.genre_all) {
                            movieAdapter.setMovies(movies);
                        }

                        return false;
                    }
                });

                popupMenu.setGravity(Gravity.END);
                popupMenu.show();
            }
        });
    }

    private void handleSearchBar() {
        SearchView searchView = findViewById(R.id.search_view_all);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // when user press search button
                movieAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) { // when user type
                if (TextUtils.isEmpty(newText)) {
                    movieAdapter.setMovies(movies);
                }
                return true;
            }
        });
    }

    private void handleMovieViewAllRecyclerView() {
        viewAllMovieRecyclerView = findViewById(R.id.view_all_content_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        viewAllMovieRecyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(movies, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ViewAllActivity.this, MovieTrailerActivity.class);
                intent.putExtra("movieId", movieAdapter.getMovies().get(position).getId());
                startActivity(intent);
            }
            @Override
            public void onLongItemClick(int position) {

            }
        }, this);
        viewAllMovieRecyclerView.setAdapter(movieAdapter);
    }
}