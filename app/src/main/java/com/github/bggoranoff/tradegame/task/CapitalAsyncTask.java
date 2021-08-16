package com.github.bggoranoff.tradegame.task;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class CapitalAsyncTask extends AsyncTask<Void, Void, Void> {

    private AppCompatActivity activity;

    public CapitalAsyncTask(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Wallet wallet = CapitalObservable.getInstance().getWallet();
        String[] symbols = wallet.getPositions().keySet().toArray(new String[wallet.getPositions().size()]);

        try {
            Map<String, Stock> stocks = YahooFinance.get(symbols);
            float capital = 0.0f;
            for(String symbol : symbols) {
                float stockPrice = Objects.requireNonNull(stocks.get(symbol)).getQuote().getPrice().floatValue();
                for(Position position : Objects.requireNonNull(wallet.getPositions().get(symbol))) {
                    capital += position.getQuantity() * stockPrice;
                }
            }
            final float calculatedCapital = capital + wallet.getMoney();
            activity.runOnUiThread(() -> {
                CapitalObservable.getInstance().setCapital(calculatedCapital);
            });
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
