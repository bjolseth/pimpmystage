package com.cisco.telepresence.sandbox.stage;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class ScaleFrameTouchListener implements View.OnTouchListener {

    private final static String TAG = "pimpmystage";
    private final ScaleGestureDetector scaleDetector;

    public ScaleFrameTouchListener(Context context) {
        ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                Log.i(TAG, "scale " + scaleGestureDetector.getScaleFactor());
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

            }
        };

        scaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i(TAG, "Touch view");
        scaleDetector.onTouchEvent(motionEvent);
        return false;
    }
}
