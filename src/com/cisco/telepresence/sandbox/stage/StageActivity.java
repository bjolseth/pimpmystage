package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.ArrayList;
import java.util.List;

public class StageActivity extends Activity
{
    private static final String TAG = "Sandbox";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage);
        createDummySetup();
    }

    private void createDummySetup() {
        Screen screen = new Screen();
        Frame f1 = new Frame(Frame.FrameType.VIDEO, 4000, 4000, 0, 0, "Frame 1");
        Frame f2 = new Frame(Frame.FrameType.VIDEO, 4000, 4000, 4000, 4000, "Frame 2");
        List<Frame> frames = new ArrayList<Frame>();
        frames.add(f1);
        frames.add(f2);
        screen.setFrames(frames);

        ScreenView screenView = (ScreenView) findViewById(R.id.singlescreen);
        screenView.setScreen(screen);

        screenView.invalidate();

        ScaleFrameTouchListener stl = new ScaleFrameTouchListener(this);
        setTouchListenerOnFrames(screenView, stl);
    }


    private void setTouchListenerOnFrames(ScreenView screenView, View.OnTouchListener touchListener) {
        for (int i=0; i<screenView.getChildCount(); i++) {
            View view = screenView.getChildAt(i);
            if (view instanceof FrameView) {
                view.setOnTouchListener(touchListener);
            }
        }
    }
}
