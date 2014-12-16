package com.cisco.telepresence.sandbox.stage.layout;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.cisco.telepresence.sandbox.stage.util.Animations;
import com.cisco.telepresence.sandbox.stage.util.Debug;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.ArrayList;
import java.util.List;

/**
 * All layout changes are restricted to Prominent, Overlay or Equal layout family modes.
 */
public class PredefineLayoutDirector implements LayoutDirector, Animator.AnimatorListener {

    private final float OverlayPiPHeightPercent = 0.20f;
    private final float TriggerPointOverlayPercent = 0.80f;

    private final ScreenView screenView;
    public enum LayoutFamily {Prominent, Overlay, Equal, Single};
    private LayoutFamily currentFamily = LayoutFamily.Single;

    private List<FrameView> frames;
    private boolean isAnimating = false;


    public PredefineLayoutDirector(ScreenView screenView) {
        this.screenView = screenView;
        frames = screenView.getFrameViews();

        if (! frames.isEmpty())
            setMainView(frames.get(0));
    }

    @Override
    public void moveView(View view, int dx, int dy) {
        // only swap views, don't move freely
    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void tempNextFamily() {
        if (currentFamily == LayoutFamily.Single)
            setLayoutFamily(LayoutFamily.Overlay);
        else if (currentFamily == LayoutFamily.Overlay)
            setLayoutFamily(LayoutFamily.Prominent);
        else if (currentFamily == LayoutFamily.Prominent)
            setLayoutFamily(LayoutFamily.Equal);
        else if (currentFamily == LayoutFamily.Equal)
            setLayoutFamily(LayoutFamily.Single);
    }

    public void setLayoutFamily(LayoutFamily family) {
        currentFamily = family;
        Debug.debug("Set layout family %s", family);
        updatePositions();
    }

    @Override
    public void updatePositions() {
        List<Rect> bounds = new ArrayList<Rect>();
        if (currentFamily == LayoutFamily.Equal)
            bounds = getPerfectEqualPositions();
        else if (currentFamily == LayoutFamily.Prominent)
            bounds = getPerfectProminentPositions((int) (screenView.getHeight() * TriggerPointOverlayPercent));
        else if (currentFamily == LayoutFamily.Overlay)
            bounds = getPerfectOverlayPositions();
        else if (currentFamily == LayoutFamily.Single)
            bounds = getPerfectSinglePositions();

        int i = 0;
        for (FrameView frame : frames) {
            Animations.animateFrameSizeAndPos(frame, bounds.get(i), this);
            i++;
        }
    }

    @Override
    public void scaleView(View scaledView, float scale) {
        if (! (scaledView instanceof FrameView))
            return;

        FrameView view = (FrameView) scaledView;

        if (isAnimating) {
            return; // Don't change frames while animating them
        }

        if (currentFamily == LayoutFamily.Single)
            scaleInSingle(view, scale);
        else if (currentFamily == LayoutFamily.Overlay)
            scaleInOverlay(view, scale);
        else if (currentFamily == LayoutFamily.Prominent)
            scaleInProminent(view, scale);
        else if (currentFamily == LayoutFamily.Equal)
            scaleInEqual(view, scale);
    }

    private void scaleInEqual(FrameView view, float scale) {

        Rect bounds = view.getBounds();
        int shrinkPerEvent = 0;
        if (scale < 1) shrinkPerEvent = 3;
        else if (scale > 1) shrinkPerEvent = -3;

        shrinkCentered(bounds, shrinkPerEvent);
        view.setBounds(bounds);

        if(scale > 1 && view.getHeight() > 0.5 * getScreenRect().height()) {
            setMainView(view);
            setLayoutFamily(LayoutFamily.Prominent);
        }
    }

    private void scaleInProminent(View view, float scale) {
        boolean isMainView = view == getMainView();

        if (! isMainView)
            scale = 1 / scale;

        int newHeight = (int) (getMainView().getHeight() * scale);

        float prominentPercent = newHeight / (float) getScreenRect().height();

        float equalTriggerPercent = getTriggerPointEqualPercent();

        if (scale > 1 && prominentPercent > TriggerPointOverlayPercent) {
            setLayoutFamily(LayoutFamily.Overlay);
        }
        else if (prominentPercent < equalTriggerPercent) {
            setLayoutFamily(LayoutFamily.Equal);
        }
        else if (scale < 1) {
            List<Rect> bounds = getPerfectProminentPositions(newHeight);
            for (int i=0; i<bounds.size(); i++) {
                frames.get(i).setBounds(bounds.get(i)); // don't animate this
            }
        }
    }

    private void scaleInOverlay(View view, float scale) {
        boolean isMainView = view == getMainView();

        if (isMainView) {

            // scale is actually exactly 1 when starting gesture
            if (scale < 1)
                setLayoutFamily(LayoutFamily.Prominent);
            else if (scale > 1)
                setLayoutFamily(LayoutFamily.Single);
        }
    }

    private void scaleInSingle(View view, float scale) {
        if (view == getMainView()) {

            if (scale < 1 && frames.size() > 1)
                setLayoutFamily(LayoutFamily.Overlay);

            else {
                int growPerEvent = (scale > 1 ?  2 : -2);
                FrameView main = getMainView();
                Rect rect = main.getBounds();
                shrinkCentered(rect, -growPerEvent);
                main.setBounds(rect);
            }
        }
    }

    private void shrinkCentered(Rect r, int distance) {
        r.left = r.left + distance;
        r.right = r.right - distance;
        r.top = r.top + distance;
        r.bottom = r.bottom - distance;
    }

    private float getTriggerPointEqualPercent() {
        int count = frames.size();
        if (count < 4)
            return 0.5f;
        else
            return (float) count / (count + 1);
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


    @Override
    public void swapPositionAndSize(FrameView view1, FrameView view2) {
        if (view1 == getMainView())
            setMainView(view2);
        else if (view2 == getMainView())
            setMainView(view1);

        Rect bounds1 = view1.getBounds();
        Rect bounds2 = view2.getBounds();

        Animations.animateFrameSizeAndPos(view1, bounds2, this);
        Animations.animateFrameSizeAndPos(view2, bounds1, this);
    }


    private Rect getScreenRect() {
        return new Rect(0, 0, screenView.getWidth(), screenView.getHeight());
    }

    private List<Rect> getPerfectSinglePositions() {
        List<Rect> bounds = getPerfectOverlayPositions();
        for (int i=1; i < bounds.size(); i++) {
            bounds.get(i).top = screenView.getHeight(); // place the pips 'under' the screen bottom
        }
        return bounds;
    }

    private List<Rect>getPerfectOverlayPositions() {
        List<Rect> bounds = new ArrayList<Rect>();

        Rect prominent = getScreenRect();
        bounds.add(prominent);

        int pipHeight = (int) (screenView.getHeight() * OverlayPiPHeightPercent);
        int pipWidth = (int) (pipHeight * 16/9.);

        int xPip = (screenView.getWidth() - pipWidth*(frames.size() -1))/2;
        int yPip = screenView.getHeight() - pipHeight;
        for (int i = 1; i<frames.size(); i++) {
            Rect boundsPip = new Rect(xPip, yPip, xPip + pipWidth, yPip + pipHeight);
            xPip += pipWidth;
            bounds.add(boundsPip);
        }

        return bounds;
    }

    private List<Rect> getPerfectEqualPositions() {
        List<Rect> bounds = new ArrayList<Rect>();

        int xCenter = screenView.getWidth()/2;
        int yCenter = screenView.getHeight()/2;

        if (frames.size() < 4) {
            return getPerfectProminentPositions(yCenter);
        }

        // For anything more than 3 frames, we draw four in a grid and skip the rest
        bounds.add(new Rect(0, 0, xCenter, yCenter));
        bounds.add(new Rect(xCenter, 0, xCenter*2, yCenter));
        bounds.add(new Rect(0, yCenter, xCenter, yCenter*2));
        bounds.add(new Rect(xCenter, yCenter, xCenter*2, yCenter*2));
        return bounds;
    }

    private List<Rect> getPerfectProminentPositions(int prominentHeight) {
        List<Rect> bounds = new ArrayList<Rect>();

        // Prominent frame
        int prominentWidth = prominentHeight * 16/9;
        int max = screenView.getWidth();
        int x = (max-prominentWidth)/2;
        Rect prominent = new Rect(x, 0, x + prominentWidth, prominentHeight);

        bounds.add(prominent);

        List<FrameView> frames = screenView.getFrameViews();
        int pipHeight = screenView.getHeight() - prominentHeight;
        int pipWidth = (int) (pipHeight * 16/9.);

        int xPip = (screenView.getWidth() - pipWidth*(frames.size() -1))/2;
        int yPip = prominentHeight;
        for (int i = 1; i<frames.size(); i++) {
            Rect boundsPip = new Rect(xPip, yPip, xPip + pipWidth, yPip + pipHeight);
            bounds.add(boundsPip);
            xPip += pipWidth;
        }

        return bounds;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        isAnimating = true;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        isAnimating = false;
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        isAnimating = false;
    }

    @Override
    public void onAnimationRepeat(Animator animator) {}
}
