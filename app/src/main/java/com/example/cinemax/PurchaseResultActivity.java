package com.example.cinemax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cinemax.utils.DateUtils;
import com.example.cinemax.utils.TimeModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PurchaseResultActivity extends AppCompatActivity {
    ImageView qrCodeImageview;
    TextView movieName;
    TextView cinemaName;
    TextView date;
    TextView time;
    TextView seat;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    String CinemaAddress;

    private final int FINE_PERMISSION_CODE = 1;
    private final int IMAGE_STORAGE_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_result);

        setupPurchaseResultActivity();

        String MovieName = getIntent().getStringExtra("movieName");
        movieName.setText(MovieName);
        String CinemaName = getIntent().getStringExtra("cinemaName");
        CinemaAddress = CinemaName;
        cinemaName.setText(CinemaName);
        String Date = getIntent().getStringExtra("date");
        date.setText(Date);
        String Time = getIntent().getStringExtra("time");
        time.setText(TimeModel.showTimeWithFormat(Time));
        String Seat = getIntent().getStringExtra("seat");
        seat.setText(Seat);

        generateQRCode(MovieName, CinemaName, DateUtils.getDateInNumberFormat(Date), Time, Seat);
        handleClickButtons();
    }

    private void setupPurchaseResultActivity() {
        qrCodeImageview = findViewById(R.id.activity_purchase_result_image);
        movieName = findViewById(R.id.activity_purchase_result_movie_name);
        cinemaName = findViewById(R.id.activity_purchase_result_cinema_name);
        date = findViewById(R.id.activity_purchase_result_date);
        time = findViewById(R.id.activity_purchase_result_time);
        seat = findViewById(R.id.activity_purchase_result_seat);
    }

    private void handleClickButtons() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Button directionBtn = findViewById(R.id.activity_purchase_result_direction);
        directionBtn.setOnClickListener(view -> {
            fetchLastLocation();
        });

        TextView shareBtn = findViewById(R.id.activity_purchase_result_share);
        shareBtn.setOnClickListener(view -> {
            shareTicket();
        });

        ImageView closeBtn = findViewById(R.id.activity_purchase_result_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseResultActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void shareTicket() {
        if (ActivityCompat.checkSelfPermission(PurchaseResultActivity.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PurchaseResultActivity.this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, IMAGE_STORAGE_PERMISSION_CODE);
            return;
        }
        Bitmap ticket = captureTicket(findViewById(R.id.activity_purchase_result_image));
        storeAndShareTicket(ticket);
    }

    private void storeAndShareTicket(Bitmap ticket) {
        OutputStream outputStream = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "ticket.jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "TicketBooking");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                ticket.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Objects.requireNonNull(outputStream);
                Toast.makeText(getApplicationContext(), "Ticket saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(intent, "Share ticket"));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private Bitmap captureTicket(View view) {
        Bitmap returnBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(getResources().getColor(android.R.color.white));
        }
        view.draw(canvas);
        return returnBitmap;
    }

    private void generateQRCode(String movieName, String cinemaName, String date, String time, String seat) {
        String content = movieName.trim() + cinemaName.trim() + date.trim() + time.trim() + seat.trim();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImageview.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void showDirection(String userAddress, String cinemaAddress) {
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + userAddress + "/" + cinemaAddress);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PurchaseResultActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                Geocoder geocoder = new Geocoder(PurchaseResultActivity.this, Locale.forLanguageTag("vi"));
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    String userAddress = addresses.get(0).getAddressLine(0);
                    showDirection("My current location", CinemaAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IMAGE_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareTicket();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}