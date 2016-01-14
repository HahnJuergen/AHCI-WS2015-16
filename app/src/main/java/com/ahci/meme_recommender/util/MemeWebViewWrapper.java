package com.ahci.meme_recommender.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;

import de.ahci.memewebview.MemeWebView;


/*
 * @TODO this needs lots of work!!
 * The decrease in loading times for 9gag probably worth it (if we can get it done).
 *
 *
 */
/**
 *
 */
public class MemeWebViewWrapper {

    private MemeWebView[] webViews;
    private RelativeLayout root;

    private int currentViewIndex;
    private int currentLoadIndex;

    public MemeWebViewWrapper(Context context, RelativeLayout root, int howManyMore) {
        this.root = root;

        webViews = new MemeWebView[1 + howManyMore];
        webViews[0] = (MemeWebView) root.findViewById(R.id.webview);
        for(int i = 0; i < howManyMore; i++) {
            webViews[i + 1] = copyBasicWebview(context, webViews[0]);
        }

        setupWebViews();

        webViews[0].bringToFront();
        currentViewIndex = 0;
        currentLoadIndex = 0;
    }

    private void setupWebViews() {
        for(int i = 0; i < webViews.length; i++) {
            webViews[i].getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36\n");
            webViews[i].getSettings().setJavaScriptEnabled(true);
            webViews[i].getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        }
    }

    public void loadUrl(String url) {
        webViews[currentLoadIndex].loadUrl(url);
        currentLoadIndex++;
        if(currentLoadIndex >= webViews.length) currentLoadIndex = 0;
    }

    public void showNext() {
//        webViews[currentViewIndex].loadUrl("about:blank");
//        webViews[currentViewIndex].setVisibility(View.INVISIBLE);

        currentViewIndex++;
        if(currentViewIndex >= webViews.length) currentViewIndex = 0;
        webViews[currentViewIndex].bringToFront();

        webViews[currentViewIndex].setVisibility(View.VISIBLE);
    }

    private MemeWebView copyBasicWebview(Context context, MemeWebView basic) {
        MemeWebView copy = new MemeWebView(context);
        root.addView(copy, basic.getLayoutParams());
        copy.setVisibility(View.INVISIBLE);

        return copy;
    }

}
