package com.example.ndk_opencv_androidstudio.face_detection;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.example.ndk_opencv_androidstudio.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by jonbr on 04.11.2015.
 */
public class FaceDetectionTrainer {

    private final FaceDetector detector;
    private final Context context;

    private File resultsFolder;

    public FaceDetectionTrainer(FaceDetector detector, Context context) {
        this.detector = detector;
        this.context = context;
        resultsFolder = new File(Environment.getExternalStorageDirectory(), "face_detect_trainings");
        if (!resultsFolder.exists()) {
            resultsFolder.mkdir();
        }
    }

    public void train() {
        Log.d("ammi_ml", "starting training");
        new Thread(new DetectorThread(detector, this, 0)).start();
    }


    private synchronized void saveResults(Face face, int index) {
        if(face != null) {
            File file = new File(resultsFolder, resAsStrings[index] + ".txt");
            try {
                FileWriter writer = new FileWriter(file);

                writer.write("participant: " + resAsStrings[index] + "\n");
                writer.write("face: " + getFaceDescription(face) + "\n");

                for(Landmark lm : face.getLandmarks()) {
                    writer.write("\t" + getLmDescription(lm) + "\n");
                }

                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(index == drawables.length - 1) {
            Log.d("ammi_ml", "finished training");
            return;
        }
        new Thread(new DetectorThread(detector, this, index+1)).start();;
    }

    private String getLmDescription(Landmark lm) {
        String desc = "";

        switch(lm.getType()) {
            case Landmark.RIGHT_MOUTH: desc += "right_mouth: "; break;
            case Landmark.BOTTOM_MOUTH: desc += "bottom_mouth: "; break;
            case Landmark.LEFT_MOUTH: desc += "left_mouth: "; break;
            case Landmark.RIGHT_CHEEK: desc += "right_cheek: "; break;
            case Landmark.LEFT_CHEEK: desc += "left_cheek: "; break;
            case Landmark.LEFT_EAR: desc += "left_ear: "; break;
            case Landmark.LEFT_EAR_TIP: desc += "left_ear_tip: "; break;
            case Landmark.RIGHT_EAR: desc += "right_ear: "; break;
            case Landmark.RIGHT_EAR_TIP: desc += "right_ear_tip: "; break;
            case Landmark.LEFT_EYE: desc += "left_eye: "; break;
            case Landmark.RIGHT_EYE: desc += "right_eye: "; break;
            case Landmark.NOSE_BASE: desc += "nose_base: "; break;
        }

        desc += lm.getPosition().x + ", " + lm.getPosition().y;

        return desc;
    }

    private String getFaceDescription(Face face) {
        String desc = "";
        desc += "width: " + face.getWidth();
        desc += "\theight: " + face.getHeight();
        desc += "\tposition: " + face.getPosition().x + ", " + face.getPosition().y;

        return desc;
    }


    private class DetectorThread implements Runnable {

        FaceDetector detector;
        FaceDetectionTrainer callback;
        int index;

        public DetectorThread(FaceDetector detector, FaceDetectionTrainer callback,
                              int index) {
            this.detector = detector;
            this.callback = callback;
            this.index = index;
        }

        @Override
        public void run() {
            Frame.Builder builder = new Frame.Builder();
            builder.setBitmap(BitmapFactory.decodeResource(context.getResources(), drawables[index]));

            SparseArray<Face> faces = detector.detect(builder.build());
            if(faces.size() > 0) {
                callback.saveResults(faces.get(0), index);
            } else {
                callback.saveResults(null, index);
            }
        }

    }


    private static int[] drawables = new int[]{
            R.drawable.subject01_happy,
            R.drawable.subject01_normal,
            R.drawable.subject01_sad,
            R.drawable.subject01_sleepy,
            R.drawable.subject01_surprised,
            R.drawable.subject01_wink,
            R.drawable.subject02_happy,
            R.drawable.subject02_normal,
            R.drawable.subject02_sad,
            R.drawable.subject02_sleepy,
            R.drawable.subject02_surprised,
            R.drawable.subject02_wink,
            R.drawable.subject03_happy,
            R.drawable.subject03_normal,
            R.drawable.subject03_sad,
            R.drawable.subject04_normal,
            R.drawable.subject04_sad,
            R.drawable.subject04_sleepy,
            R.drawable.subject04_surprised,
            R.drawable.subject04_wink,
            R.drawable.subject05_happy,
            R.drawable.subject05_normal,
            R.drawable.subject05_sad,
            R.drawable.subject05_sleepy,
            R.drawable.subject05_surprised,
            R.drawable.subject05_wink,
            R.drawable.subject06_happy,
            R.drawable.subject06_normal,
            R.drawable.subject06_sad,
            R.drawable.subject06_sleepy,
            R.drawable.subject06_surprised,
            R.drawable.subject06_wink,
            R.drawable.subject07_happy,
            R.drawable.subject07_normal,
            R.drawable.subject07_sad,
            R.drawable.subject07_sleepy,
            R.drawable.subject07_surprised,
            R.drawable.subject07_wink,
            R.drawable.subject08_happy,
            R.drawable.subject08_normal,
            R.drawable.subject08_sad,
            R.drawable.subject08_sleepy,
            R.drawable.subject08_surprised,
            R.drawable.subject08_wink,
            R.drawable.subject09_happy,
            R.drawable.subject09_normal,
            R.drawable.subject09_sad,
            R.drawable.subject09_sleepy,
            R.drawable.subject09_surprised,
            R.drawable.subject09_wink,
            R.drawable.subject10_happy,
            R.drawable.subject10_normal,
            R.drawable.subject10_sad,
            R.drawable.subject10_sleepy,
            R.drawable.subject10_surprised,
            R.drawable.subject10_wink,
            R.drawable.subject11_happy,
            R.drawable.subject11_normal,
            R.drawable.subject11_sad,
            R.drawable.subject11_sleepy,
            R.drawable.subject11_surprised,
            R.drawable.subject11_wink,
            R.drawable.subject12_happy,
            R.drawable.subject12_normal,
            R.drawable.subject12_sad,
            R.drawable.subject12_sleepy,
            R.drawable.subject12_surprised,
            R.drawable.subject12_wink,
            R.drawable.subject13_happy,
            R.drawable.subject13_normal,
            R.drawable.subject13_sad,
            R.drawable.subject13_sleepy,
            R.drawable.subject13_surprised,
            R.drawable.subject13_wink,
            R.drawable.subject14_happy,
            R.drawable.subject14_normal,
            R.drawable.subject14_sad,
            R.drawable.subject14_sleepy,
            R.drawable.subject14_surprised,
            R.drawable.subject14_wink,
            R.drawable.subject15_happy,
            R.drawable.subject15_normal,
            R.drawable.subject15_sad,
            R.drawable.subject15_sleepy,
            R.drawable.subject15_surprised,
            R.drawable.subject15_wink
    };

    private static String[] resAsStrings = {
            "subject01_happy",
            "subject01_normal",
            "subject01_sad",
            "subject01_sleepy",
            "subject01_surprised",
            "subject01_wink",
            "subject02_happy",
            "subject02_normal",
            "subject02_sad",
            "subject02_sleepy",
            "subject02_surprised",
            "subject02_wink",
            "subject03_happy",
            "subject03_normal",
            "subject03_sad",
            "subject03_sleepy",
            "subject03_surprised",
            "subject03_wink",
            "subject04_happy",
            "subject04_normal",
            "subject04_sad",
            "subject04_sleepy",
            "subject04_surprised",
            "subject04_wink",
            "subject05_happy",
            "subject05_normal",
            "subject05_sad",
            "subject05_sleepy",
            "subject05_surprised",
            "subject05_wink",
            "subject06_happy",
            "subject06_normal",
            "subject06_sad",
            "subject06_sleepy",
            "subject06_surprised",
            "subject06_wink",
            "subject07_happy",
            "subject07_normal",
            "subject07_sad",
            "subject07_sleepy",
            "subject07_surprised",
            "subject07_wink",
            "subject08_happy",
            "subject08_normal",
            "subject08_sad",
            "subject08_sleepy",
            "subject08_surprised",
            "subject08_wink",
            "subject09_happy",
            "subject09_normal",
            "subject09_sad",
            "subject09_sleepy",
            "subject09_surprised",
            "subject09_wink",
            "subject10_happy",
            "subject10_normal",
            "subject10_sad",
            "subject10_sleepy",
            "subject10_surprised",
            "subject10_wink",
            "subject11_happy",
            "subject11_normal",
            "subject11_sad",
            "subject11_sleepy",
            "subject11_surprised",
            "subject11_wink",
            "subject12_happy",
            "subject12_normal",
            "subject12_sad",
            "subject12_sleepy",
            "subject12_surprised",
            "subject12_wink",
            "subject13_happy",
            "subject13_normal",
            "subject13_sad",
            "subject13_sleepy",
            "subject13_surprised",
            "subject13_wink",
            "subject14_happy",
            "subject14_normal",
            "subject14_sad",
            "subject14_sleepy",
            "subject14_surprised",
            "subject14_wink",
            "subject15_happy",
            "subject15_normal",
            "subject15_sad",
            "subject15_sleepy",
            "subject15_surprised",
            "subject15_wink"
    };

}
