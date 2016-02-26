package com.ahci.meme_recommender.activity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.activity.correct_ratings.RatingsHistoryDialog;
import com.ahci.meme_recommender.activity.correct_ratings.RatingsHistoryListAdapter;
import com.ahci.meme_recommender.face_detection.FaceTracker;
import com.ahci.meme_recommender.model.MemeList;

public class EmoticonIconView {

    public static boolean RUN_TIMER = true;
    public static boolean KILL_TIMER = false;
    private RelativeLayout root;
    private Context context;

    private ImageView emoticon;

    private Handler updateHandler;

    public EmoticonIconView(RelativeLayout root, final Context context, final MemeList memeList) {
        this.root = root;
        this.context = context;

        this.emoticon = (ImageView) root.findViewById(R.id.emoticon_icon);
        updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 0:
                        update(msg.getData().getFloat("smiling_probability"));
                        break;
                }

                super.handleMessage(msg);
            }
        };

        emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RatingsHistoryDialog(context, memeList).show();
            }
        });
    }

    public void startUpdates() {
        Thread thread = new Thread(new EmoticonIconUpdater());
        thread.start();
    }

    private void update(float smilingProbability) {

        int backgroundColor = getBackgroundColor(smilingProbability);
        root.setBackgroundColor(backgroundColor);

        if(smilingProbability > 0 && smilingProbability < 0.4f) {
            emoticon.setImageResource(R.drawable.emoticon_neutral);
        } else if(smilingProbability >= 0.4f) {
            emoticon.setImageResource(R.drawable.emoticon_smiling);
        } else {

        }
    }

    private int getBackgroundColor(float smilingProbability) {
        if(smilingProbability < 0) {
            return Color.rgb(0xFF, 0xA0, 0xA0);
        } else {
            int[] lowest = {0xE0, 0xE0, 0x99};
            int[] highest = {0x99, 0xFF, 0x99};

            int redValue = (int) (lowest[0] * (1 - smilingProbability)) + (int) (highest[0] * smilingProbability);
            int greenValue = (int) (lowest[1] * (1 - smilingProbability)) + (int) (highest[1] * smilingProbability);
            int blueValue = (int) (lowest[2] * (1 - smilingProbability)) + (int) (highest[2] * smilingProbability);

            return Color.rgb(redValue, greenValue, blueValue);
        }
    }

    private class EmoticonIconUpdater implements Runnable {

        @Override
        public void run() {
            while(!KILL_TIMER) {
                if (RUN_TIMER) {

                    float smilingProbability;
                    try {
                        smilingProbability = FaceTracker.getWeightedLastSmilingProbability();
                    } catch (Exception e) {
                        e.printStackTrace();
                        smilingProbability = 0;
                    }

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putFloat("smiling_probability", smilingProbability);
                    msg.setData(data);
                    msg.what = 0;

                    updateHandler.sendMessage(msg);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
