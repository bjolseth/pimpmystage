package com.cisco.telepresence.sandbox.stage.layout;

import com.cisco.telepresence.sandbox.stage.view.FrameView;

public interface LayoutDirector
{
    public void scaleCentered(FrameView ghostView, float scale);
    public void moveView(FrameView ghostView, int dx, int dy);
}
