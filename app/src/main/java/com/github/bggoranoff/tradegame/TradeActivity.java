package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.Objects;

public class TradeActivity extends AppCompatActivity {

    private Button openButton;
    private LinearLayout linearLayout;

    private void redirectToAssetActivity(View view) {
        // TODO: redirect to asset activity with extra asset id
    }

    private void clickAssetIcon(View view) {
        Toast.makeText(this, view.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    private void fillTable() {
        fillStocksTable();
        fillCommoditiesTable();
        fillIndexTable();
        fillCryptoTable();
        fillForexTable();
    }

    private void fillStocksTable() {
        TableLayout stocksTableLayout = findViewById(R.id.stocksTableLayout);
        for(int i = 0; i < stocksTableLayout.getChildCount(); i++) {
            View rowView = stocksTableLayout.getChildAt(i);
            if(rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                for(int j = 0; j < row.getChildCount(); j++) {
                    View cell = row.getChildAt(j);
                    if(cell instanceof ImageView && !cell.getTag().toString().equals("")) {
                        cell.setOnClickListener(this::clickAssetIcon);
                    }
                }
            }
        }
    }

    private void fillCommoditiesTable() {
        TableLayout stocksTableLayout = findViewById(R.id.commoditiesTableLayout);
        for(int i = 0; i < stocksTableLayout.getChildCount(); i++) {
            View rowView = stocksTableLayout.getChildAt(i);
            if(rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                for(int j = 0; j < row.getChildCount(); j++) {
                    View cell = row.getChildAt(j);
                    if(cell instanceof ImageView && !cell.getTag().toString().equals("")) {
                        cell.setOnClickListener(this::clickAssetIcon);
                    }
                }
            }
        }
    }

    private void fillIndexTable() {
        TableLayout stocksTableLayout = findViewById(R.id.indexTableLayout);
        for(int i = 0; i < stocksTableLayout.getChildCount(); i++) {
            View rowView = stocksTableLayout.getChildAt(i);
            if(rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                for(int j = 0; j < row.getChildCount(); j++) {
                    View cell = row.getChildAt(j);
                    if(cell instanceof ImageView && !cell.getTag().toString().equals("")) {
                        cell.setOnClickListener(this::clickAssetIcon);
                    }
                }
            }
        }
    }

    private void fillCryptoTable() {
        TableLayout stocksTableLayout = findViewById(R.id.cryptoTableLayout);
        for(int i = 0; i < stocksTableLayout.getChildCount(); i++) {
            View rowView = stocksTableLayout.getChildAt(i);
            if(rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                for(int j = 0; j < row.getChildCount(); j++) {
                    View cell = row.getChildAt(j);
                    if(cell instanceof ImageView && !cell.getTag().toString().equals("")) {
                        cell.setOnClickListener(this::clickAssetIcon);
                    }
                }
            }
        }
    }

    private void fillForexTable() {
        TableLayout stocksTableLayout = findViewById(R.id.forexTableLayout);
        for(int i = 0; i < stocksTableLayout.getChildCount(); i++) {
            View rowView = stocksTableLayout.getChildAt(i);
            if(rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                for(int j = 0; j < row.getChildCount(); j++) {
                    View cell = row.getChildAt(j);
                    if(cell instanceof ImageView && !cell.getTag().toString().equals("")) {
                        cell.setOnClickListener(this::clickAssetIcon);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        Objects.requireNonNull(getSupportActionBar()).hide();

        linearLayout = findViewById(R.id.stocksLinearLayout);
        fillTable();

        openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(this::redirectToAssetActivity);
    }
}