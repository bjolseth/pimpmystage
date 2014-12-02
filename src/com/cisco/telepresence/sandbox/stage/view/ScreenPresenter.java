package com.cisco.telepresence.sandbox.stage.view;

import android.view.View;
import android.view.ViewGroup;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.FrameTouchListener;
import com.cisco.telepresence.sandbox.stage.StageActivity;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;

import java.util.ArrayList;
import java.util.List;

public class ScreenPresenter implements FrameTouchListener.FrameTouchCallback {

    private Frame tmpFrame;
    private FrameView tmpGhost;
    private ScreenView screenView;

    public ScreenPresenter(ScreenView screenView) {
        this.screenView = screenView;
        createDummySetup();
    }

    private void createDummySetup() {
        Screen screen = new Screen();
        Frame f1 = new Frame(Frame.FrameType.VIDEO, 3000, 3000, 3500, 3500, "Frame 1");
        List<Frame> frames = new ArrayList<Frame>();
        frames.add(f1);
        screen.setFrames(frames);

        screenView.setScreen(screen);

        screenView.invalidate();

        FrameTouchListener stl = new FrameTouchListener(screenView.getContext(), this);
        setTouchListenerOnFrames(screenView, stl);

        tmpFrame = f1;
        createGhostFrameTemporarily();
    }


    private void createGhostFrameTemporarily() {
        tmpGhost = new FrameView(screenView.getContext(), tmpFrame, screenView.getScaleWidth(), screenView.getScaleHeight());
        ((ViewGroup) screenView.findViewById(R.id.singlescreen)).addView(tmpGhost);
    }

    private void setTouchListenerOnFrames(ScreenView screenView, View.OnTouchListener touchListener) {
        for (int i=0; i<screenView.getChildCount(); i++) {
            View view = screenView.getChildAt(i);
            if (view instanceof FrameView) {
                view.setOnTouchListener(touchListener);
            }
        }
    }

    @Override
    public void onScaleView(View view, float scale) {
        StageActivity.debug(String.format("scaling %.2f", scale));
        tmpGhost.scaleCentered(scale);
    }

    @Override
    public void onSingleTap(View view) {
        StageActivity.debug("Single tap view");
    }

    @Override
    public void onDoubleTap(View view) {
        StageActivity.debug("Double tap view");
    }

    @Override
    public void onMove(View view, int dx, int dy) {
        StageActivity.debug(String.format("Move %d, %d", dx, dy));
        tmpGhost.move(dx, dy);
    }

    @Override
    public void onEndTouch() {
    }


}
