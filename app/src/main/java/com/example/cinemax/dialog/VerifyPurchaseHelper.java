package com.example.cinemax.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cinemax.R;
import com.example.cinemax.model.Seat;
import com.example.cinemax.repository.BookingRepository;
import com.example.cinemax.utils.TimeModel;

public class VerifyPurchaseHelper extends DialogFragment {
    TextView movieName;
    TextView cinemaName;
    TextView date;
    TextView time;
    TextView seat;
    TextView price;
    private HandlerDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.verify_purchase_dialog, null);

        movieName = view.findViewById(R.id.ticket_movie_name_dialog);
        movieName.setText(BookingRepository.getInstance().getMovieName());
        cinemaName = view.findViewById(R.id.ticket_cinema_dialog);
        cinemaName.setText(BookingRepository.getInstance().getCinemaName());
        date = view.findViewById(R.id.ticket_date_dialog);
        date.setText("Date: " + BookingRepository.getInstance().getDate());
        time = view.findViewById(R.id.ticket_time_dialog);
        time.setText("Time: " + TimeModel.showTimeWithFormat(BookingRepository.getInstance().getTime()));
        seat = view.findViewById(R.id.ticket_seat_dialog);
        seat.setText("Seat: " + Seat.convertIntegerSeatToString(BookingRepository.getInstance().getSeat()));
        price = view.findViewById(R.id.ticket_price_dialog);
        price.setText("Price: " + BookingRepository.getInstance().getSeat().size() * 15 + "$");

        builder.setView(view)
                .setTitle("Do you want to purchase this ticket?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VerifyPurchaseHelper.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        listener.handle();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (HandlerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface HandlerDialogListener {
        void handle();
    }
}

