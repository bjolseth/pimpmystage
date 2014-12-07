package com.cisco.telepresence.sandbox.stage.view;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.StageActivity;
import com.cisco.telepresence.sandbox.stage.util.MultiTouchListener;


public class ScreenPresenter implements MultiTouchListener.MultiTouchCallback, View.OnDragListener {

    private static final String TAG = "pimpmystage";
    private ScreenView screenView;
    private LayoutDirector layoutDirector;
    private View viewBeingDragged;

    public ScreenPresenter(ScreenView screenView, LayoutDirector director) {
        this.screenView = screenView;
        this.layoutDirector = director;

        MultiTouchListener multiTouchListener = new MultiTouchListener(screenView.getContext(), this);
        setTouchListenerOnAllFrames(screenView, multiTouchListener);
        //setDragListenerOnAllFrames(screenView, this);
        setManualDragListenerOnScreenView(screenView, this);
    }

    private void setManualDragListenerOnScreenView(ScreenView screenView, ScreenPresenter screenPresenter) {
        screenView.setOnDragListener(this);
    }

    private void setTouchListenerOnAllFrames(ScreenView screenView, View.OnTouchListener touchListener) {
        for (int i=0; i<screenView.getChildCount(); i++) {
            View view = screenView.getChildAt(i);
            if (view instanceof FrameView) {
                view.setOnTouchListener(touchListener);
            }
        }
    }

    private void setDragListenerOnAllFrames(ScreenView screenView, View.OnDragListener dragListener) {
        for (int i=0; i<screenView.getChildCount(); i++) {
            View view = screenView.getChildAt(i);
            if (view instanceof FrameView) {
                view.setOnDragListener(dragListener);
            }
        }
    }

    @Override
    public void onScaleView(View view, float scale) {
        layoutDirector.scaleView(view, scale);
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
    public void onLongPress(View view) {
        StageActivity.debug(String.format("long press view " + view));
        viewBeingDragged = view;
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
        view.startDrag(null, shadow, null, 0);
    }

    @Override
    public void onEndTouch() {
    }

    @Override
    public boolean onDrag(View dropZoneView, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action == DragEvent.ACTION_DROP) {
            StageActivity.debug("Drop dropZoneView " + viewBeingDragged + " on " + dropZoneView);

            if (dropZoneView instanceof FrameView && viewBeingDragged instanceof FrameView)
                layoutDirector.swapPositionAndSize((FrameView) viewBeingDragged, (FrameView) dropZoneView);

            else if (dropZoneView instanceof ScreenView && viewBeingDragged instanceof FrameView) {
               StageActivity.debug("move frame " + viewBeingDragged + " manually to " + dragEvent.getX() + "," + dragEvent.getY());
                layoutDirector.moveView(viewBeingDragged, (int) dragEvent.getX(), (int) dragEvent.getY());
            }
        }
        return true;
    }

}
