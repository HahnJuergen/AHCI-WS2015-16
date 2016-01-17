package com.ahci.meme_recommender.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class Meme {

    private String url;
    private String id;

    public Meme() {

    }

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
}
