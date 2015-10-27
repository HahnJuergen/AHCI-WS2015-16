package com.example.ndk_opencv_androidstudio.face_detection;

import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceTracker extends Tracker<Face> {
    private Overlay mOverlay;
    private FaceDataRetriever mFaceDataRetriever;

    FaceTracker(Overlay overlay) {
        mOverlay = overlay;
        mFaceDataRetriever = new FaceDataRetriever(overlay);
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem(int faceId, Face item) {
        //mFaceDataRetriever.setId(faceId);
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        //mOverlay.add(mFaceDataRetriever);
        mFaceDataRetriever.updateFace(face);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        //mOverlay.remove(mFaceDataRetriever);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
       //mOverlay.remove(mFaceDataRetriever);
    }
}