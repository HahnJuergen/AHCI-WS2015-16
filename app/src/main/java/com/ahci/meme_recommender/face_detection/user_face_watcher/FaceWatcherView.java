package com.ahci.meme_recommender.face_detection.user_face_watcher;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
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
    private static long timeLastErrorMessageChangeOccured;

    public static int CAMERA_WIDTH;
    public static int CAMERA_HEIGHT;

    private TextView correctionView;

    private List<Float> last15SmilingValues;
    private Thread timerThread;
    private Timer timer;

    private Handler correctionViewHandler;

    private String errorViewMessage;

    private boolean hideIfNecessary;

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

    /* @TODO decomposition */
    public void onFaceUpdate(Face face) {
        timeLastFaceRetreived = System.currentTimeMillis();

        last15SmilingValues.add(0, face.getIsSmilingProbability());
        while (last15SmilingValues.size() > 15) {
            last15SmilingValues.remove(15);
        }

        if(face.getIsSmilingProbability() >= 0) {
            return;
        } else {
            boolean hideError = true;
            int smileCount = 0;
            for(int i = 0; i < last15SmilingValues.size(); i++) {
                if(last15SmilingValues.get(i) > 0) {
                    smileCount++;
                }
            }

            if(smileCount >= last15SmilingValues.size() - 5) hideError = true;
            else hideError = false;

            Message msg = new Message();
            if(hideError) {
                msg.what = HIDE;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("message", getContext().getResources().getString(R.string.cant_detect_smile));
                msg.setData(bundle);
                msg.what = SHOW;

            }
            correctionViewHandler.sendMessage(msg);
        }
    }

    /**
     * Keeps messages hidden.
     */
    public void hideMessagesIfNecessary() {
        hideIfNecessary = true;
        correctionView.setVisibility(View.GONE);
    }

    /**
     * Shows messages (not automatically, only if it is necessary.)
     */
    public void showMessagesIfNecessary() {
        hideIfNecessary = false;
    }

    private void setup() {
        timeLastErrorMessageChangeOccured = System.currentTimeMillis() - 5000;

        hideIfNecessary = false;

        errorViewMessage = "";
        setupCorrectionNotificationView();

        last15SmilingValues = new ArrayList<>();

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

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(
                (int) convertDpToPixel(60, getContext()),
                0,
                (int) convertDpToPixel(60, getContext()),
                (int) convertDpToPixel(70, getContext()));


        correctionView.setBackgroundResource(R.drawable.swipe_emoticon_background);
        correctionView.setLayoutParams(params);
        correctionView.setPadding(20, 20, 20, 20);
        correctionView.setTextColor(Color.rgb(200, 30, 30));
        correctionView.setTypeface(null, Typeface.BOLD);
        correctionView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);



        correctionView.setVisibility(View.GONE);

        this.addView(correctionView);
    }

    private void hideErrorMessage() {
        if(System.currentTimeMillis() < timeLastErrorMessageChangeOccured + 2000) return;
        correctionView.setVisibility(View.GONE);
        timeLastErrorMessageChangeOccured = System.currentTimeMillis();
    }

    private void showErrorMessage(String newMessage) {
        if(System.currentTimeMillis() < timeLastErrorMessageChangeOccured + 2000) return;
        if(!hideIfNecessary) {
            correctionView.setVisibility(View.VISIBLE);
            timeLastErrorMessageChangeOccured = System.currentTimeMillis();
        }

        if(!newMessage.equals(errorViewMessage)) {
            correctionView.setText(newMessage);
            timeLastErrorMessageChangeOccured = System.currentTimeMillis();
        }
        errorViewMessage = newMessage;
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


    // http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

}
