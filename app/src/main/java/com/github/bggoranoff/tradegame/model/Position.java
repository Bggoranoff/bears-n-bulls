package com.github.bggoranoff.tradegame.model;

public class Position {
    private String symbol;
    private float quantity;
    private float price;
    private boolean buy;

    public Position(String symbol, float price, float quantity, boolean buy) {
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.buy = buy;
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

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }
}
