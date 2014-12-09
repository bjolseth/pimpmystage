package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.cisco.telepresence.sandbox.stage.model.Frame;
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

        // set overlay as default
        currentBigPipPercent = TriggerPointOverlayPercent + 0.01f;
        setMainView(frames.get(0));
        currentFamily = LayoutFamily.Overlay;
    }

    private FrameView getMainView() {
        if (frames.isEmpty())
            return null;
        return frames.get(0);
    }

    private void setMainView(FrameView view) {
        frames.remove(view);
        frames.add(0, view);
        sendViewToBack(view);
    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void setBigPipPercent(float percent) {
        percent = Math.min(1, percent);

        if (frames.size() < 4)
            percent = Math.max(0.49f, percent);
        else
            percent = Math.max(0.65f, percent);

        currentBigPipPercent = percent;
        updatePositions();
    }

    @Override
    public void updatePositions() {
        if (frames.isEmpty())
            return;
        
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
        scale = (float) Math.sqrt(scale); // Make scaling slower/less aggressive

        // Make
        if (currentFamily == LayoutFamily.Equal && scale > 1) {
            setMainView((FrameView) view);
            setBigPipPercent(currentBigPipPercent * scale);
            determineLayoutMode();
        }

        // If we are making a small pip larger, make the main pip smaller (invert scaling)
        else {
            if (view != getMainView()) {
                scale = 1 / scale;
            }
            setBigPipPercent(currentBigPipPercent * scale);
        }

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

    private void stopAnimations() {
        for (FrameView view : frames) {
            view.clearAnimation();
        }
    }

    private void determineLayoutMode() {
        float triggerEqual = TriggerPointEqualPercent;
        if (frames.size() > 3)
            triggerEqual = 2/3.f;

        if (currentFamily == LayoutFamily.Prominent && currentBigPipPercent > TriggerPointOverlayPercent) {
            stopAnimations();
            switchLayoutFamily(LayoutFamily.Overlay);
            drawOverlayMode();
        }
        else if (currentFamily == LayoutFamily.Overlay && currentBigPipPercent < TriggerPointOverlayPercent) {
            stopAnimations();
            switchLayoutFamily(LayoutFamily.Prominent);
            drawProminentMode(true);
        }
        else if (currentFamily == LayoutFamily.Prominent && currentBigPipPercent < triggerEqual) {
            stopAnimations();
            switchLayoutFamily(LayoutFamily.Equal);
            drawEqualMode(true);
        }
        else if (currentFamily == LayoutFamily.Equal && currentBigPipPercent > triggerEqual) {
            stopAnimations();
            switchLayoutFamily(LayoutFamily.Prominent);
            drawProminentMode(true);
        }
    }

    private void switchLayoutFamily(LayoutFamily layout) {
        currentFamily = layout;
    }

    @Override
    public void swapPositionAndSize(FrameView view1, FrameView view2) {
        if (view1 == getMainView())
            setMainView(view2);
        else if (view2 == getMainView())
            setMainView(view1);

        Rect bounds1 = view1.getBounds();
        Rect bounds2 = view2.getBounds();

        Animations.animateFrame(view1, bounds2);
        Animations.animateFrame(view2, bounds1);
    }


    private void drawProminentMode(boolean animated) {

        FrameView prominentView = getMainView();
        int prominentHeight = (int) (screenView.getHeight() * currentBigPipPercent);

        // Prominent frame
        int prominentWidth = prominentHeight * 16/9;
        int max = screenView.getWidth();
        int x = (max-prominentWidth)/2;
        Rect bounds = new Rect(x, 0, x + prominentWidth, prominentHeight);
        prominentView.setBounds(bounds);

        List<FrameView> frames = screenView.getFrameViews();
        int pipHeight = screenView.getHeight() - prominentHeight;
        int pipWidth = (int) (pipHeight * 16/9.);

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
        // only swap views, don't move freely
    }

}
