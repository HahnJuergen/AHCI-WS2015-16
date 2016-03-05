package com.ahci.meme_recommender.activity.correct_ratings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.model.Meme;
import com.ahci.meme_recommender.model.MemeList;
import com.ahci.meme_recommender.model.Rating;
import com.ahci.meme_recommender.model.Storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RatingsHistoryDialog {

    private Context context;
    private MemeList memeList;

    private TextView selectedItemTitle;
    private ImageView selectedItemPositive;
    private ImageView selectedItemNeutral;

    private RatingsHistoryListAdapter adapter;

    public RatingsHistoryDialog(Context context, MemeList memeList) {
        this.context = context;
        this.memeList = memeList;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        List<Rating> sessionRatings = getSessionRatings();
        builder.setTitle(R.string.session_ratings);
        if(sessionRatings.size() == 0) {
            builder.setMessage(R.string.no_ratings_in_current_session);
        } else {
            builder.setView(createDialogView(sessionRatings));
        }
        builder.setNeutralButton(R.string.close_history_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private View createDialogView(List<Rating> sessionRatings) {
        RelativeLayout wrapper = (RelativeLayout)
                ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.ratings_history_dialog, null);

        setupRatingsList(sessionRatings, wrapper);
        setupSelectedRating(sessionRatings, wrapper);

        return wrapper;
    }

    private void setupSelectedRating(List<Rating> sessionRatings, RelativeLayout wrapper) {
        selectedItemTitle = (TextView) wrapper.findViewById(R.id.ratings_history_selected_title);
        selectedItemNeutral = (ImageView) wrapper.findViewById(R.id.ratings_history_selected_neutral);
        selectedItemPositive = (ImageView) wrapper.findViewById(R.id.ratings_history_selected_happy);

        if(sessionRatings.size() > 0) setSelectedRating(sessionRatings.get(0));
    }

    private void setSelectedRating(Rating rating) {
        Meme m = rating.loadMeme(memeList);
        if(m == null) { // should not even be possible but who knows...
            selectedItemTitle.setText(R.string.ratings_history_title_no_longer_available);
        } else {
            selectedItemTitle.setText(rating.loadMeme(memeList).getTitle());
        }

        if(rating.getRatingValue() == 1) {
            updateSelectedItemButtons(selectedItemPositive, selectedItemNeutral, rating);
        } else {
            updateSelectedItemButtons(selectedItemNeutral, selectedItemPositive, rating);
        }
    }

    private void updateSelectedItemButtons(final ImageView selectedItem, final ImageView otherItem, final Rating rating) {
        selectedItem.setBackgroundColor(Color.rgb(0x8D, 0xD5, 0xF2));
        otherItem.setBackgroundColor(Color.argb(0, 0, 0, 0));

        selectedItem.setOnClickListener(null);
        otherItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating.setSentToServer(0);
                rating.setRatingValue(rating.getRatingValue() == 1 ? 0 : 1);
                rating.update(new Storage(context));
                adapter.notifyDataSetChanged();
                updateSelectedItemButtons(otherItem, selectedItem, rating);
            }
        });
    }

    private void setupRatingsList(List<Rating> sessionRatings, RelativeLayout wrapper) {
        ListView ratingsListView = (ListView) wrapper.findViewById(R.id.ratings_history_dialog_item_list);
        adapter = new RatingsHistoryListAdapter(context, memeList);
        ratingsListView.setAdapter(adapter);

        ratingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                askToCorrectRating(position);
            }
        });

        ratingsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                askToCorrectRating(position);
                return true;
            }
        });

        adapter.addAll(sessionRatings);
        adapter.notifyDataSetChanged();
    }

    private void askToCorrectRating(int position) {
        Rating rating = adapter.getItem(position);
        Meme m = rating.loadMeme(memeList);
        if(m != null) {
            setSelectedRating(rating);
        }
    }

    private List<Rating> getSessionRatings() {
        List<Rating> sessionRatings = Rating.loadLastNRatings(new Storage(context), 20);

        Iterator<Rating> iterator = sessionRatings.iterator();

        while(iterator.hasNext()) {
            Rating rating = iterator.next();
            if(rating.loadMeme(memeList) == null) iterator.remove();
        }

        return sessionRatings;
    }
}
