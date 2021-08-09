package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.bggoranoff.tradegame.model.Wallet;
import com.github.bggoranoff.tradegame.observable.CapitalObservable;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    private SharedPreferences sharedPreferences;
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
                "com.github.bggoranoff.tradinggame",
                Context.MODE_PRIVATE
        );

        usernameEditText = findViewById(R.id.editTextUsername);
        usernameEditText.setText(sharedPreferences.getString("username", ""));

        layout = findViewById(R.id.homeLayout);
        layout.setOnClickListener(this::hideKeyboard);

        capitalView = findViewById(R.id.capitalTextView);
        capitalView.setOnClickListener(this::hideKeyboard);

        tradeButton = findViewById(R.id.tradeButton);
        tradeButton.setOnClickListener(this::redirectToTradeActivity);

        portfolioButton = findViewById(R.id.portfolioButton);
        portfolioButton.setOnClickListener(this::redirectToPortfolioActivity);

        manualView = findViewById(R.id.manualTextView);
        manualView.setOnClickListener(this::redirectToManualActivity);

        CapitalObservable.getInstance().setWallet(new Wallet());
        CapitalObservable.getInstance().setCapital(1000.0f);
        // TODO: retrieve saved wallet and calculate capital
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            hideKeyboard(v);
        }
        return false;
    }
}