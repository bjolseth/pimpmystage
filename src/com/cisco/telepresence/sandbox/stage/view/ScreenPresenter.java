package com.cisco.telepresence.sandbox.stage.view;

import android.view.View;
import android.view.ViewGroup;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.FrameTouchListener;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.StageActivity;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;

import java.util.ArrayList;
import java.util.List;

public class ScreenPresenter implements FrameTouchListener.FrameTouchCallback {

    private static final String TAG = "pimpmystage";
    private ScreenView screenView;
    private FrameView ghostView;
    private FrameView viewBeingTouched;
    private static final float GhostOpacity = 0.1f;
    private LayoutDirector layoutDirector;

    public ScreenPresenter(ScreenView screenView, LayoutDirector director) {
        this.screenView = screenView;
        this.layoutDirector = director;
        FrameTouchListener stl = new FrameTouchListener(screenView.getContext(), this);

        setTouchListenerOnFrames(screenView, stl);
    }

    private void createGhostView(FrameView view) {
        FrameView clone = view.createClone(screenView.getScaleWidth(), screenView.getScaleHeight());
        ((ViewGroup) screenView.findViewById(R.id.singlescreen)).addView(clone);
        ghostView = clone;
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

        if (view instanceof FrameView) {
            viewBeingTouched = (FrameView) view;
            screenView.bringChildToFront(view);
            if (ghostView == null)
                createGhostView(viewBeingTouched);
            viewBeingTouched.setAlpha(GhostOpacity);
            layoutDirector.scaleCentered(ghostView, scale);
        }
        StageActivity.debug(String.format("scaling %.2f", scale));
    }

    @Override
    public void onSingleTap(View view) {
        StageActivity.debug("Single tap view");
    }

    @Override
    public void onDoubleTap(View view) {
        StageActivity.debug("Double tap view");
        screenView.bringChildToFront(view);
    }

    @Override
    public void onMove(View view, int dx, int dy) {
        StageActivity.debug(String.format("Move %d, %d", dx, dy));

        if (view instanceof FrameView) {
            viewBeingTouched = (FrameView) view;
            screenView.bringChildToFront(view);
            if (ghostView == null)
                createGhostView(viewBeingTouched);
            viewBeingTouched.setAlpha(GhostOpacity);
            layoutDirector.moveView(ghostView, dx, dy);
        }
    }

    @Override
    public void onEndTouch() {
        if (ghostView != null && viewBeingTouched != null)
            viewBeingTouched.setLayoutParams(ghostView.getLayoutParams());

        if (viewBeingTouched != null)
            viewBeingTouched.setAlpha(1f);

        screenView.removeView(ghostView);
        viewBeingTouched = null;
        ghostView = null;
    }

}
