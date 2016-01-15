package com.ahci.meme_recommender.json_parser;

import com.ahci.meme_recommender.model.Meme;

import org.json.*;

/**
 * Created by jurgenhahn on 28/10/15.
 */
public class JSONParser {

    public static Meme[] loadMemes(String json) throws JSONException {
        JSONObject root = getRootObject(json);
        JSONArray memeArray = root.getJSONArray("images");

        Meme[] memes = new Meme[memeArray.length()];
        for(int i = 0; i < memes.length; i++) {
            memes[i] = getMemeFromJSON(memeArray.getJSONObject(i));
        }
        return memes;
    }

    private static JSONObject getRootObject(String s) throws JSONException {
        return (JSONObject) new JSONTokener(s).nextValue();
    }

    private static Meme getMemeFromJSON(JSONObject json) throws JSONException {
        Meme meme = new Meme();
        meme.setId(json.getInt("id"));
        meme.setUrl(json.getString("url"));
        return meme;
    }

    public static int[] loadRatedMemeIDs(String json) throws JSONException {
        JSONObject root = getRootObject(json);
        if(root.has("rated_meme_ids")) {
            JSONArray arr = root.getJSONArray("rated_meme_ids");
            int[] ratedMemeIDs = new int[arr.length()];
            for(int i = 0; i < ratedMemeIDs.length; i++) {
                ratedMemeIDs[i] = arr.getInt(i);
            }
            return ratedMemeIDs;
        } else {
            return new int[0];
        }
    }
}
