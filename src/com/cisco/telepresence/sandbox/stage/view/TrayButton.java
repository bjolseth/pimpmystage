package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.R;

public class TrayButton extends LinearLayout {

    private Frame.FrameType type;
    private String name;

    public TrayButton(Context context, Frame.FrameType type, String name) {
        super(context);
        this.type = type;
        this.name = name;
        inflate(context, R.layout.tray_item, this);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ((TextView) findViewById(R.id.tray_item_text)).setText(name);
    }

    public Frame.FrameType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
