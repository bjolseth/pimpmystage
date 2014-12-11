package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.codec.CodecInterface;
import com.cisco.telepresence.sandbox.stage.codec.SimulatedCodec;
import com.cisco.telepresence.sandbox.stage.layout.CodecCustomLayoutHelper;
import com.cisco.telepresence.sandbox.stage.layout.LayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.ManualLayoutDirector;
import com.cisco.telepresence.sandbox.stage.layout.PredefineLayoutDirector;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.topmenu.TopMenuHandler;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;
import com.cisco.telepresence.sandbox.stage.view.TrayButton;

public class StageController implements  View.OnTouchListener, View.OnSystemUiVisibilityChangeListener {

    public static final int LEAN_BACK_TIMEOUT = 20000;
    private View stage;
    private Context context;
    private ScreenPresenter screenPresenter;
    private CodecInterface codec;
    private int lastSystemUIVisibility;
    private Handler enterLeanBackTimer;
    private StageNavigator stageNavigator;
    private TopMenuHandler topMenuHandler;

    public StageController(Context context, View stage, boolean freeLayout) {
        this.context = context;
        this.stage = stage;

        enterLeanBackTimer = new Handler();
        stageNavigator = new StageNavigator((ViewGroup) stage.findViewById(R.id.screens));
        codec = new SimulatedCodec();

        topMenuHandler = new TopMenuHandler(stage, stageNavigator);
        stage.findViewById(R.id.garbageCan).setOnDragListener(topMenuHandler);
        populateTray();
        setListeners();

        createMonitor(StageNavigator.MONITOR_LEFT, freeLayout);
        createMonitor(StageNavigator.MONITOR_MIDDLE, freeLayout);
        createMonitor(StageNavigator.MONITOR_RIGHT, freeLayout);

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

    private void createMonitor(final int monitorIndex, boolean freeLayout) {
        int monitorViewId = R.id.singlescreen;
        if (monitorIndex == 0)
            monitorViewId = R.id.leftscreen;
        else if (monitorIndex == 2)
            monitorViewId = R.id.rightscreen;

        ScreenView screenView = (ScreenView) stage.findViewById(monitorViewId);

        Screen screen = new Screen();
        screenView.setScreen(screen);

        LayoutDirector director = freeLayout ? new ManualLayoutDirector(screenView) :
                new PredefineLayoutDirector(screenView);

        screenPresenter = new ScreenPresenter(screenView, director);
        screenPresenter.setMonitorSelectedListener(new ScreenPresenter.MonitorListener() {
            @Override
            public void monitorSelected() {
                stageNavigator.focusOnView(monitorIndex);
            }
        });

        CodecCustomLayoutHelper layoutHandler = new CodecCustomLayoutHelper(codec, screenView);
        screenPresenter.setLayoutChangeHandler(layoutHandler);

        // Need to update this screenview after size has been determined
        // There's probably a better way to do this without timer
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                director.updatePositions();
//            }
//        }, 500);

        topMenuHandler.setLayoutMode(director instanceof PredefineLayoutDirector);
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
