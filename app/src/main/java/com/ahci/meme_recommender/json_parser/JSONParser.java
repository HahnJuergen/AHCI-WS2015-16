package com.ahci.meme_recommender.json_parser;

import com.ahci.meme_recommender.model.Meme;

import org.json.*;

/**
 * The JSONParser gets all the information from the JSON responses the server sent.
 */
public class JSONParser {

    /**
     * Reads the meme objects from the JSON string
     *
     * @param json server response for request to see images
     * @return list of Meme objects
     * @throws JSONException
     */
    public static Meme[] loadMemes(String json) throws JSONException {
        JSONObject root = getRootObject(json);
        JSONArray memeArray = root.getJSONArray("images");

        Meme[] memes = new Meme[memeArray.length()];
        for(int i = 0; i < memes.length; i++) {
            memes[i] = getMemeFromJSON(memeArray.getJSONObject(i));
        }
        return memes;
    }

    /**
     * Returns the root object of the string (parses the JSON string)
     * @param s json string
     * @return root object of the parsed json
     * @throws JSONException
     */
    private static JSONObject getRootObject(String s) throws JSONException {
        return (JSONObject) new JSONTokener(s).nextValue();
    }

    /**
     * Converts a JSONObject with the data for a Meme object to a Meme object.
     * @param json with keys "id" and "url"
     * @return Meme object with that data
     * @throws JSONException
     */
    private static Meme getMemeFromJSON(JSONObject json) throws JSONException {
        Meme meme = new Meme();
        meme.setId(json.getString("id"));
        meme.setUrl(json.getString("url"));
        meme.setTitle(json.getString("title"));
        return meme;
    }

    /**
     * Loads the IDs of the memes for which ratings were sent to the server from the json string.
     * @param json json string
     * @return list if meme IDs (possibly empty, never null)
     * @throws JSONException
     */
    public static String[] loadRatedMemeIDs(String json) throws JSONException {
        JSONObject root = getRootObject(json);
        if(root.has("rated_meme_ids")) {
            JSONArray arr = root.getJSONArray("rated_meme_ids");
            String[] ratedMemeIDs = new String[arr.length()];
            for(int i = 0; i < ratedMemeIDs.length; i++) {
                ratedMemeIDs[i] = arr.getString(i);
            }
            return ratedMemeIDs;
        } else {
            return new String[0];
        }
    }
}
