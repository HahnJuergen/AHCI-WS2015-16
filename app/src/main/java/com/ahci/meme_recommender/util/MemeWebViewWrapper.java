package com.ahci.meme_recommender.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;

import de.ahci.memewebview.MemeWebView;


/*
 * @TODO this needs lots of work!!
 * The decrease in loading times for 9gag probably worth it (if we can get it done).
 * @TODO This works better now after introducing the meme list, but I'm still not entirely happy.
 * I don't really see any bugs, but we should disable loading new memes (in the main activity)
 * at least until switching the views is complete or check if the background has loaded before a user can
 * switch. (Or at least the one in the front has finished loading before a user can switch...)
 */
/**
 *
 */
public class MemeWebViewWrapper {

    private static boolean LOG = false;

    private WebView backWebView;
    private WebView frontWebView;
    private RelativeLayout root;

    private String urlFront, urlBack;

    public MemeWebViewWrapper(Context context, RelativeLayout root) {
        this.root = root;

        frontWebView = (WebView) root.findViewById(R.id.webview);
        backWebView = copyBasicWebview(context, frontWebView);

        setupWebViews();

        frontWebView.bringToFront();
    }

    private void setupWebViews() {
        WebView[] webViews = new WebView[] {frontWebView, backWebView};
        for(int i = 0; i < webViews.length; i++) {
            webViews[i].getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36\n");
            webViews[i].getSettings().setJavaScriptEnabled(true);
            webViews[i].getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        }
    }

    public void showBackWebView() {
        if(LOG)Log.d("ahci_meme_web_view", "Switching web views");
        backWebView.bringToFront();
        WebView tmp = backWebView;
        backWebView = frontWebView;
        frontWebView = tmp;

        String tmpUrl = urlBack;
        urlBack = urlFront;
        urlFront = tmpUrl;
    }

    public void loadUrlInBackground(String url) {
        if(LOG) Log.d("ahci_meme_web_view", "loading in background: " + url);
        urlBack = url;
        backWebView.loadUrl(url);
    }

    public void loadUrlInFront(String url) {
        if(LOG) Log.d("ahci_meme_web_view", "loading in front: " + url);
        urlFront = url;
        frontWebView.loadUrl(url);
    }

    private WebView copyBasicWebview(Context context, WebView basic) {
        WebView copy = new MemeWebView(context);
        root.addView(copy, basic.getLayoutParams());

        return copy;
    }

    // @TODO this is part of the reason the "next meme" button must wait until this is fully loaded:
    // might still be the previous meme otherwise
    public String getCurrentMemeURL() {
        return urlFront;
    }

}
