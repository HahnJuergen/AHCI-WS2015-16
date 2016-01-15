package com.ahci.meme_recommender.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class Meme {

    private String url;
    private int id;

    public Meme() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
