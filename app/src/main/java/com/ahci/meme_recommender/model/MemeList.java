package com.ahci.meme_recommender.model;

// @TODO parts of this must be changed to something more sophisticated when the back button is implemented

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of memes that were displayed in this session.
 */
public class MemeList {

    private List<Meme> memes;

    private int currentlyOnDisplayIndex;

    public MemeList() {
        memes = new ArrayList<>();
        currentlyOnDisplayIndex = -1;
    }

    public void add(Meme meme) {
        memes.add(meme);
    }

    /**
     * @param url the meme's url
     * @return The meme with that url or null if no such meme is in the list.
     */
    public Meme getForUrl(String url) {
        for(int i = memes.size() - 1; i >= 0; i--) {
            if(memes.get(i).getUrl().equals(url)) return memes.get(i);
        }
        return null;
    }

    public Meme next() {
        if(currentlyOnDisplayIndex < memes.size() - 1) {
            currentlyOnDisplayIndex++;
            return memes.get(currentlyOnDisplayIndex);
        } else {
            return null;
        }
    }

    public Meme previous() {
        if(currentlyOnDisplayIndex > 0) {
            currentlyOnDisplayIndex--;
            return memes.get(currentlyOnDisplayIndex);
        } else {
            return null;
        }
    }

    public List<Meme> getList() {
       return this.memes;
    }

}
