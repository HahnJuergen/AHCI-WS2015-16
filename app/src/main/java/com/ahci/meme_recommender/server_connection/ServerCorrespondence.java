package com.ahci.meme_recommender.server_connection;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.json_parser.JSONParser;
import com.ahci.meme_recommender.model.Rating;
import com.ahci.meme_recommender.util.NetworkState;

import org.json.JSONException;

import java.util.List;

/**
 * All communication with the meme recommender server happens in this class.
 */
public class ServerCorrespondence {

    public static String SERVER = "http://192.168.0.165:8080";
    private static final String REQUEST_ID="/request_id.json";
    private static final String GET_MEMES = "/load_images.json";

    private static Context context;

    public static void requestId(final Context c, ServerResponseHandler listener,
                                 ServerErrorHandler errorHandler) {
        if(!NetworkState.getInstance(c).isOnline()) {
            errorHandler.onNoNetworkAvailable();
            return;
        }

        new DownloadTask(listener, SERVER + REQUEST_ID, errorHandler).execute();
    }

    public static void getMemeImages(String userId, int howMany, List<Rating> ratings,
                                     final Context c, final ServerResponseHandler listener,
                                     ServerErrorHandler errorHandler) {
        if(!NetworkState.getInstance(c).isOnline()) {
            errorHandler.onNoNetworkAvailable();
            return;
        }

        context = c;

        String url = SERVER + GET_MEMES + "?"
                + "user_id=" + userId + "&" + "how_many=" + howMany
                + Rating.toUrlParam(ratings, true);
        new DownloadTask(listener, url, errorHandler).execute();
    }

    public interface ServerResponseHandler {
        public void handleResponse(String response);
    }

    public interface ServerErrorHandler {

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
