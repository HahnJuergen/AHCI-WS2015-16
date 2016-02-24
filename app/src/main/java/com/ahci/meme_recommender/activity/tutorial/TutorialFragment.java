package com.ahci.meme_recommender.activity.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahci.meme_recommender.R;

public class TutorialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.tutorial_page, container, false);
        Bundle args = getArguments();
        int position = args.getInt("position");

        TutorialPage page = TutorialHelper.PAGES[position];

        rootView.setBackgroundColor(page.getColor());
        ((TextView) rootView.findViewById(R.id.tutorial_page_title)).setText(page.getTitle());
        ((ImageView) rootView.findViewById(R.id.tutorial_page_image)).setImageResource(page.getImageResource());
        ((TextView) rootView.findViewById(R.id.tutorial_page_explanation)).setText(page.getExplanation());

        if(position == TutorialHelper.PAGES.length - 1) {
            TextView startButton = ((TextView) rootView.findViewById(R.id.tutorial_page_explanation));
            startButton.setBackgroundResource(R.drawable.white_button_borders);
            startButton.setPadding(40, 20, 40, 20);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TutorialHelper.getOnFinishListener() != null) {
                        TutorialHelper.getOnFinishListener().onTutorialFinish();
                    }
                }
            });
        }

        return rootView;
    }

}
