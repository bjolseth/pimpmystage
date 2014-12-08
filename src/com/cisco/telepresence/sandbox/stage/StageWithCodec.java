package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutChangeHandler;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.ArrayList;
import java.util.List;

public class StageWithCodec extends Activity
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
        createCodecFreeMode(screenView);
    }


    private void createCodecFreeMode(ScreenView screenView) {
        CodecInterface codec = new CodecInterface();

        LayoutDirector director = new ManualLayoutDirector(screenView);
        LayoutChangeHandler l = new LayoutChangeHandler(codec, screenView);
        List<Frame> frames = l.initLayoutWithFramesFromCodec();
        Screen screen = new Screen();
        screen.setFrames(frames);
        screenView.setScreen(screen);

        ScreenPresenter p = new ScreenPresenter(screenView, director);
        p.setLayoutChangeHandler(new LayoutChangeHandler(codec, screenView));

    }

    public static void debug(String msg) {
        Log.i(TAG, msg);
        debugView.setText(msg);
    }
}
