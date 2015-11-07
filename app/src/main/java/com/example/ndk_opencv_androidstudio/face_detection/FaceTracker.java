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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FaceTracker extends Tracker<Face> {
    private boolean isNextImage = false;
    private FaceDataRetriever mFaceDataRetriever;
    private Context context;
    private int increment = 0;

    public FaceTracker(Overlay overlay, Context context) {
        this.context = context;
        mFaceDataRetriever = new FaceDataRetriever(overlay);

        Button b = (Button) ((Activity) context).findViewById(R.id.buttonNext);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ServerCorrespondence.downloading = true;
                isNextImage = true;
            }
        });
    }

    private List<Double> happiness = new ArrayList<>();

    /**
     * Update the position/characteristics of the face within the overlay.
     *
     * !!!CENTRAL UPDATE LOOP FOR FACE RECOGNITION!!!
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        if(!ServerCorrespondence.downloading) {
            mFaceDataRetriever.updateFace(face);
            happiness.add((mFaceDataRetriever.getSmilingProbability()));
        } else if(isNextImage){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(context).setTitle("Stimmt das?")
                            .setMessage("Wir denken du fandest das Bild witzig! (" + getMaximum(happiness) + ")")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("TEST", "POSITIVE");
                                    save();
                                    reset();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("TEST", "NEGATIVE");
                                    reset();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            ServerCorrespondence.getMemeImage("/load_images.json", context, ++increment);

            isNextImage = false;
        }
    }

    private void save() {
        //write everything to file
        //userid \t picid \t smile_probability \t ...
        
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "data.csv");
            FileWriter fw = new FileWriter(file, true);

            for(int i = 0; i < happiness.size(); i++) {
                fw.append("Happiness: " + happiness.get(i));
            }


        } catch (Exception e) {}
    }

    private void reset() {
        happiness.clear();
    }

    private double getMaximum(List<Double> l) {
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