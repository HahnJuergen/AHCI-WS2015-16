/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ahci.meme_recommender.activity;

import android.content.SharedPreferences;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.face_detection.FaceTracker;
import com.ahci.meme_recommender.face_detection.FaceTrackerFactory;
import com.ahci.meme_recommender.face_detection.OnFaceUpdateListener;
import com.ahci.meme_recommender.face_detection.user_face_watcher.FaceWatcherView;
import com.ahci.meme_recommender.json_parser.JSONParser;
import com.ahci.meme_recommender.model.Rating;
import com.ahci.meme_recommender.model.Storage;
import com.ahci.meme_recommender.server_connection.ServerCorrespondence;
import com.ahci.meme_recommender.util.MemeWebViewWrapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MainActivity extends AppCompatActivity implements FaceTrackerFactory.OnNewTrackerListener {
    /** Only value that works right now is 1... */
    private static final int SITES_LOADED_AT_ONCE = 1;
    private static final String TAG = "FaceTracker";

    private FaceTracker faceTracker;

    private RelativeLayout nextButton;
    private RelativeLayout prevButton;

    private EmoticonIconView emoticonIconView;
    private SwipeAnimationView swipeAnimationView;

    private MemeWebViewWrapper memeWebViewWrapper;

    private int userId = -1;
    private ServerCorrespondence.ServerResponseHandler onMemeDownloadListener;
    private ServerCorrespondence.ServerErrorHandler networkErrorHandler;

    private CameraSourceHelper cameraSourceHelper;
    private FaceWatcherView faceWatcherView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        userId = getId();

        if(firstAppStart()) {
            showTutorial();
        } else {
            setup();
            if(userId != -1) {
                loadFirstMemes();
            }
        }
    }

    private void loadFirstMemes() {
        ServerCorrespondence.getMemeImages(userId, SITES_LOADED_AT_ONCE + 1, Rating.loadRatingsToSendToServer(new Storage(this)),
                this, onMemeDownloadListener, networkErrorHandler);
    }

    private void loadNextMeme() {
        ServerCorrespondence.getMemeImages(userId, SITES_LOADED_AT_ONCE, Rating.loadRatingsToSendToServer(new Storage(this)),
                this, onMemeDownloadListener, networkErrorHandler);
    }

    private void setup() {
        referenceViews();
        setupControlButtons();
        cameraSourceHelper.checkCamera();
        setupMemeWebView();
        setupOnMemeDownloadListener();
        setupNetworkErrorHelper();
    }

    private void setupControlButtons() {
        setupNextMemeButton(nextButton);
        setupPrevMemeButton(prevButton);

    }

    private void setupMemeWebView() {
        memeWebViewWrapper = new MemeWebViewWrapper(this,
                (RelativeLayout) findViewById(R.id.webview_wrapper), SITES_LOADED_AT_ONCE);
    }

    /*
     * @TODO implement this
     */
    /**
     * @return
     * <ul>
     *     <li><em>True</em> if this is the first start of the app</li>
     *     <li><em>False</em> if this is not the first start of the app</li>
     * </ul>
     */
    private boolean firstAppStart() {
        return false;
    }

    private int getId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.contains("user_id")) {
            getIdFromServer();
            return -1;
        } else {
            return prefs.getInt("user_id", -1);
        }
    }

    /* @TODO implement this */
    private void showTutorial() {

    }

    private void setupOnMemeDownloadListener() {
        this.onMemeDownloadListener = new ServerCorrespondence.ServerResponseHandler() {
            @Override
            public void handleResponse(String response) {
                try {
                    String[] urls = JSONParser.getImageURLs(JSONParser.getRootObject(response).getJSONArray("images"));
                    updateWebView(urls);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        };
    }

    private void setupNetworkErrorHelper() {
        this.networkErrorHandler = new NetworkErrorHelper(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraSourceHelper.startCameraSource();
        FaceWatcherView.RUN_TIMER = true;
        EmoticonIconView.RUN_TIMER = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSourceHelper.stopMPreview();
        FaceWatcherView.RUN_TIMER = false;
        EmoticonIconView.RUN_TIMER = false;
    }

    @Override
    protected void onDestroy() {
        FaceWatcherView.KILL_TIMER = true;
        EmoticonIconView.KILL_TIMER = true;
        super.onDestroy();
        cameraSourceHelper.release();
    }

    public void setupNextMemeButton(RelativeLayout nextMemeButton) {
        nextMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceTracker.doNotTrack();
                int recognizedEmotion = FaceTracker.classify();

                int reaction = 0;
                if (faceTracker != null) {
                    faceTracker.reset();
                }
                loadNextMeme();

                showEmoticonAnimation(recognizedEmotion);
            }
        });
    }

    private void showEmoticonAnimation(int recognizedEmotion) {
        swipeAnimationView.showAnimation(recognizedEmotion, new SwipeAnimationView.AnimationStateListener() {
            @Override
            public void onAnimationFinish() {
                FaceTracker.doTrack();
                memeWebViewWrapper.showNext();
                faceWatcherView.showMessagesIfNecessary();
            }

            @Override
            public void onAnimationStart() {
                faceWatcherView.hideMessagesIfNecessary();
            }
        });
    }

    private void updateWebView(String... urls) {
        for (String url : urls) {
            memeWebViewWrapper.loadUrl(url);
        }
    }

    /* @TODO */
    private void setupPrevMemeButton(RelativeLayout prevButton) {
    }

    /**
     * Callback for when a new face tracker is created.
     * Adds the FaceWatcherView (that informs the user when the device can't recognize
     * their facial expression or can't find their face) as an on update listener to the tracker.
     * @param faceTracker
     * The face tracker that was created.
     */
    @Override
    public void newFacetrackerCreated(FaceTracker faceTracker) {
        this.faceTracker = faceTracker;
        faceTracker.addOnUpdateListener(this.faceWatcherView);
    }

    /**
     * References all the activity's views.
     */
    private void referenceViews() {
        cameraSourceHelper = new CameraSourceHelper(this, this);
        cameraSourceHelper.referenceViews();

        nextButton = (RelativeLayout) this.findViewById(R.id.relative_layout_next);
        prevButton = (RelativeLayout) this.findViewById(R.id.relative_layout_previous);

        emoticonIconView = new EmoticonIconView((RelativeLayout) this.findViewById(R.id.relative_layout_emoticon), this);
        emoticonIconView.startUpdates();

        faceWatcherView = (FaceWatcherView) findViewById(R.id.face_watcher_view);

        swipeAnimationView = new SwipeAnimationView(
                (RelativeLayout) this.findViewById(R.id.emoticon_for_swipe_animation_wrapper),
                this);
    }

    private void getIdFromServer() {
        ServerCorrespondence.requestId(this, new ServerCorrespondence.ServerResponseHandler() {
            @Override
            public void handleResponse(String response) {
                JSONTokener tokener = new JSONTokener(response);
                try {
                    JSONObject obj = (JSONObject) tokener.nextValue();
                    if (obj.getString("status").equals("ok")) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("user_id", obj.getInt("id"));
                        editor.apply();

                        // this has not happened at this point, so the "basic usage"
                        // is started now!
                        loadFirstMemes();

                    } else {
                        throw new Exception("Response: Status was not \"ok\".");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, networkErrorHandler);
    }

    /**
     * Either kills the app (if the permission was not granted)
     * or creates the camera source.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CameraSourceHelper.RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        cameraSourceHelper.onRequestPermissionResult(grantResults);
    }

}
