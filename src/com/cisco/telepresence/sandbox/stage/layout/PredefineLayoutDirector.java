package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.StageActivity;
import com.cisco.telepresence.sandbox.stage.util.Animations;
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
    private List<FrameView> frames;
    private LayoutFamily currentFamily = LayoutFamily.Overlay;

    private final float OverlayPiPHeightPercent = 0.20f;
    private final float MinimumsEqualPercent = 0.49f;
    private final float TriggerPointEqualPercent = 0.50f;
    private final float TriggerPointOverlayPercent = 0.80f;

    private float currentBigPipPercent = 0.7f;

    public PredefineLayoutDirector(ScreenView screenView) {
        this.screenView = screenView;
        frames = screenView.getFrameViews();
    }

    private FrameView getMainView() {
        return frames.get(0);
    }

    private void setMainView(FrameView view) {
        frames.remove(view);
        frames.add(0, view);
    }

    public void setBigPipPercent(float percent) {
        currentBigPipPercent = percent;
        determineLayoutMode();

        if (currentFamily == LayoutFamily.Prominent)
            drawProminentMode(false);
        else if (currentFamily == LayoutFamily.Overlay)
            drawOverlayMode();
        else if (currentFamily == LayoutFamily.Equal)
            drawEqualMode(false);
    }

    @Override
    public void scaleView(View view, float scale) {

//        // If we are making a small pip larger, make the main pip smaller (invert scaling)
//        if (view != screenView.getFrameViews().get(0))
////        if ((float) view.getLayoutParams().height / screenView.getLayoutParams().height < 0.5)
//            scale = 1 / scale;
    }

    private void drawEqualMode(boolean animate) {
        if (frames.size() < 4) {
            drawOverlayMode();
            return;
        }

        int xCenter = screenView.getWidth()/2;
        int yCenter = screenView.getHeight()/2;

        Rect r1 = new Rect(0, 0, xCenter, yCenter);
        Rect r2 = new Rect(xCenter, 0, xCenter*2, yCenter);
        Rect r3 = new Rect(0, yCenter, xCenter, yCenter*2);
        Rect r4 = new Rect(xCenter, yCenter, xCenter*2, yCenter*2);

        if (animate) {
            Animations.animateFrame(frames.get(0), r1);
            Animations.animateFrame(frames.get(1), r2);
            Animations.animateFrame(frames.get(2), r3);
            Animations.animateFrame(frames.get(3), r4);
        }
        else {
            frames.get(0).setBounds(r1);
            frames.get(1).setBounds(r2);
            frames.get(2).setBounds(r3);
            frames.get(3).setBounds(r4);
        }
    }

    private void drawOverlayMode() {
        Log.i(TAG, "Scaling in overlay mode");

        FrameView prominentView = getMainView();
        int prominentHeight = (int) (screenView.getHeight() * currentBigPipPercent);
        int prominentWidth = prominentHeight * 16/9;

        int max = screenView.getWidth();
        if (prominentWidth > max)
            prominentWidth = max;

        // Prominent frame
        int x = (max-prominentWidth)/2;
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

        float triggerEqual = TriggerPointEqualPercent;
        if (frames.size() > 3)
            triggerEqual = 2/3.f;

        if (currentFamily == LayoutFamily.Prominent && currentBigPipPercent > TriggerPointOverlayPercent)
            switchLayoutFamily(LayoutFamily.Overlay);

        else if (currentFamily == LayoutFamily.Overlay && currentBigPipPercent < TriggerPointOverlayPercent)
            switchLayoutFamily(LayoutFamily.Prominent);

        else if (currentFamily == LayoutFamily.Prominent && currentBigPipPercent < triggerEqual) {
            switchLayoutFamily(LayoutFamily.Equal);
            drawEqualMode(true);
        }

        else if (currentFamily == LayoutFamily.Equal && currentBigPipPercent > triggerEqual) {
            switchLayoutFamily(LayoutFamily.Prominent);
            drawProminentMode(true);
        }

    }

    public void switchLayoutFamily(LayoutFamily layout) {
        currentFamily = layout;
    }

    private void drawProminentMode(boolean animated) {

        FrameView prominentView = getMainView();
        int prominentHeight = (int) (screenView.getHeight() * currentBigPipPercent);
        StageActivity.debug("scaling " + currentBigPipPercent + " to height" + prominentHeight);

        // Prominent frame
        int prominentWidth = prominentHeight * 16/9;
        int max = screenView.getWidth();
        int x = (max-prominentWidth)/2;
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
            if (animated)
                Animations.animateFrame(pip, boundsPip);
            else
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
