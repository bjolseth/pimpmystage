package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutChangeHandler;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.List;

public class StageWithoutCodec extends Activity
{

    private static final String TAG = "pimpmystage";
    private PredefineLayoutDirector director;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage);

        ScreenView screenView = (ScreenView) findViewById(R.id.singlescreen);
        createFakeCodecPredefinedLayoutMode(screenView);
    }


    private void createFakeCodecPredefinedLayoutMode(ScreenView screenView) {
        CodecInterface codec = new SimulatedCodec();

        List<Frame> frames = codec.getFrames();
        Screen screen = new Screen();
        screen.setFrames(frames);
        screenView.setScreen(screen);

        director = new PredefineLayoutDirector(screenView);

        ScreenPresenter p = new ScreenPresenter(screenView, director);

        LayoutChangeHandler layoutHandler = new LayoutChangeHandler(codec, screenView);
        p.setLayoutChangeHandler(layoutHandler);

        // Need to update this screenview after size has been determined
        // There's probably a better way to do this without timer
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                director.updatePositions();
            }
       }, 500);
    }

    private void setupLayoutSlider() {
        SeekBar bar = ((SeekBar) findViewById(R.id.layoutslider));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                director.setBigPipPercent((50 + value) / 100.f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        bar.setVisibility(View.VISIBLE);
    }


}
