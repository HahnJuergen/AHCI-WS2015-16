package de.ahci.memewebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * MemeView: loads URLs from some sites differently.
 */
public class MemeWebView extends WebView {

    private static final int MEME_LOADED_SUCCESSFULLY = 0;
    private static final int MEME_NOT_LOADED = 1;

    private Handler onMemeDownloadedHandler;

    public MemeWebView(Context context) {
        super(context);
        setupMemeWebView();
    }

    public MemeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupMemeWebView();
    }

    public MemeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupMemeWebView();
    }

    private void setupMemeWebView() {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return true;
            }
        });
        this.setWebChromeClient(new WebChromeClientWithAlert(getContext()));
        setupHandler();
    }

    private void setupHandler() {
        onMemeDownloadedHandler = new Handler() {
            @Override
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MEME_LOADED_SUCCESSFULLY:
                        show9GagSite(Jsoup.parse(msg.getData().getString("html")));
                        break;
                    case MEME_NOT_LOADED:
                        showError();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * @TODO show error in HTML, or create some sort of callback that handles this error and
     * automatically loads the next meme and informs the server or something.
     */
    private void showError() {

    }

    /**
     * Modifies the 9Gag site to include the custom made stylesheet and script (@TODO)
     *
     * @param doc
     */
    private void show9GagSite(Document doc) {
        Log.v("ahci_meme_view", "showing 9gag site");

        fix9GagSiteForDisplay(doc);

        loadDataWithBaseURL("http://9gag.com", doc.toString(), "text/html", "UTF-8", null);
    }

    private void fix9GagSiteForDisplay(Document doc) {
        StringBuilder style = new StringBuilder();
        style.append("<style>");
        try {
            style.append(loadAsset("9gag_style.css"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        style.append("</style>");
        doc.head().append(style.toString());

        Elements body = doc.getElementsByTag("body");
        if(body.size() > 0) {
            StringBuilder script = new StringBuilder();
            script.append("<script>");
            try {
                script.append(loadAsset("9gag_script.js"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            script.append("</script>");
            body.get(0).append(script.toString());
        }
    }

    /**
     * The MemeWebView handles this differently than the other web views if it recognizes the url.
     *
     * @param url
     */
    @Override
    public void loadUrl(String url) {
        Log.v("ahci_meme_view", "loading url: " + url);
        if (url.startsWith("http://9gag.com/")) {

            load9GagMeme(url);
        } else {
            super.loadUrl(url);
        }
    }

    private void load9GagMeme(String url) {
        Log.v("ahci_meme_view", "loading 9gag meme: " + url);
        new Thread(new MemeDownloadRunnable(url, onMemeDownloadedHandler)).start();
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    private class MemeDownloadRunnable implements Runnable {

        String url;
        Handler handler;

        private MemeDownloadRunnable(String url, Handler handler) {
            this.url = url;
            this.handler = handler;
        }

        @Override
        public void run() {
            Message message = new Message();
            Bundle data = new Bundle();
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36\n")
                        .referrer("http://www.google.com")
                        .get();
                data.putString("html", doc.toString());
                message.what = MEME_LOADED_SUCCESSFULLY;
                message.setData(data);
            } catch (Exception e) {
                e.printStackTrace();
                message.what = MEME_NOT_LOADED;
            }

            handler.sendMessage(message);
        }
    }

    private String loadAsset(String assetPath) throws IOException {
        StringBuilder buf = new StringBuilder();
        InputStream assetAsString = getContext().getAssets().open(assetPath);
        BufferedReader in =
                new BufferedReader(new InputStreamReader(assetAsString, "UTF-8"));
        String line;

        while ((line = in.readLine()) != null) {
            buf.append(line).append("\n");
        }

        in.close();
        return buf.toString();
    }
}
