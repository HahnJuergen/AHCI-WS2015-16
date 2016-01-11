package com.ahci.meme_recommender.server_connection;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.json_parser.JSONParser;
import com.ahci.meme_recommender.util.NetworkState;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jurgenhahn on 27/10/15.
 */
public class ServerCorrespondence {

    private static Context context;
    private static int increment = -1;

    public static boolean downloading = false;

    public static void getMemeImage(final String param, final Context c, OnMemeDownloadFinishedListener listener) {
        if(!NetworkState.getInstance(c).isOnline()) {
            listener.onNoNetworkAvailable();
            return;
        }

        downloading = true;
        context = c;
        increment++;

        new DownloadTask(listener).execute(param);
    }

    public static void updateWebView(final String[] urls) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                WebView wv = (WebView) ((Activity) context).findViewById(R.id.webview);
                wv.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        downloading = false;
                    }
                });

                wv.loadUrl(urls[increment]);
            }
        });
    }

    private static class DownloadTask extends AsyncTask<String, Void, String> {

        private OnMemeDownloadFinishedListener listener;

        boolean noConnectionToServer = false;
        boolean noConnectionAtAll = false;

        public DownloadTask(OnMemeDownloadFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getMemeServerResponse(params);
            } catch (Exception e) {
                try {
                    noConnectionToServer = true;
                    tryToConnectToGoogle();
                } catch (Exception e2) {
                    noConnectionAtAll = true;
                }
            }
            return "";
        }

        private void tryToConnectToGoogle() throws Exception {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(200);
            connection.connect();
        }

        private String getMemeServerResponse(String... params) throws Exception {
            URL url = new URL("http://192.168.0.165:8080" + params[0]);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();

            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder toReturn = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                toReturn.append(line);

            return toReturn.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(!noConnectionToServer) {
                super.onPostExecute(s);
                try {
                    String[] urls = JSONParser.getImageURLs(JSONParser.getRootObject(s).getJSONArray("images"));

                    updateWebView(urls);
                    listener.onMemeDownloadFinished();

                } catch(JSONException je) {}

            } else if(!noConnectionAtAll) {
                listener.onNoConnectionToServerPossible();
            } else {
                listener.onNoConnectionAtAllPossible();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public interface OnMemeDownloadFinishedListener {
        public void onMemeDownloadFinished();

        /**
         * The user has disabled all connections. This error can be caught.
         */
        public void onNoNetworkAvailable();

        /**
         * The server is offline. Nothing can be done then.
         */
        public void onNoConnectionToServerPossible();

        /**
         * The user has enabled networking, but the device can't access any web page.
         * Waiting for a couple of seconds might be the best option in this case.
         */
        public void onNoConnectionAtAllPossible();
    }
}
