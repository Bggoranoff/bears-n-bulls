package com.github.bggoranoff.tradegame.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.bggoranoff.tradegame.R;
import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class PositionsAdapter extends BaseAdapter {

    private AppCompatActivity activity;
    private ArrayList<Position> positions;
    private final SQLiteDatabase db;
    private static LayoutInflater inflater = null;

    public PositionsAdapter(AppCompatActivity activity, ArrayList<Position> positions, SQLiteDatabase db) {
        this.activity = activity;
        this.positions = positions;
        this.db = db;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void displayPositionsProfit() {
        HashMap<String, HashSet<Position>> currentPositions = CapitalObservable.getInstance().getWallet().getPositions();
        positions = new ArrayList<>();
        for(String key : currentPositions.keySet()) {
            positions.addAll(Objects.requireNonNull(currentPositions.get(key)));
        }
        notifyDataSetChanged();
    }

    public void displayPositionsProfit(String key) {
        HashMap<String, HashSet<Position>> currentPositions = CapitalObservable.getInstance().getWallet().getPositions();
        positions = new ArrayList<>();
        positions.addAll(Objects.requireNonNull(currentPositions.get(key)));
        notifyDataSetChanged();
    }

    private void deletePosition(int position) {
        Position positionToDelete = positions.remove(position);
        notifyDataSetChanged();

        AsyncTask.execute(() -> {
            try {
                Stock stock = YahooFinance.get(positionToDelete.getSymbol());
                BigDecimal price = stock.getQuote(true).getPrice();
                activity.runOnUiThread(() -> {
                    CapitalObservable.getInstance().getWallet().closePosition(positionToDelete, price.floatValue());
                    CapitalObservable.getInstance().setWallet(CapitalObservable.getInstance().getWallet());
                    DatabaseManager.deletePosition(db, positionToDelete);
                    activity.getSharedPreferences("com.github.bggoranoff.tradegame", Context.MODE_PRIVATE)
                            .edit().putFloat("money", CapitalObservable.getInstance().getCapital()).apply();
                });
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void deleteAll() {
        CapitalObservable.getInstance().setWallet(new Wallet());
        CapitalObservable.getInstance().setCapital(1000.0f);

        activity.getSharedPreferences("com.github.bggoranoff.tradegame", Context.MODE_PRIVATE)
                .edit().putFloat("money", CapitalObservable.getInstance().getCapital()).apply();
        DatabaseManager.wipe(db);

        positions = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void displayPositionDetails(String title, int position) {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(positions.get(position).toString())
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public int getCount() {
        return positions.size();
    }

    @Override
    public Position getItem(int position) {
        return positions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
            view = inflater.inflate(R.layout.position_row, null);
        }

        TextView positionTitleView = view.findViewById(R.id.positionTitle);
        String title = (positions.get(position).isBuy() ? "Buy " : "Sell ") + positions.get(position).getSymbol();
        positionTitleView.setText(title);
        positionTitleView.setOnClickListener(v -> displayPositionDetails(title, position));

        TextView positionProfitView = view.findViewById(R.id.positionProfitView);
        float profit = (1 - positions.get(position).getPrice() / positions.get(position).getCurrentPrice()) * 100;

        if(!positions.get(position).isBuy()) {
            profit = -profit;
        }
        
        String profitPercentage = String.format(Locale.ENGLISH, "%.2f", profit) + "%";
        positionProfitView.setText(profitPercentage);
        positionProfitView.setTextColor(activity.getResources().getColor(profit >= 0 ? R.color.green : R.color.red, activity.getTheme()));

        TextView positionTimeView = view.findViewById(R.id.positionTime);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = df.format(new Date(positions.get(position).getTimeInMillis()));
        positionTimeView.setText(date);

        ImageView positionDeleteView = view.findViewById(R.id.positionDeleteIcon);
        positionDeleteView.setOnClickListener(v -> deletePosition(position));

        return view;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
        notifyDataSetChanged();
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }
}
