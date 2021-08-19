package com.github.bggoranoff.tradegame.model;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Position {
    private String symbol;
    private float quantity;
    private float price;
    private float currentPrice;
    private boolean buy;
    private long timeInMillis;

    public Position(String symbol,long timeInMillis , float price, float quantity, boolean buy) {
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.buy = buy;
        this.timeInMillis = timeInMillis;
        this.currentPrice = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @NonNull
    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH);
        String date = df.format(new Date(timeInMillis));
        return String.format(Locale.ENGLISH, "Opened on: %s\nInvested: $%.2f\nQuantity: %.6f", date, quantity * price, quantity);
    }
}
