package com.cisco.telepresence.sandbox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

public class SandboxActivity extends Activity {

    private final static String LOG_TAG = "Sandy";
    private float scale = 1.0f;
    private View box;
    private int initBoxWidth;
    private int initBoxHeight;
    private ScaleGestureDetector scaleDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playstage);

        setListeners();
    }

    private void setListeners() {
        View stage = findViewById(R.id.stage);
        box = findViewById(R.id.movableBox);
        initBoxWidth = box.getLayoutParams().width;
        initBoxHeight = box.getLayoutParams().height;

        box.setOnTouchListener(new BoxTouchListener());
        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());

    }

    private Runnable resetBox = new Runnable() {
        @Override
        public void run() {
            repositionBox(0,0);
            box.setVisibility(View.VISIBLE);
        }
    };

    private void repositionBox(int x, int y) {
        View box = findViewById(R.id.movableBox);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) (box.getLayoutParams());
        lp2.leftMargin = x;
        lp2.topMargin = y;
        box.setLayoutParams(lp2);
    }

    private final class BoxTouchListener implements View.OnTouchListener {
        private float startTouchX, startTouchY;
        private float startBoxX, startBoxY;

        public boolean onTouch(final View view, MotionEvent e) {
            if (e.getPointerCount() == 2) {
                Log.i(LOG_TAG, "Scaling started");
                return scaleDetector.onTouchEvent(e);
            }
            else {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(LOG_TAG, "Touch down. Start drag");
                    startTouchX = (int) e.getX();
                    startTouchY = (int) e.getY();
                    startBoxX = ((RelativeLayout.LayoutParams) box.getLayoutParams()).leftMargin;
                    startBoxY = ((RelativeLayout.LayoutParams) box.getLayoutParams()).topMargin;
                    return true;
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    Log.i(LOG_TAG, "Touch up");
                    return true;
                } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    //Log.i(LOG_TAG, String.format("Touch move %f,%f", e.getX(), e.getY()));
                    int x = (int) (startBoxX + e.getX() - startTouchX);
                    int y = (int) (startBoxY + e.getY() - startTouchY);
                    repositionBox(x, y);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) (box.getLayoutParams());
            int w = (int) (initBoxWidth * scale);
            int h = (int) (initBoxHeight * scale);
            lp.width = w;
            lp.height = h;
            //Log.i(LOG_TAG, String.format("set size %d x %d, orig %d x %d, scale %f", w, h, initBoxWidth, initBoxHeight, scale));
            box.setLayoutParams(lp);
            return true;
        }
    }


}
