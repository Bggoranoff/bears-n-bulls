package com.github.bggoranoff.tradegame.observable;

import com.github.bggoranoff.tradegame.model.Wallet;

import java.util.Observable;

public class CapitalObservable extends Observable {

    private static CapitalObservable instance = null;

    public static CapitalObservable getInstance() {
        if(instance == null) {
            instance = new CapitalObservable();
        }

        return instance;
    }

    private float capital = 1000.0f;

    public float getCapital() { return capital; }

    public void setCapital(float capital) {
        if(capital != this.capital) {
            this.capital = capital;
            this.setChanged();
            this.notifyObservers(new ValueKey(ValueName.CAPITAL));
        }
    }

    private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
        this.setChanged();
        this.notifyObservers(new ValueKey(ValueName.WALLET));
    }
}