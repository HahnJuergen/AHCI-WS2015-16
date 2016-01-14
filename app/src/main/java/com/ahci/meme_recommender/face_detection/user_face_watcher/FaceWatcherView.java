package com.ahci.meme_recommender.face_detection.user_face_watcher;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.face_detection.OnFaceUpdateListener;
import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;
import java.util.List;

/**
 * This View is a mostly-invisible overlay that watches how users look at the camera
 * (via Face objects) and may print warnings / tells the users how to correct the angle
 * or position of their devices.
 */
public class FaceWatcherView extends RelativeLayout implements OnFaceUpdateListener {

    private static final boolean LOGGING = true;
    private static final int SHOW = 0;
    private static final int HIDE = 1;
    public static boolean RUN_TIMER = true;
    public static boolean KILL_TIMER = false;

    private static long timeLastFaceRetreived;

    public static int CAMERA_WIDTH;
    public static int CAMERA_HEIGHT;

    private TextView correctionView;

    private List<Float> last5SmilingValues;
    private Thread timerThread;
    private Timer timer;

    private Handler correctionViewHandler;

    private boolean cantFindFaceOrSmile;
    private String errorViewMessage;

    public static void startTimer() {
        if(!RUN_TIMER) {
            RUN_TIMER = true;
        }
    }

    public FaceWatcherView(Context context) {
        super(context);
        setup();
    }

    public FaceWatcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public FaceWatcherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FaceWatcherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    private void setup() {
        cantFindFaceOrSmile = true;
        errorViewMessage = "";
        setupCorrectionNotificationView();

        last5SmilingValues = new ArrayList<>();

        timer = new Timer();
        timerThread = new Thread(timer);
        timerThread.start();

        setupCorrectionViewHandler();
    }

    private void setupCorrectionViewHandler() {
        correctionViewHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case SHOW:
                        showErrorMessage(msg.getData().getString("message"));
                        break;
                    case HIDE:
                        hideErrorMessage();
                        break;
                }

                super.handleMessage(msg);
            }
        };
    }

    private void setupCorrectionNotificationView() {
        correctionView = new TextView(this.getContext());

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 100);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 0, 50);
        correctionView.setBackgroundColor(Color.WHITE);
        correctionView.setLayoutParams(params);

        correctionView.setVisibility(View.GONE);

        this.addView(correctionView);
    }

    public void onFaceUpdate(Face face) {
        timeLastFaceRetreived = System.currentTimeMillis();

        last5SmilingValues.add(0, face.getIsSmilingProbability());
        while (last5SmilingValues.size() > 5) {
            last5SmilingValues.remove(5);
        }

        if(face.getIsSmilingProbability() >= 0) {
            return;
        } else {
            for(int i = 0; i < last5SmilingValues.size(); i++) {
                if(last5SmilingValues.get(i) > 0) {
                    Message msg = new Message();
                    msg.what = HIDE;
                    correctionViewHandler.sendMessage(msg);
                }

                if(i == last5SmilingValues.size() - 1 && i >= 3) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message", getContext().getResources().getString(R.string.cant_detect_smile));
                    Message msg = new Message();
                    msg.setData(bundle);
                    msg.what = SHOW;
                    correctionViewHandler.sendMessage(msg);
                }
            }
        }
    }

    private void hideErrorMessage() {
        if(!cantFindFaceOrSmile)
            correctionView.setVisibility(View.GONE);
        cantFindFaceOrSmile = true;
    }

    private void showErrorMessage(String newMessage) {
        if(cantFindFaceOrSmile) {
            correctionView.setVisibility(View.VISIBLE);
        }
        cantFindFaceOrSmile = false;
        if(!newMessage.equals(errorViewMessage)) {
            correctionView.setText(newMessage);
        }
        errorViewMessage = newMessage;
    }

    public boolean cantFindFaceOrSmile() {
        return cantFindFaceOrSmile;
    }


    /*
    From: https://developers.google.com/vision/face-detection-concepts
    Euler Y angle	detectable landmarks
        < -36 degrees	left eye, left mouth, left ear, nose base, left cheek
        -36 degrees to -12 degrees	left mouth, nose base, bottom mouth, right eye, left eye, left cheek, left ear tip
        -12 degrees to 12 degrees	right eye, left eye, nose base, left cheek, right cheek, left mouth, right mouth, bottom mouth
        12 degrees to 36 degrees	right mouth, nose base, bottom mouth, left eye, right eye, right cheek, right ear tip
        > 36 degrees	right eye, right mouth, right ear, nose base, right cheek
     */

    private static synchronized long getTimeLastFaceRetreived() {
        return timeLastFaceRetreived;
    }

    private class Correction {
        private boolean hasCorrection = false;
        /**
         * Possible values:
         * <ul>
         *     <li>-1 = turn to right</li>
         *     <li>0 = don't turn face</li>
         *     <li>1 = turn to left</li>
         * </ul>
         */
        private int fixRotation = 0;
        private int fixXPos = 0;
        private int fixYPos = 0;
    }

    private static class Log {

        static void d(String tag, String message) {
            if(LOGGING) {
                android.util.Log.d(tag, message);
            }
        }
    }

    private class Timer implements Runnable{

        public Timer() {


        }

        @Override
        public void run() {
            while(!KILL_TIMER) {
                if (RUN_TIMER) {
                    long currentTime = System.currentTimeMillis();

                    if (currentTime >= timeLastFaceRetreived + 500l) {
                        Bundle bundle = new Bundle();
                        bundle.putString("message", getContext().getResources().getString(R.string.cant_find_face));
                        Message msg = new Message();
                        msg.setData(bundle);
                        msg.what = SHOW;
                        correctionViewHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = HIDE;
                        correctionViewHandler.sendMessage(msg);
                    }

                    try {
                        Thread.sleep(50l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
