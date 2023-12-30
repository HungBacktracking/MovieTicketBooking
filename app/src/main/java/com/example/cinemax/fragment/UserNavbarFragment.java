package com.example.cinemax.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinemax.MainActivity;
import com.example.cinemax.ProfileActivity;
import com.example.cinemax.PurchaseHistoryActivity;
import com.example.cinemax.R;
import com.example.cinemax.SignInActivity;
import com.example.cinemax.ViewAllActivity;
import com.example.cinemax.WishlistActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class UserNavbarFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView titleHeader;
    ImageView profileImage;
    Button menuViewAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userNavbarView = inflater.inflate(R.layout.user_navbar_fragment, container, false);
        profileImage = userNavbarView.findViewById(R.id.user_navbar_fragment_profile_image);
        menuViewAll = userNavbarView.findViewById(R.id.navbar_option_view_all);
        titleHeader = userNavbarView.findViewById(R.id.user_navbar_fragment_header);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String newTitle = bundle.getString("titleHeader");
            titleHeader.setText(newTitle);
        }

        DatabaseReference userRef = myRef.child("users").child(mAuth.getCurrentUser().getUid());
        getUserData(userRef);

        handleUserNavbar();

        return userNavbarView;
    }

    public void getUserData(DatabaseReference userRef) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("profilePic").getValue().toString()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleUserNavbar() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        menuViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
    }

    private void openDrawer() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openDrawer();
        }

        if (getActivity() != null && getActivity() instanceof ViewAllActivity) {
            ((ViewAllActivity) getActivity()).openDrawer();
        }
    }

}
