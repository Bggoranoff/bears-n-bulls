package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class AssetActivity extends AppCompatActivity {

    private Stock stock;

    private TextView assetPriceView;
    private TextView assetPercentageView;
    private LineChart lineChart;
    private SeekBar seekBar;

    private void displayStockData(Stock stock, List<HistoricalQuote> history) {
        runOnUiThread(() -> {

            String currentPrice = "$" + stock.getQuote().getPrice().toString();
            assetPriceView.setText(currentPrice);

            BigDecimal percentage = stock.getQuote().getChangeInPercent();
            String currentPercentage = percentage.toString() + "%";
            assetPercentageView.setText(currentPercentage);
            if(percentage.compareTo(BigDecimal.ZERO) < 0) {
                assetPercentageView.setTextColor(getColor(R.color.red));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        Objects.requireNonNull(getSupportActionBar()).hide();

        assetPriceView = findViewById(R.id.assetPriceView);
        assetPercentageView = findViewById(R.id.assetPercentView);

        lineChart = findViewById(R.id.lineChart);
        seekBar = findViewById(R.id.lineChartSeekBar);

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
    }
}