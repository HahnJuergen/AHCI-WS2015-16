package com.ahci.meme_recommender.activity.tutorial;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.activity.MainActivity;
import com.ahci.meme_recommender.face_detection.user_face_watcher.FaceWatcherView;

public class TutorialHelper {

    public static final TutorialPage[] PAGES = {
            new TutorialPage("Viele Memes", "Wir haben Memes von den größten Meme-Seiten, z.B. 9gag und Memebase!", R.drawable.emoticon_smiling_big, 0xff1abc9c),
            new TutorialPage("Bitte Lächeln :)", "Lächle in die Frontkamera, um zu zeigen, dass dir ein Bild gefällt!", R.drawable.emoticon_smiling_big, 0xffe67e22),
            new TutorialPage("Keine Videoaufnahme", "Du wirst nicht gefilmt! Es wird kein Video von dir gespeichert.", R.drawable.emoticon_smiling_big, 0xFF9b59b6),
            new TutorialPage("Intelligente Vorschläge", "Wir merken uns, was dir gefällt, und zeigen dir mehr solche Bilder an!", R.drawable.emoticon_smiling_big, 0xFFf39c12),
            new TutorialPage("Korrekturen & Hilfe", "Klicke auf den Emoticon unten in der Mitte, um falsch erkannte Gesichtsausdrücke zu korrigieren. Oben rechts befindet sich eine Hilfefunktion.", R.drawable.emoticon_smiling_big, 0xFF3498db),
            new TutorialPage("Los geht's!", "Starten", R.drawable.emoticon_smiling_big, 0xFF2ecc71)
    };
    public static final int CONTROL_CIRCLE_COLOR_INACTIVE = 0xffcccccc;
    public static final int CONTROL_CIRCLE_COLOR_ACTIVE = 0xffffffff;
    private static OnFinishListener onFinishListener;

    public static void setOnFinishListener(OnFinishListener onFinish) {
        onFinishListener = onFinish;
    }

    public static OnFinishListener getOnFinishListener() {
        return onFinishListener;
    }

    private View rootView;
    private Context ctx;
    private ViewPager pager;
    private LinearLayout controlsView;

    private TextView[] controlCircles;

    public TutorialHelper(Context ctx, View root, OnFinishListener onFinish) {
        this.rootView = root;
        this.ctx = ctx;
        pager = (ViewPager) root.findViewById(R.id.tutorial_view_pager);
        controlsView = (LinearLayout) root.findViewById(R.id.tutorial_controls);
        onFinishListener = onFinish;
    }

    public void showTutorial(FragmentManager fm, WindowManager wm) {
        rootView.setVisibility(View.VISIBLE);
        pager.setVisibility(View.VISIBLE);
        PagerAdapter adapter = new PagerAdapter(fm);
        pager.setAdapter(adapter);
        setupOnPagerChangeListener();
        pager.setCurrentItem(0);
        initControlCircles(wm, adapter);
    }

    private void setupOnPagerChangeListener() {
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < controlCircles.length; i++) {
                    controlCircles[i].setTextColor(CONTROL_CIRCLE_COLOR_INACTIVE);
                }
                controlCircles[position].setTextColor(CONTROL_CIRCLE_COLOR_ACTIVE);
                controlsView.setBackgroundColor(PAGES[position].getColor());
            }
        });
    }

    private void initControlCircles(WindowManager wm, PagerAdapter adapter) {
        controlsView.setBackgroundColor(PAGES[0].getColor());

        controlCircles = new TextView[adapter.getCount()];
        int itemSize = getItemSize(wm, adapter);

        for(int i = 0; i < adapter.getCount(); i++) {
            TextView v = new TextView(ctx);
            v.setText("o");
            if(i == 0) v.setTextColor(CONTROL_CIRCLE_COLOR_ACTIVE);
            else v.setTextColor(CONTROL_CIRCLE_COLOR_INACTIVE);
            v.setOnClickListener(new ControlCircleClickListener(i, pager));
            v.setGravity(Gravity.CENTER);
            controlsView.addView(v, new LinearLayout.LayoutParams(itemSize, itemSize));
            controlCircles[i] = v;
        }
    }

    private int getItemSize(WindowManager wm, PagerAdapter adapter) {
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int itemSize = size.x / adapter.getCount() / 2;
        if(itemSize > FaceWatcherView.convertDpToPixel(40, ctx)) itemSize = (int) FaceWatcherView.convertDpToPixel(40, ctx);
        return itemSize;
    }

    public void hide() {
        rootView.setVisibility(View.GONE);
    }

    public interface OnFinishListener {
        public void onTutorialFinish();
    }

    private class ControlCircleClickListener implements View.OnClickListener {

        private final ViewPager pager;
        private int index;

        public ControlCircleClickListener(int index, ViewPager pager) {
            this.index = index;
            this.pager = pager;
        }

        @Override
        public void onClick(View v) {
            pager.setCurrentItem(index);
        }
    }
}
