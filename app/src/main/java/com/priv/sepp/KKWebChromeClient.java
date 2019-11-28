package com.priv.sepp;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class KKWebChromeClient extends WebChromeClient {
    public void onProgressChanged(WebView webView, int i) {
        super.onProgressChanged(webView, i);
    }

    public void onReceivedTitle(WebView webView, String str) {
        super.onReceivedTitle(webView, str);
    }
}
