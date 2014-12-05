package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.util.FrameDragBuilder;

public class TestView extends FrameView {

    public TestView(Context context, Frame frame, float scaleX, float scaleY) {
        super(context, frame, scaleX, scaleY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        int tmpX = (int) motionEvent.getX();
        int tmpY = (int) motionEvent.getY();

        Log.i("jalla", "touch event " + motionEvent.getAction() + ", " + tmpX + "," + tmpY);

        if (motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
            tmpX = (int) motionEvent.getX();
            tmpY = (int) motionEvent.getY();

        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
        }
        return true;

    }
}
