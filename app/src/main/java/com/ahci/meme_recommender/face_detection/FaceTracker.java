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
package com.ahci.meme_recommender.face_detection;

import android.content.Context;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FaceTracker extends Tracker<Face> {

    private static class FloatComparator implements Comparator<Float> {
        @Override
        public int compare(Float lhs, Float rhs) {
            return Float.compare(lhs, rhs);
        }
    }

    private static FloatComparator FLOAT_COMPARATOR;

    static {
        FLOAT_COMPARATOR = new FloatComparator();
    }

    public static final int NOT_SMILING = 0;
    public static final int SMILING = 1;

    private static boolean doNotTrack;

    private static int pic_id = 0;
    private final int USER_ID = 3;

    private FaceDataRetriever mFaceDataRetriever;
    private Context context;

    /** those are all static because the actual face tracker may change at any time. */
    private static List<PointF> positionsLeftEyes = new ArrayList<>();
    private static List<PointF> positionsRightEyes = new ArrayList<>();
    private static List<PointF> positionsLeftMouth = new ArrayList<>();
    private static List<PointF> positionsRightMouth = new ArrayList<>();
    private static List<PointF> positionsNoseBase = new ArrayList<>();
    private static List<PointF> positionsBottomMouth = new ArrayList<>();
    private static List<PointF> positionsLeftCheek = new ArrayList<>();
    private static List<PointF> positionsRightCheek = new ArrayList<>();
    private static List<PointF> positionsFace = new ArrayList<>();

    private static List<Integer> faceIds = new ArrayList<>();

    private static List<Float> smilingProbability = new ArrayList<>();
    private static List<Float> eulerYFaceList = new ArrayList<>();
    private static List<Float> eulerZFaceList = new ArrayList<>();
    private static List<Float> faceWidthList = new ArrayList<>();
    private static List<Float> faceHeightList = new ArrayList<>();
    private static List<Float> leftEyeOpenProbabilities = new ArrayList<>();
    private static List<Float> rightEyeOpenProbabilities = new ArrayList<>();

    private static List<Long> times = new ArrayList<>();
    private static Set<OnFaceUpdateListener> onFaceUpdateListeners;

    static {
        onFaceUpdateListeners = new HashSet<>();
    }

    public static void doNotTrack() {
        doNotTrack = true;
    }

    public static void doTrack() {
        doNotTrack = false;
    }

    public FaceTracker(Overlay overlay, Context context) {
        this.context = context;
        mFaceDataRetriever = new FaceDataRetriever(overlay);
    }

    public void addOnUpdateListener(OnFaceUpdateListener onFaceUpdateListener) {
        if(!onFaceUpdateListeners.contains(onFaceUpdateListener)) {
            onFaceUpdateListeners.add(onFaceUpdateListener);
        }
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     *
     * !!!CENTRAL UPDATE LOOP FOR FACE RECOGNITION!!!
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        for(OnFaceUpdateListener listener : onFaceUpdateListeners) {
            listener.onFaceUpdate(face);
        }

        if(!doNotTrack) {

            mFaceDataRetriever.updateFace(face);

            positionsLeftEyes.add(mFaceDataRetriever.getPosLeftEye());
            positionsRightEyes.add(mFaceDataRetriever.getPosRightEye());
            positionsLeftMouth.add(mFaceDataRetriever.getPosLeftMouth());
            positionsRightMouth.add(mFaceDataRetriever.getPosRightMouth());
            positionsNoseBase.add(mFaceDataRetriever.getPosNoseBase());
            positionsBottomMouth.add(mFaceDataRetriever.getPosBottomMouth());
            positionsLeftCheek.add(mFaceDataRetriever.getPosLeftCheek());
            positionsRightCheek.add(mFaceDataRetriever.getPosRightCheek());
            positionsFace.add(mFaceDataRetriever.getFacePosition());

            faceIds.add(mFaceDataRetriever.getFaceId());

            smilingProbability.add(mFaceDataRetriever.getSmilingProbability());
            eulerYFaceList.add(mFaceDataRetriever.getFaceEulerY());
            eulerZFaceList.add(mFaceDataRetriever.getFaceEulerZ());
            faceWidthList.add(mFaceDataRetriever.getFaceWidth());
            faceHeightList.add(mFaceDataRetriever.getFaceHeight());
            leftEyeOpenProbabilities.add(mFaceDataRetriever.getLeftEyeOpenProbability());
            rightEyeOpenProbabilities.add(mFaceDataRetriever.getRightEyeOpenProbability());
            times.add(System.currentTimeMillis());
        }
    }

    public void save(int selectedEmotion, int userID) {
        //write everything to file
        //userid \t picid \t smile_probability \t ...
        try {

            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/user_test_data");
            String fName = "data_" + userID + ".csv";
            if(!dir.exists()) {
                dir.mkdirs();
                File file = new File(dir, fName);
                if (!Arrays.asList(dir.listFiles()).contains(fName)) {
                    try {
                        FileWriter fw = new FileWriter(file, true);
                        fw.append("user_id\t"
                                        + "pic_id\t"
                                        + "sp\t"
                                        + "xle\t"
                                        + "yle\t"
                                        + "leop\t"
                                        + "xre\t"
                                        + "yre\t"
                                        + "reop\t"
                                        + "xlm\t"
                                        + "ylm\t"
                                        + "xrm\t"
                                        + "yrm\t"
                                        + "xlc\t"
                                        + "ylc\t"
                                        + "xrc\t"
                                        + "yrc\t"
                                        + "xnb\t"
                                        + "ynb\t"
                                        + "xbm\t"
                                        + "ybm\t"
                                        + "x_face\t"
                                        + "y_face\t"
                                        + "face_id\t"
                                        + "euler_y\t"
                                        + "euler_z\t"
                                        + "face_width\t"
                                        + "face_height\t"
                                        + "emotion_was\t"
                                        + "timestamp\n"
                        );

                        fw.flush();
                        fw.close();
                    } catch (Exception e) {
                    }
                }
            }

            File file = new File(dir, fName);

            try {
                FileWriter fw = new FileWriter(file, true);

                for(int i = 0; i < smilingProbability.size(); i++) {
                    fw.append("" + userID + "\t" + pic_id + "\t" + smilingProbability.get(i) + "\t");
                    fw.append(getLineOfPointTuples(positionsLeftEyes.get(i)));
                    fw.append("" + leftEyeOpenProbabilities.get(i) + "\t");
                    fw.append(getLineOfPointTuples(positionsRightEyes.get(i)));
                    fw.append("" + rightEyeOpenProbabilities.get(i) + "\t");
                    fw.append(getLineOfPointTuples(positionsLeftMouth.get(i)));
                    fw.append(getLineOfPointTuples(positionsRightMouth.get(i)));
                    fw.append(getLineOfPointTuples(positionsLeftCheek.get(i)));
                    fw.append(getLineOfPointTuples(positionsRightCheek.get(i)));
                    fw.append(getLineOfPointTuples(positionsNoseBase.get(i)));
                    fw.append(getLineOfPointTuples(positionsBottomMouth.get(i)));
                    fw.append(getLineOfPointTuples(positionsFace.get(i)));

                    fw.append("" + faceIds.get(i) + "\t");
                    fw.append("" + eulerYFaceList.get(i) + "\t");
                    fw.append("" + eulerZFaceList.get(i) + "\t");
                    fw.append("" + faceWidthList.get(i) + "\t");
                    fw.append("" + faceHeightList.get(i) + "\t");
                    fw.append("" + selectedEmotion + "\t");
                    fw.append("" + times.get(i) + "\n");
                }

                fw.flush();
                fw.close();
            } catch (Exception e) {}


        } catch (Exception e) {}

        pic_id++;
    }

    private String getLineOfPointTuples(PointF p) {
        return (p != null) ? ("" + p.x + "\t" + p.y + "\t") : ("NA\t");
    }

    public void reset() {
        positionsLeftEyes.clear();
        positionsRightEyes.clear();
        positionsLeftMouth.clear();
        positionsRightMouth.clear();
        positionsNoseBase.clear();
        positionsBottomMouth.clear();
        positionsLeftCheek.clear();
        positionsRightCheek.clear();
        positionsFace.clear();

        faceIds.clear();

        smilingProbability.clear();
        eulerYFaceList.clear();
        eulerZFaceList.clear();
        faceWidthList.clear();
        faceHeightList.clear();
        leftEyeOpenProbabilities.clear();
        rightEyeOpenProbabilities.clear();
        times.clear();
    }

    private double getMaximum(List<Float> l) {
        double max = -1d;

        for(double d : l) max = max < d ? d : max;

        return max;
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

    /**
     *
     * In order to select the emoticon.
     * @return
     * One of the constants defined in the class:
     * <ul>
     *     <li>NOT_SMILING</li>
     *     <li>SMILING</li>
     * </ul>
     */
    public static int classify() {
        int classification = NOT_SMILING;

        if(smilingProbability.size() == 0) return classification; // @TODO return -1 instead

        Summary<Float> summary = new Summary<>();

        for(int i = 0; i < smilingProbability.size(); i++) {
            summary.add(smilingProbability.get(i));
        }

        if((summary.getValueAt(FLOAT_COMPARATOR, 0.7f) >= 0.4f ||
                summary.getValueAt(FLOAT_COMPARATOR, 0.95f) >= 0.8f)
            && someHighValuesInSecondHalf()
                ){
            classification = SMILING;
        }

        return classification;
    }

    private static boolean someHighValuesInSecondHalf() {
        int highValueCount = 0;
        for(int i = smilingProbability.size() / 2; i < smilingProbability.size(); i++) {
            if(smilingProbability.get(i) >= 0.3f) highValueCount++;
        }

        return highValueCount >= 6;
    }


    public static float getWeightedLastSmilingProbability() {
        if(smilingProbability.size() == 0) return 0.0f;

        float averageSmilingProbability = 0f;
        int maxLast = 10;
        if(maxLast > smilingProbability.size()) maxLast = smilingProbability.size();

        float totalWeight = 0;

        if(maxLast != 0) {
            for (int i = smilingProbability.size() - 1; i > smilingProbability.size() - maxLast; i--) {
                averageSmilingProbability += smilingProbability.get(i) * (1.0f / Math.pow(i, 5));
                totalWeight += 1.0f / Math.pow(i, 5);
            }
            averageSmilingProbability /= totalWeight;
        }

        return averageSmilingProbability;
    }
}