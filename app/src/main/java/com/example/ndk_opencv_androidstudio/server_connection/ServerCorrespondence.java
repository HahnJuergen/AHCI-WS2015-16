package com.example.ndk_opencv_androidstudio.server_connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jurgenhahn on 27/10/15.
 */
public class ServerCorrespondence {

    public static void testCorrespondence(final String param) {
        new DownloadTest().execute(param);
    }

    private static class DownloadTest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String s = "";
            try {

                URL url = new URL("http://192.168.1.53:8080/?" + params[0]);

                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.connect();

                InputStream is = connection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = br.readLine()) != null)
                    s = line;

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

            Log.i("TEST", "Result from AT: " + s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
