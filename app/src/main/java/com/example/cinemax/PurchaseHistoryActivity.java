package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cinemax.adapter.PurchaseHistoryAdapter;
import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.model.Purchase;
import com.example.cinemax.model.Seat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PurchaseHistoryActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    RecyclerView purchaseHistoryRecyclerView;
    RecyclerView.Adapter purchaseHistoryAdapter;
    DatabaseReference userRef;

    ArrayList<Purchase> purchases;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        mAuth = FirebaseAuth.getInstance();
        userRef = myRef.child("users").child(mAuth.getCurrentUser().getUid());

        getPurchasesData(userRef);

        ImageView backBtn = findViewById(R.id.activity_booking_backward_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getPurchasesData(DatabaseReference userRef) {
        userRef.child("purchases").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchases = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Purchase purchase = Purchase.fromFirebaseData(dataSnapshot);
                    purchases.add(purchase);
                }
                handlePurchaseRecyclerView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handlePurchaseRecyclerView() {
        purchaseHistoryRecyclerView = findViewById(R.id.purchase_history_recycler_view);
        purchaseHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        purchaseHistoryAdapter = new PurchaseHistoryAdapter(purchases, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
                Purchase purchase = purchases.get(position);
                if (purchase.getStatus().equals("booked")) {
                    Intent intent = new Intent(PurchaseHistoryActivity.this, PurchaseResultActivity.class);
                    intent.putExtra("movieName", purchase.getMovieName());
                    intent.putExtra("cinemaName", purchase.getCinemaName());
                    intent.putExtra("date", purchase.getDate());
                    intent.putExtra("time", purchase.getTime());
                    String newSeats = "";
                    int size = purchase.getSeat().size();
                    for (int i = 0; i <  size; i++) {
                        newSeats = newSeats + purchase.getSeat().get(i);
                        if (i != size - 1) {
                            newSeats += ",";
                        }
                    }
                    intent.putExtra("seat", newSeats);
                    startActivity(intent);
                }
                else if (purchase.getStatus().equals("inprogress")) {
                    Intent intent = new Intent(PurchaseHistoryActivity.this, BookingActivity.class);
                    intent.putExtra("purchaseId", purchase.getId());
                    intent.putExtra("movieId", purchase.getMovieId());
                    intent.putExtra("cinemaId", purchase.getCinemaId());
                    intent.putExtra("date", purchase.getDate());
                    intent.putExtra("time", purchase.getTime());
                    intent.putExtra("seat", Seat.convertIntegerSeatToString(purchase.getSeat()));
                    intent.putExtra("retryPurchase", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongItemClick(int position) {

            }
        });
        purchaseHistoryRecyclerView.setAdapter(purchaseHistoryAdapter);
    }
}