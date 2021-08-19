package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.bggoranoff.tradegame.util.DatabaseManager;
import com.github.bggoranoff.tradegame.util.PositionsAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class AssetActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SQLiteDatabase db;
    private PositionsAdapter adapter;

    private Stock stock;
    private boolean active;

    private TextView assetPriceView;
    private TextView assetPercentageView;
    private LineChart lineChart;
    private Button buyButton;
    private Button sellButton;
    private ListView positionsListView;


    private void displayStockData(Stock stock, List<HistoricalQuote> history) {
        runOnUiThread(() -> {
            ArrayList<Entry> valueSet = new ArrayList<>();
            ArrayList<String> daySet = new ArrayList<>();
            for(int i = 0; i < history.size(); i++) {
                valueSet.add(new Entry(i, history.get(i).getClose().floatValue()));
                daySet.add(history.get(i).getDate().get(Calendar.DAY_OF_MONTH) + "/" + (history.get(i).getDate().get(Calendar.MONTH) + 1));
            }

            int color = getColor(history.get(0).getClose().compareTo(history.get(history.size() - 1).getClose()) > 0 ? R.color.red : R.color.green);
            LineData data = new LineData(getLineDataSet(valueSet, stock.getSymbol(), color));

            lineChart.setData(data);
            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(daySet));

            lineChart.getXAxis().setTypeface(Typeface.MONOSPACE);
            lineChart.getAxisLeft().setTypeface(Typeface.MONOSPACE);
            lineChart.getAxisRight().setTypeface(Typeface.MONOSPACE);
            lineChart.getLegend().setTypeface(Typeface.MONOSPACE);

            lineChart.getDescription().setText(stock.getName());
            lineChart.getDescription().setTypeface(Typeface.MONOSPACE);
            lineChart.getDescription().setTextSize(15f);

            lineChart.animateXY(2000, 2000);
            lineChart.invalidate();
        });
    }

    private LineDataSet getLineDataSet(ArrayList<Entry> valueSet, String label, int color) {
        LineDataSet lineDataSet = new LineDataSet(valueSet, label);
        lineDataSet.setColor(getColor(R.color.darker_gray));
        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(color);
        lineDataSet.setDrawValues(false);
        return lineDataSet;
    }

    private void updateStockPrice() {
        AsyncTask.execute(() -> {
            try {
                stock = YahooFinance.get(getIntent().getStringExtra("asset"));
                BigDecimal price = stock.getQuote(true).getPrice();
                BigDecimal percentage = stock.getQuote().getChangeInPercent();

                runOnUiThread(() -> {
                    String currentPrice = "$" + price.toString();
                    assetPriceView.setText(currentPrice);

                    String currentPercentage = percentage.toString() + "%";
                    assetPercentageView.setText(currentPercentage);
                    if(percentage.compareTo(BigDecimal.ZERO) < 0) {
                        assetPercentageView.setTextColor(getColor(R.color.red));
                    }

                    int lastIndex = lineChart.getData().getEntryCount() - 1;
                    lineChart.getData().getDataSetByLabel(stock.getSymbol(), true).removeEntry(lastIndex);
                    lineChart.getData().getDataSetByLabel(stock.getSymbol(), true).addEntry(new Entry(lastIndex + 1, price.floatValue()));
                    lineChart.getData().notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                    lineChart.setVisibleXRangeMaximum(120f);
                    lineChart.moveViewToX(lastIndex + 1);

                    Wallet wallet = CapitalObservable.getInstance().getWallet();
                    float stockPrice = stock.getQuote().getPrice().floatValue();
                    try {
                        for (Position position : Objects.requireNonNull(wallet.getPositions().get(stock.getSymbol()))) {
                            position.setCurrentPrice(stockPrice);
                        }
                        CapitalObservable.getInstance().setWallet(wallet);
                        adapter.displayPositionsProfit(stock.getSymbol());
                    } catch(NullPointerException ignored) {

                    }

                    Date lastTradeTime = stock.getQuote().getLastTradeTime().getTime();
                    Date currentTime = new Date();

                    Toast.makeText(this, lastTradeTime.toString(), Toast.LENGTH_SHORT).show();

                    if(Math.abs(lastTradeTime.getTime() - currentTime.getTime()) < 1000 * 60 * 30) {
                        buyButton.setEnabled(true);
                        sellButton.setEnabled(true);
                        buyButton.setBackgroundColor(getResources().getColor(R.color.green, getTheme()));
                        sellButton.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
                    } else {
                        buyButton.setEnabled(false);
                        sellButton.setEnabled(false);
                        buyButton.setBackgroundColor(getResources().getColor(R.color.gray, getTheme()));
                        sellButton.setBackgroundColor(getResources().getColor(R.color.gray, getTheme()));
                    }

                    if(active) {
                        new Handler().postDelayed(this::updateStockPrice, 5000);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void openPosition(View view) {
        boolean buy = view.getId() == R.id.buyButton;

        final EditText dialogEditText = new EditText(this);
        dialogEditText.setHint("Amount");
        dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle((buy ? "Buy " : "Sell ") + stock.getSymbol())
                .setMessage("Enter the amount you want to invest:")
                .setView(dialogEditText)
                .setCancelable(false)
                .setPositiveButton(buy ? "Buy" : "Sell", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            try {
                float investedAmount = Float.parseFloat(dialogEditText.getText().toString());
                Wallet wallet = CapitalObservable.getInstance().getWallet();
                if (investedAmount <= wallet.getMoney()) {
                    float currentPrice = stock.getQuote().getPrice().floatValue();

                    Position openedPosition = new Position(
                            stock.getSymbol(),
                            new Date().getTime(),
                            currentPrice,
                            investedAmount / currentPrice,
                            buy
                    );

                    wallet.addPosition(openedPosition);
                    adapter.getPositions().add(openedPosition);
                    adapter.notifyDataSetChanged();

                    CapitalObservable.getInstance().setWallet(wallet);
                    saveWallet();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Amount not available!", Toast.LENGTH_SHORT).show();
                }
            } catch(NumberFormatException ex) {
                Toast.makeText(this, "Enter a valid amount!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveWallet() {
        Wallet wallet = CapitalObservable.getInstance().getWallet();
        sharedPreferences.edit().putFloat("money", wallet.getMoney()).apply();
        DatabaseManager.deletePositions(db, stock.getSymbol());
        HashSet<Position> positions = Objects.requireNonNull(wallet.getPositions().get(stock.getSymbol()));
        for(Position position : positions) {
            DatabaseManager.savePosition(db, position);
        }
    }

    private void displayPositions() {
        Wallet wallet = CapitalObservable.getInstance().getWallet();
        HashSet<Position> positions = wallet.getPositions().get(getIntent().getStringExtra("asset"));
        if(positions == null) {
            positions = new HashSet<>();
        }
        adapter.setPositions(new ArrayList<>(positions));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.tradegame", Context.MODE_PRIVATE);
        db = openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);

        assetPriceView = findViewById(R.id.assetPriceView);
        assetPercentageView = findViewById(R.id.assetPercentView);

        lineChart = findViewById(R.id.lineChart);
        String stockId = getIntent().getStringExtra("asset");
        AsyncTask.execute(() -> {
            try {
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                from.add(Calendar.MONTH, -1);
                stock = YahooFinance.get(stockId, from, to, Interval.DAILY);
                displayStockData(stock, stock.getHistory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(this::openPosition);

        sellButton = findViewById(R.id.sellButton);
        sellButton.setOnClickListener(this::openPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
        active = false;
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

        active = true;
        updateStockPrice();
    }
}