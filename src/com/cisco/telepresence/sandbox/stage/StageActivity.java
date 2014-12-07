package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.CodecInterface;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Call;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;
import com.cisco.telepresence.system.ServiceProvider;
import com.cisco.telepresence.system.SystemAIDLService;

import java.util.ArrayList;
import java.util.List;

public class StageActivity extends Activity
{
    private static final String TAG = "pimpmystage";
    private static TextView debugView;
    private PredefineLayoutDirector layoutDirector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage);
        debugView = (TextView) findViewById(R.id.debugLabel);

        ScreenView screenView = (ScreenView) findViewById(R.id.singlescreen);
        createPredefineLayoutFamilySetup(screenView);
        setupLayoutSlider();

    }

    private void buildFramesBasedOnCalls() {
        CodecInterface codec = new CodecInterface();
        int layoutId = 1;
        int monitorOutput = 1;
        codec.createCustomLayout(layoutId);
        sleep(1000);

        int i = 0;
        for (Call call : codec.getCalls()) {
            Log.i(TAG, call.toString());
            codec.createCustomVideoFrame(layoutId, call.getCallId(), i+1, i*5000, i*5000, 5000, 5000, i+1);
            sleep(1000);
            i++;
        }

        codec.assignCustomLayoutToOutput(layoutId, monitorOutput);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setupLayoutSlider() {
        ((SeekBar) findViewById(R.id.layoutslider)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                layoutDirector.setBigPipPercent((50 + value)/100.f);
                debug("Layout slider value: " + value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    private void createPredefineLayoutFamilySetup(ScreenView screenView) {
        layoutDirector = new PredefineLayoutDirector(screenView);

        Screen screen = new Screen();
        List<Frame> frames = new ArrayList<Frame>();

        int bigSize = 7000;
        int max = 10000;
        int smallSize = max - bigSize;
        Frame f1 = new Frame(Frame.FrameType.VIDEO, bigSize, bigSize, (max-bigSize)/2, 0, "Mr Prominent");
        frames.add(f1);

        int numberOfPips = 3;
        int spacing = 50;
        int y = bigSize;
        int x = (max - (numberOfPips*smallSize))/2 - spacing*(numberOfPips-1);
        for (int i=0; i<numberOfPips; i++) {
            Frame pip = new Frame(Frame.FrameType.VIDEO, smallSize - spacing*2, smallSize - spacing*2, x, y + spacing, "Pip " + (i+1));
            x += smallSize + spacing;
            frames.add(pip);
        }

        screen.setFrames(frames);
        screenView.setScreen(screen);

        new ScreenPresenter(screenView, layoutDirector);
    }


    private void createManualDummySetup(ScreenView screenView) {
        LayoutDirector director = new ManualLayoutDirector(screenView);

        Screen screen = new Screen();
        List<Frame> frames = new ArrayList<Frame>();

        Frame f1 = new Frame(Frame.FrameType.VIDEO, 6000, 6000, 1000, 0, "Mr Jones");
        frames.add(f1);

        Frame f3 = new Frame(Frame.FrameType.LOCAL_PRESENTATATION, 3800, 3800, 2000, 6100, "PowerPoint");
        frames.add(f3);

        Frame f2 = new Frame(Frame.FrameType.SELFVIEW, 2000, 2000, 8000, 7000, "You");
        frames.add(f2);

        screen.setFrames(frames);
        screenView.setScreen(screen);

        new ScreenPresenter(screenView, director);
    }


    public static void debug(String msg) {
        Log.i(TAG, msg);
        debugView.setText(msg);
    }
}
