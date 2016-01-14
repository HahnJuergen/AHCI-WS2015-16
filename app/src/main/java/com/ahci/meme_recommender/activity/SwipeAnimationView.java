package com.ahci.meme_recommender.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ahci.meme_recommender.R;
import com.ahci.meme_recommender.face_detection.FaceTracker;

/**
 * When the user asks for the next image, their rating is shown on the screen in this view.
 */
public class SwipeAnimationView {

    private Context context;

    private RelativeLayout root;
    private ImageView emoticonImage;

    public SwipeAnimationView(RelativeLayout root, Context context) {
        this.context = context;
        this.root = root;
        this.emoticonImage = (ImageView) root.findViewById(R.id.emoticon_for_swipe_animation);
    }

    public void showAnimation(int whichEmoticon, AnimationStateListener listener) {

        final Animation animation = setupAnimation(listener);

        root.bringToFront();

        root.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        root.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;

        selectImage(whichEmoticon);

        root.startAnimation(animation);
    }

    private void selectImage(int whichEmoticon) {
        emoticonImage.setImageResource(
                whichEmoticon == FaceTracker.NOT_SMILING?
                        R.drawable.emoticon_neutral_big : R.drawable.emoticon_smiling_big
        );
    }

    @NonNull
    private Animation setupAnimation(final AnimationStateListener animationStateListener) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.rating_animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation a) {
                animationStateListener.onAnimationStart();
                root.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation a) {
                if (a.equals(animation)) {
                    root.setVisibility(View.GONE);
                    animationStateListener.onAnimationFinish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation a) {

            }
        });
        return animation;
    }

    public interface AnimationStateListener {
        public void onAnimationFinish();
        public void onAnimationStart();
    }

}
