package com.github.bggoranoff.tradegame.util;

import android.app.ProgressDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TradeWebViewClient extends WebViewClient {
    private ProgressDialog progressDialog;

    public TradeWebViewClient(ProgressDialog progressDialog) {
        setProgressDialog(progressDialog);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
