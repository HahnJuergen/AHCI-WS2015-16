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
package com.example.ndk_opencv_androidstudio.face_detection;

import android.graphics.PointF;
import android.text.LoginFilter;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceTracker extends Tracker<Face> {
    private FaceDataRetriever mFaceDataRetriever;

    FaceTracker(Overlay overlay) {
        mFaceDataRetriever = new FaceDataRetriever(overlay);
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     *
     * !!!CENTRAL UPDATE LOOP!!!
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mFaceDataRetriever.updateFace(face);

        Log.i("TEST", "" + mFaceDataRetriever.getPosLeftEye().x + " : " + mFaceDataRetriever.getPosRightEye().y);
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem(int faceId, Face item) {
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
    }
}