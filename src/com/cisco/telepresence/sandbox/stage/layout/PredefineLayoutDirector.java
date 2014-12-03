package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.util.Log;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.List;

/**
 * All layout changes are restricted to Prominent, Overlay or Equal layout family modes.
 */
public class PredefineLayoutDirector implements LayoutDirector{

    private final ScreenView screenView;
    private static final String TAG = "pimpmystage";

    public PredefineLayoutDirector(ScreenView screenView) {
        this.screenView = screenView;
    }

    @Override
    public void scaleCentered(FrameView view, float scale) {
        //view.scaleCentered(scale);
        scaleInProminentMode(view, scale);
    }

    private void scaleInProminentMode(FrameView view, float scale) {
        int prominentWidth = (int) (view.getBounds().width() * scale);

        // Prominent frame
        int max = screenView.getWidth();
        int x = (max-prominentWidth)/2;
        int prominentHeight = prominentWidth * 9/16;
        Rect bounds = new Rect(x, 0, x + prominentWidth, prominentHeight);
        view.setBounds(bounds);

        List<FrameView> frames = screenView.getFrameViews();
        int pipHeight = screenView.getHeight() - prominentHeight;
        int pipWidth = (int) (pipHeight * 16/9.);

        // TODO Need to find pips properly
        int xPip = (screenView.getWidth() - pipWidth*(frames.size() -1))/2;
        int yPip = prominentHeight;
        for (int i = 1; i<frames.size(); i++) {
            FrameView pip = frames.get(i);
            Rect boundsPip = new Rect(xPip, yPip, xPip + pipWidth, yPip + pipHeight);
            pip.setBounds(boundsPip);
            xPip += pipWidth;
        }

    }


    @Override
    public void moveView(FrameView view, int dx, int dy) {
        // Don't allow currently, only resizing
        //view.move(dx, dy);
    }

}
