package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.util.Log;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class ManualLayoutDirector implements LayoutDirector{

    private final ScreenView screenView;
    private static final String TAG = "pimpmystage";

    public ManualLayoutDirector(ScreenView screenView) {
        this.screenView = screenView;
    }

    @Override
    public void scaleCentered(FrameView view, float scale) {
        view.scaleCentered(scale);
        keepViewInsideScreen(view);
    }

    @Override
    public void moveView(FrameView view, int dx, int dy) {
        view.move(dx, dy);
        keepViewInsideScreen(view);
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
