package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.List;

/**
 * All layout changes are restricted to Prominent, Overlay or Equal layout family modes.
 */
public class PredefineLayoutDirector implements LayoutDirector{

    private final ScreenView screenView;
    private static final String TAG = "pimpmystage";

    private enum LayoutFamily {Prominent, Overlay, Equal};

    private LayoutFamily currentFamily = LayoutFamily.Overlay;
    float OverlayPiPHeightPercent = 0.20f;
    float TriggerPointOverlayPercent = 0.80f;
    float TriggerPointEqualPercent = 0.50f;
    float MinimumsEqualPercent = 0.49f;

    public PredefineLayoutDirector(ScreenView screenView) {
        this.screenView = screenView;
    }

    @Override
    public void scaleView(View view, float scale) {
        if (! (view  instanceof ScreenView))
            return;

        scale = (float) Math.sqrt(scale); // make scaling less aggressive
        if (currentFamily == LayoutFamily.Prominent)
            scaleInProminentMode(scale);
        else if (currentFamily == LayoutFamily.Overlay)
            scaleInOverlayMode(scale);
        else if (currentFamily == LayoutFamily.Equal)
            scaleInEqualMode(scale);

        determineLayoutMode();
    }

    private void scaleInEqualMode(float scale) {
        Log.i(TAG, "Scaling in Equal mode");
        List<FrameView> frames = screenView.getFrameViews();

        int maxHeight = screenView.getHeight();
        int maxWidth = screenView.getWidth();

        FrameView prominent = frames.get(0);

        if (frames.size() == 2) {

            int w = (int) ((prominent.getBounds().width()) * scale);
            int minimumsWidth = (int) (screenView.getWidth() * MinimumsEqualPercent);
            w = Math.max(w, minimumsWidth);
            int height = w*9/16;
            int y = (maxHeight-height)/2;
            int distance = (maxWidth/2 - w)*2;
            Rect bounds1 = new Rect(0, y, w, y + height);
            Rect bounds2 = new Rect(w + distance, y, w*2 + distance, y + height);

            frames.get(0).setBounds(bounds1);
            frames.get(1).setBounds(bounds2);
        }
    }

    private void scaleInOverlayMode(float scale) {
        Log.i(TAG, "Scaling in overlay mode");

        FrameView prominentView = screenView.getFrameViews().get(0);
        int prominentWidth = (int) (prominentView.getBounds().width() * scale);

        int max = screenView.getWidth();
        if (prominentWidth > max)
            prominentWidth = max;

        // Prominent frame
        int x = (max-prominentWidth)/2;
        int prominentHeight = prominentWidth * 9/16;
        Rect bounds = new Rect(x, 0, x + prominentWidth, prominentHeight);
        prominentView.setBounds(bounds);

        List<FrameView> frames = screenView.getFrameViews();

        int pipHeight = (int) (screenView.getHeight() * OverlayPiPHeightPercent);
        int pipWidth = (int) (pipHeight * 16/9.);

        // TODO Need to find pips properly
        int xPip = (screenView.getWidth() - pipWidth*(frames.size() -1))/2;
        int yPip = screenView.getHeight() - pipHeight;
        for (int i = 1; i<frames.size(); i++) {
            FrameView pip = frames.get(i);
            Rect boundsPip = new Rect(xPip, yPip, xPip + pipWidth, yPip + pipHeight);
            pip.setBounds(boundsPip);
            xPip += pipWidth;
        }

    }

    private void determineLayoutMode() {
        FrameView prominentView = screenView.getFrameViews().get(0);
        int prominentHeight= prominentView.getBounds().height();

        float percentSize = (prominentHeight / (float) screenView.getHeight()) ;
        Log.i(TAG, "percent: " + percentSize);

        if (percentSize < TriggerPointEqualPercent)
            currentFamily = LayoutFamily.Equal;
        else if (percentSize > TriggerPointOverlayPercent)
            currentFamily = LayoutFamily.Overlay;
        else
            currentFamily = LayoutFamily.Prominent;


    }

    private void scaleInProminentMode(float scale) {

        FrameView prominentView = screenView.getFrameViews().get(0);
        int prominentWidth = (int) (prominentView.getBounds().width() * scale);

        // Prominent frame
        int max = screenView.getWidth();
        int x = (max-prominentWidth)/2;
        int prominentHeight = prominentWidth * 9/16;
        Rect bounds = new Rect(x, 0, x + prominentWidth, prominentHeight);
        prominentView.setBounds(bounds);

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
    public void moveView(View view, int dx, int dy) {
        // Don't allow currently, only resizing
        //view.move(dx, dy);
    }

}
