package com.example.ndk_opencv_androidstudio.user_test_001;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ndk_opencv_androidstudio.R;

import java.util.List;

/**
 * The EmotionListAdapter class creates a list of emotion items.
 */
public class EmotionListAdapter extends ArrayAdapter<Emotion> {

    private Context context;

    public EmotionListAdapter(Context context, List<Emotion> emotions) {
        super(context, R.layout.choose_emotion_dialog_item, emotions);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.choose_emotion_dialog_item, null);
        }

        Emotion emotion = this.getItem(position);

        // set name of emotion
        ((TextView) convertView.findViewById(R.id.choose_emotion_dialog_emotion_name)).setText(emotion.getName());
        ((ImageView) convertView.findViewById(R.id.choose_emotion_dialog_image)).setImageResource(emotion.getImage());

        return convertView;
    }


}
