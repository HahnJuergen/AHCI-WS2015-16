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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.camera_preview.CameraSourcePreview;
import com.ahci.meme_recommender.face_detection.FaceTracker;
import com.ahci.meme_recommender.face_detection.FaceTrackerFactory;
import com.ahci.meme_recommender.face_detection.Overlay;
import com.ahci.meme_recommender.face_detection.user_face_watcher.FaceWatcherView;
import com.ahci.meme_recommender.server_connection.ServerCorrespondence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements FaceTrackerFactory.OnNewTrackerListener,
    ServerCorrespondence.OnMemeDownloadFinishedListener {
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private Overlay mOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private FaceTracker faceTracker;

    private RelativeLayout nextButton;
    private RelativeLayout prevButton;
    private RelativeLayout emoticonButton;

    private WebView memeWebView;

    private int userId = -1;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        if(firstAppStart()) {
            showTutorial();
        } else {
            setup();
            ServerCorrespondence.getMemeImage("/load_images.json", this, this);
        }
    }


    private void setup() {
        referenceViews();
        setupControlButtons();
        checkCamera();
        setupMemeWebView();
    }

    private void setupControlButtons() {
        setupNextMemeButton(nextButton);
        setupPrevMemeButton(prevButton);

    }

    private void setupMemeWebView() {
        memeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ServerCorrespondence.downloading = false;
            }
        });

        memeWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36\n");
        memeWebView.getSettings().setJavaScriptEnabled(true);
        memeWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * @TODO implement this
     * @return
     * <ul>
     *     <li><em>True</em> if this is the first start of the app</li>
     *     <li><em>False</em> if this is not the first start of the app</li>
     * </ul>
     */
    private boolean firstAppStart() {
        return false;
    }

    /** @TODO implement this */
    private void showTutorial() {

    }

    /**
     * Checks the camera permission before accessing the camera. Rrequests permission if the
     * permission is not granted yet.
     */
    private void checkCamera() {
        //
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new FaceTrackerFactory(mOverlay, this, this))
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
        FaceWatcherView.CAMERA_HEIGHT = 640;
        FaceWatcherView.CAMERA_WIDTH = 480;
    }

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
        FaceWatcherView.RUN_TIMER = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        FaceWatcherView.RUN_TIMER = false;
    }

    @Override
    protected void onDestroy() {
        FaceWatcherView.KILL_TIMER = true;
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Either kills the app (if the permission was not granted)
     * or creates the camera source.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        /* Kills the app, but informs the user first */
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /* @TODO */
    public void setupNextMemeButton(RelativeLayout nextMemeButton) {
        nextMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceTracker.doNotTrack();
                if(faceTracker != null) faceTracker.reset();
                ServerCorrespondence.getMemeImage("/load_images.json", MainActivity.this, MainActivity.this);
            }
        });
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
        faceTracker.addOnUpdateListener((FaceWatcherView) findViewById(R.id.face_watcher_view));
    }

    /**
     * Callback that is called when the HTML for a meme is downloaded.
     */
    @Override
    public void onMemeDownloadFinished() {
        FaceTracker.doTrack();
    }

    /**
     * @TODO Possibly move this (and the other ServerCorr. interface methods to a separate own file
     *
     * Displays a dialog that allows the user to open the network connectivity settings or
     * close the app.
     */
    @Override
    public void onNoNetworkAvailable() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(this.getString(R.string.no_network_enabled_title))
                .setMessage(R.string.no_network_enabled_body)
                .setPositiveButton(R.string.open_network, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });

        dialogBuilder.create().show();
    }

    @Override
    public void onNoConnectionToServerPossible() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(this.getString(R.string.server_offline))
                .setMessage(R.string.server_offline_body)
                .setPositiveButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });

        dialogBuilder.create().show();
    }

    @Override
    public void onNoConnectionAtAllPossible() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(this.getString(R.string.all_servers_offline))
                .setMessage(R.string.all_servers_offline_body)
                .setPositiveButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });

        dialogBuilder.create().show();
    }

    /**
     * References all the activity's views.
     */
    private void referenceViews() {
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mOverlay = (Overlay) findViewById(R.id.faceOverlay);

        nextButton = (RelativeLayout) this.findViewById(R.id.relative_layout_next);
        prevButton = (RelativeLayout) this.findViewById(R.id.relative_layout_previous);
        emoticonButton = (RelativeLayout) this.findViewById(R.id.relative_layout_emoticon);
        memeWebView = (WebView) findViewById(R.id.webview);
    }
}
