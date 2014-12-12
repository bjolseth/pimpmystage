package com.cisco.telepresence.sandbox.stage;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.util.Animations;
import com.cisco.telepresence.sandbox.stage.util.Debug;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class OnHoldController {

    private ViewGroup screens;
    private Context context;

    public OnHoldController(Context context, ViewGroup screens) {
        this.context = context;
        this.screens = screens;
    }

    public void putOnHold() {
        Debug.debug("click hold");
        View left = screens.findViewById(R.id.leftscreen);
        View middle = screens.findViewById(R.id.singlescreen);
        View right = screens.findViewById(R.id.rightscreen);

        float scale = 0.3f;
        Animations.animateMoveAndScale(middle, new Point(0, 0), new Point(0, 0), 1f, scale);
        Animations.animateMoveAndScale(left, new Point(0, 0), new Point(900, 0), 1f, scale- 0.1f);
        Animations.animateMoveAndScale(right, new Point(0, 0), new Point(-900,0), 1f, scale -0.1f);

        addConversations();

        screens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume();
            }
        });
    }

    public void resume() {
        Debug.debug("Resuming");
        View left = screens.findViewById(R.id.leftscreen);
        View middle = screens.findViewById(R.id.singlescreen);
        View right = screens.findViewById(R.id.rightscreen);

        float scale = 0.3f;
        Animations.animateMoveAndScale(middle, new Point(0, 0), new Point(0, 0), scale, 1);
        Animations.animateMoveAndScale(left, new Point(900, 0), new Point(0, 0), scale- 0.1f, 1);
        Animations.animateMoveAndScale(right, new Point(-900,0), new Point(0, 0), scale -0.1f, 1);

        removeConversations();
    }

    private void removeConversations() {
        for (int i = screens.getChildCount() -1; i>=0; i--) {
            View child = screens.getChildAt(i);
            Debug.debug("View %d type %s", i, child.getClass());
            if (! (child instanceof ScreenView))
                screens.removeView(child);
        }
    }

    private void addConversations() {
        int startX = 1000;
        int x = startX;
        int y = 0;
        int w = 220;
        int h = 120;
        int hSpacing = 150;
        int vSpacing = 70;


        int i = 0;
        for (int row=0; row<3; row++) {
            for (int col=0; col<3; col++) {

                // skip center element
                boolean skipElement = (col == 1 && row == 1);
                if (! skipElement) {

                    View call = (i==0 ? createPlaceNewCall() : createConversation(i-1) );

                    call.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h, x, y));
                    screens.addView(call);
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                    call.startAnimation(fadeInAnimation);
                }
                x += w + hSpacing;
                i++;

                if (i > 6)
                    return;
            }
            x = startX;
            y += h + vSpacing;
        }
    }

    private View createConversation(int index) {
        int[] image = {R.drawable.meeting1, R.drawable.presentation1, R.drawable.meeting2, R.drawable.meeting3, 0, R.drawable.presentation3};
        int img = image[index];

        View call = View.inflate(context, R.layout.conversation_onhold, null);
        ((ImageView) call.findViewById(R.id.conversation_icon)).setImageResource(img);
        return call;
    }

    private View createPlaceNewCall() {
        return View.inflate(context, R.layout.placenewcall, null);
    }
}
