package com.github.bggoranoff.tradegame.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Wallet {
    private float money;
    private HashMap<String, HashSet<Position>> positions;

    public Wallet() {
        this.money = 1000.0f;
        this.positions = new HashMap<>();
    }

    public Wallet(float money, HashMap<String, HashSet<Position>> positions) {
        this.money = money;
        this.positions = positions;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public HashMap<String, HashSet<Position>> getPositions() {
        return positions;
    }

    public void addPosition(Position position) {
        if(positions.containsKey(position.getSymbol())) {
            positions.put(position.getSymbol(), new HashSet<>());
        }
        money -= position.getPrice() * position.getQuantity();
        Objects.requireNonNull(positions.get(position.getSymbol())).add(position);
    }

    public void closePosition(Position position, float currentPrice) {
        if(positions.containsKey(position.getSymbol()) && positions.get(position.getSymbol()).contains(position)) {
            Objects.requireNonNull(positions.get(position.getSymbol())).remove(position);
            money += position.getQuantity() * currentPrice;
        }
    }
}
