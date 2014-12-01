package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;
import com.cisco.telepresence.sandbox.stage.model.Screen;
import com.cisco.telepresence.sandbox.stage.model.Stage;

public class StageView extends AbsoluteLayout {

    public StageView(Context context) {
        super(context);
    }

    public StageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setStage(Stage stage) {
        for (Screen screen : stage.getScreens()) {
            ScreenView screenView = new ScreenView(getContext());
            screenView.setScreen(screen);
            addView(screenView); // TODO set screen position
        }
    }
}
