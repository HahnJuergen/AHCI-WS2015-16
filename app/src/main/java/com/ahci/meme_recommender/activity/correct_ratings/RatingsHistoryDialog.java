package com.ahci.meme_recommender.activity.correct_ratings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.model.MemeList;
import com.ahci.meme_recommender.model.Rating;
import com.ahci.meme_recommender.model.Storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RatingsHistoryDialog {

    private Context context;
    private MemeList memeList;

    private ListView ratingsList;
    private Storage storage;

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

        ListView ratingsListView = (ListView) wrapper.findViewById(R.id.ratings_history_dialog_item_list);
        RatingsHistoryListAdapter adapter = new RatingsHistoryListAdapter(context, memeList);
        ratingsListView.setAdapter(adapter);

        adapter.addAll(sessionRatings);
        adapter.notifyDataSetChanged();

        return wrapper;
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
