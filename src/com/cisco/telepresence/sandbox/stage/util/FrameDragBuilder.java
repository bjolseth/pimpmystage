package com.cisco.telepresence.sandbox.stage.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class FrameDragBuilder extends View.DragShadowBuilder {

    private Drawable shadow;


    public FrameDragBuilder(View v) {
        super(v);
        shadow = new ColorDrawable(0x33999999);
    }

    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        int width, height;

        // Sets the width of the shadow to half the width of the original View
        width = getView().getWidth();

        // Sets the height of the shadow to half the height of the original View
        height = getView().getHeight();

        shadow.setBounds(0, 0, width, height);

        int w = 100, h = 100*9/16;
        size.set(w, h);

        touch.set(w/2, h/2);
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas);
    }
}