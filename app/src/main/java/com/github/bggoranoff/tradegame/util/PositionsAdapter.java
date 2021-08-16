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
import java.util.Locale;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class PositionsAdapter extends BaseAdapter {

    private AppCompatActivity activity;
    private ArrayList<Position> positions;
    private SQLiteDatabase db;
    private static LayoutInflater inflater = null;

    public PositionsAdapter(AppCompatActivity activity, ArrayList<Position> positions, SQLiteDatabase db) {
        this.activity = activity;
        this.positions = positions;
        this.db = db;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void deletePosition(int position) {
        Position positionToDelete = positions.get(position);
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

                    positions.remove(position);
                    notifyDataSetChanged();
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
        String title = positions.get(position).getSymbol();
        title = (positions.get(position).isBuy() ? "Buy " : "Sell ") + title;
        positionTitleView.setText(title);

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
