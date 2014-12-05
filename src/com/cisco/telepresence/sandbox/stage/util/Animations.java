package com.cisco.telepresence.sandbox.stage.util;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import com.cisco.telepresence.sandbox.stage.view.FrameView;

public class Animations {

    private static final int ANIMATION_TIME = 200;

    public static void animateFrame(final FrameView frame, Rect newPos) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(ANIMATION_TIME);

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

        anim.start();
    }

}
