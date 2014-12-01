package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;

public class ScreenView extends AbsoluteLayout {

    private final static int LOGICAL_WIDTH = 10000;
    private final static int LOGICAL_HEIGHT = LOGICAL_WIDTH;
    private final String TAG = "pimpmystage";

    public ScreenView(Context context) {
        super(context);
    }

    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScreen(Screen screen) {
        float sx = getScaleWidth();
        float sy = getScaleHeight();

        for (Frame frame : screen.getFrames()) {
            FrameView frameView = new FrameView(getContext());
            int width = (int) (frame.getWidth() * sx);
            int height = (int) (frame.getHeight() * sy);
            int x = (int) (frame.getX() * sx);
            int y = (int) (frame.getY() * sy);
            LayoutParams layout = new LayoutParams(width, height, x, y);
            frameView.setLayoutParams(layout);
            frameView.setBackgroundColor(Color.GREEN);
            addView(frameView);

            Log.i(TAG, String.format("adding frame %d,%d,%d,%d", x, y, width, height));
        }
    }

    private float getScaleWidth() {
        float factor = getLayoutParams().width / (float) LOGICAL_WIDTH;
        return factor;
    }

    private float getScaleHeight() {
        float factor = getLayoutParams().height / (float) LOGICAL_HEIGHT;
        return factor;
    }

}
