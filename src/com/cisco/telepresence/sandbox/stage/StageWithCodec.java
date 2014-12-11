package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.codec.CodecInterface;
import com.cisco.telepresence.sandbox.stage.codec.RealCodec;
import com.cisco.telepresence.sandbox.stage.layout.CodecCustomLayoutHelper;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

import java.util.List;

public class StageWithCodec extends Activity
{
    private static final String TAG = "pimpmystage";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage);

        ScreenView screenView = (ScreenView) findViewById(R.id.singlescreen);
        createCodecFreeMode(screenView);
    }

    private void createCodecFreeMode(ScreenView screenView) {
        CodecInterface codec = new RealCodec();

        LayoutDirector director = new ManualLayoutDirector(screenView);
        CodecCustomLayoutHelper layoutHandler = new CodecCustomLayoutHelper(codec, screenView);
        List<Frame> frames = codec.getFrames();
        Screen screen = new Screen();
        screen.setFrames(frames);
        screenView.setScreen(screen);

        ScreenPresenter p = new ScreenPresenter(screenView, director);
        p.setLayoutChangeHandler(layoutHandler);
    }

}
