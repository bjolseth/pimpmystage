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

    private void keepViewInsideScreen(FrameView ghostView) {
        Rect view = ghostView.getBounds();

        // positions
        if (view.left < 0)
            ghostView.setPos(0, view.top);
        if (view.top < 0)
            ghostView.setPos(view.left, 0);
        if (view.right > screenView.getWidth())
            ghostView.setPos(screenView.getWidth() - view.width(), view.top);
        if (view.bottom > screenView.getHeight())
            ghostView.setPos(view.left, screenView.getHeight() - view.height());

        // size (assuming frame rects always have the same aspect ratio as the screen
        if (view.width() > screenView.getWidth() || view.height() > screenView.getHeight()) {
            ghostView.setSize(screenView.getWidth(), screenView.getHeight());
            ghostView.setPos(0, 0);
        }
    }
}
