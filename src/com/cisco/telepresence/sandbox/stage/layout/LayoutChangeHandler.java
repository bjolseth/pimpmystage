package com.cisco.telepresence.sandbox.stage.layout;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import com.cisco.telepresence.sandbox.stage.CodecInterface;
import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.ArrayList;
import java.util.List;

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
    }

    public List<Frame> initLayoutWithFramesFromCodec() {

        codec.resetCustomLayout();
        codec.createCustomLayout(LayoutId);

        List<Frame> frames = new ArrayList<Frame>();
        int i = 0;
        for (Call call : codec.getCalls()) {
            int layer = i + 1;
            Frame frame = new Frame(i+1, Frame.FrameType.VIDEO, 4000, 4000, i*2000, i*2000, call.getName(), layer);
            frames.add(frame);
            // TODO move this to the layout director or somewhere more appropriate
            codec.createCustomVideoFrame(LayoutId, call.getCallId(), frame.getFrameId(), frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), frame.getLayer());
            i++;
        }

        codec.assignCustomLayoutToOutput(LayoutId, MonitorOutput);
        return frames;
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

        // if we receive many frame change commands rapidly, we just ignore anyone except the last
        postponeCommands.removeCallbacksAndMessages(null);
        postponeCommands.postDelayed(new Runnable() {
            @Override
            public void run() {
                codec.updateCustomVideoFrame(layoutId, frame.getFrameId(), x, y, w, h, frame.getLayer());
            }
        }, BufferTimeMs);

    }
}
