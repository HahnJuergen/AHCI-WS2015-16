package com.ahci.meme_recommender.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.activity.correct_ratings.CorrectClassificationDialog;
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
                        update(msg.getData().getInt("smiling"));
                        break;
                }

                super.handleMessage(msg);
            }
        };

        emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CorrectClassificationDialog(context, memeList).show();
            }
        });
    }

    public void startUpdates() {
        Thread thread = new Thread(new EmoticonIconUpdater());
        thread.start();
    }

    private void update(int smiling) {

        int backgroundColor = getBackgroundColor(smiling);
        root.setBackgroundColor(backgroundColor);

        if(smiling == FaceTracker.NOT_SMILING) {
            emoticon.setImageResource(R.drawable.emoticon_neutral);
        } else if(smiling == FaceTracker.SMILING) {
            emoticon.setImageResource(R.drawable.emoticon_smiling);
        } else {

        }
    }

    private int getBackgroundColor(int smiling) {
        if(smiling == FaceTracker.SMILING) {
            return Color.rgb(0x99, 0xFF, 0x99);
        } else {
            return Color.rgb(0xE0, 0x99, 0x99);
        }
    }

    private class EmoticonIconUpdater implements Runnable {

        @Override
        public void run() {
            while(!KILL_TIMER) {
                if (RUN_TIMER) {

                    int classification = 0;
                    try {
                        classification = FaceTracker.classify();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("smiling", classification);
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
