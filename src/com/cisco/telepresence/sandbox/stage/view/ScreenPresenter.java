package com.cisco.telepresence.sandbox.stage.view;

import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.layout.CodecCustomLayoutHelper;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.util.Debug;
import com.cisco.telepresence.sandbox.stage.util.MultiTouchListener;
import com.cisco.telepresence.sandbox.stage.util.StageDragShadow;


public class ScreenPresenter implements MultiTouchListener.MultiTouchCallback, View.OnDragListener {

    private ScreenView screenView;
    private LayoutDirector layoutDirector;
    private boolean isCurrentlyInDragMode = false;
    private boolean isCurrentlyScaling = false;
    private int dragStartX;
    private int dragStartY;
    private CodecCustomLayoutHelper layoutChangeHandler;
    private View.OnTouchListener touchListener;
    private View.OnDragListener onDragListener;
    private MonitorListener monitorListener;

    public interface MonitorListener {
        void monitorSelected();
    }

    public ScreenPresenter(ScreenView screenView, LayoutDirector director) {
        this.screenView = screenView;
        this.layoutDirector = director;

        MultiTouchListener multiTouchListener = new MultiTouchListener(screenView.getContext(), this);
        setTouchListenerOnAllFrames(screenView, multiTouchListener);
        setManualDragListenerOnScreenView(screenView, this);
        screenView.setOnTouchListener(multiTouchListener);

        // don't want to listen to drops on other targets if we are doing free layout composition
        if (! (director instanceof ManualLayoutDirector))
            setDragListenerOnAllFrames(screenView, this);

    }

    public void setMonitorSelectedListener(MonitorListener listener) {
        monitorListener = listener;
    }

    public void setLayoutChangeHandler(CodecCustomLayoutHelper handler) {
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
        if (!(view  instanceof FrameView))
            return;

        isCurrentlyScaling = true;
        layoutDirector.scaleView(view, scale);
        if (view instanceof FrameView && layoutChangeHandler != null)
            layoutChangeHandler.frameHasChanged((FrameView) view);
    }

    @Override
    public void onSingleTap(View view) {
        if (monitorListener!= null)
            monitorListener.monitorSelected();
    }

    @Override
    public void onDoubleTap(View view) {
        ((PredefineLayoutDirector) layoutDirector).tempNextFamily();
    }

    @Override
    public void onLongPress(View view) {
        if (view instanceof FrameView) {
            View.DragShadowBuilder shadow = createShadow((FrameView) view);
            view.startDrag(null, shadow, view, 0);
        }
    }

    private View.DragShadowBuilder createShadow(FrameView view) {
        boolean useSmallPips = layoutDirector instanceof PredefineLayoutDirector;
        View.DragShadowBuilder shadow = (useSmallPips ?
                new StageDragShadow(view)
                : new View.DragShadowBuilder(view));
        return shadow;
    }

    @Override
    public void onEndTouch() {
        if (isCurrentlyScaling) {
            layoutDirector.updatePositions();
            isCurrentlyScaling = false;
        }
    }

    @Override
    public boolean onDrag(View dropTarget, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        View viewBeingDragged = (View) dragEvent.getLocalState();

        if (action == DragEvent.ACTION_DROP) {
            //Debug.debug("drop %s on %s", viewBeingDragged, dropTarget.getClass());
            if (viewBeingDragged instanceof FrameView)
                onFrameViewDropped((FrameView) viewBeingDragged, dropTarget, dragEvent);
            else if (viewBeingDragged instanceof TrayButton) {
                addTrayElementToScreen((TrayButton) viewBeingDragged);
            }
            isCurrentlyInDragMode = false;
        }
        else if (action == DragEvent.ACTION_DRAG_ENTERED) {
            //Debug.debug("drag enterd on %d, %d", dragStartX, dragStartY);
        }
        else if (action == DragEvent.ACTION_DRAG_LOCATION) {
            // Detect drag started (use this instead of entered, to get coordinates in the correct coord system (!?)
            if (! isCurrentlyInDragMode) {
                isCurrentlyInDragMode = true;
                dragStartX = (int) dragEvent.getX();
                dragStartY = (int) dragEvent.getY();
                //Debug.debug("start drag on %d, %d", dragStartX, dragStartY);
            }
            //Debug.debug("move drag on %d, %d", (int) dragEvent.getX(), (int) dragEvent.getY());
        }
        return true;
    }

    private void addTrayElementToScreen(TrayButton button) {
        // TODO need to make unique frame ids
        Frame.FrameType type = button.getType();
        Frame frame = new Frame(0, type, 4000, 4000, 0, 0, button.getName(), 1);
        final FrameView view = new FrameView(screenView.getContext(), frame, screenView.getScaleWidth(), screenView.getScaleHeight());

        if (type == Frame.FrameType.VIDEO)
            view.animateCallingHack();

        addFrame(view);
    }

    private void addFrame(FrameView view) {
        screenView.addFrame(view);
        view.setOnTouchListener(touchListener);
        view.setOnDragListener(onDragListener);
        layoutDirector.updatePositions();
    }

    private void onFrameViewDropped(FrameView frameView, View dropZoneView, DragEvent dragEvent) {

        if (dropZoneView instanceof FrameView) {
            // moved from other screen
            if (dropZoneView.getParent() != frameView.getParent()) {
                //Debug.debug("frame dropped on frame in other screen. moving");
                addFrame(frameView);
            }
            else {
                layoutDirector.swapPositionAndSize(frameView, (FrameView) dropZoneView);
            }
            if (layoutChangeHandler != null)
                layoutChangeHandler.frameHasChanged(frameView);
        }

        else if (dropZoneView instanceof ScreenView) {
            if (dropZoneView != frameView.getParent()) {
                Debug.debug("frame dropped on other screen. moving");
                addFrame(frameView);
            }
            //Debug.debug("stop drag: %d, %d", (int) dragEvent.getX(), (int) dragEvent.getY());
            int dx = (int) dragEvent.getX() - dragStartX;
            int dy = (int) dragEvent.getY() - dragStartY;
            //Debug.debug("frame dropped on %d, %d", dx, dy);
            //Debug.debug("frame dropped, relative move frame: %d, %d", dx, dy);
            layoutDirector.moveView(frameView, dx, dy);
            if (layoutChangeHandler != null)
                layoutChangeHandler.frameHasChanged(frameView);
        }

        isCurrentlyInDragMode = false;
    }

}
