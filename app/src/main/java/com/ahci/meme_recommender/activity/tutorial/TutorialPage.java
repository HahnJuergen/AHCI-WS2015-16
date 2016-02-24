package com.ahci.meme_recommender.activity.tutorial;

import android.graphics.drawable.Drawable;

class TutorialPage {

    private String title, explanation;
    private int imageResource;
    private int color;

    public TutorialPage(String title, String explanation, int imageResource, int color) {
        setTitle(title);
        setExplanation(explanation);
        setImageResource(imageResource);
        setColor(color);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
