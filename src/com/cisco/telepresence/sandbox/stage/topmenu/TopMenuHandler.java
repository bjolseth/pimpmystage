package com.cisco.telepresence.sandbox.stage.topmenu;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.StageNavigator;
import com.cisco.telepresence.sandbox.stage.util.Animations;
import com.cisco.telepresence.sandbox.stage.util.Debug;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class TopMenuHandler implements View.OnDragListener {

    private final View stage;
    private final StageNavigator stageNavigator;
    private LayoutListener layoutListener;
    private boolean isManualLayoutMode = false;

    public interface LayoutListener {
        void layoutChanged(boolean isManualLayout);
    }

    public TopMenuHandler(View stage, StageNavigator stageNavigator) {
        this.stage = stage;
        this.stageNavigator = stageNavigator;

        setupListeners(stage);
    }

    public void setLayoutListener(LayoutListener listener) {
        layoutListener = listener;
    }

    private void setupListeners(View stage) {
        stage.findViewById(R.id.zoom_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageNavigator.zoomOut();
            }
        });
        stage.findViewById(R.id.layout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Debug.debug("click layout");
                toggleLayoutMode();
            }
        });
    }

    public void toggleLayoutMode() {
        ImageView lock = (ImageView) stage.findViewById(R.id.layout_button);

        isManualLayoutMode = !isManualLayoutMode;

        if (isManualLayoutMode)
            lock.setImageResource(R.drawable.unlocked);
        else
            lock.setImageResource(R.drawable.locked);

        if (layoutListener != null)
            layoutListener.layoutChanged(isManualLayoutMode);

    }

    @Override
    public boolean onDrag(View menuElement, DragEvent dragEvent) {

        Object item = dragEvent.getLocalState();

        if (dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED)
            stage.findViewById(R.id.garbageCan).setAlpha(1.0f);

        else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED)
            stage.findViewById(R.id.garbageCan).setAlpha(0);

        else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
            growDropZone(true, menuElement);
        }

        else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED) {
            growDropZone(false, menuElement);
        }

        else if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
            growDropZone(false, menuElement);

            if (menuElement.getId() == R.id.garbageCan) {
                if (item instanceof FrameView)
                    removeFrame((FrameView) item);
            }
        }

        return true;
    }

    private void growDropZone(boolean grow, View view) {
        if (grow)
            Animations.animateScaling(view, 1f, 2f);
        else
            Animations.animateScaling(view, 2f, 1f);
    }

    private void removeFrame(FrameView frame) {
        ViewParent parent = frame.getParent();
        if (parent instanceof ScreenView) {
            ((ScreenView) parent).remove(frame);
        }
        //layoutDirector.updatePositions();
    }


}