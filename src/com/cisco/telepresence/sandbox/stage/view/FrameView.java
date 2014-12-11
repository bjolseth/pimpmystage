package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.util.Avatars;

public class FrameView extends RelativeLayout {

    private final Frame frame;
    private int imageResourceId;

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

        imageResourceId = Avatars.getAvatarImageId(name, type);

        img.setImageResource(imageResourceId);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResourceId);
//        Bitmap blur = ImageBlur.blurBitmap(getContext(), bitmap);
//        img.setImageBitmap(blur);

        TextView text = (TextView) findViewById(R.id.avatar_text);
        text.setText(name);
    }

    public int getAvatarImageResource() {
        return imageResourceId;
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
