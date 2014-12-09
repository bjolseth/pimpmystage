package com.cisco.telepresence.sandbox.stage.layout;

import android.view.View;
import com.cisco.telepresence.sandbox.stage.view.FrameView;

public interface LayoutDirector
{
    public void scaleView(View view, float scale);
    public void moveView(View view, int x, int y);
    public void swapPositionAndSize(FrameView view1, FrameView view2);
    public void updatePositions();

    }
