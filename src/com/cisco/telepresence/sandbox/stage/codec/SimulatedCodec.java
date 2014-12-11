package com.cisco.telepresence.sandbox.stage.codec;

import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.sandbox.stage.model.Frame;

import java.util.ArrayList;
import java.util.List;

public class SimulatedCodec implements CodecInterface {

    @Override
    public List<Call> getCalls() {
        List<Call> calls = new ArrayList<Call>();

        String[] names = {"HDMI", "DVI", "Licorice", "Kvikklunsj", "Fandango"};

        for (int i=0; i < names.length; i++) {
            Call call = new Call(i+1, names[i]);
            calls.add(call);
        }
        return calls;
    }

    @Override
    public List<Frame> getFrames() {
        List<Frame> frames = new ArrayList<Frame>();
        int i=0;
        for (Call call : getCalls()) {
            Frame.FrameType type = (i < 2 ? Frame.FrameType.LOCAL_PRESENTATATION : Frame.FrameType.VIDEO);
            Frame frame = new Frame(call.getCallId(), type,
                    2000, 2000, i*1000, i*1000, call.getName(), i+1);
            frames.add(frame);
            i++;
        }
        return frames;
    }

    @Override
    public void createCustomLayout(int layoutId) {
    }

    @Override
    public void createCustomVideoFrame(int layoutId, int callId, int frameId, int posX, int posY, int width, int height, int layer) {
    }

    @Override
    public void updateCustomVideoFrame(int layoutId, int frameId, int posX, int posY, int width, int height, int layer) {
    }

    @Override
    public void assignCustomLayoutToOutput(int layoutId, int outputId) {
    }

    @Override
    public void removeCustomFrame(int frameId, int layoutId) {
    }

    @Override
    public void resetCustomLayout() {
    }
}
