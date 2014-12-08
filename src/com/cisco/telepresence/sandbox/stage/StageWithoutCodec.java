package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.ArrayList;
import java.util.List;

public class StageWithoutCodec extends Activity
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
    }

    private void setupLayoutSlider() {
        SeekBar bar = ((SeekBar) findViewById(R.id.layoutslider));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                layoutDirector.setBigPipPercent((50 + value) / 100.f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        bar.setVisibility(View.VISIBLE);
    }

    private void createPredefineLayoutFamilySetup(ScreenView screenView) {
        layoutDirector = new PredefineLayoutDirector(screenView);

        Screen screen = new Screen();
        List<Frame> frames = new ArrayList<Frame>();

        int bigSize = 7000;
        int max = 10000;
        int smallSize = max - bigSize;
        Frame f1 = new Frame(1, Frame.FrameType.VIDEO, bigSize, bigSize, (max-bigSize)/2, 0, "Mr Prominent", 1);
        frames.add(f1);

        int numberOfPips = 3;
        int spacing = 50;
        int y = bigSize;
        int x = (max - (numberOfPips*smallSize))/2 - spacing*(numberOfPips-1);
        for (int i=0; i<numberOfPips; i++) {
            Frame pip = new Frame(i+1, Frame.FrameType.VIDEO, smallSize - spacing*2, smallSize - spacing*2, x, y + spacing, "Pip " + (i+1), i+1);
            x += smallSize + spacing;
            frames.add(pip);
        }

        screen.setFrames(frames);
        screenView.setScreen(screen);

        new ScreenPresenter(screenView, layoutDirector);
        setupLayoutSlider();
    }

    private void createManualDummySetup(ScreenView screenView) {
        LayoutDirector director = new ManualLayoutDirector(screenView);

        Screen screen = new Screen();
        List<Frame> frames = new ArrayList<Frame>();

        Frame f1 = new Frame(1, Frame.FrameType.VIDEO, 6000, 6000, 1000, 0, "Mr Jones", 1);
        frames.add(f1);

        Frame f3 = new Frame(2, Frame.FrameType.LOCAL_PRESENTATATION, 3800, 3800, 2000, 6100, "PowerPoint", 2);
        frames.add(f3);

        Frame f2 = new Frame(3, Frame.FrameType.SELFVIEW, 2000, 2000, 8000, 7000, "You", 3);
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
