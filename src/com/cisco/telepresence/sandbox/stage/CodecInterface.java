package com.cisco.telepresence.sandbox.stage;

import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.sandbox.stage.model.Frame;

import java.util.List;


public interface CodecInterface {
    List<Call> getCalls();

    List<Frame> getFrames();

    void createCustomLayout(int layoutId);

    void createCustomVideoFrame(int layoutId, int callId, int frameId, int posX, int posY, int width, int height, int layer);

    void updateCustomVideoFrame(int layoutId, int frameId, int posX, int posY, int width, int height, int layer);

    void assignCustomLayoutToOutput(int layoutId, int outputId);

    void removeCustomFrame(int frameId, int layoutId);

    void resetCustomLayout();
}
