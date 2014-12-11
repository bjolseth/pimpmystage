package com.cisco.telepresence.sandbox.stage.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.view.FrameView;

public class StageDragShadow extends View.DragShadowBuilder {

    private final Drawable shadow;
    private static final int WIDTH = 250;
    public StageDragShadow(FrameView view) {
        super(view);
        Bitmap b = BitmapFactory.decodeResource(view.getResources(), view.getAvatarImageResource());
        shadow = new BitmapDrawable(b);
    }

    public void onProvideShadowMetrics (Point size, Point touch) {

        int height = (int) (WIDTH * 9./16);
        shadow.setBounds(0, 0, WIDTH, height);
        size.set(WIDTH, height);

        // TODO would like to *not* center but that is not trivial to do
        touch.set(WIDTH / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }

}
