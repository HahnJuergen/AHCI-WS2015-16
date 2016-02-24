package com.ahci.meme_recommender.model;

// @TODO parts of this must be changed to something more sophisticated when the back button is implemented

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of memes that were displayed in this session.
 */
public class MemeList {

    private List<Meme> memes;

    public MemeList() {
        memes = new ArrayList<>();
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

    public Meme getAtIndex(int index) {
        return memes.get(index);
    }

    public List<Meme> getList() {
       return this.memes;
    }

}
