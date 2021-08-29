package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.bggoranoff.tradegame.component.AssetView;
import com.github.bggoranoff.tradegame.model.Asset;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.bggoranoff.tradegame.task.CapitalAsyncTask;
import com.github.bggoranoff.tradegame.util.AssetConstants;
import com.github.bggoranoff.tradegame.util.Extras;
import com.github.bggoranoff.tradegame.util.IconsSelector;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import static com.github.bggoranoff.tradegame.util.AssetConstants.*;

public class TradeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private Timer timer;
    private CapitalAsyncTask capitalTask;
    private Observer capitalObserver;

    private Button openButton;
    private LinearLayout linearLayout;
    private TextView textView;
    private LinearLayout profileLayout;
    private TextView profileUsernameView;
    private TextView profileCapitalView;
    private View lastClicked = null;

    private void redirectToAssetActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), AssetActivity.class);
        intent.putExtra("asset", textView.getText().toString());
        startActivity(intent);
    }

    private void redirectToPortfolioActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
        startActivity(intent);
        finish();
    }

    private void clickAssetIcon(View view) {
        if(lastClicked != null) {
            lastClicked.setAlpha(1.0f);
        } else {
            openButton.setEnabled(true);
        }
        lastClicked = view;
        textView.setText(view.getTag().toString());
        view.setAlpha(.5f);
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
        fillTable(STOCKS, stocksTableLayout);
    }

    private void fillCommoditiesTable() {
        TableLayout commoditiesTableLayout = linearLayout.findViewById(R.id.commoditiesTableLayout);
        fillTable(COMMODITIES, commoditiesTableLayout);
    }

    private void fillIndexTable() {
        TableLayout indexTableLayout = linearLayout.findViewById(R.id.indexTableLayout);
        fillTable(INDEX, indexTableLayout);
    }

    private void fillCryptoTable() {
        TableLayout cryptoTableLayout = linearLayout.findViewById(R.id.cryptoTableLayout);
        fillTable(CRYPTO, cryptoTableLayout);
    }

    private void fillForexTable() {
        TableLayout forexTableLayout = linearLayout.findViewById(R.id.forexTableLayout);
        fillTable(FOREX, forexTableLayout);
    }

    private void fillTable(HashMap<String, Asset> assets, TableLayout tableLayout) {
        int excessViewsCount = 4 - (assets.size() % 4 == 0 ? 4 : assets.size() % 4);

        TableRow currentRow = new TableRow(this);
        int currentIndex = 0;

        for(String currency : assets.keySet()) {
            if(currentIndex % 4 == 0) {
                if(currentIndex != 0) {
                    TableRow finalCurrentRow = currentRow;
                    tableLayout.post(() -> {
                        tableLayout.addView(finalCurrentRow);
                    });
                }
                currentRow = new TableRow(this);
                currentRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            }

            Asset asset = assets.get(currency);
            assert asset != null;

            AssetView cell = new AssetView(this, asset);
            cell.setOnClickListener(this::clickAssetIcon);
            currentRow.addView(cell);

            currentIndex++;
        }

        if(excessViewsCount > 0) {
            for(int i = 0; i < excessViewsCount; i++) {
                View emptyView = new AssetView(this);

                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, IconsSelector.getInDps(this, AssetConstants.DIMENSIONS), 1.0f);
                int m = IconsSelector.getInDps(this, AssetConstants.MARGIN);
                layoutParams.setMargins(m, m, m, m);
                emptyView.setLayoutParams(layoutParams);

                emptyView.setAlpha(0.0f);

                currentRow.addView(emptyView);
            }
            TableRow finalCurrentRow = currentRow;
            tableLayout.post(() -> {
                tableLayout.addView(finalCurrentRow);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        sharedPreferences = getSharedPreferences(
                MainActivity.PACKAGE,
                Context.MODE_PRIVATE
        );

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        profileUsernameView = getSupportActionBar().getCustomView().findViewById(R.id.profileUsernameView);
        profileUsernameView.setText(sharedPreferences.getString(Extras.USERNAME, MainActivity.DEFAULT_USERNAME));

        profileCapitalView = getSupportActionBar().getCustomView().findViewById(R.id.profileCapitalView);
        capitalObserver = (observable, arg) -> {
            profileCapitalView.setText(String.format(Locale.ENGLISH, "$%.2f", CapitalObservable.getInstance().getCapital()));
        };

        profileLayout = Objects.requireNonNull(getSupportActionBar()).getCustomView().findViewById(R.id.profileLayout);
        profileLayout.setOnClickListener(this::redirectToPortfolioActivity);

        linearLayout = findViewById(R.id.stocksLinearLayout);
        textView = findViewById(R.id.stockTitleView);
        fillTable();

        openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(this::redirectToAssetActivity);
        openButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        CapitalObservable.getInstance().addObserver(capitalObserver);
        profileCapitalView.setText(String.format(Locale.ENGLISH, "$%.2f", CapitalObservable.getInstance().getCapital()));

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                capitalTask = new CapitalAsyncTask(TradeActivity.this);
                capitalTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 0, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        CapitalObservable.getInstance().deleteObserver(capitalObserver);
        timer.cancel();
        capitalTask.cancel(true);
    }
}