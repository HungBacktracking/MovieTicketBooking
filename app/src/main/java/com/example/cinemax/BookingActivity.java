package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.cinemax.adapter.RecyclerViewClickInterface;
import com.example.cinemax.adapter.SeatAdapter;
import com.example.cinemax.dialog.ProgressHelper;
import com.example.cinemax.dialog.VerifyPurchaseHelper;
import com.example.cinemax.model.Seat;
import com.example.cinemax.repository.BookingRepository;
import com.example.cinemax.utils.DateModel;
import com.example.cinemax.utils.Screen;
import com.example.cinemax.utils.TimeModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity implements VerifyPurchaseHelper.HandlerDialogListener {
    RecyclerView seatRecyclerView;
    RecyclerView.Adapter seatAdapter;
    ArrayList<Seat> seats;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference cinemaRef;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference userPurchaseRef;
    String purchaseId;
    String cinemaId;
    String movieId;
    String fullDate;
    String time;
    TextView totalTicket;
    TextView totalPrice;
    String bookedSeat;
    String CinemaName;
    String MovieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        defineCurrentPurchase();
        handlePurchaseInfo();
        getSeatData();

        totalTicket = findViewById(R.id.activity_booking_ticket);
        totalPrice = findViewById(R.id.activity_booking_total_payable);

        handleBookingBackward();
        handlePurchaseFinish();
    }

    private void handleBookingBackward() {
        ImageView backBtn = findViewById(R.id.activity_booking_backward_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userChosenSeats = Seat.convertIntegerSeatToString(BookingRepository.getInstance().getSeat());

                userPurchaseRef.child("seat").setValue(userChosenSeats).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                            Toast.makeText(BookingActivity.this, "Server excess rating...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                finish();
            }
        });
    }

    private void defineCurrentPurchase() {
        seats = new ArrayList<>();
        for (int i = 0; i < 144; i++) {
            seats.add(new Seat(i, false, true));
        }

        if (getIntent().hasExtra("retryPurchase")) {
            BookingRepository.getInstance().resetCurrentPurchase();

            BookingRepository.getInstance().setPurchaseId(getIntent().getStringExtra("purchaseId"));
            BookingRepository.getInstance().setCinemaId(getIntent().getStringExtra("cinemaId"));
            BookingRepository.getInstance().setMovieId(getIntent().getStringExtra("movieId"));
            BookingRepository.getInstance().setDate(getIntent().getStringExtra("date"));
            BookingRepository.getInstance().setTime(getIntent().getStringExtra("time"));
            BookingRepository.getInstance().setSeat(Seat.convertStringSeatToInteger(getIntent().getStringExtra("seat")));
            BookingRepository.getInstance().setCinemaName(getIntent().getStringExtra("cinemaName"));
            BookingRepository.getInstance().setMovieName(getIntent().getStringExtra("movieName"));
        }

        purchaseId = BookingRepository.getInstance().getPurchaseId();
        cinemaId = BookingRepository.getInstance().getCinemaId();
        movieId = BookingRepository.getInstance().getMovieId();
        fullDate = BookingRepository.getInstance().getDateInNumberFormat();
        time = BookingRepository.getInstance().getTime();
        CinemaName = BookingRepository.getInstance().getCinemaName();
        MovieName = BookingRepository.getInstance().getMovieName();

        userPurchaseRef =  myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("purchases").child(purchaseId);
        cinemaRef = myRef.child("cinema").child(cinemaId);
    }

    private void getSeatData() {
        cinemaRef = cinemaRef.child(movieId).child(fullDate);
        cinemaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookedSeat = snapshot.child(time).getValue(String.class);
                ArrayList<Integer> bookedSeatArray = Seat.convertStringSeatToInteger(bookedSeat);
                for (int seat : bookedSeatArray) {
                    seats.get(seat).setBooked(true);
                    seats.get(seat).setAvailable(false);
                }

                ArrayList<Integer> userChosenSeat = BookingRepository.getInstance().getSeat();
                if (userChosenSeat != null) {
                    for (int seat : userChosenSeat) {
                        seats.get(seat).setAvailable(false);
                    }
                    int total = userChosenSeat.size();
                    totalTicket.setText("x" + String.valueOf(total));
                    totalPrice.setText("$" + String.valueOf(total * 15));
                }

                handleSeatRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleSeatRecyclerView() {
        seatRecyclerView = findViewById(R.id.activity_booking_seat_recycler_view);
        seatRecyclerView.setLayoutManager(new GridLayoutManager(this, 12));
        seatAdapter = new SeatAdapter(seats, new RecyclerViewClickInterface() {
            @Override
            public void onItemClick(int position) {
                Seat seat = seats.get(position);
                if (!seat.isBooked()) {
                    if (seat.isAvailable()) {
                        seat.setAvailable(false);
                        BookingRepository.getInstance().addSeat(seat.getIdx());
                    } else {
                        seat.setAvailable(true);
                        if (BookingRepository.getInstance().getSeat().size() > 0) {
                            BookingRepository.getInstance().removeSeat(seat.getIdx());
                        }
                    }
                    int total = BookingRepository.getInstance().getSeat().size();
                    totalTicket.setText("x" + String.valueOf(total));
                    totalPrice.setText("$" + String.valueOf(total * 15));
                    seatAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onLongItemClick(int position) {

            }
        });
        seatRecyclerView.setAdapter(seatAdapter);
    }

    private void handlePurchaseInfo() {
        ImageView movieBackdrop = findViewById(R.id.activity_booking_backdrop);
        TextView movieName = findViewById(R.id.card_booking_item_name);
        myRef.child("movies").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String movieNameStr = snapshot.child("title").getValue(String.class);
                movieName.setText(movieNameStr);

                Screen screen = new Screen(BookingActivity.this);
                int targetWidth = screen.getScreenWidth();
                int targetHeight = screen.getScreenHeight() / 2;

                String backdrop = snapshot.child("backdrop_path").getValue(String.class);
                Glide.with(BookingActivity.this).load("https://image.tmdb.org/t/p/w500" + backdrop).placeholder(R.drawable.movie_detail_background_image).override(targetWidth, targetHeight).fitCenter().into(movieBackdrop);
                screen.destructor();

                MovieName = movieNameStr;
                BookingRepository.getInstance().setMovieName(movieNameStr);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        movieName.setText(MovieName);

        Button date = findViewById(R.id.activity_booking_booking_date);
        date.setText(DateModel.getDayOfWeek(BookingRepository.getInstance().getDate()));

        Button time = findViewById(R.id.activity_booking_booking_time);
        time.setText(TimeModel.showTimeWithFormat(BookingRepository.getInstance().getTime()));

        Button cinema = findViewById(R.id.activity_booking_booking_revenue);
        cinemaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String cinemaName = snapshot.child("name").getValue(String.class);
                cinema.setText(cinemaName);
                CinemaName = cinemaName;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (getIntent().hasExtra("movieBackdrop")) {
            String backdrop = getIntent().getStringExtra("movieBackdrop");
            Glide.with(this).load("https://image.tmdb.org/t/p/w500" + backdrop).placeholder(R.drawable.movie_detail_background_image).override(1250,600).fitCenter().into(movieBackdrop);
        }
    }

    private void handlePurchaseFinish() {
        ImageView finishBtn = findViewById(R.id.activity_booking_forward_btn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BookingRepository.getInstance().getSeat().size() == 0) {
                    Toast.makeText(BookingActivity.this, "Please choose at least one seat", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    VerifyPurchaseHelper verifyPurchaseHelper = new VerifyPurchaseHelper();
                    verifyPurchaseHelper.show(getSupportFragmentManager(), "verify purchase");
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
    public void handle() {
        userPurchaseRef.child("status").setValue("booked").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(BookingActivity.this, "Error in handling your purchase", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final String userChosenSeats = Seat.convertIntegerSeatToString(BookingRepository.getInstance().getSeat());

        userPurchaseRef.child("seat").setValue(userChosenSeats).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(BookingActivity.this, "Server excess rating...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cinemaRef.child(time).setValue(bookedSeat + "," + userChosenSeats).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ProgressHelper.showDialog(BookingActivity.this, "Loading...");
                    Intent intent = new Intent(BookingActivity.this, PurchaseResultActivity.class);
                    intent.putExtra("movieName", MovieName);
                    intent.putExtra("cinemaName", CinemaName);
                    intent.putExtra("date", BookingRepository.getInstance().getDate());
                    intent.putExtra("time", BookingRepository.getInstance().getTime());
                    intent.putExtra("seat", userChosenSeats);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BookingActivity.this, "Error in booking", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
