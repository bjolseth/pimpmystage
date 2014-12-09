package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.util.ImageBlur;

public class FrameView extends RelativeLayout {

    private final Frame frame;

    public FrameView(Context context, Frame frame, float scaleX, float scaleY) {
        super(context);
        this.frame = frame;

        int width = (int) (frame.getWidth() * scaleX);
        int height = (int) (frame.getHeight() * scaleY);
        int x = (int) (frame.getX() * scaleX);
        int y = (int) (frame.getY() * scaleY);
        AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(width, height, x, y);
        setLayoutParams(layout);
        View.inflate(context, R.layout.avatar, this);
        styleAvatar(frame.getFrameType(), frame.getName());
    }

    private void styleAvatar(Frame.FrameType type, String name) {
        ImageView img = (ImageView) findViewById(R.id.avatar_icon);

        int image = R.drawable.avatar_camera;
        if (type == Frame.FrameType.SELFVIEW)
            image = R.drawable.avatar_camera;
        else if (type == Frame.FrameType.VIDEO)
            image = R.drawable.avatar_single;
        else if (type == Frame.FrameType.LOCAL_PRESENTATATION)
            image = R.drawable.avatar_pc;

        if (name.equalsIgnoreCase("meeting1"))
            image = R.drawable.meeting1;
        else if (name.equalsIgnoreCase("meeting2"))
            image = R.drawable.meeting2;
        else if (name.equalsIgnoreCase("meeting3"))
            image = R.drawable.meeting3;
        else if (name.equalsIgnoreCase("presentation1"))
            image = R.drawable.presentation1;
        else if (name.equalsIgnoreCase("team"))
            image = R.drawable.team_photo;

        img.setImageResource(image);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
//        Bitmap blur = ImageBlur.blurBitmap(getContext(), bitmap);
//        img.setImageBitmap(blur);

        TextView text = (TextView) findViewById(R.id.avatar_text);
        text.setText(name);
    }

    // Cloned view that can be used to move and resize during gestures
    // Original view can remain but be invisible, to avoid screwing up the coordinate system during change
    public FrameView createClone(float scaleX, float scaleY) {
        FrameView view = new FrameView(getContext(), frame, scaleX, scaleY);
        view.setLayoutParams(this.getLayoutParams());
        return view;
    }

    public void move(int dx, int dy) {
        AbsoluteLayout.LayoutParams current = (AbsoluteLayout.LayoutParams) getLayoutParams();
        setPos(current.x + dx, current.y + dy);
    }

    public void setPos(int x, int y) {
        AbsoluteLayout.LayoutParams current = (AbsoluteLayout.LayoutParams) getLayoutParams();
        AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(current.width, current.height, x, y);
        setLayoutParams(layout);
    }

    public void setSize(int width, int height) {
        AbsoluteLayout.LayoutParams current = (AbsoluteLayout.LayoutParams) getLayoutParams();
        AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(width, height, current.x, current.y);
        setLayoutParams(layout);
    }

    public void setBounds(Rect bounds) {
        AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(bounds.width(), bounds.height(), bounds.left, bounds.top);
        setLayoutParams(layout);
    }

    public void scaleCentered(float scale) {
        AbsoluteLayout.LayoutParams current = (AbsoluteLayout.LayoutParams) getLayoutParams();

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
        int x = ((AbsoluteLayout.LayoutParams) getLayoutParams()).x;
        int y = ((AbsoluteLayout.LayoutParams) getLayoutParams()).y;
        return new Rect(x, y, x + getLayoutParams().width, y + getLayoutParams().height);
    }

    public int getFrameId() {
        return frame.getFrameId();
    }

    public int getLayer() {
        return frame.getLayer();
    }

    public String toString() {
        return String.format("Frame %s", frame.getName());
    }
}
