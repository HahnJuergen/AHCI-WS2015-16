package com.example.ndk_opencv_androidstudio.server_connection;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.53:8080/?" + param);

                    URLConnection connection = url.openConnection();
                    connection.setDoOutput(true);
                    connection.connect();

                    InputStream is = connection.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    String line;

                    while ((line = br.readLine()) != null)
                        Log.d("TEST", line);

                } catch (Exception e) { e.printStackTrace(); }

            }
        }).start();
    }
}
