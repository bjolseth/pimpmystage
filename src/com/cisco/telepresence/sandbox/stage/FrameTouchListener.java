package com.cisco.telepresence.sandbox.stage;

import android.content.Context;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector;

public class FrameTouchListener implements View.OnTouchListener, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector detector;
    private View currentView;
    private int previousX;
    private int previousY;
    private final FrameTouchCallback callback;
    private boolean isScaling = false;

    private static final String TAG = "pimpmystage";

    public interface FrameTouchCallback {
        void onScaleView(View view, float scale);
        void onSingleTap(View view);
        void onDoubleTap(View view);
        void onMove(View view, int dx, int dy);
        void onEndTouch();
    }



    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        callback.onDoubleTap(currentView);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        callback.onSingleTap(currentView);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
//        Log.i(TAG, "on long press");
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
//        Log.i(TAG, "on fling");
        return true;
    }

    public FrameTouchListener(final Context context, final FrameTouchCallback callback) {
        this.callback = callback;
        detector = new GestureDetector(context, this);
        detector.setOnDoubleTapListener(this);

        ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                callback.onScaleView(currentView, scaleGestureDetector.getScaleFactor());
                return true;
            }
        };

        scaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        currentView = view;
        detector.onTouchEvent(motionEvent);
        scaleDetector.onTouchEvent(motionEvent);

        boolean severalFingers = motionEvent.getPointerCount() > 1;
        if (severalFingers)
            isScaling = true;

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            previousX = (int) motionEvent.getX(0);
            previousY = (int) motionEvent.getY(0);
        }
        else if (action == MotionEvent.ACTION_MOVE && ! isScaling) {
            int dx = (int) motionEvent.getX(0) - previousX;
            int dy = (int) motionEvent.getY(0) - previousY;
            previousX = (int) motionEvent.getX(0);
            previousY = (int) motionEvent.getY(0);
            callback.onMove(currentView, dx, dy);
//            Log.i(TAG, "move detected " + dx +"," + dy);
        }
        else if (action == MotionEvent.ACTION_UP) {
            isScaling = false;
            callback.onEndTouch();
        }
        return true;
    }
}
