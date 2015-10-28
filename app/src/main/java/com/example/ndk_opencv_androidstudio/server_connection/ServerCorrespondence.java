package com.example.ndk_opencv_androidstudio.server_connection;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.example.ndk_opencv_androidstudio.R;
import com.example.ndk_opencv_androidstudio.json_parser.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static boolean recentDownload = false;

    public static void getMemeImage(final String param, final Context c) {
        context = c;

        new DownloadTest().execute(param);
    }

    public static void updateWebView(final String[] urls) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                WebView wv = (WebView) ((Activity) context).findViewById(R.id.webview);
                wv.loadUrl(urls[0]);
            }
        });
    }

    private static class DownloadTest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String s = "";
            try {
                URL url = new URL("http://192.168.1.53:8080" + params[0]);

                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.connect();

                InputStream is = connection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = br.readLine()) != null)
                   s += line;

            } catch (Exception e) { e.printStackTrace(); }

            return s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String[] urls = JSONParser.getImageURLs(JSONParser.getRootObject(s).getJSONArray("images"));

                updateWebView(urls);

            } catch(JSONException je) {}
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
