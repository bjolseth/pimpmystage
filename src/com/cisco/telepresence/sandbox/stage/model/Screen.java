package com.cisco.telepresence.sandbox.stage.model;

import java.util.ArrayList;
import java.util.List;

public class Screen {

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    private List<Frame> frames = new ArrayList<Frame>();
}
