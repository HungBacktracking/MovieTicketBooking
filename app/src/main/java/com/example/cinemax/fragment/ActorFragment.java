package com.example.cinemax.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemax.adapter.ActorAdapter;
import com.example.cinemax.model.Actor;
import com.example.cinemax.utils.DateUtils;
import com.example.cinemax.model.Cinema;
import com.example.cinemax.utils.DateModel;
import com.example.cinemax.utils.TimeModel;
import com.example.cinemax.R;
import com.example.cinemax.repository.BookingRepository;
import com.example.cinemax.adapter.CinemaAdapter;
import com.example.cinemax.adapter.DateAdapter;
import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.adapter.TimeAdapter;
import com.example.cinemax.view_models.ActorViewModel;
import com.example.cinemax.view_models.CinemaSelectingViewModel;

import java.util.ArrayList;

public class ActorFragment extends Fragment {
    RecyclerView actorRecyclerView;
    RecyclerView.Adapter actorAdapter;
    TextView emptyShowView;
    ArrayList<Actor> actors;
    ActorViewModel actorViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View actorView = inflater.inflate(R.layout.actor_fragment, container, false);
        actorRecyclerView = actorView.findViewById(R.id.actor_recycler_view);
        emptyShowView = actorView.findViewById(R.id.emptyView);

        actorViewModel = new ViewModelProvider(this).get(ActorViewModel.class);
        actorViewModel.getActors(BookingRepository.getInstance().getMovieId());
        ObserveAnyChange();

        return actorView;
    }
    private void handleActorRecylerView() {
        actorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        actorAdapter = new ActorAdapter(actors, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onLongItemClick(int position) {
            }
        }, getActivity());
        actorRecyclerView.setAdapter(actorAdapter);
    }
    private void ObserveAnyChange() {
        actorViewModel.getActors().observe(getViewLifecycleOwner(), new Observer<ArrayList<Actor>>() {
            @Override
            public void onChanged(ArrayList<Actor> actorList) {
                if (actorList != null) {
                    actorRecyclerView.setVisibility(View.VISIBLE);
                    emptyShowView.setVisibility(View.GONE);

                    actors = actorList;
                    handleActorRecylerView();
                }

                if (actorList.isEmpty()) {
                    actorRecyclerView.setVisibility(View.GONE);
                    emptyShowView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

