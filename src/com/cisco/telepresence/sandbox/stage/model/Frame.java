package com.cisco.telepresence.sandbox.stage.model;

public class Frame {

    public enum FrameType {
        VIDEO,
        LOCAL_PRESENTATATION,
        FAREND_PRESENTATION,
        SELFVIEW
    }

    private FrameType frameType;
    private int width;
    private int height;
    private int x;
    private int y;
    private int layer;
    private String name;
    private int frameId;

    public Frame(int frameId, FrameType type, int width, int height, int x, int y, String name, int layer) {
        this.frameId = frameId;
        this.frameType = type;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.name = name;
        this.layer = layer;
    }

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public FrameType getFrameType() {
        return frameType;
    }

    public void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String toString() {
        return String.format("Frame: %s, (%d x %d) @ (%d, %d)", name, width, height, x, y);
    }
}
