package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.content.Context;
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

public class StageController implements  View.OnTouchListener {

    private View stage;
    private Context context;
    private ScreenPresenter screenPresenter;
    private CodecInterface codec;
    private StageNavigator stageNavigator;
    private TopMenuHandler topMenuHandler;
    private LeanBackController leanBackController;
    private OnHoldController onHoldController;

    public StageController(Context context, View stage, boolean freeLayout) {
        this.context = context;
        this.stage = stage;

        stageNavigator = new StageNavigator((ViewGroup) stage.findViewById(R.id.screens));
        codec = new SimulatedCodec();

        topMenuHandler = new TopMenuHandler(stage, stageNavigator);
        onHoldController = new OnHoldController(context, (ViewGroup) stage.findViewById(R.id.screens));

        populateTray();
        leanBackController = new LeanBackController((Activity) context, stage);
        setListeners();

        createMonitor(StageNavigator.MONITOR_LEFT, freeLayout);
        createMonitor(StageNavigator.MONITOR_MIDDLE, freeLayout);
        createMonitor(StageNavigator.MONITOR_RIGHT, freeLayout);
    }

    private void setListeners() {
        stage.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTray(true);
            }
        });
        stage.findViewById(R.id.garbageCan).setOnDragListener(topMenuHandler);

        stage.findViewById(R.id.glass_pane).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                leanBackController.resetLeanBackTimer();
                if (stage.findViewById(R.id.tray_frame).getVisibility() == View.VISIBLE)
                    showTray(false);
                return false; // vital, otherwise no one else will get touch events
            }
        });

        leanBackController.setLeanBackListener(new LeanBackController.LeanBackListener() {
            @Override
            public void leanBackModeChanged(boolean isInLeanBackMode) {
                stage.findViewById(R.id.callcontrolbar).setVisibility(isInLeanBackMode ? View.INVISIBLE: View.VISIBLE);
                stage.findViewById(R.id.topbar).setVisibility(isInLeanBackMode ? View.INVISIBLE : View.VISIBLE);
                showTray(false);
            }
        });

        stage.findViewById(R.id.end_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endPressed();
            }
        });

        stage.findViewById(R.id.hold_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHoldController.putOnHold();
            }
        });
    }

    private void endPressed() {
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
        screenPresenter.setMonitorSelectedListener(new ScreenPresenter.ScreenSelectedCallback() {
            @Override
            public void monitorSelected() {
                stageNavigator.focusOnView(monitorIndex);
            }
        });

        CodecCustomLayoutHelper layoutHandler = new CodecCustomLayoutHelper(codec, screenView);
        screenPresenter.setLayoutChangeHandler(layoutHandler);


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


}
