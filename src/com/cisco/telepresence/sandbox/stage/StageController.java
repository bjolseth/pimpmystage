package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.layout.LayoutChangeHandler;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.view.FrameView;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;
import com.cisco.telepresence.sandbox.stage.view.TrayButton;

public class StageController implements View.OnDragListener, View.OnTouchListener, View.OnSystemUiVisibilityChangeListener {

    public static final int LEAN_BACK_TIMEOUT = 10000;
    private View stage;
    private Context context;
    private PredefineLayoutDirector director;
    private ScreenPresenter screenPresenter;
    private CodecInterface codec;
    private int lastSystemUIVisibility;
    private Handler enterLeanBackTimer;
    private StageNavigator stageNavigator;

    public StageController(Context context, View stage) {
        this.context = context;
        this.stage = stage;

        enterLeanBackTimer = new Handler();
        stageNavigator = new StageNavigator((ViewGroup) stage.findViewById(R.id.screens));
        codec = new SimulatedCodec();

        stage.findViewById(R.id.garbageCan).setOnDragListener(this);
        populateTray();
        setListeners();

        createMonitor(StageNavigator.MONITOR_LEFT);
        createMonitor(StageNavigator.MONITOR_MIDDLE);
        createMonitor(StageNavigator.MONITOR_RIGHT);

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

        stage.findViewById(R.id.zoom_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageNavigator.zoomOut();
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

    private void createMonitor(final int monitorIndex) {
        int monitorViewId = R.id.singlescreen;
        if (monitorIndex == 0)
            monitorViewId = R.id.leftscreen;
        else if (monitorIndex == 2)
            monitorViewId = R.id.rightscreen;

        ScreenView screenView = (ScreenView) stage.findViewById(monitorViewId);

        Screen screen = new Screen();
        screenView.setScreen(screen);

        director = new PredefineLayoutDirector(screenView);

        screenPresenter = new ScreenPresenter(screenView, director);
        screenPresenter.setMonitorSelectedListener(new ScreenPresenter.MonitorListener() {
            @Override
            public void monitorSelected() {
                stageNavigator.focusOnView(monitorIndex);
            }
        });

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
        ViewParent parent = frame.getParent();
        if (parent instanceof ScreenView) {
            ((ScreenView) parent).remove(frame);
        }
        //screenViewMiddle.remove(frame);
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
        int newVisibility =
                 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

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
