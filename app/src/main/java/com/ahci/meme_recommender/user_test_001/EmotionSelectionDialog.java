package com.ahci.meme_recommender.user_test_001;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;

import java.util.ArrayList;
import java.util.List;

public class EmotionSelectionDialog {

    private final Context context;
    private int selectedEmotion = 0;
    private  ListView list;
    private AlertDialog dialog;

    public EmotionSelectionDialog(Context context) {
        this.context = context;
    }

    public void show() {
        dialog.show();
        list = (ListView) dialog.findViewById(R.id.choose_emotion_dialog_emotion_list);
        List<Emotion> emotions = getEmotions();
        final EmotionListAdapter adapter = new EmotionListAdapter(context, emotions);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < parent.getChildCount(); i++) {
                    parent.getChildAt(i).setBackgroundColor(Color.argb(0, 255, 255, 255));
                }

                view.setBackgroundColor(Color.rgb(240, 240, 255));
                selectedEmotion = position;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private View getView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.choose_emotion_dialog, null);

        return root;
    }

    public void setup(DialogInterface.OnClickListener listener) {
        dialog = new AlertDialog.Builder(context)
                .setTitle("Was war deine Reaktion?")
                .setPositiveButton("Ok", listener)
                .setView(getView(context))
                .create();
    }

    private List<Emotion> getEmotions() {
        List<Emotion> emotions = new ArrayList<>();

        emotions.add(new Emotion("Neutral / Andere", R.drawable.neutral));
        emotions.add(new Emotion("Lächeln", R.drawable.kind_of_happy));
        emotions.add(new Emotion("Grinsen", R.drawable.happy));
        emotions.add(new Emotion("Lachen", R.drawable.laughing));
        emotions.add(new Emotion("lautes Lachen", R.drawable.really_laughing));

        return emotions;
    }

    public int getSelectedEmotion() {
        return selectedEmotion;
    }
}
