package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import com.cisco.telepresence.sandbox.stage.codec.CodecInterface;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class LayoutChangeHandler {

    private CodecInterface codec;
    private ScreenView screenView;
    private Handler postponeCommands = new Handler();
    private static final int BufferTimeMs = 300;
    private static final int LayoutId = 1;
    private static final int MonitorOutput = 1;

    public LayoutChangeHandler(CodecInterface codec, ScreenView screenView) {
        this.codec = codec;
        this.screenView = screenView;
        initLayoutWithFramesFromCodec();
    }

    private void initLayoutWithFramesFromCodec() {

        codec.resetCustomLayout();
        codec.createCustomLayout(LayoutId);

        for (Frame frame : codec.getFrames()) {
            codec.createCustomVideoFrame(LayoutId, frame.getFrameId(), frame.getFrameId(), frame.getX(), frame.getY(),
                    frame.getWidth(), frame.getHeight(), frame.getLayer());
        }

        codec.assignCustomLayoutToOutput(LayoutId, MonitorOutput);
    }

    public void frameHasChanged(final FrameView frame) {
        float sx = screenView.getScaleWidth();
        float sy = screenView.getScaleHeight();

        Rect bounds = frame.getBounds();
        final int x = (int) (bounds.left / sx);
        final int y = (int) (bounds.top / sy);
        final int w = (int) (bounds.width() / sx);
        final int h = (int) (bounds.height() / sy);

        Log.i("customlayout", String.format("Update frame %s to %d, %d, %d x %d", frame, x, y, w, h));
        final int layoutId = 1;

        // if we receive many frame change commands rapidly, we just ignore them except the last
        postponeCommands.removeCallbacksAndMessages(null);
        postponeCommands.postDelayed(new Runnable() {
            @Override
            public void run() {
                codec.updateCustomVideoFrame(layoutId, frame.getFrameId(), x, y, w, h, frame.getLayer());
            }
        }, BufferTimeMs);

    }
}
