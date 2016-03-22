package com.ahci.meme_recommender.activity.correct_ratings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.face_detection.FaceTracker;
import com.ahci.meme_recommender.model.MemeList;

public class CorrectClassificationDialog {

    private Context context;
    private MemeList memeList;

    private ImageView selectedItemPositive;
    private ImageView selectedItemNeutral;

    public CorrectClassificationDialog(Context context, MemeList memeList) {
        this.context = context;
        this.memeList = memeList;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(createDialogView());

        builder.setPositiveButton(R.string.close_history_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private View createDialogView() {
        RelativeLayout wrapper = (RelativeLayout)
                ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.change_rating_dialog, null);

        setupSelectedRating(wrapper);

        return wrapper;
    }

    private void setupSelectedRating(RelativeLayout wrapper) {
        selectedItemNeutral = (ImageView) wrapper.findViewById(R.id.change_rating_selected_neutral);
        selectedItemPositive = (ImageView) wrapper.findViewById(R.id.change_rating_selected_happy);

        setSelectedRating();
    }

    private void setSelectedRating() {
        if(FaceTracker.classify() == FaceTracker.SMILING) {
            updateSelectedItemButtons(selectedItemPositive, selectedItemNeutral, FaceTracker.SMILING);
        } else {
            updateSelectedItemButtons(selectedItemNeutral, selectedItemPositive, FaceTracker.NOT_SMILING);
        }
    }

    private void updateSelectedItemButtons(final ImageView selectedItem, final ImageView otherItem, final int rating) {
        selectedItem.setBackgroundResource(R.drawable.blue_button_borders);
        otherItem.setBackgroundResource(R.drawable.white_button_borders);

        selectedItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceTracker.hardSetRating(rating);
                setSelectedRating();
            }
        });
        otherItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceTracker.hardSetRating(rating == FaceTracker.SMILING? FaceTracker.NOT_SMILING : FaceTracker.SMILING);
                setSelectedRating();
            }
        });
    }

}
