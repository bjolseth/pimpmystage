package com.cisco.telepresence.sandbox.stage;

import android.os.RemoteException;
import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.system.ServiceProvider;
import com.cisco.telepresence.system.SystemAIDLService;
import com.cisco.telepresence.system.types.data.CallStatus;

import java.util.ArrayList;
import java.util.List;

public class CodecInterface {

    private SystemAIDLService service;

    public CodecInterface() {
        service = getService();
    }

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

    private static SystemAIDLService getService() {
        return ServiceProvider.getSystemService();
    }

    public void createCustomLayout(int layoutId) {
        try {
            service.createCustomLayout(layoutId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void createCustomVideoFrame(int layoutId, int callId, int frameId, int posX, int posY, int width, int height, int layer) {
        try {
            service.createCustomVideoFrame(layoutId, callId, frameId, posX, posY, width, height, layer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomVideoFrame(int layoutId, int frameId, int posX, int posY, int width, int height, int layer) {
        try {
            service.updateCustomVideoFrame(layoutId, frameId, posX, posY, width, height, layer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void assignCustomLayoutToOutput(int layoutId, int outputId) {
        try {
            service.assignCustomLayoutToOutput(layoutId, outputId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void removeCustomFrame(int frameId, int layoutId) {
        try {
            service.removeCustomFrame(frameId, layoutId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
