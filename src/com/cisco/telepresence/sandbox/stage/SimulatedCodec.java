package com.cisco.telepresence.sandbox.stage;

import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.sandbox.stage.model.Frame;

import java.util.ArrayList;
import java.util.List;

public class SimulatedCodec implements CodecInterface {

    @Override
    public List<Call> getCalls() {
        int initialParticipants = 4;
        List<Call> calls = new ArrayList<Call>();

        for (int i=0; i<initialParticipants; i++) {
            Call call = new Call(i+1, "Participant " + (i+1));
            calls.add(call);
        }
        return calls;
    }

    @Override
    public List<Frame> getFrames() {
        List<Frame> frames = new ArrayList<Frame>();
        int i=0;
        for (Call call : getCalls()) {
            Frame frame = new Frame(call.getCallId(), Frame.FrameType.VIDEO,
                    2000, 2000, i*1000, i*1000, call.getName(), i+1);
            frames.add(frame);
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
