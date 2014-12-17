package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class ManualLayoutDirector implements LayoutDirector {

    private final ScreenView screenView;
    private static final String TAG = "pimpmystage";

    public ManualLayoutDirector(ScreenView screenView) {
        this.screenView = screenView;
    }

    @Override
    public void scaleView(View view, float scale) {
        if (view instanceof FrameView) {
            ((FrameView) view).scaleCentered(scale);
            keepViewInsideScreen((FrameView) view);
        }
    }

    @Override
    public void moveView(View view, int dx, int dy) {
        if (view instanceof FrameView) {
            ((FrameView) view).move(dx, dy);
            keepViewInsideScreen((FrameView) view);
        }
    }

    @Override
    public void swapPositionAndSize(FrameView view1, FrameView view2) {
        // nothing to do, all positioning is manual
    }

    @Override
    public void updatePositions() {
        // nothing to do, all positioning is manual
    }

    private void keepViewInsideScreen(FrameView view) {
        Rect bounds = view.getBounds();

        boolean isOutside = false;
        int newX = bounds.left;
        int newY = bounds.top;

        // positions
        if (bounds.left < 0) {
            newX = 0;//ghostView.setPos(0, view.top);
            isOutside = true;
        }
        if (bounds.top < 0) {
            newY = 0; //ghostView.setPos(view.left, 0);
            isOutside = true;
        }
        if (bounds.right > screenView.getWidth()) {
            newX = screenView.getWidth() - bounds.width();
            isOutside = true;
        }

        if (bounds.bottom > screenView.getHeight()) {
            newY = screenView.getHeight() - bounds.height();
            isOutside = true;
        }
        if (isOutside)
            view.setPos(newX, newY);

        // size (assuming frame rects always have the same aspect ratio as the screen
        if (bounds.width() > screenView.getWidth() || bounds.height() > screenView.getHeight()) {
            view.setSize(screenView.getWidth(), screenView.getHeight());
            view.setPos(0, 0);
        }
    }
}
