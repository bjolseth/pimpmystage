package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutChangeHandler;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.util.Debug;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;
import com.cisco.telepresence.sandbox.stage.view.TrayButton;

import java.util.List;

public class StageController implements View.OnDragListener, View.OnTouchListener, View.OnSystemUiVisibilityChangeListener {

    public static final int LEAN_BACK_TIMEOUT = 10000;
    private View stage;
    private Context context;
    private PredefineLayoutDirector director;
    private ScreenPresenter screenPresenter;
    private CodecInterface codec;
    private ScreenView screenView;
    private int lastSystemUIVisibility;
    private Handler enterLeanBackTimer;

    public StageController(Context context, View stage) {
        this.context = context;
        this.stage = stage;
        screenView = (ScreenView) stage.findViewById(R.id.singlescreen);
        enterLeanBackTimer = new Handler();
        createFakeCodecPredefinedLayoutMode(screenView);

        stage.findViewById(R.id.garbageCan).setOnDragListener(this);
        populateTray();
        setListeners();
        resetLeanBackTimer();
    }

    private void setListeners() {
        stage.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTray(true);
            }
        });

        stage.findViewById(R.id.glass_pane).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                resetLeanBackTimer();
                if (stage.findViewById(R.id.tray_frame).getVisibility() == View.VISIBLE)
                    showTray(false);
                return false;
            }
        });

        ((Activity) context).getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
    }

    private void populateTray() {
        ViewGroup tray = (ViewGroup) stage.findViewById(R.id.tray);
        for(Frame frame : codec.getFrames()) {
            TrayButton button = new TrayButton(context, frame.getFrameType(), frame.getName());
            tray.addView(button);
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
                ((TextView) dropTarget).setTextColor(Color.BLACK);
                if (item instanceof FrameView)
                    removeFrame((FrameView) item);
            }
        }

        return true;
    }

    private void showTray(final boolean show) {
        final View tray = stage.findViewById(R.id.tray_frame);

        // already in the right state
        if ((show && tray.getVisibility() == View.VISIBLE) ||
                ! show && tray.getVisibility() != View.VISIBLE)
            return;

        int animation = show ? R.anim.slide_up : R.anim.slide_down;
        Animation slide = AnimationUtils.loadAnimation(context, animation);
        if (show)
            tray.setVisibility(View.VISIBLE);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tray.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        tray.startAnimation(slide);

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

    protected void enableLeanBackMode(boolean enabled) {
        int newVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if(enabled) {
            newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN
                    |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Set the visibility
        ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(newVisibility);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        boolean outOfLeanback =  ((lastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0
                && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0);

        if (outOfLeanback)
            resetLeanBackTimer();

        lastSystemUIVisibility = visibility;
        setLeanBackVisibility(!outOfLeanback);
    }

    private void setLeanBackVisibility(boolean goingToLeanback) {
        stage.findViewById(R.id.callcontrolbar).setVisibility(goingToLeanback ? View.INVISIBLE: View.VISIBLE);
        showTray(false);
    }

    private void resetLeanBackTimer() {
        // First cancel any queued events - i.e. resetting the countdown clock
        Runnable enterLeanback = new Runnable() {
            @Override
            public void run() {
                enableLeanBackMode(true);
            }
        };
        enterLeanBackTimer.removeCallbacksAndMessages(null);
        // And fire the event in 3s time
        enterLeanBackTimer.postDelayed(enterLeanback, LEAN_BACK_TIMEOUT);
    }
}
