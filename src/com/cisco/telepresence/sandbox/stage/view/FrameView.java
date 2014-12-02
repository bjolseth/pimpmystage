package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.stage.model.Frame;

public class FrameView extends AbsoluteLayout {

    public FrameView(Context context, Frame frame, float scaleX, float scaleY) {
        super(context);
        int width = (int) (frame.getWidth() * scaleX);
        int height = (int) (frame.getHeight() * scaleY);
        int x = (int) (frame.getX() * scaleX);
        int y = (int) (frame.getY() * scaleY);
        LayoutParams layout = new LayoutParams(width, height, x, y);
        setLayoutParams(layout);
        setBackgroundColor(0x66009900); // TODO
    }

    public void move(int dx, int dy) {
        LayoutParams current = (LayoutParams) getLayoutParams();
        LayoutParams layout = new LayoutParams(current.width, current.height, current.x + dx, current.y + dy);
        setLayoutParams(layout);
    }

    public void scaleCentered(float scale) {
        LayoutParams current = (LayoutParams) getLayoutParams();

        float aspect = 16.f/9.f;

        float newWidth = current.width * scale;
        float newHeight = newWidth / aspect;

        int xCenter = (int) (current.x + current.width/2.);
        int yCenter = (int) (current.y + current.height/2.);

        current.width = (int) newWidth;
        current.height = (int) newHeight;

        current.x = xCenter - (int) (current.width / 2.);
        current.y = yCenter - (int) (current.height / 2.);

        setLayoutParams(current);
    }
}
