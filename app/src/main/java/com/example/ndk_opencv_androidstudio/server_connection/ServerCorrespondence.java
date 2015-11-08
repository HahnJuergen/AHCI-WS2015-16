package com.example.ndk_opencv_androidstudio.server_connection;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    private static int increment = -1;

    public static boolean downloading = false;

    public static void getMemeImage(final String param, final Context c, OnMemeDownloadFinishedListener listener) {
        downloading = true;
        context = c;
        increment++;


        new DownloadTest(listener).execute(param);
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

    private static class DownloadTest extends AsyncTask<String, Void, String> {

        private OnMemeDownloadFinishedListener listener;

        public DownloadTest(OnMemeDownloadFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("/load_images.json")) {
                return TEST_JSON_RESPONSE;
            }
            String s = "";
            try {
                URL url = new URL("http://192.168.178.31:8080" + params[0]);

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
                listener.onMemeDownloadFinished();

            } catch(JSONException je) {}
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public interface OnMemeDownloadFinishedListener {
        public void onMemeDownloadFinished();
    }

    private static final String TEST_JSON_RESPONSE = "{\n" +
            "\timages: [\n" +
            "\t\t{\n" +
            "\t\t\tid: 0,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aZNoBBp?sc=cute\",\n" +
            "\t\t\ttitle: \"Ew human\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 1,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a1YnGBD?sc=comic\",\n" +
            "\t\t\ttitle: \"Good Hair Day\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 2,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aNKX956\",\n" +
            "\t\t\ttitle: \"Dog taking his teddy to bed\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 3,\n" +
            "\t\t\turl: \"http://9gag.com/gag/286508\",\n" +
            "\t\t\ttitle: \"Socially Awkward Penguin\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 4,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a7KexQq\",\n" +
            "\t\t\ttitle: \"What do you see in the Mirror, Professor Dumbledore?\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 5,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aB3exy2\",\n" +
            "\t\t\ttitle: \"Shiba in a sombrero\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 6,\n" +
            "\t\t\turl: \"http://9gag.com/gag/apBnWP8?sc=comic\",\n" +
            "\t\t\ttitle: \"Something good\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 7,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aynZBd8?sc=comic\",\n" +
            "\t\t\ttitle: \"When the research went too far\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 8,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a2qAQMY\",\n" +
            "\t\t\ttitle: \"Make it behave, hooman\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 9,\n" +
            "\t\t\turl: \"http://9gag.com/gag/am8yKzv\",\n" +
            "\t\t\ttitle: \"15-day-old hamster babies\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 10,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aRV9vZM?sc=comic\",\n" +
            "\t\t\ttitle: \"Game of Thrones in nutshell\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 11,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a9P4OAZ?sc=comic\",\n" +
            "\t\t\ttitle: \"It is complete\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 12,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aKByLW6\",\n" +
            "\t\t\ttitle: \"Cats and boxes - Not every box is empty.\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 13,\n" +
            "\t\t\turl: \"http://9gag.com/gag/5090489\",\n" +
            "\t\t\ttitle: \"Scumbag Steve is Scumbag!\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 14,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a6Lzxnm\",\n" +
            "\t\t\ttitle: \"Happy dance\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 15,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a2q9ob9?sc=comic\",\n" +
            "\t\t\ttitle: \"The future of FPS\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 16,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aEzED5M?sc=comic\",\n" +
            "\t\t\ttitle: \"Click wisely\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 17,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a2qyZ0O\",\n" +
            "\t\t\ttitle: \"Dumbledore is savage af\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 18,\n" +
            "\t\t\turl: \"http://9gag.com/gag/anXEbe0\",\n" +
            "\t\t\ttitle: \"If Programming Languages Were Weapons\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 19,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aepE8PW?sc=1\",\n" +
            "\t\t\ttitle: \"Looks like the cat's in the bag.\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 20,\n" +
            "\t\t\turl: \"http://9gag.com/gag/azVQORm?sc=comic\",\n" +
            "\t\t\ttitle: \"Just read this...\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 21,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aGRbqYz?sc=comic\",\n" +
            "\t\t\ttitle: \"What playing video games taught me\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 22,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a3LedEr?sc=comic\",\n" +
            "\t\t\ttitle: \"Daily ritual\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 23,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a7bm6Xr?sc=comic\",\n" +
            "\t\t\ttitle: \"Right in the feels! *cries in Spanish*\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 24,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aKByDmb?sc=comic\",\n" +
            "\t\t\ttitle: \"This is why I loved juice with ice as a kid.\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 25,\n" +
            "\t\t\turl: \"http://9gag.com/gag/anB3Yg0?sc=comic\",\n" +
            "\t\t\ttitle: \"Just having a dinner...\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 26,\n" +
            "\t\t\turl: \"http://9gag.com/gag/agNbZL6?sc=comic\",\n" +
            "\t\t\ttitle: \"Every Time I Download Mods\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 27,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a1A3jAP\",\n" +
            "\t\t\ttitle: \"Wiggle Wiggle Wiggle\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 28,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aDmNLXK?sc=comic\",\n" +
            "\t\t\ttitle: \"People Who Like Saddest Turtle\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 29,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aEzjNpK?sc=comic\",\n" +
            "\t\t\ttitle: \"Every... Damn... Time\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 30,\n" +
            "\t\t\turl: \"http://9gag.com/gag/agNpdor?sc=comic\",\n" +
            "\t\t\ttitle: \"Persuasion and Doors (By Classic Randy)\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 31,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a6L4A6m?sc=comic\",\n" +
            "\t\t\ttitle: \"Elevator\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 32,\n" +
            "\t\t\turl: \"http://9gag.com/gag/ajnGOGw?sc=comic\",\n" +
            "\t\t\ttitle: \"Immortality\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 33,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aPG0nrw\",\n" +
            "\t\t\ttitle: \"Whack-A-Cat\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 34,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aWON0v4?sc=comic\",\n" +
            "\t\t\ttitle: \"Instructions unclear.\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 35,\n" +
            "\t\t\turl: \"http://9gag.com/gag/abbm6Xb?sc=comic\",\n" +
            "\t\t\ttitle: \"Cat's logic\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 36,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aXXzM82\",\n" +
            "\t\t\ttitle: \"Hey Bear... Hey Human!\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 37,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aLB3zzW\",\n" +
            "\t\t\ttitle: \"*Flumph!*\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 38,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a2qVbvw\",\n" +
            "\t\t\ttitle: \"They have no regard for stop signs either\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 39,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aq2O6dR\",\n" +
            "\t\t\ttitle: \"When you introduce a dog to an 8 year old cat\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 40,\n" +
            "\t\t\turl: \"http://9gag.com/gag/azVBDxN?sc=comic\",\n" +
            "\t\t\ttitle: \"I Think I'm Happy\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 41,\n" +
            "\t\t\turl: \"http://9gag.com/gag/arR0mNV\",\n" +
            "\t\t\ttitle: \"Day 133, They still suspect nothing\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 42,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a9Pe6AK?sc=meme\",\n" +
            "\t\t\ttitle: \"There is no problem which can't be solved\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 43,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aNKXR3G\",\n" +
            "\t\t\ttitle: \"Just me? okay...\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 44,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a8j3NPp\",\n" +
            "\t\t\ttitle: \"10 Points to Dumbledore\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 45,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aAp7zZg\",\n" +
            "\t\t\ttitle: \"Quick brown fox jumps over the lazy dog\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 46,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aDmLeMZ?sc=1\",\n" +
            "\t\t\ttitle: \"A Dog Doing Tricks On A Trampoline\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 47,\n" +
            "\t\t\turl: \"http://9gag.com/gag/a3LDA33\",\n" +
            "\t\t\ttitle: \"All smartphones should power on like this\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 48,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aXbKVrP\",\n" +
            "\t\t\ttitle: \"It's a lovely garden you have there. Would be a shame if something were to... happen to it...\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 49,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aAp769d\",\n" +
            "\t\t\ttitle: \"Parents will relate\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 50,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aMQ6AW1\",\n" +
            "\t\t\ttitle: \"Petting An Owl.\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 51,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aRV8bYB\",\n" +
            "\t\t\ttitle: \"I'm often too lazy to eat\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 52,\n" +
            "\t\t\turl: \"http://9gag.com/gag/aYp2mo2\",\n" +
            "\t\t\ttitle: \"If you've ever wondered what Programming is like, This about sums it up.\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\tid: 53,\n" +
            "\t\t\turl: \"http://9gag.com/gag/abyvZLb\",\n" +
            "\t\t\ttitle: \"Schrodinger's Troll\"\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";
}
