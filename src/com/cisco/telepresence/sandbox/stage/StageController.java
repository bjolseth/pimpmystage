package com.cisco.telepresence.sandbox.stage;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutChangeHandler;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;
import android.view.ViewGroup.LayoutParams;

import java.util.List;

public class StageController implements View.OnDragListener, View.OnTouchListener {

    private View stage;
    private Context context;
    private static final String TAG = "pimpmystage";
    private PredefineLayoutDirector director;
    private ScreenPresenter screenPresenter;
    private CodecInterface codec;
    private ScreenView screenView;

    public StageController(Context context, View stage) {
        this.context = context;
        this.stage = stage;
        screenView = (ScreenView) stage.findViewById(R.id.singlescreen);
        createFakeCodecPredefinedLayoutMode(screenView);

        stage.findViewById(R.id.garbageCan).setOnDragListener(this);
        populateTray();
    }

    private void populateTray() {
        int n = 15;
        ViewGroup tray = (ViewGroup) stage.findViewById(R.id.tray);
        for (int i=0; i<n; i++) {
            View.inflate(context, R.layout.tray_item, tray);
            ViewGroup button = (ViewGroup) tray.getChildAt(i);
            TextView t = (TextView) button.findViewById(R.id.tray_item_text);
            t.setText("Button " + (i + 1));

            button.setOnTouchListener(this);
        }
    }

    private void createFakeCodecPredefinedLayoutMode(ScreenView screenView) {
        codec = new SimulatedCodec();

        List<Frame> frames = codec.getFrames();
        Screen screen = new Screen();
        screen.setFrames(frames);
        screenView.setScreen(screen);

        director = new PredefineLayoutDirector(screenView);

        screenPresenter = new ScreenPresenter(screenView, director);

        LayoutChangeHandler layoutHandler = new LayoutChangeHandler(codec, screenView);
        screenPresenter.setLayoutChangeHandler(layoutHandler);

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
        SeekBar bar = ((SeekBar) stage.findViewById(R.id.layoutslider));
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

    @Override
    public boolean onDrag(View dropTarget, DragEvent dragEvent) {

        Object item = dragEvent.getLocalState();

        if (dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED)
            stage.findViewById(R.id.garbageCan).setAlpha(1.0f);
        else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED)
            stage.findViewById(R.id.garbageCan).setAlpha(0);
        else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
            if (dropTarget.getId() == R.id.garbageCan)
                ((TextView) dropTarget).setTextColor(Color.RED);
        }
        else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED) {
            if (dropTarget.getId() == R.id.garbageCan)
                ((TextView) dropTarget).setTextColor(Color.BLACK);
        }
        else if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
            if (dropTarget.getId() == R.id.garbageCan) {
                Log.i("jalla", "remove object " + dragEvent.getLocalState());
                ((TextView) dropTarget).setTextColor(Color.BLACK);
                if (item instanceof FrameView)
                    removeFrame((FrameView) item);
            }
        }

        return true;
    }



    private void removeFrame(FrameView frame) {
        screenView.remove(frame);
        director.updatePositions();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
            view.startDrag(null, shadow, view, 0);
        }
        return false;
    }
}
