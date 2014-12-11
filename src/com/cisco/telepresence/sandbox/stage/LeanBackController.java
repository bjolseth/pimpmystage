package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import com.cisco.telepresence.sandbox.R;

public class LeanBackController implements View.OnSystemUiVisibilityChangeListener {

    public static final int LEAN_BACK_TIMEOUT = 5000;
    private Handler enterLeanBackTimer;
    private Activity activity;
    private View stage;
    private int lastSystemUIVisibility;
    private LeanBackListener leanBackListener;

    public interface LeanBackListener {
        public void leanBackModeChanged(boolean isInLeanBackMode);
    }

    public LeanBackController(Activity activity, View stage) {
        this.activity = activity;
        this.stage = stage;

        enterLeanBackTimer = new Handler();
        resetLeanBackTimer();
        activity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
    }

    public void setLeanBackListener(LeanBackListener listener) {
        leanBackListener = listener;
    }

    protected void enableLeanBackMode(boolean enabled) {
        int newVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if(enabled) {
            newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN
                    |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Set the visibility
        activity.getWindow().getDecorView().setSystemUiVisibility(newVisibility);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        boolean outOfLeanback =  ((lastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0
                && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0);

        if (outOfLeanback)
            resetLeanBackTimer();

        lastSystemUIVisibility = visibility;
        if (leanBackListener != null)
            leanBackListener.leanBackModeChanged(!outOfLeanback);
    }

    public void resetLeanBackTimer() {
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
