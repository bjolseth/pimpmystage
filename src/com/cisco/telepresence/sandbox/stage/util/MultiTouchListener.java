package com.cisco.telepresence.sandbox.stage.util;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class MultiTouchListener implements View.OnTouchListener, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector simpleTouchdetector;
    private View currentView;
    private final MultiTouchCallback callback;
    private final int LONGPRESS_TIMEOUT = 250;

    // Need our own, since the one from the gesturelistener doesn't respect changing the android longpress timeout
    private Handler longPressHandler = new Handler();

    private static final String TAG = "pimpmystage";

    public interface MultiTouchCallback {
        void onScaleView(View view, float scale);
        void onSingleTap(View view);
        void onDoubleTap(View view);
        void onEndTouch();
        void onLongPress(View view);
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
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return true;
    }

    public MultiTouchListener(final Context context, final MultiTouchCallback callback) {
        this.callback = callback;
        simpleTouchdetector = new GestureDetector(context, this);
        simpleTouchdetector.setOnDoubleTapListener(this);

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

        boolean severalFingers = motionEvent.getPointerCount() > 1;
        if (severalFingers) {
            scaleDetector.onTouchEvent(motionEvent);
            longPressHandler.removeCallbacksAndMessages(null);
        }
        else {
            simpleTouchdetector.onTouchEvent(motionEvent);
        }

        Runnable doLongPress = new Runnable() {
            @Override
            public void run() {
                callback.onLongPress(currentView);
            }
        };

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            longPressHandler.postDelayed(doLongPress, LONGPRESS_TIMEOUT);
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            longPressHandler.removeCallbacksAndMessages(null);

            if (motionEvent.getPointerCount() == 1) {
                callback.onEndTouch();
            }
        }

        return true;
    }
}
