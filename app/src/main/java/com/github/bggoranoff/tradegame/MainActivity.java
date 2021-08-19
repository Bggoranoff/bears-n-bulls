package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;
import com.github.bggoranoff.tradegame.task.CapitalAsyncTask;
import com.github.bggoranoff.tradegame.util.DatabaseManager;
import com.github.bggoranoff.tradegame.util.IconsSelector;

import java.util.Locale;
import java.util.Objects;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    private SharedPreferences sharedPreferences;
    private SQLiteDatabase db;
    private Observer capitalObserver;
    private CapitalAsyncTask capitalTask;
    private Timer timer;

    private EditText usernameEditText;
    private ConstraintLayout layout;
    private TextView capitalView;
    private Button tradeButton;
    private Button portfolioButton;
    private TextView manualView;

    private void redirectToManualActivity(View view) {
        hideKeyboard(view);
        Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
        startActivity(intent);
    }

    private void redirectToPortfolioActivity(View view) {
        hideKeyboard(view);
        Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
        startActivity(intent);
    }

    private void redirectToTradeActivity(View view) {
        hideKeyboard(view);
        Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard(View view) {
        saveUsername();
        if(getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void saveUsername() {
        String username = usernameEditText.getText().toString();
        sharedPreferences.edit().putString("username", username).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences(
                "com.github.bggoranoff.tradegame",
                Context.MODE_PRIVATE
        );

        usernameEditText = findViewById(R.id.editTextUsername);
        usernameEditText.setText(sharedPreferences.getString("username", ""));

        layout = findViewById(R.id.homeLayout);
        layout.setOnClickListener(this::hideKeyboard);

        capitalView = findViewById(R.id.capitalTextView);
        capitalView.setOnClickListener(this::hideKeyboard);

        portfolioButton = findViewById(R.id.portfolioButton);
        portfolioButton.setOnClickListener(this::redirectToPortfolioActivity);

        tradeButton = findViewById(R.id.tradeButton);
        tradeButton.setOnClickListener(this::redirectToTradeActivity);
        portfolioButton.post(() -> {
            tradeButton.setWidth(portfolioButton.getWidth());
        });

        manualView = findViewById(R.id.manualTextView);
        manualView.setOnClickListener(this::redirectToManualActivity);

        float money = sharedPreferences.getFloat("money", 1000.0f);
        capitalObserver = (observable, arg) -> {
            capitalView.setText(String.format(Locale.ENGLISH, "$%.2f", CapitalObservable.getInstance().getCapital()));
        };

        db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);
        Wallet wallet = DatabaseManager.getWallet(db);
        wallet.setMoney(money);

        CapitalObservable.getInstance().setWallet(wallet);
        CapitalObservable.getInstance().setCapital(wallet.getMoney());
    }

    @Override
    protected void onResume() {
        super.onResume();

        capitalView.setText(String.format(Locale.ENGLISH, "$%.2f", CapitalObservable.getInstance().getCapital()));
        CapitalObservable.getInstance().addObserver(capitalObserver);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                capitalTask = new CapitalAsyncTask(MainActivity.this);
                capitalTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 0, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();

        CapitalObservable.getInstance().deleteObserver(capitalObserver);
        capitalTask.cancel(true);
        timer.cancel();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            hideKeyboard(v);
        }
        return false;
    }
}