package com.cisco.telepresence.sandbox.stage.view;

import android.view.DragEvent;
import android.view.View;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutChangeHandler;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.util.MultiTouchListener;


public class ScreenPresenter implements MultiTouchListener.MultiTouchCallback, View.OnDragListener {

    private ScreenView screenView;
    private LayoutDirector layoutDirector;
    boolean isCurrentlyInDragMode = false;
    int dragStartX, dragStartY;
    private LayoutChangeHandler layoutChangeHandler;
    private View.OnTouchListener touchListener;
    private View.OnDragListener onDragListener;


    public ScreenPresenter(ScreenView screenView, LayoutDirector director) {
        this.screenView = screenView;
        this.layoutDirector = director;

        MultiTouchListener multiTouchListener = new MultiTouchListener(screenView.getContext(), this);
        setTouchListenerOnAllFrames(screenView, multiTouchListener);
        setDragListenerOnAllFrames(screenView, this);
        setManualDragListenerOnScreenView(screenView, this);
    }

    public void setLayoutChangeHandler(LayoutChangeHandler handler) {
        this.layoutChangeHandler = handler;
    }

    private void setManualDragListenerOnScreenView(ScreenView screenView, ScreenPresenter screenPresenter) {
        screenView.setOnDragListener(this);
    }

    private void setTouchListenerOnAllFrames(ScreenView screenView, View.OnTouchListener touchListener) {
        this.touchListener = touchListener;
        for (int i=0; i<screenView.getChildCount(); i++) {
            View view = screenView.getChildAt(i);
            if (view instanceof FrameView) {
                view.setOnTouchListener(touchListener);
            }
        }
    }

    private void setDragListenerOnAllFrames(ScreenView screenView, View.OnDragListener dragListener) {
        this.onDragListener = dragListener;
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
        if (view instanceof FrameView && layoutChangeHandler != null)
            layoutChangeHandler.frameHasChanged((FrameView) view);
    }

    @Override
    public void onSingleTap(View view) {
    }

    @Override
    public void onDoubleTap(View view) {
    }

    @Override
    public void onLongPress(View view) {
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
        view.startDrag(null, shadow, view, 0);
    }

    @Override
    public void onEndTouch() {
    }


    @Override
    public boolean onDrag(View dropTarget, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        View viewBeingDragged = (View) dragEvent.getLocalState();

        if (action == DragEvent.ACTION_DROP) {
            if (viewBeingDragged instanceof FrameView)
                onFrameViewDropped((FrameView) viewBeingDragged, dropTarget, dragEvent);
            else if (viewBeingDragged instanceof TrayButton) {
                addTrayElementToScreen((TrayButton) viewBeingDragged);
            }
        }
        else if (action == DragEvent.ACTION_DRAG_ENTERED) {
            dragEnteredOrLeft(viewBeingDragged, dropTarget, true);
        }
        else if (action == DragEvent.ACTION_DRAG_EXITED) {
            dragEnteredOrLeft(viewBeingDragged, dropTarget, false);
        }
        else if (action == DragEvent.ACTION_DRAG_LOCATION) {
            // Detect drag started (use this instead of entered, to get coordinates in the correct coord system (!?)
            if (! isCurrentlyInDragMode) {
                isCurrentlyInDragMode = true;
                dragStartX = (int) dragEvent.getX();
                dragStartY = (int) dragEvent.getY();
            }
        }
        return true;
    }

    private void addTrayElementToScreen(TrayButton button) {
        // TODO need to make unique frame ids
        Frame frame = new Frame(0, Frame.FrameType.VIDEO, 0, 0, 2000, 2000, button.getName(), 1);
        FrameView view = new FrameView(screenView.getContext(), frame, screenView.getScaleWidth(), screenView.getScaleHeight());
        screenView.addFrame(view);
        view.setOnTouchListener(touchListener);
        view.setOnDragListener(onDragListener);
        layoutDirector.updatePositions();
    }

    private void dragEnteredOrLeft(View viewBeingDragged, View dropTarget, boolean entered) {
    }

    private void onFrameViewDropped(FrameView frameView, View dropZoneView, DragEvent dragEvent) {
        if (dropZoneView instanceof FrameView) {
            layoutDirector.swapPositionAndSize(frameView, (FrameView) dropZoneView);
            if (layoutChangeHandler != null)
                layoutChangeHandler.frameHasChanged(frameView);
        }

        else if (dropZoneView instanceof ScreenView) {
            int dx = (int) dragEvent.getX() - dragStartX;
            int dy = (int) dragEvent.getY() - dragStartY;
            layoutDirector.moveView(frameView, dx, dy);
            if (layoutChangeHandler != null)
                layoutChangeHandler.frameHasChanged(frameView);
            isCurrentlyInDragMode = false;
        }

    }

}
