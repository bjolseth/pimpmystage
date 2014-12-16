package com.cisco.telepresence.sandbox.stage.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.view.FrameView;

public class Animations {

    private static final int ANIMATION_TIME = 200;

    public static void animateFrameSizeAndPos(final FrameView frame, Rect newPos, final Animator.AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(250);

        Rect existingPos = frame.getBounds();
        final int x0 = existingPos.left;
        final int y0 = existingPos.top;
        final int w0 = existingPos.width();
        final int h0 = existingPos.height();

        final int x1 = newPos.left;
        final int y1 = newPos.top;
        final int w1 = newPos.width();
        final int h1 = newPos.height();

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float percent = (Float) valueAnimator.getAnimatedValue();
                int x = (int) (x0 + (x1 -x0) * percent);
                int y = (int) (y0 + (y1 - y0) * percent);
                int w = (int) (w0 + (w1 - w0) * percent);
                int h = (int) (h0 + (h1 - h0) * percent);
                frame.setBounds(new Rect(x, y, x+w, y+h));
            }
        });

        if (listener != null)
            anim.addListener(listener);

        anim.start();
    }

    public static void animateScaling(View view, float startScale,float endScale) {
        AnimatorSet set = new AnimatorSet();
        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, startScale, endScale);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale, endScale);
        Animator moveX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, view.getTranslationX(), 0);

        set.play(moveX).with(scaleX).with(scaleY);
        set.setDuration(ANIMATION_TIME);
        set.start();
    }

    public static AnimatorSet animateMoveAndScale(View view, Point startPos, Point endPos, float startScale, float endScale) {
        AnimatorSet set = new AnimatorSet();
        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, startScale, endScale);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale, endScale);
        Animator moveX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, startPos.x, endPos.x);
        Animator moveY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startPos.y, endPos.y);

        set.play(moveX).with(moveY).with(scaleX).with(scaleY);
        set.setDuration(ANIMATION_TIME);
        set.start();
        return set;
    }
}
