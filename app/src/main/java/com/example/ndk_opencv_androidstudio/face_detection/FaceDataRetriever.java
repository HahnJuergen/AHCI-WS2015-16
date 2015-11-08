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

import com.example.ndk_opencv_androidstudio.auxiliary.AnalyticGeometry;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceDataRetriever extends Overlay.ViewTransformation {
    private PointF posLeftEye = null, posRightEye = null, posLeftMouth = null,
        posRightMouth = null, posNoseBase = null, posBottomMouth = null,
        posLeftCheek = null, posRightCheek = null, facePosition = null;

    private int faceId = -1;

    private PointF[] landmarkPositions = null;

    private float smilingProbability = Face.UNCOMPUTED_PROBABILITY, faceEulerY = -1.0f,
            faceEulerZ = -1.0f, faceWidth = -1.0f, faceHeight = -1.0f, leftEyeOpenProbability = -1.0f,
            rightEyeOpenProbability = -1.0f;

    public FaceDataRetriever(Overlay overlay) {
        super(overlay);
    }

    public void updateFace(Face face) {
        postInvalidate();

        smilingProbability = face.getIsSmilingProbability();

        faceEulerY = face.getEulerY();
        faceEulerZ = face.getEulerZ();
        faceHeight = face.getHeight();
        faceWidth = face.getWidth();

        facePosition = face.getPosition();

        faceId = face.getId();

        leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
        rightEyeOpenProbability = face.getIsRightEyeOpenProbability();

        for(Landmark lm : face.getLandmarks()) {
            if(lm.getType() == Landmark.LEFT_EYE) posLeftEye = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.RIGHT_EYE) posRightEye = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.LEFT_MOUTH) posLeftMouth = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.RIGHT_MOUTH) posRightMouth = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.NOSE_BASE) posNoseBase = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.BOTTOM_MOUTH) posBottomMouth = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.LEFT_CHEEK) posLeftCheek = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
            if(lm.getType() == Landmark.RIGHT_CHEEK) posRightCheek = new PointF(translateX(lm.getPosition().x), translateY(lm.getPosition().y));
        }

        landmarkPositions = new PointF[] {
                posLeftEye,
                posRightEye,
                posLeftCheek,
                posRightCheek,
                posNoseBase,
                posLeftMouth,
                posRightMouth,
                posBottomMouth
        };
    }

    public final double getDistanceBetweenEyes() {
        return (
                (posLeftEye != null && posRightEye != null) ?
                        AnalyticGeometry.getGeometricVectorNorm(AnalyticGeometry.getGeometricVector(posLeftEye, posRightEye))
                        :
                        (double) Face.UNCOMPUTED_PROBABILITY
        );
    }

    public final double getDistanceBetweenMouthPoints() {
        return (
                (posLeftMouth != null && posRightMouth != null) ?
                        AnalyticGeometry.getGeometricVectorNorm(AnalyticGeometry.getGeometricVector(posLeftMouth, posRightMouth))
                        :
                        (double) Face.UNCOMPUTED_PROBABILITY
        );
    }

    public final double getDistanceBetweenCheekPoints() {
        return (
                (posLeftCheek != null && posRightCheek != null) ?
                        AnalyticGeometry.getGeometricVectorNorm(AnalyticGeometry.getGeometricVector(posLeftCheek, posRightCheek))
                        :
                        (double) Face.UNCOMPUTED_PROBABILITY
        );
    }

    public final float getSmilingProbability() {
        return smilingProbability;
    }

    public final PointF getPosLeftEye() {
        return posLeftEye;
    }

    public final PointF getPosRightEye() {
        return posRightEye;
    }

    public final PointF getPosLeftMouth() {
        return posLeftMouth;
    }

    public final PointF getPosRightMouth() {
        return posRightMouth;
    }

    public final PointF getPosNoseBase() {
        return posNoseBase;
    }

    public final PointF getPosBottomMouth() {
        return posBottomMouth;
    }

    public final PointF getPosLeftCheek() {
        return posLeftCheek;
    }

    public final PointF getPosRightCheek() {
        return posRightCheek;
    }

    public final PointF[] getLandmarkPositions() {
        return landmarkPositions;
    }

    public PointF getFacePosition() {
        return facePosition;
    }

    public int getFaceId() {
        return faceId;
    }

    public float getFaceEulerY() {
        return faceEulerY;
    }

    public float getFaceEulerZ() {
        return faceEulerZ;
    }

    public float getFaceWidth() {
        return faceWidth;
    }

    public float getFaceHeight() {
        return faceHeight;
    }

    public float getLeftEyeOpenProbability() {
        return leftEyeOpenProbability;
    }

    public float getRightEyeOpenProbability() {
        return rightEyeOpenProbability;
    }
}
