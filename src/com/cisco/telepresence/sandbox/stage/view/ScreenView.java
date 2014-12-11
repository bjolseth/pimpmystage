package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;

import java.util.ArrayList;
import java.util.List;

public class ScreenView extends AbsoluteLayout {

    private final static int LOGICAL_WIDTH = 10000;
    private final static int LOGICAL_HEIGHT = LOGICAL_WIDTH;
    private final String TAG = "pimpmystage";
    private List<FrameView> frames = new ArrayList<FrameView>();

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

        removeAllViews();
        for (Frame frame : screen.getFrames()) {
            FrameView frameView = new FrameView(getContext(), frame, sx, sy);
            addView(frameView);
            frames.add(frameView);
        }
    }

    public List<FrameView> getFrameViews() {
        return frames;
    }


    public float getScaleWidth() {
        float factor = getLayoutParams().width / (float) LOGICAL_WIDTH;
        return factor;
    }

    public float getScaleHeight() {
        float factor = getLayoutParams().height / (float) LOGICAL_HEIGHT;
        return factor;
    }

    public void remove(FrameView frame) {
        removeView(frame);
        frames.remove(frame);
    }

    public void addFrame(FrameView view) {
        // moved from another screen
        if (view.getParent() != null)
            ((ScreenView) view.getParent()).remove(view);

        addView(view);
        frames.add(view);
    }
}
