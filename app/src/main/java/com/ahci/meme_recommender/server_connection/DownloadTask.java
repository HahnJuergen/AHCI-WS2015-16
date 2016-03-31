package com.ahci.meme_recommender.server_connection;

import android.os.AsyncTask;
import android.util.Log;

import com.ahci.meme_recommender.json_parser.JSONParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class DownloadTask extends AsyncTask<String, Void, String> {

    private boolean noConnectionToServer = false;
    private boolean noConnectionAtAll = false;

    private String url;

    private ServerCorrespondence.ServerResponseHandler listener;
    private ServerCorrespondence.ServerErrorHandler errorHandler;

    public DownloadTask(ServerCorrespondence.ServerResponseHandler listener, String url,
                        ServerCorrespondence.ServerErrorHandler errorHandler) {
        this.listener = listener;
        this.url = url;
        this.errorHandler = errorHandler;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getServerResponse();
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

    private String getServerResponse() throws Exception {
        URL url = new URL(this.url);

        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(1500);
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
        if (!noConnectionToServer) {
            super.onPostExecute(s);
            listener.handleResponse(s);
        } else if (!noConnectionAtAll) {
            Log.d("ahci_network_error", "No connection to server possible.");
            errorHandler.onNoConnectionToServerPossible();
        } else {
            Log.d("ahci_network_error", "No connection to any server possible.");
            errorHandler.onNoConnectionAtAllPossible();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
