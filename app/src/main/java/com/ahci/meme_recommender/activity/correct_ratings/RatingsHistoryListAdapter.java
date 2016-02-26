package com.ahci.meme_recommender.activity.correct_ratings;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.model.Meme;
import com.ahci.meme_recommender.model.MemeList;
import com.ahci.meme_recommender.model.Rating;

public class RatingsHistoryListAdapter extends ArrayAdapter<Rating> {

    private final MemeList memeList;

    public RatingsHistoryListAdapter(Context context, MemeList memeList) {
        super(context, R.layout.ratings_history_item);
        this.memeList = memeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ratingsItemView = convertView;
        if(ratingsItemView == null) {
            ratingsItemView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.ratings_history_item, null);
        }

        Rating rating = getItem(position);
        Meme meme = rating.loadMeme(memeList);
        if(meme == null) {
            ((TextView) ratingsItemView.findViewById(R.id.ratings_history_item_title)).setText(R.string.ratings_history_title_no_longer_available);
        } else {
            ((TextView) ratingsItemView.findViewById(R.id.ratings_history_item_title)).setText(meme.getTitle());
        }

        ((ImageView) ratingsItemView.findViewById(R.id.ratings_history_item_rating))
                .setImageResource(rating.getRatingValue() == 1?
                        R.drawable.emoticon_smiling :
                        R.drawable.emoticon_neutral);

        return ratingsItemView;
    }
}
