package com.cisco.telepresence.sandbox;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

public class SandboxActivityOld extends Activity {

    private final static String LOG_TAG = "Sandy";
    private Handler dragTimerHandler = new Handler();
    private float scale = 1.0f;
    private View box;
    private int initBoxWidth;
    private int initBoxHeight;
    private ScaleGestureDetector scaleDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        showContextCard(false);
        showDragOutAffordance(false);
        setListeners();
        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    private void setListeners() {
        View stage = findViewById(R.id.stage);
        box = findViewById(R.id.movableBox);
        initBoxWidth = box.getLayoutParams().width;
        initBoxHeight = box.getLayoutParams().height;
        View screen = findViewById(R.id.welcome_screen);

        screen.setOnDragListener(new OutsideStageDragListener());
        stage.setOnDragListener(new StageDragListener());
        box.setOnTouchListener(new StageTouchListenerUsingDragNDropFramework());
    }

    private void showContextCard(boolean visible) {
        findViewById(R.id.context_card).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        scaleDetector.onTouchEvent(ev);
//        return true;
//    }

    private Runnable hideContextCard = new Runnable() {
        @Override
        public void run() {
            showContextCard(false);
        }
    };

    private void showDragOutAffordance(boolean show) {
        findViewById(R.id.dragremove_affordance).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private Runnable resetBox = new Runnable() {
        @Override
        public void run() {
            View box = findViewById(R.id.movableBox);
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) (box.getLayoutParams());
            lp2.leftMargin = 0;
            lp2.topMargin = 0;
            box.setLayoutParams(lp2);
            box.setVisibility(View.VISIBLE);
        }
    };

    private final class StageTouchListenerUsingDragNDropFramework implements View.OnTouchListener {
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            Log.i(LOG_TAG, "fingers: " + motionEvent.getPointerCount());
            if (motionEvent.getPointerCount() == 2) {
                return scaleDetector.onTouchEvent(motionEvent);
            }
            else {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(LOG_TAG, "Touch down. Start drag");

                    dragTimerHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                            view.startDrag(data, shadowBuilder, view, 0);
                            view.setVisibility(View.INVISIBLE);
                            showDragOutAffordance(true);
                        }
                    }, 200);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    dragTimerHandler.removeCallbacksAndMessages(null);
                    showContextCard(true);
                    Handler handler = new Handler();
                    handler.postDelayed(hideContextCard, 1500);
                    Log.i(LOG_TAG, "Touch up");
                    view.setVisibility(View.VISIBLE);
                    showDragOutAffordance(false);
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
            Log.i(LOG_TAG, String.format("set size %d x %d, orig %d x %d, scale %f", w, h, initBoxWidth, initBoxHeight, scale));
            box.setLayoutParams(lp);
            return true;
        }
    }

    class OutsideStageDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    Log.i(LOG_TAG, "Drag drop");
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(resetBox, 1500);
                    showDragOutAffordance(false);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    class StageDragListener implements View.OnDragListener {
        private int startX=0, startY=0;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //Log.i(LOG_TAG, "Drag started");
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    //Log.i(LOG_TAG, "start " + event.getX() + "," + event.getY());
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.i(LOG_TAG, "exited " + event.getX() + "," + event.getY());
//                    v.setBackgroundDrawable(normalShape);
                    break;

                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    int stupidOffsetSinceDropAndStartUsesDifferentReference = 100;
                    int dx = (int) (event.getX() - startX + stupidOffsetSinceDropAndStartUsesDifferentReference);
                    int dy = (int) (event.getY() - startY + stupidOffsetSinceDropAndStartUsesDifferentReference);
                    int movement = (int) Math.sqrt(dx * dx + dy * dy);
                    Log.i(LOG_TAG, "Drag length=" + movement);
                    if (movement < 30) {
                        showContextCard(true);
                        Handler handler = new Handler();
                        handler.postDelayed(hideContextCard, 1500);
                    }
                    else {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) (view.getLayoutParams());
                        lp.leftMargin = (int) event.getX() - view.getWidth()/2;
                        lp.topMargin = (int) event.getY() - view.getHeight()/2;
                        view.setLayoutParams(lp);
                    }
                    view.setVisibility(View.VISIBLE);
                    showDragOutAffordance(false);
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);
                    Log.i(LOG_TAG, "end " + event.getX() + "," + event.getY());
                default:
                    break;
            }
            return true;
        }
    }

}
