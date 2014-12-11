package com.cisco.telepresence.sandbox.stage;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;


/**
 * For now, just prototyped to support fixed 3 monitor setup
 */
public class StageNavigator {

    public static final int MONITOR_LEFT = 0, MONITOR_MIDDLE = 1, MONITOR_RIGHT = 2;
    private ViewGroup stage;
    private static final int AnimationTime = 200;
    private static final int ScreenOffset = 1015;

    private float ZoomOutScale = 0.4f;
    int demoStep = 0;
    boolean isZoomedOut;

    public StageNavigator(ViewGroup stage) {
        this.stage = stage;
        isZoomedOut = stage.getScaleX() < 1;
    }

    public void zoomOut() {
        if (isZoomedOut)
            return;

        AnimatorSet set = new AnimatorSet();
        Animator scaleX = ObjectAnimator.ofFloat(stage, View.SCALE_X, 1, ZoomOutScale);
        Animator scaleY = ObjectAnimator.ofFloat(stage, View.SCALE_Y, 1, ZoomOutScale);
        Animator moveX = ObjectAnimator.ofFloat(stage, View.TRANSLATION_X, stage.getTranslationX(), 0);

        set.play(moveX).with(scaleX).with(scaleY);
        set.setDuration(AnimationTime);
        set.start();
        isZoomedOut = true;
    }

    public void focusOnView(int view) {
        float startX = stage.getTranslationX();
        float startScale = stage.getScaleX();

        float endX = 0;
        if (view == MONITOR_LEFT)
            endX = ScreenOffset;
        else if (view == MONITOR_RIGHT)
            endX = -ScreenOffset;

        AnimatorSet set = new AnimatorSet();
        Animator scaleX = ObjectAnimator.ofFloat(stage, View.SCALE_X, startScale, 1);
        Animator scaleY = ObjectAnimator.ofFloat(stage, View.SCALE_Y, startScale, 1);
        Animator moveX = ObjectAnimator.ofFloat(stage, View.TRANSLATION_X, startX, endX);

        set.play(scaleX).with(scaleY).with(moveX);
        set.setDuration(AnimationTime);
        set.start();

        isZoomedOut = false;
    }

}
