package com.cisco.telepresence.sandbox.stage.util;

import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.model.Frame;

public class Avatars {

    public static int getAvatarImageId(String name, Frame.FrameType type) {

        int image = R.drawable.avatar_camera;
        if (type == Frame.FrameType.SELFVIEW)
            image = R.drawable.avatar_camera;
        else if (type == Frame.FrameType.VIDEO)
            image = R.drawable.avatar_single;
        else if (type == Frame.FrameType.LOCAL_PRESENTATATION)
            image = R.drawable.avatar_pc;

        if (name.equalsIgnoreCase("licorice"))
            image = R.drawable.meeting1;
        else if (name.equalsIgnoreCase("kvikklunsj"))
            image = R.drawable.meeting2;
        else if (name.equalsIgnoreCase("fandango"))
            image = R.drawable.meeting3;
        else if (name.equalsIgnoreCase("hdmi"))
            image = R.drawable.presentation1;
        else if (name.equalsIgnoreCase("dvi"))
            image = R.drawable.yosemite;
        else if (name.equalsIgnoreCase("team"))
            image = R.drawable.team_photo;
        else if (name.equalsIgnoreCase("dude1"))
            image = R.drawable.ugly1;
        else if (name.equalsIgnoreCase("dude2"))
            image = R.drawable.ugly2;
        else if (name.equalsIgnoreCase("dude3"))
            image = R.drawable.ugly3;
        else if (name.equalsIgnoreCase("dude4"))
            image = R.drawable.ugly4;
        else if (name.equalsIgnoreCase("selfview"))
            image = R.drawable.selfview;

        return image;
    }


    public static int getRoundAvatar(String name) {
        int image = R.drawable.avatar_camera;
        if (! name.startsWith("Dude"))
            image = getAvatarImageId(name, Frame.FrameType.VIDEO);
        else {
            if (name.equalsIgnoreCase("dude1"))
                image = R.drawable.ugly1_round;
            else if (name.equalsIgnoreCase("dude2"))
                image = R.drawable.ugly2_round;
            else if (name.equalsIgnoreCase("dude3"))
                image = R.drawable.ugly3_round;
            else if (name.equalsIgnoreCase("dude4"))
                image = R.drawable.ugly4_round;
        }
        return image;
    }

}
