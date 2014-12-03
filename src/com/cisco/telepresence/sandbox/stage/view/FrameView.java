package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.graphics.Rect;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.stage.model.Frame;

public class FrameView extends AbsoluteLayout {

    private final Frame frame;

    public FrameView(Context context, Frame frame, float scaleX, float scaleY) {
        super(context);
        this.frame = frame;

        int width = (int) (frame.getWidth() * scaleX);
        int height = (int) (frame.getHeight() * scaleY);
        int x = (int) (frame.getX() * scaleX);
        int y = (int) (frame.getY() * scaleY);
        LayoutParams layout = new LayoutParams(width, height, x, y);
        setLayoutParams(layout);

        // TODO
        if (frame.getFrameType() == Frame.FrameType.SELFVIEW)
            setBackgroundColor(0x66000099);
        else
            setBackgroundColor(0x66009900);
    }

    // Cloned view that can be used to move and resize during gestures
    // Original view can remain but be invisible, to avoid screwing up the coordinate system during change
    public FrameView createClone(float scaleX, float scaleY) {
        FrameView view = new FrameView(getContext(), frame, scaleX, scaleY);
        view.setLayoutParams(this.getLayoutParams());
        return view;
    }

    public void move(int dx, int dy) {
        LayoutParams current = (LayoutParams) getLayoutParams();
        setPos(current.x + dx, current.y + dy);
    }

    public void setPos(int x, int y) {
        LayoutParams current = (LayoutParams) getLayoutParams();
        LayoutParams layout = new LayoutParams(current.width, current.height, x, y);
        setLayoutParams(layout);
    }

    public void setSize(int width, int height) {
        LayoutParams current = (LayoutParams) getLayoutParams();
        LayoutParams layout = new LayoutParams(width, height, current.x, current.y);
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

    public Rect getBounds() {
        int x = ((LayoutParams) getLayoutParams()).x;
        int y = ((LayoutParams) getLayoutParams()).y;
        return new Rect(x, y, x + getLayoutParams().width, y + getLayoutParams().height);
    }
}
