package com.cisco.telepresence.sandbox.stage;

import android.os.RemoteException;
import android.util.Log;
import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.system.ServiceProvider;
import com.cisco.telepresence.system.SystemAIDLService;
import com.cisco.telepresence.system.types.data.CallStatus;

import java.util.ArrayList;
import java.util.List;

public class RealCodec implements CodecInterface {

    private SystemAIDLService service;

    public RealCodec() {
        service = getService();
    }

    @Override
    public List<Call> getCalls() {
        List<Call> calls = new ArrayList<Call>();
        try {
            for (CallStatus call : service.getCalls()) {
                calls.add(new Call(call.getCallId(), call.getDisplayName()));
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return calls;
    }

    @Override
    public List<Frame> getFrames() {
        List<Frame> frames = new ArrayList<Frame>();
        int i = 0;
        for (Call call : getCalls()) {
            int layer = i + 1;
            Frame frame = new Frame(call.getCallId(), Frame.FrameType.VIDEO, 4000, 4000, i*2000, i*2000, call.getName(), layer);
            frames.add(frame);
            i++;
        }
        return frames;
    }

    private static SystemAIDLService getService() {
        return ServiceProvider.getSystemService();
    }

    @Override
    public void createCustomLayout(int layoutId) {
        try {
            service.createCustomLayout(layoutId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createCustomVideoFrame(int layoutId, int callId, int frameId, int posX, int posY, int width, int height, int layer) {
        try {
            service.createCustomVideoFrame(layoutId, callId, frameId, posX, posY, width, height, layer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCustomVideoFrame(int layoutId, int frameId, int posX, int posY, int width, int height, int layer) {
        try {
            service.updateCustomVideoFrame(layoutId, frameId, posX, posY, width, height, layer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void assignCustomLayoutToOutput(int layoutId, int outputId) {
        try {
            service.assignCustomLayoutToOutput(layoutId, outputId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCustomFrame(int frameId, int layoutId) {
        try {
            service.removeCustomFrame(frameId, layoutId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetCustomLayout() {
        try {
            service.resetCustomLayout();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
