package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.R;

public class StageActivityOld extends Activity
{
    private static final String TAG = "Sandbox";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummystage);
        addListeners();
    }

    private void addListeners() {
        findViewById(R.id.stage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                Log.i(TAG, String.format("Click on dummystage location %f, %f ", e.getX(), e.getY()));
                return false;
            }
        });

        findViewById(R.id.frame1).setOnTouchListener(new BoxTouchListener());
        findViewById(R.id.frame2).setOnTouchListener(new BoxTouchListener());
    }

    private void repositionView(View view, int x, int y) {
        AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(view.getWidth(), view.getHeight(), x, y);
        view.setLayoutParams(p);
        Log.i(TAG, String.format("Moving view to %d, %d", x, y));
    }

    private class BoxTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent e) {
            int[] screenPos = {0, 0};

            view.getLocationInWindow(screenPos);

            int x = (int) e.getX(), y = (int) e.getY();

            //View dummyView = findViewById(R.id.marker);
            repositionView(view, screenPos[0] + x, screenPos[1] + y);

//            Log.i(TAG, String.format("ontouch local (%d, %d) ", x, y));
//            Log.i(TAG, String.format("ontouch screen (%d, %d) ", screenPos[0] + x, screenPos[1] + y));
            return false;
        }

    };

}
