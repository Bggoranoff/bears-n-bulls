package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class AssetActivity extends AppCompatActivity {

    private Stock stock;
    private boolean active;

    private TextView assetPriceView;
    private TextView assetPercentageView;
    private LineChart lineChart;
    private Button buyButton;
    private Button sellButton;


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
            lineChart.getDescription().setText(stock.getName());
            lineChart.getDescription().setTextSize(15f);

            lineChart.animateXY(2000, 2000);
            lineChart.invalidate();
        });
    }

    private LineDataSet getLineDataSet(ArrayList<Entry> valueSet, String label, int color) {
        LineDataSet lineDataSet = new LineDataSet(valueSet, label);
        lineDataSet.setColor(getColor(R.color.darker_gray));
        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setCircleHoleRadius(1f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
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

                    if(active) {
                        new Handler().postDelayed(this::updateStockPrice, 10000);
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
        dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle((buy ? "Buy " : "Sell ") + stock.getSymbol())
                .setMessage("Enter the amount you want to invest:")
                .setView(dialogEditText)
                .setPositiveButton(buy ? "Buy" : "Sell", (dialog, which) -> {
                    Wallet wallet = CapitalObservable.getInstance().getWallet();
                    wallet.addPosition(new Position(
                            stock.getSymbol(),
                            stock.getQuote().getPrice().floatValue(),
                            Float.parseFloat(dialogEditText.getText().toString()),
                            buy
                    ));
                    CapitalObservable.getInstance().setWallet(wallet);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        Objects.requireNonNull(getSupportActionBar()).hide();

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
    protected void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        updateStockPrice();
    }
}