package com.ahci.meme_recommender.activity.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        TutorialFragment tf = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        tf.setArguments(args);
        return tf;
    }

    @Override
    public int getCount() {
        return TutorialHelper.PAGES.length;
    }
}
