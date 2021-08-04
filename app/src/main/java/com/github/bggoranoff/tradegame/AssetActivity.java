package com.github.bggoranoff.tradegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class AssetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("asset"));
    }
}