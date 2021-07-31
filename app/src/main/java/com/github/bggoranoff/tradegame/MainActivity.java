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

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    private SharedPreferences sharedPreferences;
    private EditText usernameEditText;
    private ConstraintLayout layout;
    private TextView capitalView;
    private Button tradeButton;

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
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            hideKeyboard(v);
        }
        return false;
    }
}