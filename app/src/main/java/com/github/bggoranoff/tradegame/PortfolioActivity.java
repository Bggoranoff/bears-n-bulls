package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.bggoranoff.tradegame.util.PositionsAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class PortfolioActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private PositionsAdapter adapter;

    private TextView usernameTextView;
    private ListView positionsListView;

    private void displayPositions() {
        Wallet wallet = CapitalObservable.getInstance().getWallet();
        HashSet<Position> positions = new HashSet<>();
        for(String key : wallet.getPositions().keySet()) {
            positions.addAll(Objects.requireNonNull(wallet.getPositions().get(key)));
        }
        adapter.setPositions(new ArrayList<>(positions));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.tradegame", Context.MODE_PRIVATE);

        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(sharedPreferences.getString("username", "Guest"));

        positionsListView = findViewById(R.id.positionsListView);
        adapter = new PositionsAdapter(this, new ArrayList<>());
        displayPositions();
        positionsListView.setAdapter(adapter);
    }
}