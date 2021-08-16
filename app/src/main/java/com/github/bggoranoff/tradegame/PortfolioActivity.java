package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.bggoranoff.tradegame.task.CapitalAsyncTask;
import com.github.bggoranoff.tradegame.util.DatabaseManager;
import com.github.bggoranoff.tradegame.util.PositionsAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class PortfolioActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SQLiteDatabase db;
    private PositionsAdapter adapter;
    private Observer capitalObserver;
    private CapitalAsyncTask capitalTask;
    private Timer timer;

    private TextView usernameTextView;
    private TextView capitalView;
    private TextView profitView;
    private ListView positionsListView;
    private Button resetButton;

    private String month;
    private float monthlyBase;

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
                    capitalView.setText(String.format("$%.2f", CapitalObservable.getInstance().getCapital()));
                    sharedPreferences.edit().remove(month).apply();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void saveMonthlyBase() {
        DateFormat df = new SimpleDateFormat("MM/yyyy", Locale.ENGLISH);
        Date currentDate = new Date();
        month = df.format(currentDate);
        if(!sharedPreferences.contains(month)) {
            Toast.makeText(this, "Updating shared preferences " + CapitalObservable.getInstance().getCapital() + "!", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);

            int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, max);

            Date lastDate = calendar.getTime();
            String lastMonth = df.format(lastDate);

            sharedPreferences.edit().putFloat(month, CapitalObservable.getInstance().getCapital()).apply();
            sharedPreferences.edit().remove(lastMonth).apply();
        }
        monthlyBase = sharedPreferences.getFloat(month, 1000.0f);
    }

    private void displayMonthlyProfit(float currentCapital) {
        float profit = 1 - monthlyBase / currentCapital;
        String formattedPercentage = (profit > 0 ? "+" : "") + String.format(Locale.ENGLISH, "%.2f", profit * 100) + "%";
        profitView.setText(formattedPercentage);
        profitView.setTextColor(getResources().getColor(profit >= 0 ? R.color.green : R.color.red, getTheme()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.tradegame", Context.MODE_PRIVATE);

        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(sharedPreferences.getString("username", "Guest"));

        profitView = findViewById(R.id.monthlyProfitView);
        saveMonthlyBase();
        displayMonthlyProfit(CapitalObservable.getInstance().getCapital());

        capitalView = findViewById(R.id.capitalTextView);
        capitalObserver = (observable, arg) -> {
            capitalView.setText(String.format("$%.2f", CapitalObservable.getInstance().getCapital()));
            displayMonthlyProfit(CapitalObservable.getInstance().getCapital());
        };

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this::reset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);

        CapitalObservable.getInstance().addObserver(capitalObserver);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                capitalTask = new CapitalAsyncTask(PortfolioActivity.this);
                capitalTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 0, 3000);

        positionsListView = findViewById(R.id.positionsListView);
        adapter = new PositionsAdapter(this, new ArrayList<>(), db);
        displayPositions();
        positionsListView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();

        CapitalObservable.getInstance().deleteObserver(capitalObserver);
        timer.cancel();
        capitalTask.cancel(true);
    }
}