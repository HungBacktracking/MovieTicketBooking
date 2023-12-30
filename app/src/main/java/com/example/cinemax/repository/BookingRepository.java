package com.example.cinemax.repository;



import com.example.cinemax.model.Purchase;
import com.example.cinemax.utils.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BookingRepository {
    private Purchase currentPurchase;
    public static BookingRepository instance;
    private BookingRepository() {
        this.currentPurchase = new Purchase();
    }

    public static BookingRepository getInstance() {
        if (instance == null) {
            instance = new BookingRepository();
        }
        return instance;
    }
    public void setCurrentPurchase(Purchase purchase) {
        this.currentPurchase = purchase;
    }
    public void addSeat(int seat) {
        this.currentPurchase.addSeat(seat);
    }
    public void removeSeat(int seat) {
        this.currentPurchase.removeSeat(seat);
    }
    public void setTime(String time) {
        this.currentPurchase.setTime(time);
    }
    public void setDate(String date) {
        this.currentPurchase.setDate(date);
    }
    public void setCinemaId(String cinemaId) {
        this.currentPurchase.setCinemaId(cinemaId);
    }
    public void setPurchaseId(String purchaseId) {
        this.currentPurchase.setId(purchaseId);
    }
    public void setMovieId(String movieId) {
        this.currentPurchase.setMovieId(movieId);
    }
    public void setStatus(String status) {
        this.currentPurchase.setStatus(status);
    }
    public void resetCurrentPurchase() {
        if (this.currentPurchase != null) {
            this.currentPurchase = new Purchase();
        }
    }
    public Purchase getCurrentPurchase() {
        return this.currentPurchase;
    }
    public void setCinemaName(String cinemaName) {
        this.currentPurchase.setCinemaName(cinemaName);
    }
    public void setMovieName(String movieName) {
        this.currentPurchase.setMovieName(movieName);
    }
    public String getPurchaseId() {
        return this.currentPurchase.getId();
    }
    public String getCinemaId() {
        return this.currentPurchase.getCinemaId();
    }
    public String getMovieId() {
        return this.currentPurchase.getMovieId();
    }
    public String getDate() {
        return this.currentPurchase.getDate();
    }

    public String getDateInNumberFormat() {
        return DateUtils.getDateInNumberFormat(this.currentPurchase.getDate());
    }
    public String getTime() {
        return this.currentPurchase.getTime();
    }
    public String getCinemaName() {
        return this.currentPurchase.getCinemaName();
    }
    public String getMovieName() {
        return this.currentPurchase.getMovieName();
    }
    public ArrayList<Integer> getSeat() {
        return this.currentPurchase.getSeat();
    }
    public void setSeat(ArrayList<Integer> seat) {
        this.currentPurchase.setSeat(seat);
    }
}
