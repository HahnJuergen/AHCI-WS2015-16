package com.ahci.meme_recommender.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

/**
 * Model class for the Memes. Memes consist of an URL and an unique ID.
 */
public class Meme {

    private String url;
    private String id;
    private String title;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
