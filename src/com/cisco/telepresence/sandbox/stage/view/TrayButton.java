package com.cisco.telepresence.sandbox.stage.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.stage.model.Frame;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.util.Avatars;

public class TrayButton extends LinearLayout {

    private Frame.FrameType type;
    private String name;

    public TrayButton(Context context, Frame.FrameType type, String name) {
        super(context);
        this.type = type;
        this.name = name;
        boolean isPresentation = (type == Frame.FrameType.LOCAL_PRESENTATATION);
        int layout = isPresentation ? R.layout.tray_item_presentation : R.layout.tray_item_avatar;
        inflate(context, layout, this);

        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ((TextView) findViewById(R.id.tray_item_text)).setText(name);

        int image = (isPresentation ? Avatars.getAvatarImageId(name, type) : Avatars.getRoundAvatar(name));

        findViewById(R.id.tray_item).setBackgroundResource(image);
    }

    public Frame.FrameType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
