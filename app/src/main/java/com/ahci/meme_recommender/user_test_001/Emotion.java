package com.ahci.meme_recommender.user_test_001;

/**
 * Created by jonbr on 08.11.2015.
 */
public class Emotion {
    private final int image;
    private String name;

    public Emotion(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

}
