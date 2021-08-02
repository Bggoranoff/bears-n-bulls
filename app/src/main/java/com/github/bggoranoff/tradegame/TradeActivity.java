package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Objects;

public class TradeActivity extends AppCompatActivity {

    private Button openButton;
    private LinearLayout linearLayout;
    private TextView textView;

    private int lastClicked = -1;

    private void redirectToAssetActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), AssetActivity.class);
        intent.putExtra("asset", textView.getText().toString());
        startActivity(intent);
    }

    private void clickAssetIcon(View view) {
        if(lastClicked != -1) {
            findViewById(lastClicked).setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.light_gray_icon));
        } else {
            openButton.setEnabled(true);
        }
        lastClicked = view.getId();
        textView.setText(view.getTag().toString());
        view.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.dark_gray_icon));
    }

    private void fillTable() {
        fillStocksTable();
        fillCommoditiesTable();
        fillIndexTable();
        fillCryptoTable();
        fillForexTable();
    }

    private void fillStocksTable() {
        TableLayout stocksTableLayout = linearLayout.findViewById(R.id.stocksTableLayout);
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
        TableLayout stocksTableLayout = linearLayout.findViewById(R.id.commoditiesTableLayout);
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
        TableLayout stocksTableLayout = linearLayout.findViewById(R.id.indexTableLayout);
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
        TableLayout stocksTableLayout = linearLayout.findViewById(R.id.cryptoTableLayout);
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
        TableLayout stocksTableLayout = linearLayout.findViewById(R.id.forexTableLayout);
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
        textView = findViewById(R.id.stockTitleView);
        fillTable();

        openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(this::redirectToAssetActivity);
        openButton.setEnabled(false);
    }
}