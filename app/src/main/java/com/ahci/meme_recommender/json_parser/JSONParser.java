package com.ahci.meme_recommender.json_parser;

import org.json.*;

/**
 * Created by jurgenhahn on 28/10/15.
 */
public class JSONParser {
    public static JSONObject getRootObject(String s) throws JSONException {
        return (JSONObject) new JSONTokener(s).nextValue();
    }

    public static String[] getImageURLs(JSONArray arr) throws JSONException {
        String[] sArr = new String[arr.length()];

        for(int i = 0; i < arr.length(); i++) {
            JSONObject jo = arr.getJSONObject(i);
            sArr[i] = jo.getString("url");
        }

        return sArr;
    }
}
