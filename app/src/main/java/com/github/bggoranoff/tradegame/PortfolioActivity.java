package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.bggoranoff.tradegame.util.DatabaseManager;
import com.github.bggoranoff.tradegame.util.PositionsAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class PortfolioActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SQLiteDatabase db;
    private PositionsAdapter adapter;

    private TextView usernameTextView;
    private TextView capitalTextView;
    private ListView positionsListView;
    private Button resetButton;

    private void displayPositions() {
        Wallet wallet = CapitalObservable.getInstance().getWallet();
        HashSet<Position> positions = new HashSet<>();
        for(String key : wallet.getPositions().keySet()) {
            positions.addAll(Objects.requireNonNull(wallet.getPositions().get(key)));
        }
        adapter.setPositions(new ArrayList<>(positions));
    }

    private void reset(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reset portfolio")
                .setMessage("Are you sure you want to reset your portfolio?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    adapter.deleteAll();
                    capitalTextView.setText(String.format("$%.2f", CapitalObservable.getInstance().getCapital()));
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.tradegame", Context.MODE_PRIVATE);

        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(sharedPreferences.getString("username", "Guest"));

        capitalTextView = findViewById(R.id.capitalTextView);
        capitalTextView.setText(String.format("$%.2f", CapitalObservable.getInstance().getCapital()));

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this::reset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);

        positionsListView = findViewById(R.id.positionsListView);
        adapter = new PositionsAdapter(this, new ArrayList<>(), db);
        displayPositions();
        positionsListView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }
}