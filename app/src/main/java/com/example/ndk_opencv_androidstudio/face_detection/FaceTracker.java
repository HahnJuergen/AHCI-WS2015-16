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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Environment;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.example.ndk_opencv_androidstudio.R;
import com.example.ndk_opencv_androidstudio.server_connection.ServerCorrespondence;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.opencv.core.Point;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FaceTracker extends Tracker<Face> {

    private static boolean doNotTrack;

    private static int pic_id = 0;
    private final int USER_ID = 3;

    private FaceDataRetriever mFaceDataRetriever;
    private Context context;
    private int increment = 0;

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

    /**
     * Update the position/characteristics of the face within the overlay.
     *
     * !!!CENTRAL UPDATE LOOP FOR FACE RECOGNITION!!!
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
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
                    fw.append("" + System.currentTimeMillis() + "\n");
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
}