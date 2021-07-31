package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;

import com.github.bggoranoff.tradegame.util.TradeWebViewClient;

import java.util.Objects;

public class ManualActivity extends AppCompatActivity {

    private WebView manualView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        Objects.requireNonNull(getSupportActionBar()).hide();

        manualView = findViewById(R.id.manualWebView);
        manualView.getSettings().setJavaScriptEnabled(true);
        manualView.setWebViewClient(new TradeWebViewClient(new ProgressDialog(ManualActivity.this)));
        manualView.loadUrl("https://www.etoro.com/stocks/trading-and-investing-in-stocks/");
    }
}