package com.example.ndk_opencv_androidstudio.face_detection;

/**
 * Created by jurgenhahn on 27/10/15.
 */

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

/**
 * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
 * uses this factory to create face trackers as needed -- one for each individual.
 */
public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

    private Overlay overlay;

    public FaceTrackerFactory(Overlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public Tracker<Face> create(Face face) {
        return new FaceTracker(overlay);
    }
}