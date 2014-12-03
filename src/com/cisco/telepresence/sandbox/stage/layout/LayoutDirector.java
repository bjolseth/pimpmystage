package com.cisco.telepresence.sandbox.stage.layout;

import android.view.View;

public interface LayoutDirector
{
    public void scaleView(View view, float scale);
    public void moveView(View view, int dx, int dy);
}
