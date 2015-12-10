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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

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
    private Button nextButton;
    private int userId = -1;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mOverlay = (Overlay) findViewById(R.id.faceOverlay);

        nextButton = (Button) this.findViewById(R.id.buttonNext);
        setupNextMemeButton(nextButton);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                ServerCorrespondence.downloading = false;
            }
        });

        myWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36\n");
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

//        final UserIdInputDialog dialog = new UserIdInputDialog(this);
//        dialog.setup(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface d, int which) {
//                userId = dialog.getUserId();
//            }
//        });
//        dialog.show();

        ServerCorrespondence.getMemeImage("/load_images.json", this, this);
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
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
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    public void setupNextMemeButton(Button nextMemeButton) {
        nextMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FaceTracker.doNotTrack();
//                showEmotionPickerDialog();
                faceTracker.reset();
                ServerCorrespondence.getMemeImage("/load_images.json", MainActivity.this, MainActivity.this);
            }
        });
    }

//    private void showEmotionPickerDialog() {
//        final EmotionSelectionDialog dialog = new EmotionSelectionDialog(this);
//        dialog.setup(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface d, int which) {
//                if (faceTracker != null) {
//                    faceTracker.save(dialog.getSelectedEmotion(), userId);
//                    faceTracker.reset();
//                }
//
//                ServerCorrespondence.getMemeImage("/load_images.json", MainActivity.this, MainActivity.this);
//            }
//        });
//
//        dialog.show();
//    }

    @Override
    public void newFacetrackerCreated(FaceTracker faceTracker) {
        this.faceTracker = faceTracker;
        faceTracker.addOnUpdateListener((FaceWatcherView) findViewById(R.id.face_watcher_view));
    }

    @Override
    public void onMemeDownloadFinished() {
        FaceTracker.doTrack();
    }
}
