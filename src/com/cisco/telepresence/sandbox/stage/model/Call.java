package com.cisco.telepresence.sandbox.stage.model;

public class Call {

    private int callId;
    private String name;

    public Call(int callId, String name) {
        this.callId = callId;
        this.name = name;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("Call %d: %s", callId, name);
    }
}
