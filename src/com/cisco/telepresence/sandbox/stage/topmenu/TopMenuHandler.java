package com.cisco.telepresence.sandbox.stage.topmenu;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.util.Animations;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class TopMenuHandler implements View.OnDragListener {

    private final View stage;

    public TopMenuHandler(View stage) {
        this.stage = stage;
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