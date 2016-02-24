package com.ahci.meme_recommender.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.camera_preview.CameraSourcePreview;
import com.ahci.meme_recommender.face_detection.FaceTrackerFactory;
import com.ahci.meme_recommender.face_detection.Overlay;
import com.ahci.meme_recommender.face_detection.user_face_watcher.FaceWatcherView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

/**
 * Decomposition class/singleton, only used in MainActivity.<br/>
 * The CameraSourceHelper handles everything concerning the camera,
 * from requesting the permission to activating and closing the video capture.
 */
public class CameraSourceHelper {

    public static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    public static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final String TAG = "FaceTracker";


    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private Overlay mOverlay;

    private Activity activity;
    private FaceTrackerFactory.OnNewTrackerListener faceTrackerListener;

    public CameraSourceHelper(Activity activity, FaceTrackerFactory.OnNewTrackerListener faceTrackerListener) {
        this.activity = activity;
        this.faceTrackerListener = faceTrackerListener;
    }

    /**
     * Checks the camera permission before accessing the camera. Rrequests permission if the
     * permission is not granted yet.
     */
    public void checkCamera() {
        //
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Tries to start the camera preview, shows an error dialog if the required google play services are unavailable.
     */
    public void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS);
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

    /**
     * If the app does not have the camera permission, this method requests it.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = activity;
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

    /**
     * Creates a new camera source:
     * <ul>
     *     <li>640 * 480 px</li>
     *     <li>Front camera</li>
     *     <li>30 fps</li>
     * </ul>
     */
    private void createCameraSource() {
        Context context = activity.getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new FaceTrackerFactory(mOverlay, activity, faceTrackerListener))
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

    /**
     * Stops the camera preview.
     */
    public void stopMPreview() {
        mPreview.stop();
    }

    public void referenceViews() {
        mPreview = (CameraSourcePreview) activity.findViewById(R.id.preview);
        mOverlay = (Overlay) activity.findViewById(R.id.faceOverlay);
    }

    /**
     * Releases the camera source
     */
    public void release() {
        if(mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * If the permissions were granted: creates the camera source
     * Otherwise: calls "finish"
     * @param grantResults array containing (hopefully) PackageManager.PERMISSION_GRANTED at index 0
     */
    public void onRequestPermissionResult(int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camera source
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        /* Kills the app, but informs the user first */
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }
}
